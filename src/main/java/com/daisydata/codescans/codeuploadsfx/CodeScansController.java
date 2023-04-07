package com.daisydata.codescans.codeuploadsfx;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.io.FileUtils;
import javafx.scene.control.ChoiceBox;
import java.util.ArrayList;
import java.util.Collections;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.daisydata.codescans.codeuploadsfx.CodeScansApplication.*;

public class CodeScansController implements Initializable {

    //FXML Controller Variables
    @FXML
    public String baseDirectory = CodeScansApplication.scannedDocumentsFolder;
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
    public ChoiceBox category;
    @FXML
    public ChoiceBox<Object> subcategory;
    @FXML
    public TextField numberID;
    @FXML
    public Button submit;

    public String username = System.getProperty("user.name");

    //Required Variables for Methods
    private final GuiTools gui = new GuiTools();
    public WebEngine engine;
    public ArrayList<File> createdFiles = new ArrayList<>();
    // 0 is for names, 1 is for ids, 2 is for priority, 3 is for index, 4 is for paths
    public static HashMap[] categories = new HashMap[5];

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
    public void changeDir() {
        documentList.getChildren().clear();
        gui.folderChooser(scannedDocumentsFolder);
        CodeScansApplication.documentList.populateList(CodeScansApplication.scannedDocumentsFolder);
        setCurDir(CodeScansApplication.scannedDocumentsFolder);
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCurDir(baseDirectory);
        CodeScansApplication.dbConn.getCodeCategories();
        populateCategory();
        subcategory.setDisable(true);
        numberID.setDisable(true);
        submit.setDisable(true);
        initWebEngine();
        loadDoc();
    }
    public void initWebEngine() {
        if(web.getEngine() == null) {
            engine = new WebEngine();
        } else {
            engine = web.getEngine();
        }
        String url = Objects.requireNonNull(getClass().getResource("/web/viewer.html")).toExternalForm();
        engine.setUserStyleSheetLocation(Objects.requireNonNull(getClass().getResource("/web/viewer.css")).toExternalForm());
        engine.setJavaScriptEnabled(true);
        engine.load(url);
        engine.getLoadWorker().stateProperty().addListener((observableValue, state, t1) -> System.out.println("WebEngine Loaded"));
    }

