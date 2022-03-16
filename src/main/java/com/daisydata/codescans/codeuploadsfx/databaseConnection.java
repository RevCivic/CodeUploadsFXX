package com.daisydata.codescans.codeuploadsfx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import static com.sun.tools.javac.jvm.ByteCodes.invokedynamic;

public class databaseConnection {
    static final String JDBC_DRIVER = "com.pervasive.jdbc.v2.Driver";
    static final String DB_URL = "jdbc:pervasive://GSS1/GLOBALTST";
    static final String USER = "Master";
    static final String PASS = "master";
    static final String BASE_SQL = "SELECT CUSTOMER, NAME_CUSTOMER FROM V_CUSTOMER_MASTER WHERE NAME_CUSTOMER != ''";
    private static String ORDER_HEADER_SQL = "select a.CUSTOMER, b.NAME_CUSTOMER, a.ORDER_NO from (select ORDER_NO, CUSTOMER from ((select ORDER_NO, CUSTOMER from V_ORDER_HEADER WHERE ORDER_NO = '*!*') UNION ALL (select ORDER_NO, CUSTOMER from V_ORDER_HIST_HEAD WHERE ORDER_NO = '*!*')) c) a inner join V_CUSTOMER_MASTER b on a.CUSTOMER = b.CUSTOMER";
    private static String PO_HEADER_SQL = "select a.VENDOR, b.NAME_VENDOR, a.PURCHASE_ORDER as PO_NUM from (select PURCHASE_ORDER, VENDOR from ((select PURCHASE_ORDER, VENDOR from V_PO_HEADER WHERE PURCHASE_ORDER = '*!*') UNION ALL (select PURCHASE_ORDER, VENDOR from V_PO_H_HEADER WHERE PURCHASE_ORDER = '*!*')) c) a inner join V_VENDOR_MASTER b on a.VENDOR = b.VENDOR ";
    private static String RMA_HEADER_SQL = "SELECT CUSTOMER, NAME_CUSTOMER, RMA_ID, ORDER_NO FROM V_RMA_HIST_HEADER WHERE RMA_ID = '*!*' UNION ALL SELECT CUSTOMER, NAME_CUSTOMER, RMA_ID, ORDER_NO FROM V_RMA_HEADER WHERE RMA_ID = '*!*'";
    private static String VENDOR_MASTER_SQL = "SELECT VENDOR, NAME_VENDOR FROM V_VENDOR_MASTER WHERE VENDOR = '*!*'";
    private static String PARENT_CODE_SQL = "SELECT PATH_ID from D3_DMS_INDEX where ABS_PATH = '*!*'";
    private static String FIND_REQUISITION_SQL = "SELECT PURCHASE_ORDER from V_PO_LINES where REQUISITION_NO = '*!*'";
    private static String CHILDREN_CODE_SQL = "SELECT PATH_ID from D3_DMS_INDEX where PATH_ID like '*!*____' ORDER BY PATH_ID ASC";
    private static String INSERT_FOLDER_SQL = "INSERT INTO D3_DMS_INDEX (PATH_ID, ABS_PATH) VALUES ('*!*','!*!')";
    private static String INSERT_DOC_SQL = "INSERT INTO D3_DMS_DOCS (PATH_ID, DOC_NAME, ITEM_NUM, ITEM_TYPE, DOC_TYPE, LAST_CHG_BY, LAST_CHANGE) VALUES ('*!*','*!!*','*!!!*','*!!!!*','*!!!!!*','*!!!!!!*',*!!!!!!!*)";
    private static String FIND_RECEIVER_SQL = "SELECT RECEIVER_NO, PURCHASE_ORDER, PO_LINE, DATE_RECEIVED, PART, PACK_LIST, EXTENDED_COST, QTY_RECEIVED FROM V_PO_RECEIVER where PURCHASE_ORDER = '*!*'";
    private static String CATEGORIES_SQL = "SELECT * FROM D3_DMS_CATEGORIES";
    private static Connection conn;
    private static Statement stmt = null;
    private ResultSet rs = null;

    public static void main(String[] args) {
    }

