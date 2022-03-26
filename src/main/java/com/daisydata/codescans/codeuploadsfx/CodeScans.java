package com.daisydata.codescans.codeuploadsfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.File;
import java.io.IOException;

public class CodeScans extends Application {
    private static String APP_NAME = "CodeScans";
    private static String APP_TITLE = "Code Scanned Documents";
    public static String scannedDocumentsFolder = System.getenv("APPDATA") + "\\scannedDocuments";
    public static Stage stage;
    public static Scene scene;
    public static AnchorPane root;
    //public static DatabaseConnection dbConn;

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

    public static Stage initiateStage(){
        //dbConn = new DatabaseConnection();
        stage = new Stage();
        stage.setTitle(APP_NAME);
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent windowEvent) {
                stop(0);
            }
        });
        scene = initiateScene();
        stage.setScene(scene);
        stage.setMaximized(true);
        return stage;
    }

    public static Scene initiateScene() {
        try {
            root = FXMLLoader.load(CodeScans.class.getResource("/main.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
             root = new AnchorPane();
        }
//        DocumentListPanel documentListPanel = new DocumentListPanel(scannedDocumentsFolder);
        try {
            CodeScansWindow csw = new CodeScansWindow(scannedDocumentsFolder);
            root.getChildren().add(csw);
        } catch (IOException e) {
            e.printStackTrace();
            stop(3);
        }
        scene = new Scene(root);
        return scene;
    }

    public void preFlightCheck() {

        if (!new File(scannedDocumentsFolder).exists()) {
            (new File(scannedDocumentsFolder)).mkdirs();
        }
    }
}
