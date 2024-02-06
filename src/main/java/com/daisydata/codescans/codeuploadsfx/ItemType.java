package com.daisydata.codescans.codeuploadsfx;

public enum ItemType {
    PO("Purchase Order"),
    REQ("Requisition"),
    PACKING_SLIP("Packing Slip"),
    COC("CoC"),
    INVOICE("Invoice"),
    FIRST_ARTICLE("First Article"),
    POC("POC"),
    OUTGOING_PACKING("Outgoing Packing"),
    QUOTE("Quote"),
    SUMMARY("Summary"),
    CREDIT_MEMO("Credit Memo"),
    MISC("Miscellaneous"),
    CHECK("Check"),
    PAYMENT("Payment"),
    APPROVAL("Approval Slip"),
    ACKNOWLEDGEMENT("Acknowledgement"),
    PROGRESS_BILL("Progress Bill"),
    SHIPPING("Shipping Slip"),
    TRACKING("Tracking Number"),
    CUSTOMER_NOTES("Customer Notes"),
    CUST_SUPPLIED("Customer Supplied"),
    CUST_VERIFICATION("Customer Verification"),
    RR("Receiving Record"),
    SCRAP_AUTHORIZED("Scrap Authorization"),
    SCRAP_DENY_RTC("Scrap Denied Return To Customer"),
    FGC("Finished Good Content"),
    PIC("Picture"),
    RMA_AUTH("RMA Authorization Form"),
    RMA_FORM("RMA Form"),
    RETURNED_RMA_AUTH("Returned RMA Authorization Form"),
    REPAIR_REPORT("Repair Report"),
    RMA_REQ("RMA Request"),
    HALLIBURTON("Halliburton CPO"),
    LOCKHEED("Lockheed CPO"),
    UNASSIGNED("Unassigned CPO"),
    CUSTINFO("Customer Info"),
    CRINFO("Credit Info"),
    NDA("NDA"),
    SUPFORM("Supplier Form"),
    TAX_EXEMPT_CERT("Tax Exempt Certification"),
    W9("W9"),
    TNC("T & C"),
    INFO("Vendor Info"),
    CERT("Certificate"),
    VENDASSMNT("Vendor Assessment"),
    REPINFO("Rep Info"),
    AGREEMNT("Agreement"),
    EXHBTA("Exhibit A"),
    SDA("Sales Distribution Agreement"),
    EOL("End of Life"),
    QRR("QRR"),
    QRRCPS("QRR Customer Packing Slip"),
    QRRPO("Qrr Purchase Order"),
    QRRPS("QRR Packing Slip"),
    IRR("Internal Rejection Report"),
    NONE("NONE");

    private final String label;

    ItemType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
