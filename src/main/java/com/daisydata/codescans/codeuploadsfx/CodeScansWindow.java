package com.daisydata.codescans.codeuploadsfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.application.*;
import javafx.scene.*;

import java.io.IOException;

import java.io.File;

import codeUploads.CodingSection.CodingSet;
import layout.TableLayout;

public class CodeScansWindow extends Application {

    /**
     * Uselessness
     */
    private static final long serialVersionUID = 7882768587982505656L;

    private DocumentListPanel documentList;
    private PreviewPanel previewArea;
    private Button previewLabel;
    private Button dirPath;
    private Button processButton;
    private CodingSection codeArea;
    private databaseConnection conn;
    final private String incomingWGSSpath = "//dnas1/dms/Incoming/wgss/";

    public CodeScansWindow(String filePath) throws IOException {
        conn = new databaseConnection();
        this.setTitle("Code Scanned Documents");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        previewLabel = new JButton("Preview the PDF below");
        previewArea = new PreviewPanel();
        dirPath = new JButton(filePath);
        Font newFont = new Font(filePath, dirPath.getFont().getStyle(), 11);
        dirPath.setFont(newFont);
        dirPath.setMargin(new Insets(0, 0, 0, 0));
        dirPath.setContentAreaFilled(false);
        dirPath.setOpaque(false);
        dirPath.setFocusPainted(false);
        dirPath.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dirPath.setBorderPainted(false);
        dirPath.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Component component = (Component) e.getSource();
                CodeScansWindow frame = (CodeScansWindow) SwingUtilities.getRoot(component);
                File chosenDir = new folderChooser().start();
                if (chosenDir != null) {
                    String targetDirectory = new String(chosenDir.getAbsolutePath());
                    dirPath.setText(targetDirectory);
                    try {
                        frame.directoryButtonPressed(targetDirectory);
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                } else {
                    try {
                        frame.directoryButtonPressed(dirPath.getText());
                    } catch (IOException e1) {
                        // TODO Auto-generated catch block
                        e1.printStackTrace();
                    }
                }
            }
        });

        processButton = new JButton("Process Uploads Now");
        processButton.setFont(newFont);
        processButton.setMargin(new Insets(0, 0, 0, 0));
        processButton.setBorderPainted(false);
        processButton.setPreferredSize(new Dimension(150, 25));

        processButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Component component = (Component) e.getSource();
                CodeScansWindow frame = (CodeScansWindow) SwingUtilities.getRoot(component);
                frame.processDocuments();
            }
        });

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
        conn.deconstruct();
        processButton.setText("Currently processing");
        this.repaint();
        this.revalidate();
        ProcessUploads.main(null);
        processButton.setText("Process Uploads Now");
        this.repaint();
        this.revalidate();
        conn = new databaseConnection();
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
        conn = new databaseConnection();
        String category = this.codeArea.getCategoryValue();
        String type = this.codeArea.getTypeSelected();
        String identifier = this.codeArea.getIdentifier();
        String regex = "\\d\\d\\d\\d\\d\\d\\d";
        String[] custInfo = new String[2];
        if (category.toUpperCase().equals("WO")) {
            String woRegex = "\\d\\d\\d\\d\\d\\d-\\d\\d\\d";
            String woRegex2 = "\\d\\d\\d\\d\\d\\d";
            if (this.previewArea.getCurrentFileDisplayed().equals("")) {
                JOptionPane.showMessageDialog(null, "Please select a document to code", "Validation Results",
                        JOptionPane.CLOSED_OPTION);
            } else if (identifier.matches(woRegex) || identifier.matches(woRegex2)) {
                int confirmation = JOptionPane.showConfirmDialog(null,
                        "Add this document to work order:" + identifier + "?", "Confirmation",
                        JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    return true;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid work order number. Please verify and try again.",
                        "Validation Results", JOptionPane.CLOSED_OPTION);
            }
        } else if (category.toUpperCase().equals("SAMPLE") || category.toUpperCase().equals("MISC")) {
            if (this.previewArea.getCurrentFileDisplayed().equals("")) {
                JOptionPane.showMessageDialog(null, "Please select a document to code", "Validation Results",
                        JOptionPane.CLOSED_OPTION);
            } else {
                int confirmation = JOptionPane.showConfirmDialog(null,
                        "Add this document as a " + category + " document?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    return true;
                }
            }
        } else if (category.toUpperCase().equals("EE")) {
            String eeRegex = "\\d?\\d?\\d\\d\\d";
            if (this.previewArea.getCurrentFileDisplayed().equals("")) {
                JOptionPane.showMessageDialog(null, "Please select a document to code", "Validation Results",
                        JOptionPane.CLOSED_OPTION);
            } else if (identifier.matches(eeRegex)) {
                int confirmation = JOptionPane.showConfirmDialog(null,
                        "Add this document as a " + category + " document?", "Confirmation", JOptionPane.YES_NO_OPTION);
                if (confirmation == JOptionPane.YES_OPTION) {
                    return true;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Invalid employee number. Please verify and try again.",
                        "Validation Results", JOptionPane.CLOSED_OPTION);
            }
        } else if (!category.equals("not-selected") && !type.equals("not-selected")) {
            if (category.toUpperCase().equals("CUST") || category.toUpperCase().equals("VEND")) {
                regex = "\\d\\d\\d\\d\\d\\d";
            }
            if (identifier.matches(regex)) {
                if (this.previewArea.getCurrentFileDisplayed().equals("")) {
                    JOptionPane.showMessageDialog(null, "Please select a document to code", "Validation Results",
                            JOptionPane.CLOSED_OPTION);
                } else {
                    custInfo = conn.findFolderName(category, identifier);
                    if (custInfo.length != 0) {
                        String recipientLabel = "customer";
                        if (category.toUpperCase().equals("VEND")) {
                            recipientLabel = "vendor";
                        }
                        int confirmation = JOptionPane.showConfirmDialog(null, "Add this document to " + recipientLabel
                                        + ":" + custInfo[0] + " - " + custInfo[1] + "?", "Confirmation",
                                JOptionPane.YES_NO_OPTION);
                        if (confirmation == JOptionPane.YES_OPTION) {
                            return true;
                        }
                    } else {
                        JOptionPane.showMessageDialog(null,
                                "There is no order with the information inputted. Please verify and try again.",
                                "Validation Results", JOptionPane.CLOSED_OPTION);
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
                JOptionPane.showMessageDialog(null, "This is an invalid number.", "Validation Results",
                        JOptionPane.CLOSED_OPTION);
            }
        } else {
            JOptionPane.showMessageDialog(null, "Please select a category and type", "Validation Results",
                    JOptionPane.CLOSED_OPTION);
        }
        return false;
    }

    @Override
    public void start(Stage stage) throws Exception {

    }
}

