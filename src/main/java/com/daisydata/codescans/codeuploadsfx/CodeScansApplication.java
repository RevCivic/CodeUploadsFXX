
package com.daisydata.codescans.codeuploadsfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
import java.io.IOException;
import java.util.Objects;
import java.util.Scanner;



public class CodeScansApplication extends Application {
    private static final String APP_NAME = "CodeScans";
    private static final String APP_TITLE = "Code Scanned Documents";
    private static final String VERSION_PATH = "//dnas1/Share/Departments/IT/CodeScans2.0/Version/Version.txt";
    public static String CURRENT_VERSION = "v0.9.66";
    static Boolean LOGGING = true;
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

    // Stops the program
    public static void stop(int exitStatus) {
        controller.consumeTempFiles();
        Platform.exit();
        System.exit(exitStatus);
    }

    // Creates the window for the application
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

    // Creates the GUI on the window
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
        // Allows the user to toggle through light and dark mode
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

    // Checks for scannedDocuments folder and the .ini file and creates them if they doesn't exist.
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
    }

    // Sets the style of the theme when the user triggers the function with alt + shift
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

        try {
            File file = new File(VERSION_PATH);
            Scanner scanner = new Scanner(file);
            String latestVersion = scanner.next();
            scanner.close();
            // Gets Version.txt info and compares it to the CURRENT_VERSION and prompts for update if needed.
            if (!Objects.equals(latestVersion, CURRENT_VERSION)) {
                updatesAvailable = true;
            }

            if (updatesAvailable) {
                // Run the updater JAR
                String updaterJarPath = "//dnas1/Share/Departments/IT/CodeScans2.0/Updater/CodeScansUpdater.jar";
                ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", updaterJarPath);
                processBuilder.start();

                // Exit this process
                System.exit(0);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}