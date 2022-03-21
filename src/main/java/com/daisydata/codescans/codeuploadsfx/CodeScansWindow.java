package com.daisydata.codescans.codeuploadsfx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.*;
import java.io.IOException;
import java.io.File;
import com.daisydata.codescans.codeuploadsfx.CodingSection.CodingSet;
import com.daisydata.codescans.codeuploadsfx.GuiTools;
import org.controlsfx.control.action.Action;

//import static com.daisydata.codescans.codeuploadsfx.DatabaseConnection.conn;

public class CodeScansWindow {

    private DocumentListPanel documentList;
    private PreviewPanel previewArea;
    private Button previewLabel;
    private Button dirPath;
    private Button processButton;
    private CodingSection codeArea;
    final private String incomingWGSSpath = "//dnas1/dms/Incoming/wgss/";
    private GuiTools gui = new GuiTools;

    public CodeScansWindow(Parent parent, String filePath, DatabaseConnection conn) throws IOException {
        previewLabel = new Button("Preview the PDF below");
        previewArea = new PreviewPanel();
        HBox dirPathArea = new HBox();
        dirPath = new Button(filePath);
        Font newFont = new Font(11);
        dirPath.setFont(newFont);
        //dirPathArea.setMargin(new Insets(0, 0, 0, 0));
        //dirPathArea.setContentAreaFilled(false);
        //dirPathArea.setOpaque(false);
        //dirPathArea.setFocusPainted(false);
        dirPath.setCursor(Cursor.HAND);
        //dirPathArea.setBorderPainted(false);
        EventHandler<ActionEvent> dirButtonPressed = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                dirPath.setText(gui.folderChooser(dirPath.getText()));
            }
        };
        dirPath.setOnAction(dirButtonPressed);

        processButton = new Button("Process Uploads Now");
        processButton.setFont(newFont);
