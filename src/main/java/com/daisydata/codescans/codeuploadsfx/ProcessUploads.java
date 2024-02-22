package com.daisydata.codescans.codeuploadsfx;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import static com.daisydata.codescans.codeuploadsfx.CodeScansApplication.LOGGING;

public class ProcessUploads {
    public static String folderPath = "//dnas1/dms/Incoming/wgss/";
    public static String dmsPath = "//dnas1/dms/Documents";
    public static File uploadDirectory = new File(folderPath);
    public static File[] fileList = uploadDirectory.listFiles();

    public static String woFolder = "//dnas1/dms/Documents/Unassociated WOs";



    // Creates a connection and checks all files in the folder, processes them based on category, subcategory, and
    // number, then moves them to the proper folder, creating a path_id and updating sql tables when it moves them
    public static void main(String[] args) {
        DatabaseConnection conn = new DatabaseConnection();
        uploadDirectory = null;
        uploadDirectory = new File(folderPath);
        fileList = null;
        fileList = uploadDirectory.listFiles();
        boolean isWO = false;

        // goes over all files in the 'incoming' folder and ignores specific ones
        for(int i = 0; i < Objects.requireNonNull(fileList).length; i++) {
            File file = fileList[i];
            String po_number = "";
            if (!file.getName().equals("Thumbs.db") && !file.getName().equals("Pending") && !file.getName().equals("Invoices")) {
                String destinationFolder;
                if (file.getName().startsWith("REQ")) {
                    destinationFolder = file.getName().split("-")[1].split("_")[0];
                    po_number = conn.findReqPo(destinationFolder);
                    if (Objects.equals(po_number, "")) {
                        continue;
                    }
                }
                destinationFolder = "";
                String newFullFileName;
                String fileName;
                String[] fileInfo;
                String docType;
                String itemType;
                if (Objects.equals(po_number, "")) {
                    fileName = file.getName();
                    fileInfo = fileName.split("_");
                    docType = fileInfo[0].toLowerCase();
                } else {
                    String[] fileNameSplit = file.getName().split("\\.");
                    fileName = "PO_REQ_" + po_number + "_0." + fileNameSplit[fileNameSplit.length - 1].toLowerCase();
                    fileInfo = fileName.split("_");
                    docType = "po";
                }
                // This is set based on ncmr and req because the file names don't have a category and subcat
                // to file by. They only contain one (ex: NCMR_0002109.jpg). This catches those and sets
                // the correct doctype and itemType.
                if (docType.equalsIgnoreCase("ncmr")) {
                    itemType = docType;
                } else {
                    itemType = fileInfo[1].toLowerCase();
                }
                console("Item Type: " + itemType);
                console("DocType: " + docType);
                if (docType.toLowerCase().equalsIgnoreCase("wo")) {
                    isWO = true;
                }
                Object categoryIDObj = CodeScansController.categories[3].get(docType.toLowerCase(Locale.ROOT));
                Object categoryPath = CodeScansController.categories[4].get(docType.toLowerCase(Locale.ROOT));
                console("Category Path A: "+categoryPath);
                console("Category ID: " + categoryIDObj);
                String identifier;
                String itemNumber = null;
                String subFolder = null;
                if (categoryIDObj != null && categoryPath != null) {
                    String[] identifierInfo;
                    String catalogPath = categoryPath.toString();
                    CodeScansApplication.logger.info("Catalog Path: " + catalogPath);
                    destinationFolder = dmsPath;
                    console("FILEINFO: " + Arrays.toString(fileInfo));
                    // as with above, NCMR and REQs are structured differently. This gets the number and
                    // assigns it to itemNumber correctly.
                    if (docType.equalsIgnoreCase("ncmr")) {
                        itemNumber = fileInfo[1];
                    } else {
                        itemNumber = fileInfo[2];
                    }
                    // Gets the folder name that the file should go into, determined by the category and subcategory
                    // of the file
                    identifierInfo = conn.findFolderName(docType, itemNumber, isWO);
                    console("DOCTYPE: " + docType + "  | ITEMNUMBER: " + itemNumber +  " | INFO: " + Arrays.toString(identifierInfo));
                    if (identifierInfo[0] == null) {
//                        If IdentifierInfo[0] (Customer/Vendor Number) is null, then skip this item and restart the loop
                        continue;
                    }
                    if (identifierInfo[1] == null) {
                        continue;
                    }
                    console("Identifier Info: " + identifierInfo[0] + ", " + identifierInfo[1]);
                    CodeScansApplication.logger.info("Identifier Info: " + identifierInfo[0] + ", " + identifierInfo[1]);
                    subFolder = identifierInfo[0].substring(0, 1).toUpperCase();
                    identifier = identifierInfo[1];
                    boolean isCustOrVend = !docType.toUpperCase().contains("VEND") && !docType.toUpperCase().contains("CUST");
                    isWO = docType.toUpperCase().startsWith("WO");
                    if (isCustOrVend) {
                        destinationFolder += catalogPath + subFolder + "/" + identifier + "/" + itemNumber;
                    } else {
                        destinationFolder += catalogPath + subFolder + "/" + identifier;
                    }
                    if (isWO) {
                        destinationFolder = woFolder;
                        (new File(destinationFolder)).mkdirs();
                    } else {
                        console("Destination Folder: " + destinationFolder);
                        CodeScansApplication.logger.info("Destination Folder: " + destinationFolder);
                        (new File(destinationFolder)).mkdirs();

                    }
                }

//                Use the function to find the fully qualified path that the file will be renamed to
                newFullFileName = findValidFileName(destinationFolder, fileName);
                console("findValidFileName: "+ destinationFolder + "| " + fileName);
                identifier = itemType;
//                -Console Logging --
                console("LOGGING");
                console("Dest: " + destinationFolder);
                console("New File Name: " + newFullFileName);
                console("Item Num: " + itemNumber);
                console("Identifier: " + identifier);
                console("Subfolder: " + subFolder);
                CodeScansApplication.logger.info("LOGGING\nNew File Name: " + newFullFileName + "\nItem Num: " + itemNumber + "\nIdentifier: " + identifier + "\nSubfolder: " + subFolder + "\n");
//                swap slash orientation
                newFullFileName = newFullFileName.replace("/", "\\");
                DocumentType docTypeEnum = DocumentType.valueOf(docType.toUpperCase());
                ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase().replace("-", "_"));
                String subcategory = DetermineDocument.determineSubcategory(docTypeEnum, itemTypeEnum);
                console(identifier);
//                Write the entry to the Database
//                System.out.println("conn.addNewDocument : " + destinationFolder + ", "  + newFullFileName + ", " + itemNumber + ", " + identifier + ", " + docType);
                conn.addNewDocument(destinationFolder, newFullFileName, itemNumber, identifier, docType);
//                console("New Filename: " + newFullFileName);
//                Rename the file
                file.renameTo(new File(newFullFileName));
            }
        }
        conn.deconstruct();
    }

    // Checks to make sure the filename doesn't already exist. If it does, it'll change the number at the end
    // of the file until one doesn't exist prior to adding it to the folder
    static String findValidFileName(String folder, String fileName) {
        int numOccurrence = 1;
        String newFullFileName = folder + "/" + fileName;
        while (new File(newFullFileName).exists()) {
            int lastUnderscoreIndex = fileName.lastIndexOf("_");
            int lastDotIndex = fileName.lastIndexOf(".");

            if (lastDotIndex > lastUnderscoreIndex && lastUnderscoreIndex != -1) {
                String baseFileName = fileName.substring(0, lastUnderscoreIndex);
                String extension = fileName.substring(lastDotIndex);
                newFullFileName = folder + "/" + baseFileName + "_" + numOccurrence + extension;
            } else {
                // If the file name does not contain an underscore, just append the occurrence number to the end
                newFullFileName = folder + "/" + fileName + "_" + numOccurrence;
            }
            numOccurrence++;
        }
        return newFullFileName;
    }

    // for console logging
    private static void console(String msg) {
        if (LOGGING) {
            System.out.println(msg);
        }
    }

}
