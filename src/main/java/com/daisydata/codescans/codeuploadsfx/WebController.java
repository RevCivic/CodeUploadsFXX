package com.daisydata.codescans.codeuploadsfx;

import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import java.lang.Object;
import netscape.javascript.JSObject;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.Base64;
import java.util.ResourceBundle;

public class WebController implements Initializable {

    @FXML
    private WebView web;

    @FXML
    private Button btn;

    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = web.getEngine();
        String url = getClass().getResource("/web/viewer.html").toExternalForm();

        // connect CSS styles to customize pdf.js appearance
        engine.setUserStyleSheetLocation(getClass().getResource("/web/viewer.css").toExternalForm());

        engine.setJavaScriptEnabled(true);
        engine.load(url);

        engine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    // to debug JS code by showing console.log() calls in IDE console
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("java", new JSLogListener());
                    engine.executeScript("console.log = function(message){ java.log(message); };");

                    // this pdf file will be opened on application startup
                    if (newValue == Worker.State.SUCCEEDED) {
                        try {
                            // readFileToByteArray() comes from commons-io library
                            byte[] data = FileUtils.readFileToByteArray(new File("/path/to/file"));
                            String base64 = Base64.getEncoder().encodeToString(data);
                            // call JS function from Java code
                            engine.executeScript("openFileFromBase64('" + base64 + "')");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

        // this file will be opened on button click
        btn.setOnAction(actionEvent -> {
            try {
                byte[] data = FileUtils.readFileToByteArray(new File("/path/to/another/file"));
                String base64 = Base64.getEncoder().encodeToString(data);
                engine.executeScript("openFileFromBase64('" + base64 + "')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
