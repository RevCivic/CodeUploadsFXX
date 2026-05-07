package com.daisydata.codescans.codeuploadsfx;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProcessUploadsStandalone {

    public static String folderPath = "//dnas1/dms/Incoming/wgss/";
    public static String dmsPath = "//dnas1/dms/Documents";
    public static File uploadDirectory = new File(folderPath);
    public static final Logger logger = LogManager.getLogger(ProcessUploadsStandalone.class);
    public static String woFolder = "//dnas1/dms/Documents/Unassociated WOs";

    // Maps for categories: keys are docType strings like "po", "wo", etc.
    private static final HashMap<String, String> categoryIdMap = new HashMap<>();
    private static final HashMap<String, String> categoryPathMap = new HashMap<>();

    public static void main(String[] args) {
        logger.info("Processing uploads standalone...");

        DatabaseConnection conn = new DatabaseConnection();

        loadCategories(conn);

        File[] fileList = uploadDirectory.listFiles();
        if (fileList == null) {
            logger.error("Upload directory not found or empty: {}", folderPath);
            return;
        }

        for (File file : fileList) {
            if (file.isDirectory()) continue;

            String fileName = file.getName();

            if (fileName.equalsIgnoreCase("Thumbs.db") || fileName.equalsIgnoreCase("Pending") || fileName.equalsIgnoreCase("Invoices")) {
                continue;
            }

            logger.info("Processing file: {}", fileName);

            String poNumber = "";
            String destinationFolder = "";
            String docType = "";
            String itemType = "";

            if (fileName.startsWith("REQ")) {
                String[] parts = fileName.split("-");
                if (parts.length < 2) {
                    logger.warn("Unexpected REQ filename format: {}", fileName);
                    continue;
                }
                destinationFolder = parts[1].split("_")[0];
                poNumber = conn.findReqPo(destinationFolder);
                if (poNumber.isEmpty()) {
                    logger.warn("No PO number found for REQ: {}", destinationFolder);
                    continue;
                }
            }

            if (poNumber.isEmpty()) {
                String[] fileInfo = fileName.split("_");
                if (fileInfo.length < 2) {
                    logger.warn("Unexpected filename format: {}", fileName);
                    continue;
                }

                System.out.println("File Info: " + Arrays.toString(fileInfo));
                docType = fileInfo[0].toLowerCase();
                itemType = fileInfo[1].toLowerCase();
            } else {
                String[] fileNameSplit = fileName.split("\\.");
                fileName = "PO_REQ_" + poNumber + "_0." + fileNameSplit[fileNameSplit.length - 1].toLowerCase();
                logger.info("Renamed to: {}", fileName);
                docType = "po";
                itemType = "";
            }

            boolean isWO = docType.equalsIgnoreCase("wo");

            String categoryID = categoryIdMap.get(docType.toLowerCase());
            System.out.println("categoryID: " + categoryID);
            String categoryPath = categoryPathMap.get(docType.toLowerCase());
            System.out.println("categoryPath: " + categoryPath);

            if (categoryID == null || categoryPath == null) {
                logger.warn("Category ID or Path missing for docType: {}",  docType);
                continue;
            }

            String itemNumber = "";
            if (docType.equalsIgnoreCase("ncmr")) {
                String[] parts = fileName.split("_");
                if (parts.length >= 2) {
                    itemNumber = parts[1];
                }
            } else {
                String[] parts = fileName.split("_");
                if (parts.length >= 3) {
                    itemNumber = parts[2];
                }
            }

            String[] identifierInfo = conn.findFolderName(docType, itemNumber, isWO);
            if (identifierInfo == null || identifierInfo.length < 2) {
                logger.warn("Could not find folder info for file: {}", fileName);
                continue;
            }

            if (identifierInfo[0] == null || identifierInfo[1] == null) {
                logger.warn("Incomplete folder info for file: {}", fileName);
                continue;
            }

            String subFolder = identifierInfo[0].substring(0, 1).toUpperCase();
            String identifier = identifierInfo[1];

            // Construct destination path
            if (!docType.toUpperCase().contains("VEND") && !docType.toUpperCase().contains("CUST")) {
                destinationFolder = dmsPath + categoryPath + subFolder + File.separator + identifier + File.separator + itemNumber;
            } else {
                destinationFolder = dmsPath + categoryPath + subFolder + File.separator + identifier;
            }

            if (isWO) {
                destinationFolder = woFolder;
            }

            // Create directories if they don't exist
            File destFolderFile = new File(destinationFolder);
            if (!destFolderFile.exists() && !destFolderFile.mkdirs()) {
                logger.error("Failed to create destination directory: {}", destinationFolder);
                continue;
            }

            String newFullFileName = findValidFileName(destinationFolder, fileName);
            logger.info("Moving file to: {}", newFullFileName);

            conn.addNewDocument(destinationFolder, newFullFileName, itemNumber, itemType, docType);

            boolean success = file.renameTo(new File(newFullFileName));
            if (!success) {
                logger.error("Failed to move file: {}", fileName);
            } else {
                logger.info("Moved file successfully: {}", fileName);
            }
        }

        conn.deconstruct();
        logger.info("Processing complete.");
    }

    private static void loadCategories(DatabaseConnection conn) {
        categoryIdMap.clear();
        categoryPathMap.clear();

        String sql = "SELECT CATEGORY_NAME, CATEGORY_ID, CATEGORY_PATH FROM D3_DMS_CATEGORIES WHERE ACTIVE = 1";

        try (ResultSet rs = conn.executeQuery(sql)) {
            while (rs.next()) {
                String categoryName = rs.getString("CATEGORY_NAME").toLowerCase(Locale.ROOT);
                String categoryId = rs.getString("CATEGORY_ID").trim().toLowerCase();
                String categoryPath = rs.getString("CATEGORY_PATH").trim();
                categoryIdMap.put(categoryId, categoryId);
                categoryPathMap.put(categoryId, categoryPath);
            }
            System.out.println("categoryIdMap: " + categoryIdMap);
            System.out.println("categoryPathMap: " + categoryPathMap);
            logger.info("Loaded categories: " + categoryIdMap.keySet());
        } catch (SQLException e) {
            logger.error("Failed to load categories from database", e);
        }
    }

    static String findValidFileName(String folder, String fileName) {
        logger.debug("Checking for valid filename: {}", fileName);
        int numOccurrence = 1;
        String newFullFileName = folder + File.separator + fileName;
        while (new File(newFullFileName).exists()) {
            int lastUnderscoreIndex = fileName.lastIndexOf("_");
            int lastDotIndex = fileName.lastIndexOf(".");

            if (lastDotIndex > lastUnderscoreIndex && lastUnderscoreIndex != -1) {
                String baseFileName = fileName.substring(0, lastUnderscoreIndex);
                String extension = fileName.substring(lastDotIndex);
                newFullFileName = folder + File.separator + baseFileName + "_" + numOccurrence + extension;
            } else {
                newFullFileName = folder + File.separator + fileName + "_" + numOccurrence;
            }
            numOccurrence++;
        }
        return newFullFileName;
    }
}
