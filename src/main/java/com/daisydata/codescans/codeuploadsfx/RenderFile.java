package com.daisydata.codescans.codeuploadsfx;

import javafx.concurrent.Worker;
import javafx.scene.Node;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;

public class RenderFile {

    public static WebView webView;

    public RenderFile(String filePath) {
        WebEngine engine = new WebEngine();
        String viewer = getClass().getResource("/web/viewer.html").toExternalForm();
        engine.setUserStyleSheetLocation(getClass().getResource("/web/viewer.css").toExternalForm());
        engine.setJavaScriptEnabled(true);
        engine.load(filePath);

        InputStream stream = null;
        engine.getLoadWorker()
                .stateProperty()
                .addListener((observable, oldValue, newValue) -> {
                    // to debug JS code by showing console.log() calls in IDE console
                    JSObject window = (JSObject) engine.executeScript("window");
                    window.setMember("java", new JSLogListener());
                    engine.executeScript("console.log = function(message){ java.log(message); };");

//                     this pdf file will be opened on application startup
                    if (newValue == Worker.State.SUCCEEDED) {
                        try {
                            byte[] data = FileUtils.readFileToByteArray(new File(filePath));
                            String base64 = Base64.getEncoder().encodeToString((data));
                            engine.executeScript("openFileFromBase64('"+base64+"')");

                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            if(stream != null) {
                                try {
                                    stream.close();
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }

                            }
                        }
                    }
                });

    }
}
