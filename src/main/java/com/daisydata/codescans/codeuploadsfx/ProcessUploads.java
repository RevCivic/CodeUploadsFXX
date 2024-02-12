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




    public static void main(String[] args) {
        DatabaseConnection conn = new DatabaseConnection();
        uploadDirectory = null;
        uploadDirectory = new File(folderPath);
        fileList = null;
        fileList = uploadDirectory.listFiles();

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
                itemType = fileInfo[1].toLowerCase();
                console("Item Type: " + itemType);
                console("DocType: " + docType);
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
                    itemNumber = fileInfo[2];
                    identifierInfo = conn.findFolderName(docType, itemNumber, false);
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
                    boolean isWO = docType.toUpperCase().startsWith("WO");
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
                System.out.println("DOCTYPE: " + docType);
                DocumentType docTypeEnum = DocumentType.valueOf(docType.toUpperCase());
                System.out.println("DOCTYPE ENUM: " + docTypeEnum);
                ItemType itemTypeEnum = ItemType.valueOf(itemType.toUpperCase().replace("-", "_"));
                System.out.println("ITEM TYPE ENUM: " + itemTypeEnum);
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


        //original findValidFileName. save for reverting to original in case the new one doesn't work right.
//    static String findValidFileName(String folder, String fileName) {
//        int numOccurrence = 1;
//        boolean alreadyExists;
//        String newFullFileName;
//        String[] fileNameInfo;
//
//        if (fileName.contains("FGC")) {
//            fileNameInfo = fileName.split("_");
//            fileName = fileNameInfo[0] + "_" + fileNameInfo[1] + "_" + fileNameInfo[2] + "_" + fileNameInfo[3] + "-" + fileNameInfo[4];
//            fileName = (new StringBuilder(fileName)).insert(fileName.lastIndexOf(46), "_0").toString();
//        }
//
//        newFullFileName = folder + "/" + fileName;
//        alreadyExists = (new File(newFullFileName)).exists();
//        for (fileNameInfo = fileName.split("_"); alreadyExists; alreadyExists = (new File(newFullFileName)).exists()) {
//            String baseFileName;
//            if (fileName.contains("FGC")) {
//                baseFileName = fileNameInfo[0] + "_" + fileNameInfo[1] + "_" + fileNameInfo[2] + "_" + fileNameInfo[3] + fileNameInfo[4].substring(fileNameInfo[4].indexOf("."));
//            } else {
//
//                baseFileName = fileNameInfo[0] + "_" + fileNameInfo[1] + "_" + fileNameInfo[2] + fileNameInfo[3].substring(fileNameInfo[3].indexOf("."));
//            }
//            StringBuilder var10001 = new StringBuilder(baseFileName);
//            int var10002 = baseFileName.lastIndexOf(46);
//            int var10003 = numOccurrence++;
//            newFullFileName = folder + "/" + var10001.insert(var10002, "_" + var10003);
//
//        }
//        return newFullFileName;
//    }
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

    private static void console(String msg) {
        if (LOGGING) {
            System.out.println(msg);
        }
    }

}
