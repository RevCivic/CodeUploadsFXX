package com.daisydata.codescans.codeuploadsfx;

public enum DocumentType {
    PO("Purchase Order"),
    SO("Sales Order"),
    RMA("RMA Sales Order"),
    CPO("Customer Purchase Order"),
    CUST("Customer Info"),
    VEND("Vendor Info"),
    REP("Rep Info"),
    PART("Part Info"),
    NCMR("NCMR"),
    SAMPLE("Sample"),
    WO("Work Order");

    private final String label;

    DocumentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
