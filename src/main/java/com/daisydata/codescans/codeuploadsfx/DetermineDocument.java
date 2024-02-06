package com.daisydata.codescans.codeuploadsfx;

public class DetermineDocument {

    public static String determineCategory(DocumentType documentType) {
        return documentType.getLabel();
    }

    public static String determineSubcategory(DocumentType documentType, ItemType itemType) {
        return itemType.getLabel();
    }
}