    public databaseConnection() {
        try {
            Class.forName("com.pervasive.jdbc.v2.Driver");
            conn = DriverManager.getConnection("jdbc:pervasive://GSS1/GLOBALTST", "Master", "master");
            stmt = conn.createStatement();
        } catch (ClassNotFoundException var2) {
            var2.printStackTrace();
        } catch (SQLException var3) {
            var3.printStackTrace();
        } catch (NullPointerException var4) {
            var4.printStackTrace();
        }

    }

    public void deconstruct() {
        try {
            this.rs.close();
            System.out.println("ResultSet Closed");
            stmt.close();
            System.out.println("Statement Closed");
            conn.close();
            System.out.println("Connection Closed");
        } catch (SQLException var2) {
            System.out.println("Attempted to close connection and encountered an error");
            var2.printStackTrace();
        }

    }

    private String testDBC() {
        String result = "";
        String customerNum = "";
        String customerName = "";
        String mysql = "SELECT CUSTOMER, NAME_CUSTOMER FROM V_CUSTOMER_MASTER WHERE NAME_CUSTOMER != '' and CUSTOMER = '004076'";

        try {
            this.rs = stmt.executeQuery(mysql);
            if (this.rs.next()) {
                customerNum = this.rs.getString(1).trim();
                customerName = this.rs.getString(2).trim();
                result = customerName + " - " + customerNum;
            }
        } catch (SQLException var6) {
            var6.printStackTrace();
        }

        return result;
    }

    public String findReqPo(String reqNumber) {
        String sql = "";
        String result = "";
        sql = FIND_REQUISITION_SQL.replace("*!*", reqNumber);

        try {
            this.rs = stmt.executeQuery(sql);
            if (this.rs.next()) {
                result = this.rs.getString(1).trim();
            }

            return result;
        } catch (SQLException var8) {
            var8.printStackTrace();
            return result;
        } finally {
            ;
        }
    }

    public String selectReceiver(String poNumber) {
        String sql = "";
        String result = "";
        sql = FIND_RECEIVER_SQL.replace("*!*", poNumber);

        try {
            this.rs = stmt.executeQuery(sql);
            if (this.rs.next()) {
                GuiTools gui = new GuiTools();
                result = gui.receiverPicker(this.rs);
            }
        } catch (SQLException var5) {
            var5.printStackTrace();
        }

        return result;
    }

    public String[] findFolderName(String docType, String itemNumber) {
        String sql = "";
        String num = "";
        String name = "";
        String[] result = new String[2];
        if (!docType.toLowerCase().equals("rma") && !docType.toLowerCase().equals("so")) {
            if (!docType.toLowerCase().equals("po") && !docType.toLowerCase().equals("ncmr")) {
                if (docType.toLowerCase().equals("cust")) {
                    sql = "SELECT CUSTOMER, NAME_CUSTOMER FROM V_CUSTOMER_MASTER WHERE NAME_CUSTOMER != '' and CUSTOMER = '" + itemNumber + "'";
                } else if (docType.toLowerCase().equals("vend")) {
                    sql = VENDOR_MASTER_SQL.replace("*!*", itemNumber);
                }
            } else {
                sql = PO_HEADER_SQL.replace("*!*", itemNumber);
            }
        } else if (itemNumber.subSequence(0, 1).equals("4")) {
            sql = RMA_HEADER_SQL.replace("*!*", itemNumber);
        } else {
            sql = ORDER_HEADER_SQL.replace("*!*", itemNumber);
        }

        try {
            this.rs = stmt.executeQuery(sql);
            if (this.rs.next()) {
                num = this.rs.getString(1).trim();
                name = this.rs.getString(2).trim().replace("/", "_");
                result[0] = name;
                result[1] = num;
            }
        } catch (SQLException var8) {
            var8.printStackTrace();
        }

        return result;
    }

