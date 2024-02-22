package com.daisydata.codescans.codeuploadsfx;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.daisydata.codescans.codeuploadsfx.CodeScansApplication.*;
import static com.daisydata.codescans.codeuploadsfx.ProcessUploads.findValidFileName;

public class CodeScansController implements Initializable {

    //FXML Controller Variables
    @FXML
    public String baseDirectory = CodeScansApplication.scannedDocumentsFolder;
    @FXML
    public BorderPane pdfViewer;
    @FXML
    public Label currentDirectory;
    @FXML
    public VBox documentList;
    @FXML
    public WebView web;
    @FXML
    public Button processButton;
    @FXML
    public ChoiceBox category;
    @FXML
    public ChoiceBox<Object> subcategory;
    @FXML
    public TextField numberID;
    @FXML
    public Button submit;

    public String username = System.getProperty("user.name");
    public String cpoFolder = "//dnas1/dms/Incoming/wgss/Pending";
    public String incomingFolder = "//dnas1/dms/Incoming/wgss";

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
    // initiates the buttons and disables them by default, sets the current directory, and populates the dropdowns
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
    // starts the webengine to be able to view pdfs
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
    // function to load the document that was selected, based on the type of document
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
    // refresh button function that refreshes the sidebar document list
    public void refreshPanel(){
//        System.out.println("Refreshing document list");
        documentList.getChildren().clear();
        CodeScansApplication.documentList.populateList(scannedDocumentsFolder);
    }

    // changes the button text and starts processing the documents
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

    // populates the category dropdown
    public void populateCategory() {
        category.setItems(FXCollections.observableList(categories[0].keySet().stream().toList()));
        category.setValue("Select a Category");
        subcategory.setItems(FXCollections.observableList(Collections.singletonList(new ArrayList<>(Collections.singleton("Select a SubCategory")))));
        subcategory.setValue("Select a Subcategory");

        // listen for key presses on the ChoiceBox
        changeType(category);
    }

    private void changeType(ChoiceBox category) {
        category.setOnKeyPressed(event -> {
            String letter = event.getText().toLowerCase();
            Object selectedItem = category.getSelectionModel().getSelectedItem();
            boolean found = false;

            // find the index of the selected item
            int selectedIndex = category.getItems().indexOf(selectedItem);

            // start looking for the next item from the index after the selected item
            for (int i = selectedIndex + 1; i < category.getItems().size(); i++) {
                String item = ((String) category.getItems().get(i)).toLowerCase();
                if (item.startsWith(letter)) {
                    category.getSelectionModel().select(i);
                    found = true;
                    break;
                }
            }

            // if no item was found after the selected item, start looking from the beginning
            if (!found) {
                for (int i = 0; i < selectedIndex; i++) {
                    String item = ((String) category.getItems().get(i)).toLowerCase();
                    if (item.startsWith(letter)) {
                        category.getSelectionModel().select(i);
                        break;
                    }
                }
            }
        });
    }

    public void getCategorySelection() {
        String categorySelection = (String) category.getValue();
        if (categories[0].get(categorySelection) != null) {
            populateSubCategory();
            subcategory.setDisable(false);
        }
        System.out.println("categorySelection: " + categorySelection);
        subcategory.setValue("Select a Subcategory");
    }

    // populates the subcategory dropdown
    public void populateSubCategory(){
        ArrayList availableSubCategories = (ArrayList) categories[0].get(category.getValue());
        subcategory.setItems(FXCollections.observableList(availableSubCategories));

        // listen for key presses on the subcategory ChoiceBox
        changeType(subcategory);
        if (subcategory.getSelectionModel().getSelectedItem() == ("Halliburton CPO") || subcategory.getSelectionModel().getSelectedItem() == ("Lockheed CPO") || subcategory.getSelectionModel().getSelectedItem() == ("Unassigned CPO")) {
            submit.setDisable(false);
        }
    }
    // gets the subcategory selection that he user selects and prints it into the console for debugging
    public void getSubCategorySelection(){
        String categorySelection = (String) category.getValue();
        if (categorySelection.equalsIgnoreCase("Customer Purchase Order") && subcategory.getValue() != "Select a Subcategory" && subcategory.getValue() != null) {
            submit.setDisable(false);
        } else {
            numberIDPopulated();
        }
        if (subcategory != null && subcategory.getValue() != "Select a Subcategory") {
            numberID.setDisable(false);
        }
        if (subcategory.getValue() == "Select a Subcategory") {
            numberID.setDisable(true);
        }
        System.out.println("Subcategory Selection: " + subcategory.getValue());
    }
    public void numberIDPopulated() {
        submit.setDisable(numberID.getText().length() <= 0);
    }

