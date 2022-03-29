package com.daisydata.codescans.codeuploadsfx;

import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Base64;
import java.util.ResourceBundle;

import static com.daisydata.codescans.codeuploadsfx.CodeScansApplication.scannedDocumentsFolder;
import static com.daisydata.codescans.codeuploadsfx.CodeScansWindow.documentList;

public class CodeScansController implements Initializable {

    //FXML Controller Variables

    @FXML
    public String baseDirectory = new String(CodeScansApplication.scannedDocumentsFolder);
    @FXML
    public VBox fileList;
    @FXML
    public BorderPane pdfViewer;
    @FXML
    public StackPane stackPane;
    @FXML
    public BorderPane codeScansWindow;
    @FXML
    public BorderPane dirArea;
    @FXML
    public Label pdfLabel;
    @FXML
    public Button dirAreaButton;
    @FXML
    public Label currentDirectory;
    @FXML
    public VBox documentList;
    @FXML
    public WebView web;
    @FXML
    public Button refreshButton;
    @FXML
    public Button processButton;
    @FXML
    public HBox codeArea;
    @FXML
    public ChoiceBox<String> categoryDropdown = new ChoiceBox<>();
    @FXML
    public ChoiceBox<String> subcategoryDropdown = new ChoiceBox<>();
    @FXML
    public TextField numberID;
    //Required Variables for Methods

    private GuiTools gui = new GuiTools();
    public static ArrayList categoryIDs;
    public static ArrayList categoryNames;
    public static ArrayList subCategoryIDs;
    public static ArrayList subCategoryNames;

    //Controller Methods

    public void addDocButton(Button button) {
        documentList.getChildren().add(button);
    }
    public void removeDocButton(int index) {
        documentList.getChildren().remove(index);
    }
    public void removeAllDocButtons() {
        documentList.getChildren().removeAll();
    }
    public void setCurDir(String string) {
        String[] stringArr = string.split("\\\\");
        String trimmedString = stringArr[0]+"\\"+"...\\...\\"+stringArr[stringArr.length-1];
        currentDirectory.setText(trimmedString);
    }
    public void changeDir(MouseEvent mouseEvent) {
        documentList.getChildren().clear();
        gui.folderChooser(scannedDocumentsFolder);
        CodeScansWindow.documentList.populateList(CodeScansApplication.scannedDocumentsFolder);
        setCurDir(CodeScansApplication.scannedDocumentsFolder);
    }

//    public String getCurrentDir() {
//        return currentDirectory.getText();
//    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCurDir(baseDirectory);
        populateCategory(categoryDropdown);
        populateSubCategory(subcategoryDropdown);
        loadDoc();
    }
    public void loadDoc() {
        WebEngine engine;
        if (web.getEngine() == null) {
            engine = new WebEngine();
            System.out.println("Created new WebEngine");
        } else {
            engine = web.getEngine();
            System.out.println("Using existing WebEngine");
        }
        String url = getClass().getResource("/web/viewer.html").toExternalForm();
        engine.setUserStyleSheetLocation(getClass().getResource("/web/viewer.css").toExternalForm());
        engine.setJavaScriptEnabled(true);
        engine.load(url);
        InputStream stream = null;

        engine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == Worker.State.SUCCEEDED) {
                try {

                    byte[] data = FileUtils.readFileToByteArray(new File(CodeScansApplication.selectedFilePath));
                    String base64 = Base64.getEncoder().encodeToString((data));
                    engine.executeScript("openFileFromBase64('"+base64+"')");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
    public void refreshPanel(){
        refreshButton.setOnMouseClicked(e -> {
            System.out.println("Refreshing document list");
            for(Object i :  DocumentListPanel.files){
                System.out.println("Removing " + i);
            }
            documentList.getChildren().clear();
            CodeScansWindow.documentList.populateList(scannedDocumentsFolder);
        });
    }

    public void processUploads() {
        processButton.setText("Currently processing");
        ProcessUploads.main(null);
        processButton.setText("Process Uploads Now");
    }

    public void populateCategory(ChoiceBox<String> categoryDropdown) {
        categoryDropdown.getItems().addAll("Select a Category");
        categoryDropdown.setValue("Select a Category");
    }
    public void getCategorySelection(ChoiceBox<String> categoryDropdown){
        String categorySelection = categoryDropdown.getValue();
    }

    public ChoiceBox populateSubCategory(ChoiceBox<String> subcategoryDropdown){
        subcategoryDropdown.getItems().addAll("Select a Category First");
        subcategoryDropdown.setValue("Select a Category First");
        return subcategoryDropdown;
    }
    public void getSubCategorySelection(ChoiceBox<String> subcategoryDropdown){
        String subcategoryOption = subcategoryDropdown.getValue();
        subcategoryDropdown.getSelectionModel().selectFirst();
    }
}
