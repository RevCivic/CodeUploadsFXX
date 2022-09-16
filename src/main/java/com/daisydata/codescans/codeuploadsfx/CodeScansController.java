package com.daisydata.codescans.codeuploadsfx;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.daisydata.codescans.codeuploadsfx.CodeScansApplication.scannedDocumentsFolder;
import static com.daisydata.codescans.codeuploadsfx.CodeScansApplication.selectedFilePath;
import static java.lang.String.valueOf;

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
    public ChoiceBox category;
    @FXML
    public ChoiceBox subcategory;
    @FXML
    public TextField numberID;
    @FXML
    public Button submit;

    //Required Variables for Methods

    private GuiTools gui = new GuiTools();
    public WebEngine engine;
    public ArrayList<File> createdFiles = new ArrayList<File>();
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
    public void changeDir(MouseEvent mouseEvent) {
        documentList.getChildren().clear();
        gui.folderChooser(scannedDocumentsFolder);
        CodeScansApplication.documentList.populateList(CodeScansApplication.scannedDocumentsFolder);
        setCurDir(CodeScansApplication.scannedDocumentsFolder);
    }

//    public String getCurrentDir() {
//        return currentDirectory.getText();
//    }

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
        String url = getClass().getResource("/web/viewer.html").toExternalForm();
        engine.setUserStyleSheetLocation(getClass().getResource("/web/viewer.css").toExternalForm());
        engine.setJavaScriptEnabled(true);
        engine.load(url);
        InputStream stream = null;
        engine.getLoadWorker().stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State state, Worker.State t1) {
                System.out.println("WebEngine Loaded");
            }
        });
    }

    public void loadDoc() {
        try {
            if(selectedFilePath != null) {
                String extension = getExtensionByStringHandling(selectedFilePath).toLowerCase(Locale.ROOT);
                System.out.println(extension);
                byte[] data = null;
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
        System.out.println("Refreshing document list");
        for(Object i :  DocumentListPanel.files){
            System.out.println("Removing " + i);
        }
        documentList.getChildren().clear();
        CodeScansApplication.documentList.populateList(scannedDocumentsFolder);
    }

    public void processUploads() {
        processButton.setText("Currently processing");
        ProcessUploads.main(null);
        processButton.setText("Process Uploads Now");
    }

    public void populateCategory() {
        category.setItems(FXCollections.observableList(categories[0].keySet().stream().toList()));
        category.setValue("Select a Category");
        subcategory.setItems(FXCollections.observableList(new ArrayList<String>(Collections.singleton("Select a SubCategory"))));
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
        if (numberID.getText().length() > 0) {
            submit.setDisable(false);
        } else {
            submit.setDisable(true);
        }
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
                } else if (ext == "Unreadable") {
                    Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
                    Paragraph p = new Paragraph("The selected file is currently unreadable by CodeScans Coding this file will still work as expected, we are simply unable to show you a preview of the file. Please contact the IT Department if you would like to be able to preview this type of file in CodeScans. Happy coding!");
                    document.add(p);
                }
                document.close();
                createdFiles.add(newFile);
                return new File(newFilePath);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
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
        System.out.println("Attempting to move selected file.");
        System.out.println("Category: "+category.getValue()+"- SubCategory: "+subcategory.getValue()+"- Number: "+numberID.getText());
        if(category.getValue() != "Select a Category" && subcategory.getValue() != "Select a Sub-Category" && numberID.getText() != null) {
            File fileToMove = new File(selectedFilePath);
            System.out.println("Moving "+fileToMove.getAbsolutePath());
            String categoryID = categories[3].get(category.getValue()).toString();
            String subCategoryID = categories[3].get(subcategory.getValue()).toString();
            String number = numberID.getText().replace(".","-");
            String fileName = categoryID.toUpperCase(Locale.ROOT)+"_"+subCategoryID.toUpperCase(Locale.ROOT)+"_"+number;
            String finalFileName = fileName;
            FilenameFilter filter = (dir, name) -> name.startsWith(finalFileName);
            File[] fList = (new File("//dnas1/dms/incoming/wgss")).listFiles(filter);
            fileName += "_"+(String.valueOf(fList.length))+"."+getExtensionByStringHandling(fileToMove.getName());
            System.out.println("Renaming to "+fileName);
            fileToMove.renameTo(new File("//dnas1/dms/Incoming/wgss/" + fileName));
            gui.displayMessage(Alert.AlertType.INFORMATION, "File Moved", "Uploaded File to Queue", "File successfully uploaded to the DMS queue");
        }
        refreshPanel();
    }

    public String getExtensionByStringHandling(String filename) {
        Optional<String> oString = Optional.ofNullable(filename).filter(f -> f.contains(".")).map(f -> f.substring(filename.lastIndexOf(".") + 1));
        return oString.stream().findFirst().map(Object::toString).orElse("");
    }
}
