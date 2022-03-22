package com.daisydata.codescans.codeuploadsfx;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.kordamp.bootstrapfx.scene.layout.Panel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static javafx.geometry.Orientation.VERTICAL;


public class DocumentListPanel extends Panel {
    private static final long serialVersionUID = 1024305110690011228L;
    private BorderPane theList;
    private ArrayList<String> files;
    private ArrayList<Button> buttons;

    public DocumentListPanel(String filePath) {
        this.theList = new BorderPane();
//        this.theList.setBackground(Color.WHITE);
        File[] fList = (new File(filePath)).listFiles();
        this.files = new ArrayList();
        this.buttons = new ArrayList();
        File[] var3 = fList;
        int var4 = fList.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            File f = var3[var5];
            String fileName = f.getName();
            final String fileAbsolutePath = f.getAbsolutePath();
            Button tempButton = new Button(fileName);
//            tempButton.setMargin(new Insets(0, 0, 0, 0));
//            tempButton.setContentAreaFilled(false);
//            tempButton.setOpaque(false);
//            tempButton.setCursor(Cursor.getPredefinedCursor(12));
//            tempButton.setBorderPainted(false);
            tempButton.setOnAction(new ActionEvent() {
                public void actionPerformed(ActionEvent e) {
                    Component component = (Component)e.getSource();
                    CodeScansWindow frame = (CodeScansWindow)SwingUtilities.getRoot(component);

                    try {
                        frame.selectionButtonPressed(fileAbsolutePath);
                    } catch (IOException var5) {
                        var5.printStackTrace();
                    }

                }
            });
            this.theList.add(tempButton);
            this.files.add(fileAbsolutePath);
            this.buttons.add(tempButton);
        }


        this.theList.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setPrefSize(240, 700);
        ScrollBar scrollBar = new ScrollBar();
        scrollBar.setOrientation(VERTICAL);
        scrollBar.setUnitIncrement(16);
        this.getChildren().addAll(scrollPane, scrollBar);


        this.theList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JScrollPane scrollPanel = new JScrollPane(this.theList);
        scrollPanel.setPreferredSize(new Dimension(240, 700));
        scrollPanel.getVerticalScrollBar().setUnitIncrement(16);
        this.add(scrollPanel, "Center");
        this.revalidate();
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
        JButton toBeRemoved = (JButton)this.buttons.get(index);
        String newFile = "";
        this.theList.remove(toBeRemoved);
        this.theList.revalidate();
        this.theList.repaint();
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

