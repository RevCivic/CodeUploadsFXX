module com.daisydata.codescans.codeuploadsfx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
//    requires icepdf.viewer;
//    requires pdfbox;
    requires java.sql;
    requires jdk.compiler;
    requires icepdf.viewer;
    requires jdk.jsobject;
    requires org.apache.commons.io;
    requires itextpdf;
    requires ini4j;
    requires org.apache.logging.log4j;


    opens com.daisydata.codescans.codeuploadsfx to javafx.fxml;
    exports com.daisydata.codescans.codeuploadsfx;
}