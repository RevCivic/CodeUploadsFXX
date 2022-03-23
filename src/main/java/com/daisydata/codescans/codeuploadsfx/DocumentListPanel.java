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
    ArrayList files;
    ArrayList buttons;

    public DocumentListPanel(String filePath) {
//        VBox box = new VBox();
        File[] fList = (new File(filePath)).listFiles();
        files = new ArrayList();
        buttons = new ArrayList();
        for(int i=0;i<fList.length;i++) {
            File f = fList[i];
            String fileName = f.getName();
            final String fileAbsolutePath = f.getAbsolutePath();
            Button tempButton = new Button(fileName);
            tempButton.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    System.out.println("I GOT CLICKED!");
                }
            });
            tempButton.setText(fileName);
            files.add(fileAbsolutePath);
            buttons.add(tempButton);
            this.getChildren().add(i, tempButton);
        }
    }

//    public String removeButton(String fileAbsolutePath) {
//        int index = this.files.indexOf(fileAbsolutePath);
//        Button toBeRemoved = (Button)this.buttons.get(index);
//        String newFile = "";
//        this.theList.getChildren().remove(toBeRemoved);
//        this.buttons.remove(index);
//        this.files.remove(index);
//        if (index == this.files.size() & this.files.size() > 0) {
//            newFile = (String)this.files.get(0);
//        } else {
//            newFile = (String)this.files.get(index);
//        }
//
//        return newFile;
//    }

    public String removeButton(String fileAbsolutePath) {
        int index = this.files.indexOf(fileAbsolutePath);
        Button toBeRemoved = (Button)this.buttons.get(index);
        String newFile = "";
//        this.theList.getChildren().remove(toBeRemoved);
//        this.theList.revalidate();
//        this.theList.repaint();
//        this.theList.getChildren();
        this.buttons.remove(index);
        this.files.remove(index);
        if (index == this.files.size() & this.files.size() > 0) {
            newFile = (String)this.files.get(0);
        } else {
            newFile = (String)this.files.get(index);
        }

        return newFile;
    }
}

