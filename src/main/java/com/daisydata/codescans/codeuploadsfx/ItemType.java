package com.daisydata.codescans.codeuploadsfx;

public enum ItemType {
    ACKNOWLEDGEMENT("Acknowledgement"),
    AGREEMNT("Agreement"),
    CERT("Certificate"),
    CHECK("Check"),
    COC("CoC"),
    CRINFO("Credit Info"),
    CREDIT_MEMO("Credit Memo"),
    CUST_REJECTION("Customer Rejection Report"),
    CUST_SUPPLIED("Customer Supplied"),
    CUST_VERIFICATION("Customer Verification"),
    CUSTOMER_NOTES("Customer Notes"),
    CUSTOMER_INFO("Customer Info"),
    DESIGNPLAN("Design Plan"),
    DRAWINGS("Drawings"),
    EMAIL("Email"),
    EOL("End of Life"),
    EXHBTA("Exhibit A"),
    FGC("Finished Good Content"),
    FIRST_ARTICLE("First Article"),
    HALLIBURTON("Halliburton CPO"),
    INFO("Vendor Info"),
    INVOICE("Invoice"),
    IRR("Internal Rejection Report"),
    MISC("Miscellaneous"),
    NDA("NDA"),
    NCMR("NCMR"),
    NONE("NONE"),
    OUTGOING_PACKING("Outgoing Packing"),
    PACKING_SLIP("Packing Slip"),
    PAYMENT("Payment"),
    PIC("Picture"),
    PO("Purchase Order"),
    POC("POC"),
    PROGRESS_BILL("Progress Bill"),
    QRR("QRR"),
    QRRPO("Qrr Purchase Order"),
    QRRCPS("QRR Customer Packing Slip"),
    QRRPS("QRR Packing Slip"),
    QUOTE("Quote"),
    RMA_AUTH("RMA Authorization Form"),
    RMA_FORM("RMA Form"),
    RMA_REQ("RMA Request"),
    RETURNED_RMA_AUTH("Returned RMA Authorization Form"),
    REPAIR_REPORT("Repair Report"),
    REPINFO("Rep Info"),
    REQ("Requisition"),
    RR("Receiving Record"),
    SALES_ORDER("Sales Order"),
    SCRAP_AUTHORIZED("Scrap Authorization"),
    SCRAP_DENY_RTC("Scrap Denied Return To Customer"),
    SHIPPING("Shipping Slip"),
    SO("Sales Order"),
    SUPFORM("Supplier Form"),
    SUMMARY("Summary"),
    TAX_EXEMPT_CERT("Tax Exempt Certification"),
    TNC("T & C"),
    TRACKING("Tracking Number"),
    UNASSIGNED("Unassigned CPO"),
    VENDASSMNT("Vendor Assessment"),
    VENDOR_INFO("Vendor Info"),
    W9("W9"),
    WO("Work Order");

    private final String label;

    ItemType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