//        processButton.setMargin(new Insets(0, 0, 0, 0));
//        processButton.setBorderPainted(false);
//        processButton.setPreferredSize(new Dimension(150, 25));
        EventHandler<ActionEvent> processButtonPressed = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                processDocuments();
            }
        };
        this.directoryButtonPressed(filePath);
    }

    public static void main(String[] args) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (UnsupportedLookAndFeelException e) {
        } catch (ClassNotFoundException e) {
        } catch (InstantiationException e) {
        } catch (IllegalAccessException e) {
        }

        String scannedDocumentsFolder = System.getenv("APPDATA") + "\\scannedDocuments";

        if (!new File(scannedDocumentsFolder).exists()) {
            (new File(scannedDocumentsFolder)).mkdirs();
        }

        CodeScansWindow testWindow = new CodeScansWindow(scannedDocumentsFolder);
        testWindow.pack();
        testWindow.setVisible(true);
    }

    public void selectionButtonPressed(String fileAbsolutePath) throws IOException {
        this.previewArea.changePreview(fileAbsolutePath);
    }

    public void directoryButtonPressed(String directoryPath) throws IOException {
        this.getContentPane().removeAll();
        this.getContentPane().setBackground(Color.WHITE);
        this.dirPath.setText(directoryPath);

        double size[][] = { { 300, TableLayout.FILL }, { 20, TableLayout.FILL, 40 } };

        this.setLayout(new TableLayout(size));
        this.getContentPane().add(dirPath, "0, 0");
        this.getContentPane().add(previewLabel, "1, 0");

        documentList = new DocumentListPanel(directoryPath);
        this.getContentPane().add(documentList, "0, 1");

        previewArea = new PreviewPanel();
        this.getContentPane().add(previewArea, "1, 1");

        codeArea = new CodingSection();
        // this.getContentPane().add(codeArea, "0, 2, 1, 0");
        this.getContentPane().add(codeArea, "1, 2");

        JPanel processPanel = new JPanel();

        processPanel.setBackground(Color.WHITE);
        processPanel.add(processButton);

        this.getContentPane().add(processPanel, "0, 2");

        this.revalidate();
        this.repaint();
    }

    public void dropdownCategorySelected(CodingSet selection) {
        this.codeArea.updateTypes(selection);
    }

    public void submitButtonPressed() throws IOException {
        if (validInput()) {
            String category = this.codeArea.getCategoryValue().toUpperCase();
            String type = this.codeArea.getTypeSelected().toUpperCase();
            String identifier = this.codeArea.getIdentifier().toUpperCase();
            String currFilePath = this.previewArea.getCurrentFileDisplayed();
            File currFile = new File(currFilePath);
            String fileName = category + "_" + type + "_" + identifier + "_0.pdf";
            String filePath = incomingWGSSpath;
            if (category.equals("CPO")) {
                filePath = filePath + "Pending/";
            }
            if (type.equals("packing-slip")) {
                String append = conn.selectReceiver(identifier);
                identifier.concat("-");
                identifier.concat(append);
            }
            File newFileLocation = new File(findValidFileName(filePath, fileName));
            this.previewArea.closePreview();
            if (!newFileLocation.exists()) {
                currFile.renameTo(newFileLocation);
            } else {

            }
            String newPreviewFile = this.documentList.removeButton(currFilePath);
            if (newPreviewFile != "") {
                this.previewArea.changePreview(newPreviewFile);
            }
            this.codeArea.clearIdentifier();
            // this.directoryButtonPressed(this.dirPath.getText());
            /*
             * ProcessUploads.main(null);
             */
            conn.deconstruct();
            new Thread(() -> {
                ProcessUploads.main(null);
            }).start();
        }
    }

    public void processDocuments() {
        processButton.setText("Currently processing");
        ProcessUploads.main(null);
        processButton.setText("Process Uploads Now");
    }

    private static String findValidFileName(String folder, String fileName) {
        int numOccurence = 1;
        boolean alreadyExists = false;
        String newFullFileName = folder + fileName;

        alreadyExists = (new File(newFullFileName)).exists();

        String[] fileNameInfo = fileName.split("_");

        while (alreadyExists) {
            String baseFileName = fileNameInfo[0] + "_" + fileNameInfo[1] + "_" + fileNameInfo[2]
                    + fileNameInfo[3].substring(fileNameInfo[3].indexOf("."));
            newFullFileName = folder + new StringBuilder(baseFileName)
                    .insert(baseFileName.lastIndexOf('.'), "_" + numOccurence++).toString();
            alreadyExists = (new File(newFullFileName)).exists();
        }

        return newFullFileName;
    }

    private Boolean validInput() {
        conn = new DatabaseConnection();
        String category = this.codeArea.getCategoryValue();
        String type = this.codeArea.getTypeSelected();
        String identifier = this.codeArea.getIdentifier();
        String regex = "\\d\\d\\d\\d\\d\\d\\d";
        String[] custInfo = new String[2];
        if (category.toUpperCase().equals("WO")) {
            String woRegex = "\\d\\d\\d\\d\\d\\d-\\d\\d\\d";
            String woRegex2 = "\\d\\d\\d\\d\\d\\d";
            if (this.previewArea.getCurrentFileDisplayed().equals("")) {
                gui.staticMsg(GuiTools.StaticMsg.NO_DOCUMENT);
            } else if (identifier.matches(woRegex) || identifier.matches(woRegex2)) {
                return gui.confirmMessage(null,null,"Add this document to work order:" + identifier + "?");
            } else {
                gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
            }
        } else if (category.toUpperCase().equals("SAMPLE") || category.toUpperCase().equals("MISC")) {
            if (this.previewArea.getCurrentFileDisplayed().equals("")) {
                gui.staticMsg(GuiTools.StaticMsg.NO_DOCUMENT);
            } else {
                return gui.confirmMessage(null,null, "Add this document as a " + category + " document?");
            }
        } else if (category.toUpperCase().equals("EE")) {
            String eeRegex = "\\d?\\d?\\d\\d\\d";
            if (this.previewArea.getCurrentFileDisplayed().equals("")) {
                gui.staticMsg(GuiTools.StaticMsg.NO_DOCUMENT);
            } else if (identifier.matches(eeRegex)) {
                return gui.confirmMessage(null, null, "Add this document as a " + category + " document?");
            } else {
                gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
            }
        } else if (!category.equals("not-selected") && !type.equals("not-selected")) {
            if (category.toUpperCase().equals("CUST") || category.toUpperCase().equals("VEND")) {
                regex = "\\d\\d\\d\\d\\d\\d";
            }
            if (identifier.matches(regex)) {
                if (this.previewArea.getCurrentFileDisplayed().equals("")) {
                    gui.staticMsg(GuiTools.StaticMsg.NO_DOCUMENT);
                } else {
                    custInfo = conn.findFolderName(category, identifier);
                    if (custInfo.length != 0) {
                        String recipientLabel = "customer";
                        if (category.toUpperCase().equals("VEND")) {
                            recipientLabel = "vendor";
                        }
                        boolean confirmation = gui.confirmMessage("Confirmation", "Confirmation","Add this document to " + recipientLabel
                                        + ":" + custInfo[0] + " - " + custInfo[1] + "?");
                        if (confirmation) {
                            return true;
                        }
                    } else {
                        gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
                    }
                }
            } else if (category.equals("cpo")) {
                // TO-DO
                // Gets the RMA number, and retrieves the currently listed customer CPO number
                // Then prompt what the current customer CPO number is and ask to change it
                // If yes, prompt for the new customer CPO number, else just continue to return
                // true
                return true;
            } else if (category.equals("cycle-count") || category.equals("fga")) {
                // Weekly cycle count and finished goods audits only needs a date chosen
                // If it gets here, then there is already a date chosen
                return true;
            } else {
                gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
            }
        } else {
            gui.staticMsg(GuiTools.StaticMsg.NO_MATCH);
        }
        return false;
    }

}

