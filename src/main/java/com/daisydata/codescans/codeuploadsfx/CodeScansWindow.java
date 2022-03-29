package com.daisydata.codescans.codeuploadsfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

import java.io.IOException;

import static com.daisydata.codescans.codeuploadsfx.CodeScansApplication.controller;
import static com.daisydata.codescans.codeuploadsfx.CodeScansApplication.scannedDocumentsFolder;


//import static com.daisydata.codescans.codeuploadsfx.DatabaseConnection.conn;

public class CodeScansWindow extends BorderPane {

    public static DocumentListPanel documentList;
    private VBox documentListArea;
    private Button previewLabel;
    private Button dirPath;
    private BorderPane dirArea;
    private Button processButton;
    private CodingSection codeArea;
    final private String incomingWGSSpath = "//dnas1/dms/Incoming/wgss/";
    private GuiTools gui = new GuiTools();
    private Font newFont = new Font(11);

    public CodeScansWindow(String filePath) throws IOException {
        initDirectoryPane();
    }

    public void initDirectoryPane(){
        documentList = new DocumentListPanel(scannedDocumentsFolder);
    }
//
//    public void dropdownCategorySelected(CodingSet selection) {
//        this.codeArea.updateTypes(selection);
//    }
//
//    public void submitButtonPressed() throws IOException {
//        if (validInput()) {
//            String category = this.codeArea.getCategoryValue().toUpperCase();
//            String type = this.codeArea.getTypeSelected().toUpperCase();
//            String identifier = this.codeArea.getIdentifier().toUpperCase();
//            String currFilePath = this.previewArea.getCurrentFileDisplayed();
//            File currFile = new File(currFilePath);
//            String fileName = category + "_" + type + "_" + identifier + "_0.pdf";
//            String filePath = incomingWGSSpath;
//            if (category.equals("CPO")) {
//                filePath = filePath + "Pending/";
//            }
//            if (type.equals("packing-slip")) {
//                String append = conn.selectReceiver(identifier);
//                identifier.concat("-");
//                identifier.concat(append);
//            }
//            File newFileLocation = new File(findValidFileName(filePath, fileName));
//            this.previewArea.closePreview();
//            if (!newFileLocation.exists()) {
//                currFile.renameTo(newFileLocation);
//            } else {
//
//            }
//            String newPreviewFile = this.documentList.removeButton(currFilePath);
//            if (newPreviewFile != "") {
//                this.previewArea.changePreview(newPreviewFile);
//            }
//            this.codeArea.clearIdentifier();
//            // this.directoryButtonPressed(this.dirPath.getText());
//            /*
//             * ProcessUploads.main(null);
//             */
//            conn.deconstruct();
//            new Thread(() -> {
//                ProcessUploads.main(null);
//            }).start();
//        }
//    }

    public void processDocuments() {
        processButton.setText("Currently processing");
        ProcessUploads.main(null);
        processButton.setText("Process Uploads Now");
    }

//    private static String findValidFileName(String folder, String fileName) {
//        int numOccurence = 1;
//        boolean alreadyExists = false;
//        String newFullFileName = folder + fileName;
//
//        alreadyExists = (new File(newFullFileName)).exists();
//
//        String[] fileNameInfo = fileName.split("_");
//
//        while (alreadyExists) {
//            String baseFileName = fileNameInfo[0] + "_" + fileNameInfo[1] + "_" + fileNameInfo[2]
//                    + fileNameInfo[3].substring(fileNameInfo[3].indexOf("."));
//            newFullFileName = folder + new StringBuilder(baseFileName)
//                    .insert(baseFileName.lastIndexOf('.'), "_" + numOccurence++).toString();
//            alreadyExists = (new File(newFullFileName)).exists();
//        }
//
//        return newFullFileName;
//    }
//
//    private Boolean validInput() {
//        conn = new DatabaseConnection();
//        String category = this.codeArea.getCategoryValue();
//        String type = this.codeArea.getTypeSelected();
//        String identifier = this.codeArea.getIdentifier();
//        String regex = "\\d\\d\\d\\d\\d\\d\\d";
//        String[] custInfo = new String[2];
//        if (category.toUpperCase().equals("WO")) {
//            String woRegex = "\\d\\d\\d\\d\\d\\d-\\d\\d\\d";
//            String woRegex2 = "\\d\\d\\d\\d\\d\\d";
//            if (this.previewArea.getCurrentFileDisplayed().equals("")) {
//                gui.staticMsg(GuiTools.StaticMsg.NO_DOCUMENT);
//            } else if (identifier.matches(woRegex) || identifier.matches(woRegex2)) {
//                return gui.confirmMessage(null,null,"Add this document to work order:" + identifier + "?");
//            } else {
//                gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
//            }
//        } else if (category.toUpperCase().equals("SAMPLE") || category.toUpperCase().equals("MISC")) {
//            if (this.previewArea.getCurrentFileDisplayed().equals("")) {
//                gui.staticMsg(GuiTools.StaticMsg.NO_DOCUMENT);
//            } else {
//                return gui.confirmMessage(null,null, "Add this document as a " + category + " document?");
//            }
//        } else if (category.toUpperCase().equals("EE")) {
//            String eeRegex = "\\d?\\d?\\d\\d\\d";
//            if (this.previewArea.getCurrentFileDisplayed().equals("")) {
//                gui.staticMsg(GuiTools.StaticMsg.NO_DOCUMENT);
//            } else if (identifier.matches(eeRegex)) {
//                return gui.confirmMessage(null, null, "Add this document as a " + category + " document?");
//            } else {
//                gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
//            }
//        } else if (!category.equals("not-selected") && !type.equals("not-selected")) {
//            if (category.toUpperCase().equals("CUST") || category.toUpperCase().equals("VEND")) {
//                regex = "\\d\\d\\d\\d\\d\\d";
//            }
//            if (identifier.matches(regex)) {
//                if (this.previewArea.getCurrentFileDisplayed().equals("")) {
//                    gui.staticMsg(GuiTools.StaticMsg.NO_DOCUMENT);
//                } else {
//                    custInfo = conn.findFolderName(category, identifier);
//                    if (custInfo.length != 0) {
//                        String recipientLabel = "customer";
//                        if (category.toUpperCase().equals("VEND")) {
//                            recipientLabel = "vendor";
//                        }
//                        boolean confirmation = gui.confirmMessage("Confirmation", "Confirmation","Add this document to " + recipientLabel
//                                        + ":" + custInfo[0] + " - " + custInfo[1] + "?");
//                        if (confirmation) {
//                            return true;
//                        }
//                    } else {
//                        gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
//                    }
//                }
//            } else if (category.equals("cpo")) {
//                // TO-DO
//                // Gets the RMA number, and retrieves the currently listed customer CPO number
//                // Then prompt what the current customer CPO number is and ask to change it
//                // If yes, prompt for the new customer CPO number, else just continue to return
//                // true
//                return true;
//            } else if (category.equals("cycle-count") || category.equals("fga")) {
//                // Weekly cycle count and finished goods audits only needs a date chosen
//                // If it gets here, then there is already a date chosen
//                return true;
//            } else {
//                gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
//            }
//        } else {
//            gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
//        }
//        return false;
//    }

}

