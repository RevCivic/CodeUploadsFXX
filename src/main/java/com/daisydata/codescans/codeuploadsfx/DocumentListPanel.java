package com.daisydata.codescans.codeuploadsfx;

import javafx.beans.InvalidationListener;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.io.File;
import java.util.*;


public class DocumentListPanel extends VBox {
    static ArrayList files;
    ArrayList buttons;


    public DocumentListPanel(String filePath) {
//        VBox box = new VBox();
        populateList(filePath);
    }

    public void populateList(String filePath) {
        File[] fList = (new File(filePath)).listFiles();
        files = new ArrayList();
        buttons = new ArrayList();
        for(int i=0;i<fList.length;i++) {
            File f = fList[i];
            String fileName = f.getName();
            final String fileAbsolutePath = f.getAbsolutePath();
            Button tempButton = new Button(fileName);
            int finalI = i;
            tempButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println("File number "+ finalI +" clicked");
                    CodeScansApplication.selectedFile = finalI;
                    CodeScansApplication.selectedFilePath = fileAbsolutePath;
                    System.out.println("Current file path: "+CodeScansApplication.selectedFilePath);
                    System.out.println("Attempting to render file...");
                    CodeScansApplication.controller.loadDoc();
                }
            });
            tempButton.setText(fileName);
            files.add(fileAbsolutePath);
            buttons.add(tempButton);
            if(i==0){
                CodeScansApplication.selectedFilePath = fileAbsolutePath;
            }
            System.out.println("Adding "+fileAbsolutePath);
            CodeScansApplication.controller.addDocButton(tempButton);
        }
    }

    public void clearList() {
        files.clear();
        buttons.clear();
        CodeScansApplication.controller.removeAllDocButtons();
    }

    public void removeFile(int index) {
        files.remove(index);
        buttons.remove(index);
        CodeScansApplication.controller.removeDocButton(index);
    }
}