    // Tries to convert the active file to a viewable pdf and shows an error on the screen if it cannot.
    public File convertToPDF(String filepath, String ext){
        try {
            if (filepath != null) {
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

    // deletes the created temp files
    public void consumeTempFiles() {
        for (File createdFile : createdFiles) {
            createdFile.delete();
        }
    }

    //
    public void moveFile() {
        if (category.getValue() != "Select a Category" && subcategory.getValue() != "Select a Subcategory" && numberID.getText() != null) {
            String categoryID = "";
            String subCategoryID = "";
            String fileName = "";
            File fileToMove = new File(selectedFilePath);
            System.out.println("categries[3]: " + categories[3]);
            categoryID = categories[3].get(category.getValue()).toString();
            if (categoryID.equalsIgnoreCase("vendinfo") || categoryID.equalsIgnoreCase("info")) {
                categoryID = "vend";
            }

            logger.info("categoryID: " + categoryID);
            subCategoryID = categories[3].get(subcategory.getValue()).toString();
            String number = numberID.getText().replace(".","-");
            System.out.println("CAT_ID: " + categoryID);
            System.out.println("SUB_ID: " + subCategoryID);
            System.out.println("Number: " + number);
            boolean isWorkOrder = (number.length() == 9 || number.length() == 6 || number.indexOf("-") == 6) &&
                                    (categoryID.equalsIgnoreCase("wo") || categoryID.equalsIgnoreCase("so") ||
                                        categoryID.equalsIgnoreCase("rma")) && number.charAt(0) != '3' && number.charAt(0) != '8';
            if (isWorkOrder && !categoryID.equalsIgnoreCase("wo")) {
                String[] idents;
                idents  =  dbConn.findFolderName(categoryID, number, isWorkOrder);
                if (number.length() >= 9 || number.indexOf("-") == 6) {
                    if (number.length() == 9 || (number.length() == 10 && number.indexOf("-") == 6)) {
                        System.out.println("IS WORKORDER " + categoryID.toUpperCase(Locale.ROOT) + "_" + subCategoryID.toUpperCase(Locale.ROOT) + "_" + idents[2] + "_" + idents[0] + "-" + number.substring(number.length() - 3));
                        fileName = categoryID.toUpperCase(Locale.ROOT) + "_" + subCategoryID.toUpperCase(Locale.ROOT) + "_" + idents[2] + "_" + idents[0] + "-" + number.substring(number.length() - 3);
                    } else if (number.indexOf("-") == 6 ) {
                        fileName = categoryID.toUpperCase(Locale.ROOT) + "_" + subCategoryID.toUpperCase(Locale.ROOT) + "_" + idents[2] + "_" + idents[0];
                    }
                } else if (number.length() == 6 && idents[1].equalsIgnoreCase("")) {
                    System.out.println("IS NOT correct WORKORDER with no suffix" + categoryID.toUpperCase(Locale.ROOT) + "_" + subCategoryID.toUpperCase(Locale.ROOT) + "_" + idents[2] + "_" + idents[0]);
                    fileName = categoryID.toUpperCase(Locale.ROOT) + "_" + subCategoryID.toUpperCase(Locale.ROOT) + "_" + idents[2] + "_" + idents[0];
                }
            } else {
                fileName = categoryID.toUpperCase(Locale.ROOT) + "_" + subCategoryID.toUpperCase(Locale.ROOT) + "_" + number;
            }
            System.out.println("fileName: " + fileName);
            String[] identifiers;
            String finalFileName = fileName;
            FilenameFilter filter = (dir, name) -> name.startsWith(finalFileName);
            File[] fList = (new File("//dnas1/dms/incoming/wgss")).listFiles(filter);
            assert fList != null;
            fileName += "_"+(fList.length)+"."+getExtensionByStringHandling(fileToMove.getName());
            String directoryPath;
            if (category.getValue().equals("Customer Purchase Order")) {
                directoryPath = cpoFolder;
            } else {
                directoryPath = incomingFolder;
            }
            File newFile = new File(directoryPath + "/" + fileName);
            String newFullFileName = findValidFileName(directoryPath, fileName);
            fileToMove.renameTo(new File(newFullFileName));
            if (!category.getValue().equals("Customer Purchase Order")){
                identifiers = dbConn.findFolderName(categoryID, number, isWorkOrder);
                logger.info("identifiers: " + identifiers[0] + ", " + identifiers[1]);
                if (identifiers[0] != null) {
                    if (((!identifiers[0].equals("") && !identifiers[1].equals("")) || (!identifiers[2].equals("") && !identifiers[3].equals("")))) {
                        if (isWorkOrder) {
                            gui.displayMessage(Alert.AlertType.INFORMATION, "File Moved", "Uploaded file to Queue for: " + identifiers[3] + ": " + identifiers[4], "File successfully uploaded to the DMS queue");
                        } else {
                            gui.displayMessage(Alert.AlertType.INFORMATION, "File Moved", "Uploaded File to Queue for: " + identifiers[0] + ": " + identifiers[1], "File successfully uploaded to the DMS queue");
                        }
                    }
                } else {
                    gui.displayMessage(Alert.AlertType.INFORMATION, "File Moved", "Uploaded File to Queue","File successfully uploaded to the DMS queue");
                }
                identifiers = null;
            } else {
                gui.displayMessage(Alert.AlertType.INFORMATION, "File Moved", "Moved File to CPO Folder", "File successfully moved to the CPO folder");
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