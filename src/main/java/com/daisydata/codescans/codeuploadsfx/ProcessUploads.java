package com.daisydata.codescans.codeuploadsfx;

import java.io.File;
import java.util.HashMap;

public class ProcessUploads {
    public ProcessUploads() {
    }

    public static void main(String[] args) {
        codeUploads.databaseConnection conn = new codeUploads.databaseConnection();
        String folderPath = "//dnas1/dms/Incoming/wgss";
        HashMap<String, String> itemTypes = createHash();
        File uploadDirectory = new File(folderPath);
        File[] fileList = uploadDirectory.listFiles();
        File[] var6 = fileList;
        int var7 = fileList.length;

        for(int var8 = 0; var8 < var7; ++var8) {
            File file = var6[var8];
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
                    fileInfo = fileName.split("_", 3);
                    docType = fileInfo[0].toLowerCase();
                    itemType = fileInfo[1].toLowerCase();
                } else {
                    String[] fileNameSplit = file.getName().split("\\.");
                    fileName = "PO_REQ_" + po_number + "_0." + fileNameSplit[fileNameSplit.length - 1].toLowerCase();
                    fileInfo = fileName.split("_", 3);
                    docType = "po";
                    itemType = "req";
                }

                String itemNumber = "";
                String[] identifierInfo = new String[2];
                String catalogPath = determineCatalog(docType);
                String subFolder;
                String identifier;
                if (docType.contains("cpo")) {
                    destinationFolder = "//dnas1/dms/Incoming/wgss/Pending/";
                } else if (docType.contains("wo")) {
                    destinationFolder = "//dnas1/dms/Documents/Unassociated WOs";
                } else if (docType.contains("sample")) {
                    destinationFolder = "//dnas1/dms/Documents/Samples";
                } else if (docType.contains("misc")) {
                    destinationFolder = "//dnas1/dms/Documents/Miscellaneous";
                } else if (docType.contains("cycle-count")) {
                    destinationFolder = "//dnas1/dms/Documents/Weekly Cycle Counts";
                } else if (docType.contains("fga")) {
                    destinationFolder = "//dnas1/dms/Documents/FGA Audits";
                } else if (docType.contains("ee")) {
                    destinationFolder = "//dnas1/dms/Documents/Employee Photos";
                    itemNumber = String.format("%05d", fileInfo[2]);
                } else {
                    if (fileInfo[2].contains("_")) {
                        itemNumber = fileInfo[2].split("_")[0];
                    } else {
                        itemNumber = fileInfo[2];
                    }

                    identifierInfo = conn.findFolderName(docType, itemNumber);
                    if (identifierInfo[0] == null) {
                        continue;
                    }

                    subFolder = identifierInfo[0].substring(0, 1).toUpperCase();
                    identifier = identifierInfo[1];
                    if (!docType.toUpperCase().contains("VEND") && !docType.toUpperCase().contains("CUST")) {
                        destinationFolder = catalogPath + subFolder + "/" + identifier + "/" + itemNumber;
                    } else {
                        destinationFolder = catalogPath + subFolder + "/" + identifier;
                    }

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
                }

                newFullFileName = findValidFileName(destinationFolder, fileName);
                subFolder = (String)itemTypes.get(docType.toLowerCase());
                identifier = "";
                if (itemType.toUpperCase().contains("FGC")) {
                    identifier = "Finished Good Content";
                } else if (!docType.toUpperCase().contains("CYCLE-COUNT") && !docType.toUpperCase().contains("FGA")) {
                    identifier = (String)itemTypes.get(itemType.toLowerCase());
                } else {
                    identifier = itemType;
                }

                conn.addNewDocument(destinationFolder, newFullFileName, itemNumber, identifier, subFolder);
                newFullFileName = newFullFileName.replace("/", "\\");
                file.renameTo(new File(newFullFileName));
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

    private static String determineCatalog(String doctype) {
        if (doctype.equals("so")) {
            return "//dnas1/dms/Documents/Accounts Receivables/";
        } else if (doctype.equals("po")) {
            return "//dnas1/dms/Documents/Accounts Payable/";
        } else if (doctype.equals("rma")) {
            return "//dnas1/dms/Documents/RMA Sales Orders/";
        } else if (doctype.equals("cust")) {
            return "//dnas1/dms/Documents/Customer Info/";
        } else if (doctype.equals("vend")) {
            return "//dnas1/dms/Documents/Vendor Info/";
        } else if (doctype.equals("rep")) {
            return "//dnas1/dms/Documents/Rep Info/";
        } else if (doctype.equals("part")) {
            return "//dnas1/dms/Documents/Part Info/";
        } else if (doctype.equals("ncmr")) {
            return "//dnas1/dms/Documents/QRR/";
        } else {
            return doctype.equals("wo") ? "//dnas1/dms/Documents/QRR/" : "";
        }
    }

    private static HashMap<String, String> createHash() {
        HashMap<String, String> itemTypes = new HashMap();
        itemTypes.put("so", "Sales Order");
        itemTypes.put("rma", "RMA");
        itemTypes.put("cust", "Customer Info");
        itemTypes.put("po", "Purchase Order");
        itemTypes.put("invoice", "Invoice");
        itemTypes.put("quote", "Quote");
        itemTypes.put("coc", "CoC");
        itemTypes.put("shipping", "Shipping Slip");
        itemTypes.put("tracking", "Tracking Number");
        itemTypes.put("approval", "Approval Slip");
        itemTypes.put("acknowledgement", "Acknowledgement");
        itemTypes.put("credit-memo", "Credit Memo");
        itemTypes.put("cpo", "Customer Purchase Order");
        itemTypes.put("wo", "Work Order");
        itemTypes.put("misc", "Miscellaneous");
        itemTypes.put("payment", "Payment");
        itemTypes.put("req", "Requisition");
        itemTypes.put("poc", "POC");
        itemTypes.put("fgc", "Finished Good Content");
        itemTypes.put("packing-slip", "Packing Slip");
        itemTypes.put("outgoing-packing", "Outgoing Packing Slip");
        itemTypes.put("first-article", "First Article");
        itemTypes.put("check", "Check");
        itemTypes.put("hcpo", "Halliburton Customer Purchase Order");
        itemTypes.put("lcpo", "Lockheed Customer Purchase Order");
        itemTypes.put("rma-auth", "RMA Authorization Form");
        itemTypes.put("rma-form", "RMA Form");
        itemTypes.put("returned-rma-auth", "Returned RMA Authorization Form");
        itemTypes.put("repair-report", "Repair Report");
        itemTypes.put("halliburton", "Halliburton CPO");
        itemTypes.put("lockheed", "Lockheed CPO");
        itemTypes.put("unassigned", "Unassigned CPO");
        itemTypes.put("custinfo", "Customer Info");
        itemTypes.put("crinfo", "Credit Info");
        itemTypes.put("nda", "NDA");
        itemTypes.put("supform", "Supplier Form");
        itemTypes.put("taxexmptcert", "Tax Exempt Certificate");
        itemTypes.put("w9", "W9");
        itemTypes.put("tnc", "T & C");
        itemTypes.put("vendinfo", "Vendor Info");
        itemTypes.put("cert", "Certificate");
        itemTypes.put("vendassmnt", "Vendor Assessment");
        itemTypes.put("rep", "Rep Info");
        itemTypes.put("agreemnt", "Agreement");
        itemTypes.put("exhbta", "Exhibit A");
        itemTypes.put("sda", "Sales Distribution Agreement");
        itemTypes.put("eol", "End of Life");
        itemTypes.put("qrr", "QRR");
        itemTypes.put("ncmr", "NCMR");
        itemTypes.put("qrrcps", "QRR Customer Packing Slip");
        itemTypes.put("qrrpo", "QRR Purchase Order");
        itemTypes.put("qrrps", "QRR Packing Slip");
        itemTypes.put("rr", "Receiving Record");
        itemTypes.put("pic", "Picture");
        itemTypes.put("summary", "Summary Page");
        itemTypes.put("rma-req", "RMA Request");
        itemTypes.put("none", "NO SO");
        itemTypes.put("pic", "Picture");
        itemTypes.put("email", "Email");
        itemTypes.put("drawings", "Drawings");
        itemTypes.put("designplan", "Design Plan");
        itemTypes.put("scrap-authorized", "Scrap Authorization");
        itemTypes.put("scrap-deny-rtc", "Scrap Denied Return To Customer");
        itemTypes.put("sample", "Sample");
        itemTypes.put("progress-bill", "Progress Bill");
        itemTypes.put("cust-rejection", "Customer Rejection Report");
        itemTypes.put("customer-notes", "Customer Notes");
        itemTypes.put("vend", "Vendor Info");
        itemTypes.put("cycle-count", "Weekly Cycle Count");
        itemTypes.put("fga", "Finished Good Audit");
        itemTypes.put("irr", "Internal Rejection Report");
        itemTypes.put("ee", "Employee Document");
        itemTypes.put("photo", "Photo");
        itemTypes.put("cust-supplied", "Customer Supplied");
        return itemTypes;
    }
}