    public void addNewDocument(String directory, String docName, String itemNumber, String itemType, String docType) {
        String pathID = "";
        directory = directory.replace("/", "\\");
        String codeSQL = PARENT_CODE_SQL.replace("*!*", directory.replace("'", "''"));

        try {
            this.rs = stmt.executeQuery(codeSQL);
            if (this.rs.next()) {
                pathID = this.rs.getString(1).trim();
            }
        } catch (SQLException var12) {
            var12.printStackTrace();
        }

        String justDocName = docName.substring(docName.lastIndexOf("/") + 1).replace("/", "\\");
        String sql = INSERT_DOC_SQL;
        sql = sql.replace("*!*", pathID);
        sql = sql.replace("*!!*", justDocName);
        sql = sql.replace("*!!!*", itemNumber);
        sql = sql.replace("*!!!!*", itemType);
        sql = sql.replace("*!!!!!*", docType);
        sql = sql.replace("*!!!!!!*", System.getProperty("user.name"));
        sql = sql.replace("*!!!!!!!*", "NOW()");

        try {
            stmt.executeUpdate(sql);
        } catch (SQLException var11) {
            var11.printStackTrace();
        }

    }

    public void addNewFolder(String newFolder) {
        if (!this.pathIDExist(newFolder)) {
            String parent = this.determineParentFolder(newFolder);
            parent = parent.replace("/", "\\");
            String newFolderCode = this.determineNewFolderCode(parent);
            String sql = INSERT_FOLDER_SQL.replace("*!*", newFolderCode);
            sql = sql.replace("!*!", newFolder.replace("/", "\\").replace("'", "''"));

            try {
                stmt.executeUpdate(sql);
            } catch (SQLException var6) {
                var6.printStackTrace();
            }
        }

    }

    private String determineParentFolder(String currFolder) {
        if (currFolder.endsWith("/")) {
            currFolder = currFolder.substring(0, currFolder.length() - 2);
        }

        int lastIndex = currFolder.lastIndexOf("/");
        String parentFolder = currFolder.substring(0, lastIndex);
        return parentFolder;
    }

    private String determineNewFolderCode(String parent) {
        String newCode = "";
        String parentCode = "";
        ArrayList<String> theCodes = new ArrayList();
        String parentSql = PARENT_CODE_SQL.replace("*!*", parent.replace("'", "''"));

        try {
            this.rs = stmt.executeQuery(parentSql);
            if (this.rs.next()) {
                parentCode = this.rs.getString(1).trim();
            }
        } catch (SQLException var16) {
            var16.printStackTrace();
        }

        String childrenSql = CHILDREN_CODE_SQL.replace("*!*", parentCode);

        try {
            this.rs = stmt.executeQuery(childrenSql);

            while(this.rs.next()) {
                theCodes.add(this.rs.getString(1).trim());
            }
        } catch (SQLException var17) {
            var17.printStackTrace();
        }

        int baseLength = ((String)theCodes.get(0)).length();
        boolean foundIndex = false;
        int newIndex = 0;
        int prevIndex = 0;
        int currIndex = 0;
        Iterator var12 = theCodes.iterator();

        while(var12.hasNext()) {
            String code = (String)var12.next();
            if (code.length() > baseLength) {
                String testCode = code.substring(baseLength);
                int currNumber = Integer.parseInt(testCode);
                if (prevIndex == 0) {
                    prevIndex = currNumber;
                    currIndex = currNumber;
                } else {
                    currIndex = currNumber;
                    if (prevIndex + 1 == currNumber) {
                        prevIndex = currNumber;
                    } else if (!foundIndex) {
                        newIndex = prevIndex + 1;
                        foundIndex = true;
                    }
                }
            }
        }

        if (!foundIndex) {
            newIndex = currIndex + 1;
        }

        newCode = (String)theCodes.get(0) + ("0000" + newIndex).substring(newIndex.makeConcatWithConstants<invokedynamic>(newIndex).length());
        return newCode;
    }

    public boolean pathIDExist(String absPath) {
        absPath = absPath.replace("/", "\\");
        String codeSQL = PARENT_CODE_SQL.replace("*!*", absPath.replace("'", "''"));

        try {
            this.rs = stmt.executeQuery(codeSQL);
            return this.rs.next();
        } catch (SQLException var4) {
            var4.printStackTrace();
            return false;
        }
    }

    public ResultSet codeCategories() {
        new HashMap();
        new HashMap();

        try {
            this.rs = stmt.executeQuery(CATEGORIES_SQL);
        } catch (SQLException var7) {
            var7.printStackTrace();
        } finally {
            return this.rs;
        }
    }
}