    public void loadDoc() {
        try {
            if(selectedFilePath != null) {
                String extension = getExtensionByStringHandling(selectedFilePath).toLowerCase(Locale.ROOT);
                byte[] data;
                if(extension.matches("pdf")){
                    data = FileUtils.readFileToByteArray(new File(CodeScansApplication.selectedFilePath));
                } else if (extension.matches("jpg|jpeg|png|txt|text|log")) {
                    data = FileUtils.readFileToByteArray(convertToPDF(selectedFilePath,extension));
                } else {
                    data = FileUtils.readFileToByteArray(convertToPDF(selectedFilePath,"Unreadable"));
                }
                String base64 = Base64.getEncoder().encodeToString((data));
                engine.executeScript("openFileFromBase64('"+base64+"')");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshPanel(){
//        System.out.println("Refreshing document list");
        documentList.getChildren().clear();
        CodeScansApplication.documentList.populateList(scannedDocumentsFolder);
    }

    @FXML
    public void processUploads() {
        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(() -> processButton.setText("Currently processing"));
                // Call your long-running method here
                ProcessUploads.main(null);
                Platform.runLater(() -> processButton.setText("Process Uploads Now"));
                return null;
            }
        };
        new Thread(task).start();
    }

    public void populateCategory() {
        category.setItems(FXCollections.observableList(categories[0].keySet().stream().toList()));
        category.setValue("Select a Category");
        subcategory.setItems(FXCollections.observableList(Collections.singletonList(new ArrayList<String>(Collections.singleton("Select a SubCategory")))));
        subcategory.setValue("Select a Sub-Category");
    }
    public void getCategorySelection(){
        String categorySelection = (String) category.getValue();
        if (categories[0].get(categorySelection) != null ) {
            populateSubCategory();
            subcategory.setDisable(false);
        }
    }

    public void populateSubCategory(){
        ArrayList availableSubCategories =(ArrayList) categories[0].get(category.getValue());
        subcategory.setItems(FXCollections.observableList(availableSubCategories));
    }

    public void getSubCategorySelection(){
        if (subcategory != null && subcategory.getValue() != "Select a SubCategory") {
            numberID.setDisable(false);
        }
    }
    public void numberIDPopulated() {
        submit.setDisable(numberID.getText().length() <= 0);
    }

    public void submitDoc() {

    }

    public File convertToPDF(String filepath, String ext){
        try {
            if(filepath != null){
                File file = new File(filepath);
                Document document = new Document(PageSize.A4, 20,20,20,20);
                String newFilePath = "//dnas1/dms/Incoming/tmp/"+file.getName()+".pdf";
                File newFile = new File(newFilePath);
                newFile.createNewFile();
                PdfWriter.getInstance(document, new FileOutputStream(newFilePath));
                document.open();
                if (ext.matches("jpeg|jpg|png")) {
                    Image image = Image.getInstance(filepath);
                    document.add(image);
                } else if (ext.matches("txt|text|log")) {
                    Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
                    Paragraph p = new Paragraph(Files.readString(Path.of(filepath)));
                    document.add(p);
                } else if (ext.equals("Unreadable")) {
                    Paragraph p = new Paragraph("The selected file is currently unreadable by CodeScans. Coding this file will still work as expected, we are simply unable to show you a preview of the file. Please contact the IT Department if you would like to be able to preview this type of file in CodeScans. Happy coding!");
                    document.add(p);
                }
                document.close();
                createdFiles.add(newFile);
                return new File(newFilePath);
            }
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void consumeTempFiles() {
        for (File createdFile : createdFiles) {
            createdFile.delete();
        }
    }

    public void moveFile() {
//        System.out.println("Attempting to move selected file.");
//        System.out.println("Category: "+category.getValue()+"- SubCategory: "+subcategory.getValue()+"- Number: "+numberID.getText());
        if(category.getValue() != "Select a Category" && subcategory.getValue() != "Select a Sub-Category" && numberID.getText() != null) {
            File fileToMove = new File(selectedFilePath);
//            System.out.println("Moving "+fileToMove.getAbsolutePath());
            String categoryID = categories[3].get(category.getValue()).toString();
            String subCategoryID = categories[3].get(subcategory.getValue()).toString();
            String number = numberID.getText().replace(".","-");
            String fileName = categoryID.toUpperCase(Locale.ROOT)+"_"+subCategoryID.toUpperCase(Locale.ROOT)+"_"+number;
            String finalFileName = fileName;
            FilenameFilter filter = (dir, name) -> name.startsWith(finalFileName);
            //TODO: Make Directory a variable
            File[] fList = (new File("//dnas1/dms/incoming/wgss")).listFiles(filter);
            assert fList != null;
            fileName += "_"+(fList.length)+"."+getExtensionByStringHandling(fileToMove.getName());
//            System.out.println("Renaming to "+fileName);
            //TODO: Make Directory a variable
            System.out.println(fileName);
            fileToMove.renameTo(new File("//dnas1/dms/Incoming/wgss/" + fileName));
            String[] identifiers = dbConn.findFolderName(categoryID,number);
            if(identifiers[0] != null && identifiers[1] != null) {
                gui.displayMessage(Alert.AlertType.INFORMATION, "File Moved", "Uploaded File to Queue for: " + identifiers[0] + ": " + identifiers[1], "File successfully uploaded to the DMS queue");
            } else {
                gui.displayMessage(Alert.AlertType.INFORMATION, "File Moved", "Uploaded File to Queue","File successfully uploaded to the DMS queue");
            }
        }
        refreshPanel();
    }

    public String getExtensionByStringHandling(String filename) {
        Optional<String> oString = Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1));
        return oString.stream().findFirst().map(Object::toString).orElse("");
    }

    private void refreshPDFViewer() {
        initWebEngine();
        loadDoc();
    }

    @FXML
    private void submitMethods() {
        refreshPDFViewer();
        moveFile();
    }
}