package com.daisydata.codescans.codeuploadsfx;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class ProcessUploads {
    public static String folderPath = "//dnas1/dms/Incoming/wgss";
    public static File uploadDirectory = new File(folderPath);
    public static File[] fileList = uploadDirectory.listFiles();

    public ProcessUploads() {
    }

    public static void main(String[] args) {
        DatabaseConnection conn = new DatabaseConnection();


        for(int i = 0; i < fileList.length; i++) {
            File file = fileList[i];
            String po_number = "";
            if (!file.getName().equals("Thumbs.db") && !file.getName().equals("Pending") && !file.getName().equals("Invoices")) {
                String destinationFolder;
                if (file.getName().substring(0, 3).equals("REQ")) {
                    destinationFolder = file.getName().split("-")[1].split("_")[0];
                    po_number = conn.findReqPo(destinationFolder);
                    if (po_number == "") {
                        continue;
                    }
                }

                destinationFolder = "";
                String newFullFileName = "";
                String fileName = "";
                String[] fileInfo = null;
                String docType = "";
                String itemType = "";
                if (po_number == "") {
                    fileName = file.getName();
                    fileInfo = fileName.split("_");
                    docType = fileInfo[0].toLowerCase();
                } else {
                    String[] fileNameSplit = file.getName().split("\\.");
                    fileName = "PO_REQ_" + po_number + "_0." + fileNameSplit[fileNameSplit.length - 1].toLowerCase();
                    fileInfo = fileName.split("_");
                    docType = "po";
                    itemType = "req";
                }
                itemType = fileInfo[1].toLowerCase();
                console("Item Type: "+itemType);
                console("DocType: "+docType);
                Object categoryIDObj = CodeScansController.categories[3].get(docType.toLowerCase(Locale.ROOT));
                Object categoryPath = CodeScansController.categories[4].get(docType.toLowerCase(Locale.ROOT));
//                console("Category Path A: "+categoryPath);
                console("Category ID: "+categoryIDObj);
//                System.out.print(CodeScansController.categories[4]);
                if(categoryIDObj != null && categoryPath != null){
                    String itemNumber = "";
                    String[] identifierInfo = new String[2];
                    String catalogPath = categoryPath.toString();
                    console("Catalog Path: "+catalogPath);
                    String subFolder;
                    String identifier;

                    itemNumber = fileInfo[2];

                    console("Item Number: "+itemNumber);

                    identifierInfo = conn.findFolderName(docType, itemNumber);
                    if (identifierInfo[0] == null) {
                        // If IdentifierInfo[0] (Customer/Vendor Number) is null, then skip this item and restart the loop
                        continue;
                    }
                    console("Identifier Info: "+identifierInfo[0]+", "+identifierInfo[1]);

                    subFolder = identifierInfo[0].substring(0, 1).toUpperCase();
                    identifier = identifierInfo[1];
                    if (!docType.toUpperCase().contains("VEND") && !docType.toUpperCase().contains("CUST")) {
                        destinationFolder = catalogPath + subFolder + "/" + identifier + "/" + itemNumber;
                    } else {
                        destinationFolder = catalogPath + subFolder + "/" + identifier;
                    }
                    console("Destination Folder: "+destinationFolder);

                    if (!(new File(destinationFolder)).exists()) {
                        String parentDirectory = catalogPath + subFolder + "/" + identifier;
                        if (!(new File(parentDirectory)).exists()) {
                            String subFolderDirectory = catalogPath + subFolder;
                            if (!(new File(subFolderDirectory)).exists()) {
                                (new File(subFolderDirectory)).mkdirs();
                                conn.addNewFolder(subFolderDirectory);
                            }

                            if (!docType.toUpperCase().contains("VEND") && !docType.toUpperCase().contains("CUST")) {
                                (new File(parentDirectory)).mkdirs();
                                conn.addNewFolder(parentDirectory);
                            }
                        } else if (!conn.pathIDExist(parentDirectory)) {
                            conn.addNewFolder(parentDirectory);
                        }

                        (new File(destinationFolder)).mkdirs();
                        conn.addNewFolder(destinationFolder);
                    }
//                }

                    newFullFileName = findValidFileName(destinationFolder, fileName);
                    identifier = itemType;

                    console("Dest: "+destinationFolder);
                    console("New File Name: "+newFullFileName);
                    console("Item Num: "+itemNumber);
                    console("Identifier: "+identifier);
                    console("Subfolder: "+subFolder);

                    conn.addNewDocument(destinationFolder, newFullFileName, itemNumber, identifier, subFolder);
                    newFullFileName = newFullFileName.replace("/", "\\");
                    console("New Filename: "+newFullFileName);
                    file.renameTo(new File(newFullFileName));
                }
            }
        }

        conn.deconstruct();
    }

    private static String findValidFileName(String folder, String fileName) {
        int numOccurrence = 1;
        boolean alreadyExists = false;
        String newFullFileName = "";
        String[] fileNameInfo;
        if (fileName.contains("FGC")) {
            fileNameInfo = fileName.split("_");
            fileName = fileNameInfo[0] + "_" + fileNameInfo[1] + "_" + fileNameInfo[2] + "_" + fileNameInfo[3] + "-" + fileNameInfo[4];
            fileName = (new StringBuilder(fileName)).insert(fileName.lastIndexOf(46), "_0").toString();
        }

        newFullFileName = folder + "/" + fileName;
        alreadyExists = (new File(newFullFileName)).exists();

        for(fileNameInfo = fileName.split("_"); alreadyExists; alreadyExists = (new File(newFullFileName)).exists()) {
            String baseFileName = "";
            if (fileName.contains("FGC")) {
                baseFileName = fileNameInfo[0] + "_" + fileNameInfo[1] + "_" + fileNameInfo[2] + "_" + fileNameInfo[3] + fileNameInfo[4].substring(fileNameInfo[4].indexOf("."));
            } else {
                baseFileName = fileNameInfo[0] + "_" + fileNameInfo[1] + "_" + fileNameInfo[2] + fileNameInfo[3].substring(fileNameInfo[3].indexOf("."));
            }

            StringBuilder var10001 = new StringBuilder(baseFileName);
            int var10002 = baseFileName.lastIndexOf(46);
            int var10003 = numOccurrence++;
            newFullFileName = folder + "/" + var10001.insert(var10002, "_" + var10003).toString();
        }

        return newFullFileName;
    }

    private static void console(String msg) {
        System.out.println(msg);
    }

}
