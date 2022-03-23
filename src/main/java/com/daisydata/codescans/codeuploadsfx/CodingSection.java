package com.daisydata.codescans.codeuploadsfx;

import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class CodingSection extends Scene {
    private static final long serialVersionUID = 3130937933565507196L;
    private TextArea category;
    private Label categoryLabel;
    private TextArea docType;
    private Label docTypeLabel;
    private TextArea identifier;
    private Label identifierLabel;
    private Button submit;
    private ComboBox<CodingSection.CodingSet> categoryChoices;
    private ComboBox<CodingSection.Option> options;
    private final String[] allCategories = new String[]{"not-selected|Select a category", "so|Sales Order", "po|Purchase Order", "rma|RMA Sales Order", "cpo|Customer Purchase Order", "cust|Customer Info", "vend|Vendor Info", "rep|Rep Info", "part|Part Info", "ncmr|NCMR", "sample|Sample"};
    private final String[][] allCategoryTypes = new String[][]{{"not-selected|Please Select a Category First"}, {"not-selected|Select an Item", "so|Sales Order", "invoice|Invoice", "quote|Quote", "coc|CoC", "check|Check", "payment|Payment", "approval|Approval Slip", "acknowledgement|Acknowledgement", "packing-slip|Packing Slip", "progress-bill|Progress Bill", "credit-memo|Credit Memo", "shipping|Shipping Slip", "tracking|Tracking Number", "customer-notes|Customer Notes", "cpo|Customer Purchase Order", "wo|Work Order", "drawings|Drawings", "email|Email", "designplan|Design Plan", "misc|Miscellaneous", "cust-supplied|Customer Supplied"}, {"not-selected|Select an Item", "po|Purchase Order", "req|Requisition", "packing-slip|Packing Slip", "coc|CoC", "invoice|Invoice", "first-article|First Article", "poc|POC", "outgoing-packing|Outgoing Packing Slip", "quote|Quote", "summary|Summary Page", "credit-memo|Credit Memo", "misc|Miscellaneous"}, {"not-selected|Select an Item", "so|Sales Order", "invoice|Invoice", "coc|CoC", "check|Check", "payment|Payment", "approval|Approval Slip", "acknowledgement|Acknowledgement", "rr|Receiving Record", "quote|Quote", "progress-bill|Progress Bill", "scrap-authorized|Scrap Authorization", "scrap-deny-rtc|Scrap Denied Return To Customer", "fgc|Finished Good Content", "customer-notes|Customer Notes", "pic|Picture", "packing-slip|Packing Slip", "cust-rejection|Customer Rejection Report", "credit-memo|Credit Memo", "shipping|Shipping Slip", "tracking|Tracking Number", "cpo|Customer Purchase Order", "hcpo|Halliburton Customer Purchase Order", "lcpo|Lockheed Customer Purchase Order", "rma-auth|RMA Authorization Form", "rma-form|RMA Form", "returned-rma-auth|Returned RMA Authorization Form", "repair-report|Repair Report", "wo|Work Order", "rma-req|RMA Request", "misc|Miscellaneous"}, {"not-selected|Select an Item", "halliburton|Halliburton CPO", "lockheed|Lockheed CPO", "unassigned|Unassigned CPO"}, {"not-selected|Select an Item", "custinfo|Customer Info", "crinfo|Credit Info", "nda|NDA", "supform|Supplier Form", "taxexmptcert|Tax Exempt Certificate", "w9|W9", "tnc|T & C"}, {"not-selected|Select an Item", "vendinfo|Vendor Info", "cert|Certificate", "vendassmnt|Vendor Assessment"}, {"not-selected|Select an Item", "repinfo|Rep Info", "agreemnt|Agreement", "exhbta|Exhibit A", "sda|Sales Distribution Agreement"}, {"not-selected|Select an Item", "eol|End of Life"}, {"not-selected|Select an Item", "qrr|QRR", "ncmr|NCMR", "qrrcps|QRR Customer Packing Slip", "qrrpo|QRR Purchase Order", "qrrps|QRR Packing Slip", "rr|Receiving Record", "irr|Internal Rejection Report"}, {"sample|Sample"}};

    public CodingSection(Parent parent) {
        super(parent);
    }

//    public CodingSection() {
////        this.setBackground(Color.WHITE);
//        this.categoryChoices = new ComboBox();
//        String[] var1 = this.allCategories;
//        int var2 = var1.length;
//
//        for(int var3 = 0; var3 < var2; ++var3) {
//            String category = var1[var3];
////            this.categoryChoices.getParent().(new CodingSet(category));
//        }
//
////        if (System.getProperty("user.name").toLowerCase().equals("leellenf")) {
////            this.categoryChoices.addItem(new CodingSet("wo|Work Order"));
////            this.categoryChoices.addItem(new CodingSet("cycle-count|Weekly Cycle Count"));
////            this.categoryChoices.addItem(new CodingSet("fga|Finished Goods Audit"));
////        }
////
////        if (System.getProperty("user.name").toLowerCase().equals("travisr")) {
////            this.categoryChoices.addItem(new CodingSet("misc|Miscellaneous"));
////        }
////
////        if (System.getProperty("user.name").toLowerCase().equals("travisr") || System.getProperty("user.name").toLowerCase().equals("michaels2") || System.getProperty("user.name").toLowerCase().equals("rayr") || System.getProperty("user.name").toLowerCase().equals("wandam")) {
////            this.categoryChoices.addItem(new CodingSet("ee|Employee Document"));
////        }
////
////        this.categoryChoices.addActionListener(new ActionListener() {
////            public void actionPerformed(ActionEvent e) {
////                JComboBox<CodingSet> dropdownSelector = (JComboBox)e.getSource();
////                CodingSet selection = (CodingSet)dropdownSelector.getSelectedItem();
////                Component component = (Component)e.getSource();
////                CodeScansWindow frame = (CodeScansWindow)SwingUtilities.getRoot(component);
////                frame.dropdownCategorySelected(selection);
////            }
////        });
//        this.options = new ComboBox();
////        this.options.addItem(new Option(this.allCategoryTypes[0][0]));
//        this.category = new TextArea();
////        this.category.setBackground(Color.LIGHT_GRAY);
//        this.categoryLabel = new Label("Category:");
////        this.categoryLabel.setForeground(Color.BLUE);
//        this.docType = new TextArea();
////        this.docType.setBackground(Color.LIGHT_GRAY);
//        this.docTypeLabel = new Label("Type:");
////        this.docTypeLabel.setForeground(Color.BLUE);
//        this.identifier = new TextArea();
////        this.identifier.setBackground(Color.LIGHT_GRAY);
//        this.identifierLabel = new Label("Number/ID:");
////        this.identifierLabel.setForeground(Color.BLUE);
//        this.submit = new Button("Submit document");
////        this.submit.addActionListener(new ActionListener() {
////            public void actionPerformed(ActionEvent e) {
////                Component component = (Component)e.getSource();
////                CodeScansWindow frame = (CodeScansWindow)SwingUtilities.getRoot(component);
////
////                try {
////                    frame.submitButtonPressed();
////                } catch (IOException var5) {
////                    var5.printStackTrace();
////                }
////
////            }
////        });
////        this.add(this.categoryLabel);
////        this.add(this.categoryChoices);
////        this.add(this.docTypeLabel);
////        this.add(this.options);
////        this.add(this.identifierLabel);
////        this.add(this.identifier);
////        this.add(this.submit);
//    }

//    public void updateTypes(CodingSection.CodingSet selection) {
//        this.options.removeAllItems();
//        CodingSection.Option[] theOptions = selection.getTypes();
//        CodingSection.Option[] var3 = theOptions;
//        int var4 = theOptions.length;
//
//        for(int var5 = 0; var5 < var4; ++var5) {
//            CodingSection.Option option = var3[var5];
//            this.options.addItem(option);
//        }
//
//        this.revalidate();
//    }

//    public String getCategoryValue() {
//        return ((CodingSection.CodingSet)this.categoryChoices.getSelectedItem()).value();
//    }

//    public String getTypeSelected() {
//        return ((CodingSection.Option)this.options.getSelectedItem()).value();
//    }

    public String getIdentifier() {
        return this.identifier.getText();
    }

    public void clearIdentifier() {
        this.identifier.setText("");
    }

    private class Option {
        private String optionInfo;
        private String optionValue;
        private String original;

        public Option(String info) {
            this.original = info;
            if (info == "") {
                this.optionInfo = this.optionValue = "";
            } else {
                String[] temp = info.split("\\|");
                this.optionValue = temp[0];
                this.optionInfo = temp[1];
            }

        }

        public Option() {
            this("");
        }

        public String toString() {
            return this.optionInfo;
        }

        public String value() {
            return this.optionValue;
        }

        public String fullString() {
            return this.original;
        }
    }

    public class CodingSet {
        private CodingSection.Option category;
        private CodingSection.Option[] types;

        public CodingSet(String categoryStr) {
            this.category = CodingSection.this.new Option(categoryStr);
            if (this.category.value().equals("wo")) {
                this.types = new CodingSection.Option[1];
                this.types[0] = CodingSection.this.new Option("none|No SO");
            } else if (this.category.value().equals("misc")) {
                this.types = new CodingSection.Option[1];
                this.types[0] = CodingSection.this.new Option("none|No SO");
            } else if (this.category.value().equals("ee")) {
                this.types = new CodingSection.Option[1];
                this.types[0] = CodingSection.this.new Option("photo|Photo");
            } else {
                int ix;
                CodingSection.Option[] var10000;
                String var10005;
                if (this.category.value().equals("cycle-count")) {
                    this.types = new CodingSection.Option[52];
                    Date now = new Date();
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(now);
                    cal.add(5, -1 * cal.get(7) + 4);
                    SimpleDateFormat format1x = new SimpleDateFormat("yyyy-MM-dd");

                    for(ix = 0; ix < 52; ++ix) {
                        var10000 = this.types;
                        var10005 = format1x.format(cal.getTime());
                        var10000[ix] = CodingSection.this.new Option(var10005 + "|" + format1x.format(cal.getTime()));
                        cal.add(5, -7);
                    }
                } else if (this.category.value().equals("fga")) {
                    this.types = new CodingSection.Option[24];
                    Calendar calx = Calendar.getInstance();
                    calx.set(2, 1);
                    calx.set(1, 2018);
                    calx.set(5, 1);
                    SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");

                    for(int i = 0; i < 24; ++i) {
                        calx.add(2, 1);
                        calx.add(5, -1);
                        var10000 = this.types;
                        var10005 = format1.format(calx.getTime());
                        var10000[i] = CodingSection.this.new Option(var10005 + "|" + format1.format(calx.getTime()));
                        calx.add(5, 1);
                    }
                } else {
                    int position = 0;
                    String[] tempArr = CodingSection.this.allCategoryTypes[this.findIndexOfCategory()];
                    this.types = new CodingSection.Option[tempArr.length];
                    String[] var14 = tempArr;
                    ix = tempArr.length;

                    for(int var7 = 0; var7 < ix; ++var7) {
                        String item = var14[var7];
                        if (item == "") {
                            this.types[position] = CodingSection.this.new Option();
                        } else {
                            this.types[position] = CodingSection.this.new Option(item);
                        }

                        ++position;
                    }
                }
            }

        }

        public CodingSet() {
            this("");
        }

        public String toString() {
            return this.category.toString();
        }

        public CodingSection.Option[] getTypes() {
            return this.types;
        }

        public String value() {
            return this.category.value();
        }

        private int findIndexOfCategory() {
            int position = 0;
            String[] var2 = CodingSection.this.allCategories;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String categoryName = var2[var4];
                if (this.category.fullString() == categoryName) {
                    return position;
                }

                ++position;
            }

            return -1;
        }
    }
}
