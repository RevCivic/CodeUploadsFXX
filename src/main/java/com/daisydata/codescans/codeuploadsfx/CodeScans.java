package com.daisydata.codescans.codeuploadsfx;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class CodeScans extends Application {
    private static String APP_NAME = "CodeScans";
    private static String APP_TITLE = "Code Scanned Documents";
    public static Stage stage;
    public static Scene scene;
    //public static DatabaseConnection dbConn;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage = initiateStage();
        stage.show();
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
        return stage;
    }

    public static Scene initiateScene() {
        StackPane root = new StackPane();
        scene = new Scene(root);
        return scene;
    }
}
