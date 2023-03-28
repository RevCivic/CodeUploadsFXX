package com.daisydata.codescans.codeuploadsfx;

public class DetermineDocument {


    public static String determineCategory(String documentType){
        documentType = switch (documentType) {
            case "po" -> "Purchase Order";
            case "so" -> "Sales Order";
            case "rma" -> "RMA Sales Order";
            case "cpo" -> "Customer Purchase Order";
            case "cust" -> "Customer Info";
            case "vend" -> "Vendor Info";
            case "rep" -> "Rep Info";
            case "part" -> "Part Info";
            case "ncmr" -> "NCMR";
            case "sample" -> "Sample";
            default -> documentType;
        };
        return documentType;
    }

    public static String determineSubcategory(String documentType, String itemType) {
        if (documentType.equalsIgnoreCase("Purchase Order")) {
            itemType = switch (itemType) {
                case "po" -> "Purchase Order";
                case "req" -> "Requisition";
                case "packing-slip" -> "Packing Slip";
                case "coc" -> "CoC";
                case "invoice" -> "Invoice";
                case "first-article" -> "First Article";
                case "poc" -> "POC";
                case "outgoing-packing" -> "Outgoing Packing";
                case "quote" -> "Quote";
                case "summary" -> "Summary";
                case "credit-memo" -> "Credit Memo";
                case "misc" -> "Miscellaneous";
                default -> itemType;
            };
        } else if (documentType.equalsIgnoreCase("Sales Order")) {
            itemType = switch (itemType) {
                case "so" -> "Sales Order";
                case "invoice" -> "Invoice";
                case "quote" -> "Quote";
                case "coc" -> "CoC";
                case "check" -> "Check";
                case "payment" -> "Payment";
                case "approval" -> "Approval Slip";
                case "acknowledgement" -> "Acknowledgement";
                case "packing-slip" -> "Packing Slip";
                case "progress-bill" -> "Progress Bill";
                case "credit-memo" -> "Credit Memo";
                case "shipping" -> "Shipping Slip";
                case "tracking" -> "Tracking Number";
                case "customer-notes" -> "Customer Notes";
                case "cpo" -> "Customer Purchase Order";
                case "wo" -> "Work Order";
                case "drawings" -> "Drawings";
                case "email" -> "Email";
                case "designplan" -> "Design Plan";
                case "misc" -> "Miscellaneous";
                case "cust-supplied" -> "Customer Supplied";
                default -> itemType;
            };
        } else if (documentType.equalsIgnoreCase("RMA Sales Order")) {
            itemType = switch (itemType) {
                case "so" -> "Sales Order";
                case "invoice" -> "Invoice";
                case "coc" -> "CoC";
                case "check" -> "Check";
                case "payment" -> "Payment";
                case "approval" -> "Approval Slip";
                case "acknowledgement" -> "Acknowledgement";
                case "rr" -> "Receiving Record";
                case "quote" -> "Quote";
                case "progress-bill" -> "Progress Bill";
                case "scrap-authorized" -> "Scrap Authorization";
                case "scrap-deny-rtc" -> "Scrap Denied Return To Customer";
                case "fgc" -> "Finished Good Content";
                case "customer-notes" -> "Customer Notes";
                case "pic" -> "Picture";
                case "packing-slip" -> "Packing Slip";
                case "cust-rejection" -> "Customer Rejection Report";
                case "credit-memo" -> "Credit Memo";
                case "shipping" -> "Shipping Slip";
                case "tracking" -> "Tracking Number";
                case "cpo" -> "Customer Purchase Order";
                case "hcpo" -> "Halliburton Customer Purchase Order";
                case "lcpo" -> "Lockheed Customer Purchase Order";
                case "rma-auth" -> "RMA Authorization Form";
                case "rma-form" -> "RMA Form";
                case "returned-rma-auth" -> "Returned RMA Authorization Form";
                case "repair-report" -> "Repair Report";
                case "wo" -> "Work Order";
                case "rma-req" -> "RMA Request";
                case "misc" -> "Miscellaneous";
                default -> itemType;
            };
        } else if (documentType.equalsIgnoreCase("Customer Purchase Order")) {
            itemType = switch (itemType) {
                case "halliburton" -> "Halliburton CPO";
                case "lockeed" -> "Lockheed CPO";
                case "unassigned" -> "Unassigned CPO";
                default -> itemType;
            };
        } else if (documentType.equalsIgnoreCase("Customer Info")) {
            itemType = switch (itemType) {
                case "custinfo" -> "Customer Info";
                case "crinfo" -> "Credit Info";
                case "nda" -> "NDA";
                case "supform" -> "Supplier Form";
                case "taxexemptcert" -> "Tax Exempt Certification";
                case "w9" -> "W9";
                case "tnc" -> "T & C";
                default -> itemType;
            };
        } else if (documentType.equalsIgnoreCase("Vendor Info")) {
            itemType = switch (itemType) {
                case "vendinfo" -> "Vendor Info";
                case "cert" -> "Certificate";
                case "vendassmnt" -> "Vendor Assessment";
                default -> itemType;
            };
        } else if (documentType.equalsIgnoreCase("Rep Info")) {
            itemType = switch (itemType) {
                case "repinfo" -> "Rep Info";
                case "agreemnt" -> "Agreement";
                case "exhbta" -> "Exhibit A";
                case "sda" -> "Sales Distribution Agreement";
                default -> itemType;
            };
        } else if (documentType.equalsIgnoreCase("Part Info")) {
            itemType = switch (itemType) {
                case "eol" -> "End of Life";
                default -> itemType;
            };
        } else if (documentType.equalsIgnoreCase("NCMR")) {
            itemType = switch (itemType) {
                case "qrr" -> "QRR";
                case "ncmr" -> "NCMR";
                case "qrrcps" -> "QRR Customer Packing Slip";
                case "qrrpo" -> "Qrr Purchase Order";
                case "qrrps" -> "QRR Packing Slip";
                case "rr" -> "Receiving Record";
                case "irr" -> "Internal Rejection Report";
                default -> itemType;
            };
        } else if (documentType.equalsIgnoreCase("Sample")) {
            itemType = switch (itemType) {
                case "sample" -> "Sample";
                default -> itemType;
            };
        }
        return itemType;
    }
}
