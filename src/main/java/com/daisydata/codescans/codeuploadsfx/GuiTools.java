//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.daisydata.codescans.codeuploadsfx;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

import static javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE;
import static javafx.scene.control.ButtonBar.ButtonData.OK_DONE;


public class GuiTools {

    private Stage mainStage = CodeScansApplication.stage;

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
        File selectedDirectory;
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select a Folder");
        if(baseDir == null){
            baseDir = CodeScansApplication.scannedDocumentsFolder;
        }
        directoryChooser.setInitialDirectory(new File(baseDir));
        selectedDirectory = directoryChooser.showDialog(mainStage);
        if (selectedDirectory != null) {
            CodeScansApplication.scannedDocumentsFolder = selectedDirectory.getAbsolutePath();
        } else {
            displayMessage(Alert.AlertType.ERROR, "Error!", "Failed to select file", "File chooser did not receive a file - no changes made.");
        }
        return CodeScansApplication.scannedDocumentsFolder;
    }

    public void updateAvailableAlert(Alert.AlertType type, String title, String header, String message, String latestVersion) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        ButtonType updateBtn = new ButtonType("Update", OK_DONE);
        ButtonType cancelBtn = new ButtonType("Cancel", CANCEL_CLOSE);
        alert.getDialogPane().lookupButton(ButtonType.OK).setVisible(false);
        alert.getDialogPane().getButtonTypes().add(cancelBtn);
        alert.getDialogPane().getButtonTypes().add(updateBtn);

        alert.setResultConverter(alertBoxButton -> {
            if (alertBoxButton == updateBtn) {
                CodeScansApplication.copyUpdatedFile("//dnas1/Share/Departments/IT/CodeScans2.0/Version/" + latestVersion + "/CodeScans2 " + latestVersion + ".exe");
            }
            return alertBoxButton;
        });
        alert.showAndWait();
    }

    private static void console(String msg) {
        System.out.println(msg);
    }
}

