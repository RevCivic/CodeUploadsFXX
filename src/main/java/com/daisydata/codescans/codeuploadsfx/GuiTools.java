//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.daisydata.codescans.codeuploadsfx;

import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class GuiTools {

    public GuiTools() {
    }

    public void displayMessage(Alert.AlertType type, String title, String header, String message) {
        if(type == null){
            type = Alert.AlertType.ERROR;
        }
        if(title == null){
            title = "Error!";
        }
        if(header == null){
            header = "Verification Results";
        }
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
    public static enum StaticMsg {
        NO_DOCUMENT,
        NO_MATCH,
        YES_NO;
        private StaticMsg() {
        }
    }
    public void staticMsg(StaticMsg messageType) {
        switch (messageType) {
            case NO_DOCUMENT:
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Document");
                alert.setHeaderText("Validation");
                alert.setContentText("Please select a document to code.");
                break;
            case NO_MATCH:
                alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("No Match");
                alert.setHeaderText("Validation");
                alert.setContentText("No matches found for the entered document.");
                break;
            case YES_NO:
                alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Confirm");
                alert.setHeaderText("Confirm your choice.");
                alert.setContentText("Select 'OK' to continue, cancel or exit this window to discard choice.");
        }
    }
    public boolean confirmMessage(String title, String header, String message) {
        if(title == null){
            title = "Confirm";
        }
        if(header == null){
            header = "Confirm your choice.";
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        if(!result.isPresent() || result.get() != ButtonType.OK) {
            return false;
        } else {
            return true;
        }

    }
    public String folderChooser(String baseDir) {
        String absPath = System.getenv("APPDATA") + "\\scannedDocuments";
        File selectedDirectory;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Folder");
        if(baseDir == null){
            baseDir = System.getenv("APPDATA") + "\\scannedDocuments";
        }
        directoryChooser.setInitialDirectory(new File(baseDir));
        selectedDirectory = directoryChooser.showDialog(CodeScans.stage);
        if (selectedDirectory != null) {
            absPath = selectedDirectory.getAbsolutePath();
        } else {
            displayMessage(Alert.AlertType.ERROR, "Error!", "Failed to select file", "File chooser did ");
        }
        return absPath;
    }
//
//    public String receiverPicker(ResultSet rs) {
//        JFrame picker = this.setupFrame();
//        String selectedReceiver = "";
//
//        try {
//            ResultSetMetaData meta = rs.getMetaData();
//            int colCount = meta.getColumnCount();
//            String[] headers = new String[colCount];
//
//            for(int h = 1; h <= colCount; ++h) {
//                headers[h - 1] = meta.getColumnName(h);
//            }
//
//            DefaultTableModel tableData = this.setupTable(headers);
//
//            while(rs.next()) {
//                String[] record = new String[colCount];
//
//                for(int i = 0; i < colCount; ++i) {
//                    record[i] = rs.getString(i + 1);
//                }
//
//                tableData.addRow(record);
//            }
//        } catch (SQLException var11) {
//            var11.printStackTrace();
//        }
//
//        picker.setDefaultCloseOperation(3);
//        picker.setPreferredSize(new Dimension(400, 200));
//        picker.pack();
//        picker.setVisible(true);
//
//        while(picker.isShowing()) {
//            try {
//                TimeUnit.SECONDS.sleep(5L);
//            } catch (InterruptedException var10) {
//                var10.printStackTrace();
//            }
//        }
//
//        return selectedReceiver;
//    }
//
//    public JFrame setupFrame() {
//        JFrame frame = new JFrame();
//        frame.setTitle("Select an Option");
//        frame.setAlwaysOnTop(true);
//        JTable table = new JTable();
//        frame.add(table);
//        return frame;
//    }
//
//    public DefaultTableModel setupTable(String[] headers) {
//        DefaultTableModel model = new DefaultTableModel();
//        model.setColumnIdentifiers(headers);
//        return model;
//    }
}
