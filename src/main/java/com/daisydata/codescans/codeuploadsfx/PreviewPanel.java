//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.daisydata.codescans.codeuploadsfx;

import java.io.File;
import java.io.IOException;
//import org.apache.pdfbox.Loader;
//import org.apache.pdfbox.pdmodel.PDDocument;
//import org.icepdf.ri.common.SwingController;
//import org.icepdf.ri.common.SwingViewBuilder;

//public class PreviewPanel extends JPanel {
//    private static final long serialVersionUID = -3391352653855592159L;
//    private SwingController controller;
//    private SwingViewBuilder builder;
//    private PDDocument document;
//    private JPanel pdfPanel;
//    private File currFile;
//
//    public PreviewPanel() throws IOException {
//        this.setLayout(new BorderLayout());
//        this.controller = new SwingController();
//        this.controller.setIsEmbeddedComponent(true);
//        this.builder = new SwingViewBuilder(this.controller);
//        this.pdfPanel = this.builder.buildViewerPanel();
//        this.add(this.pdfPanel);
//    }
//
//    public void changePreview(String fullFilePath) throws IOException {
//        if (fullFilePath != null) {
//            this.currFile = this.openPDF(fullFilePath);
//            if (this.document != null) {
//                this.document.close();
//            }
//
//            System.out.print(fullFilePath);
//            this.document = Loader.loadPDF(this.currFile);
//        }
//
//        this.revalidate();
//    }
//
//    public void closePreview() throws IOException {
//        this.document.close();
//        this.revalidate();
//    }
//
//    public File openPDF(String fullFilePath) {
//        File file = new File(fullFilePath);
//        this.controller.openDocument(fullFilePath);
//        return file;
//    }
//
//    public String getCurrentFileDisplayed() {
//        return this.currFile.getAbsolutePath();
//    }
//}
