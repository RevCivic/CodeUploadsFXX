
package com.daisydata.codescans.codeuploadsfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.ini4j.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
import java.util.Scanner;



public class CodeScansApplication extends Application {
    private static final String APP_NAME = "CodeScans";
    private static final String APP_TITLE = "Code Scanned Documents";
    private static final String VERSION_PATH = "//dnas1/Share/Departments/IT/CodeScans2.0/Version/Version.txt";
    public static String CURRENT_VERSION = "v0.9.65";
    static Boolean LOGGING = false;
    public static String scannedDocumentsFolder = System.getenv("APPDATA") + "\\scannedDocuments";
    public static String iniFile = System.getenv("APPDATA") + "\\codeScans.ini";
    public static Pane root;
    public static Stage stage;
    public static Scene scene;
    public static CodeScansController controller;
    public static DatabaseConnection dbConn = new DatabaseConnection();
    public static FXMLLoader fxmlLoader;
    public static int selectedFile;
    public static String selectedFilePath;
    public static DocumentListPanel documentList;
    private final GuiTools gui = new GuiTools();
    public static final Logger logger = LogManager.getLogger(CodeScansApplication.class);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {

        preFlightCheck();
        stage = initiateStage();
        try {
            Wini ini = new Wini(new File(iniFile));
            String theme = ini.get("appearance", "theme");
            if (theme.equals("Light")) {
                scene.getStylesheets().add("Stylesheet_LightTheme.css");

            } else {
                scene.getStylesheets().add("Stylesheet_DarkTheme.css");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        stage.show();
    }

    public static void stop(int exitStatus) {
        controller.consumeTempFiles();
        Platform.exit();
        System.exit(exitStatus);
    }

    public Stage initiateStage() throws IOException {
        stage = new Stage();
        stage.getIcons().add(new Image("/codescans.png"));
        stage.setTitle(APP_NAME + " " + CURRENT_VERSION);
        stage.setOnCloseRequest(new EventHandler<>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                stop(0);
            }
        });
        stage.initModality(Modality.WINDOW_MODAL);
        scene = initiateScene();
        stage.setScene(scene);
        stage.setMaximized(false);
        return stage;
    }

    public static Scene initiateScene() throws IOException {
        fxmlLoader = new FXMLLoader(CodeScansApplication.class.getResource("/CodeScans-view.fxml"));
        controller = new CodeScansController();
        fxmlLoader.setController(controller);
        try {
            root = fxmlLoader.load();
            scene = new Scene(root);
            documentList = new DocumentListPanel(scannedDocumentsFolder);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Failed to initiate scene");
            stop(3);
        }
        scene.setOnKeyPressed(new EventHandler<>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.isControlDown() && (event.getCode() == KeyCode.SHIFT)) {
                    try {
                        changeTheme();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return scene;
    }

    public void preFlightCheck() {
        if (!new File(scannedDocumentsFolder).exists()) {
            (new File(scannedDocumentsFolder)).mkdirs();
        }
        if (!new File(iniFile).exists()) {
            try {
                new File(iniFile).createNewFile();
                Wini ini = new Wini(new File(iniFile));
                ini.put("appearance", "theme", "Dark");
                ini.store();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        logger.info("Starting Log for user: " + System.getProperty("user.name"));
        checkForUpdates();
        deleteOldCS();
    }

    public static void changeTheme() throws IOException {
        Wini ini = new Wini(new File(iniFile));
        if (scene.getStylesheets().contains("Stylesheet_LightTheme.css")) {
            scene.getStylesheets().remove("Stylesheet_LightTheme.css");
            scene.getStylesheets().add("Stylesheet_DarkTheme.css");
            ini.put("appearance", "theme", "Dark");
        } else {
            scene.getStylesheets().remove("Stylesheet_DarkTheme.css");
            scene.getStylesheets().add("Stylesheet_LightTheme.css");
            ini.put("appearance", "theme", "Light");
        }
        ini.store();
    }

    //Check for updates by looking for a text file in the Dnas1/Share/Departments/IT/CodeScans2.0/Version/ folder.
    // If it matches the current version, it'll open CodeScans normally. If it doesn't match, it'll prompt to update.
    public void checkForUpdates() {
        boolean updatesAvailable = false;
        String versionTxtPath = VERSION_PATH;

        try {
            File file = new File(versionTxtPath);
            Scanner scanner = new Scanner(file);
            String latestVersion = scanner.next();
            scanner.close();
            //Gets Version.txt info and compares it to the CURRENT_VERSION and prompts for update if needed.
            if (!Objects.equals(latestVersion, CURRENT_VERSION)) {
                updatesAvailable = true;
                gui.updateAvailableAlert(Alert.AlertType.INFORMATION, "Update Available", "Current Version: " + CURRENT_VERSION + "\nLatest Version: " + latestVersion, "There is an update available. Please update before using CodeScans", latestVersion);
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    //Copies the updated version of CodeScans from DNAS1 to the user's desktop. Replaces file if it already exists
    public static void copyUpdatedFile(String sourceFilePath) {
        Path sourcePath = Paths.get(sourceFilePath);
        Path destinationPath = Paths.get(System.getProperty("user.home"), "Desktop", sourcePath.getFileName().toString());

        try {
            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);
            Alert updatedAlert = new Alert(Alert.AlertType.INFORMATION);
            updatedAlert.setTitle("Update Successful!");
            updatedAlert.setHeaderText("Relaunch CodeScans to use the latest version.");
            //Makes the OK button close out of CodeScans so the newest version can be opened by the user
            Button okBtn = (Button) updatedAlert.getDialogPane().lookupButton(ButtonType.OK);
            okBtn.addEventFilter(ActionEvent.ACTION, event -> Platform.exit());
            updatedAlert.showAndWait();
            System.exit(0);

        } catch (IOException e) {
            //If the update fails for some reason, this should give an error. Only gotten source/destination path errors so far.
            Alert updatedAlert = new Alert(Alert.AlertType.ERROR);
            updatedAlert.setTitle("Update Failed!");
            updatedAlert.setHeaderText("Update Failed!");
            updatedAlert.setContentText("Update Failed. Error: " + e.getMessage());
            logger.error("Update Failed: " + e.getMessage());
            updatedAlert.showAndWait();
        }

    }
    public static void deleteOldCS() {
        String currentVersion = CURRENT_VERSION;
        File desktopDir = new File(System.getProperty("user.home"), "Desktop");
        File[] files = desktopDir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith("CodeScans2") && !file.getName().contains(currentVersion)) {
                    if (file.delete()) {
                        logger.info("Deleted old file: " + file.getName());
                    } else {
                        logger.error("Failed to delete file: " + file.getName());
                    }
                }
            }
        }
    }

}