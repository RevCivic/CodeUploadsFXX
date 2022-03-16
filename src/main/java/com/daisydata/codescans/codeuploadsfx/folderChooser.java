//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.daisydata.codescans.codeuploadsfx;

import java.io.File;

public class folderChooser extends JFrame {
    private static final long serialVersionUID = 1L;

    public folderChooser() {
    }

    public File start() {
        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("."));
        fc.setFileSelectionMode(1);
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == 0) {
            File yourFolder = fc.getSelectedFile();
            return yourFolder;
        } else {
            return null;
        }
    }

    public static void main(String[] args) {
        File targetFolder = (new folderChooser()).start();
        if (targetFolder == null) {
            System.exit(0);
        } else {
            System.out.println(targetFolder.getAbsolutePath());
        }

    }
}
