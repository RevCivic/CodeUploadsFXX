package com.daisydata.codescans.codeuploadsfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;

public class CodeScansApplication extends Application {
    private static String APP_NAME = "CodeScans";
    private static String APP_TITLE = "Code Scanned Documents";
    public static String scannedDocumentsFolder = System.getenv("APPDATA") + "\\scannedDocuments";
    public static Pane root;
    public static Stage stage;
    public static Scene scene;
    public static CodeScansController controller;
    //public static DatabaseConnection dbConn;
    public static FXMLLoader fxmlLoader;
    public static int selectedFile;
    public static String selectedFilePath;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        preFlightCheck();
        stage = initiateStage();
        stage.show();
//        stage.setOnCloseRequest(e -> e.consume());
    }

    public static void stop(int exitStatus) {
        //dbConn.deconstruct();
        Platform.exit();
        System.exit(exitStatus);
    }

    public Stage initiateStage() throws IOException {
        //dbConn = new DatabaseConnection();
        stage = new Stage();
        stage.setTitle(APP_NAME);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                stop(0);
            }
        });
        stage.initModality(Modality.WINDOW_MODAL);
//        stage.initOwner(this.stage);
        scene = initiateScene();
        scene.getStylesheets().add("Stylesheet.css");
        stage.setScene(scene);
        stage.setMaximized(true);
        return stage;
    }

    public static Scene initiateScene() throws IOException {
        fxmlLoader = new FXMLLoader(CodeScansApplication.class.getResource("/CodeScans-view.fxml"));
        controller = new CodeScansController();
        fxmlLoader.setController(controller);
        try {
            root = fxmlLoader.load();
            scene = new Scene(root);
            BorderPane codeScansWindow = new CodeScansWindow(scannedDocumentsFolder);
            controller.loadDoc();
        } catch (IOException e) {
            e.printStackTrace();
            stop(3);
        }
        return scene;
    }

    public void preFlightCheck() {
        if (!new File(scannedDocumentsFolder).exists()) {
            (new File(scannedDocumentsFolder)).mkdirs();
        }
        System.out.println(scannedDocumentsFolder);
    }
}
