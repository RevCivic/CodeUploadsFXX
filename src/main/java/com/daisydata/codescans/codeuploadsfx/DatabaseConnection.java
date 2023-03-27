package com.daisydata.codescans.codeuploadsfx;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

import static java.lang.String.valueOf;

public class DatabaseConnection {
    static final String JDBC_DRIVER = "com.pervasive.jdbc.v2.Driver";
    static final String DB_URL = "jdbc:pervasive://GSS1/GLOBALDDD";
    static final String USER = "Master";
    static final String PASS = "master";
//    All private Static strings are used to commit SQL Queries, some are updates, some are data getters for grabbing the number and name associated with the entered item number
    private static String ORDER_HEADER_SQL = "select a.CUSTOMER, b.NAME_CUSTOMER, a.ORDER_NO from (select ORDER_NO, CUSTOMER from ((select ORDER_NO, CUSTOMER from V_ORDER_HEADER WHERE ORDER_NO = '*!*') UNION ALL (select ORDER_NO, CUSTOMER from V_ORDER_HIST_HEAD WHERE ORDER_NO = '*!*')) c) a inner join V_CUSTOMER_MASTER b on a.CUSTOMER = b.CUSTOMER";
    private static String PO_HEADER_SQL = "select a.VENDOR, b.NAME_VENDOR, a.PURCHASE_ORDER as PO_NUM from (select PURCHASE_ORDER, VENDOR from ((select PURCHASE_ORDER, VENDOR from V_PO_HEADER WHERE PURCHASE_ORDER = '*!*') UNION ALL (select PURCHASE_ORDER, VENDOR from V_PO_H_HEADER WHERE PURCHASE_ORDER = '*!*')) c) a inner join V_VENDOR_MASTER b on a.VENDOR = b.VENDOR ";
    private static String RMA_HEADER_SQL = "SELECT CUSTOMER, NAME_CUSTOMER, RMA_ID, ORDER_NO FROM V_RMA_HIST_HEADER WHERE RMA_ID = '*!*' UNION ALL SELECT CUSTOMER, NAME_CUSTOMER, RMA_ID, ORDER_NO FROM V_RMA_HEADER WHERE RMA_ID = '*!*'";
    private static String VENDOR_MASTER_SQL = "SELECT VENDOR, NAME_VENDOR FROM V_VENDOR_MASTER WHERE VENDOR = '*!*'";
    private static String PATH_ID_SQL = "SELECT PATH_ID from D3_DMS_INDEX where ABS_PATH = '*!*'";
    private static String FIND_REQUISITION_SQL = "SELECT PURCHASE_ORDER from V_PO_LINES where REQUISITION_NO = '*!*'";
    static private String PARENT_CODE_SQL = "SELECT PATH_ID from D3_DMS_INDEX where ABS_PATH = '*!*'";
    private static String CHILDREN_CODE_SQL = "SELECT PATH_ID from D3_DMS_INDEX where PATH_ID like '*!*____' ORDER BY PATH_ID ASC";
    private static String INSERT_FOLDER_SQL = "INSERT INTO D3_DMS_INDEX (PATH_ID, ABS_PATH) VALUES ('*!*','!*!')";
    private static String INSERT_DOC_SQL = "INSERT INTO D3_DMS_DOCS (PATH_ID, DOC_NAME, ITEM_NUM, ITEM_TYPE, DOC_TYPE, LAST_CHG_BY, LAST_CHANGE) VALUES ('*!*','*!!*','*!!!*','*!!!!*','*!!!!!*','JAVA_FX',now())";
    private static String FIND_RECEIVER_SQL = "SELECT RECEIVER_NO, PURCHASE_ORDER, PO_LINE, DATE_RECEIVED, PART, PACK_LIST, EXTENDED_COST, QTY_RECEIVED FROM V_PO_RECEIVER where PURCHASE_ORDER = '*!*'";
    private static String CATEGORIES_SQL = "SELECT * FROM D3_DMS_CATEGORIES WHERE ACTIVE = 1";
    private static String OVERRIDE = "SELECT OVERRIDE FROM D3_DMS_CATEGORIES WHERE CATEGORY_ID = '*!*' AND SUBCATEGORY_ID '*!!*'";
    private static String NCMR_SQL = "SELECT VENDOR, NAME, CUSTOMER from V_QUALITY WHERE CONTROL_NUMBER = '*!*'";
    private static String CUSTOMER_SQL = "SELECT CUSTOMER, NAME_CUSTOMER FROM V_CUSTOMER_MASTER WHERE NAME_CUSTOMER != '' and CUSTOMER = *!*";
    private static Connection conn;
    private static Statement stmt = null;
    private ResultSet rs = null;

    public static void main(String[] args) {
    }

    public DatabaseConnection() {
        try {
            Class.forName("com.pervasive.jdbc.v2.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void deconstruct() {
        if (this.rs != null) {
            try {
                this.rs.close();
//                System.out.println("ResultSet Closed");
                stmt.close();
//                System.out.println("Statement Closed");
                conn.close();
//                System.out.println("Connection Closed");
            } catch (SQLException e) {
//                System.out.println("Attempted to close connection and encountered an error");
                e.printStackTrace();
            }
        }
    }

    public void closeQuery() {
        try {
            rs.close();
//            System.out.println("ResultSet Closed");
        } catch (SQLException e) {
//            System.out.println("Attempted to close query and encountered an error");
            e.printStackTrace();
        }
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
        } catch (SQLException e) {
            e.printStackTrace();
            return result;
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String[] findFolderName(String docType, String itemNumber) {
//        console("findFolderName");
        String sql = "";
        String num = "";
        String name = "";
        String[] result = new String[2];
//        Insert proper item number into respective SQL query
        switch (docType.toLowerCase()) {
            case "rma" :
                sql = RMA_HEADER_SQL.replace("*!*", itemNumber);
                break;
            case "po" :
                sql = PO_HEADER_SQL.replace("*!*", itemNumber);
                break;
            case "cust" :
                sql = CUSTOMER_SQL.replace("*!*", itemNumber);
                break;
            case "vend" :
                sql = VENDOR_MASTER_SQL.replace("*!*", itemNumber);
                break;
            case "so" :
                sql = ORDER_HEADER_SQL.replace("*!*", itemNumber);
                break;
            case "ncmr" :
                sql = NCMR_SQL.replace("*!*",itemNumber);
                break;
            case "req" :
                sql = PO_HEADER_SQL.replace("*!*", findReqPo(itemNumber));
                break;
        }

        try {
//            Attempt to execute query, returning a number and name associated with the respective object type
            this.rs = stmt.executeQuery(sql);
            if (this.rs.next()) {
                num = this.rs.getString(1).trim();
                name = this.rs.getString(2).trim();
//                console("Number: "+num);
//                console("Name: "+name);
                result[0] = name;
                result[1] = num;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String createPathID(String fullPath) {
//        console("createPathID was called.");
        String pathID = null;
//        Check if path exists
        try {
            this.rs = stmt.executeQuery(PATH_ID_SQL.replace("*!*", fullPath));
            if (this.rs.next()) {
//                Return the path_id of the existing record
                 pathID = rs.getString("PATH_ID");
//                console("PATH_ID: "+pathID);
            } else {
//                console("Path ID does not exist - creating new");
//                Split path into parts, then rebuild
                String[] rawPathParts = fullPath.split("\\\\");
                String[] pathParts = Arrays.copyOfRange(rawPathParts,2,rawPathParts.length-1);
//                Parts of the PATH_ID are broken into sections of [2][4][4][4][4]
                int[] pathIDIdentifier = {4,8,12,16};
                String newPathID = "";
                String incPath = "";
//                Gather MAX existing path_id parts for all missing/non-existent parts of the path_id
                int pathCounter = 0;
                for (int i = 0;i<(pathParts.length);i++){

//                    console("NEXT PARENT: "+pathParts[i]);
//                    Incrementally build the path to test the MAX current ID
                    if (i==0) {
                        incPath += "\\\\"+pathParts[i];
                    } else {
                        incPath += "\\"+pathParts[i];
                    }
                    String retrieveMaxID = "select substring(PATH_ID,"+3+","+pathIDIdentifier[pathCounter]+") as 'MAX_ID' from D3_DMS_INDEX where ABS_PATH like '%"+incPath+"%' ORDER BY 'MAX_ID' DESC";
                    console(retrieveMaxID);
                    this.rs = this.stmt.executeQuery(retrieveMaxID);
                    if (!rs.next()) {
//                        console("Adding new folder for parent directory "+pathParts[i]);
                        addNewFolder(incPath);
//                        console("Added new folder for parent directory "+pathParts[i]);
                    }
                    if (this.rs.getString(1) != null) {
//                        console("Adding String to Path");
                        newPathID+=this.rs.getString(1);
                    }
//                    console("New Path ID: "+newPathID);
                    if (pathCounter<3) {
                        pathCounter++;
                    }
                }

                pathID = newPathID;
//                console("FINAL PATH_ID: "+pathID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (pathID == null) {
//            console("No matches for any part of PATH_ID in D3_DMS_INDEX");
        }
        return pathID;
    }

    private String determineNewFolderCode(String parent) {
        String newCode = "";
        String parentCode = "";
        ArrayList<String> theCodes = new ArrayList<String>();
        ResultSet rs = null;
//        console("Start determineNewFolderCode");
        String parentSql = PARENT_CODE_SQL.replace("*!*", parent.replace("'", "''"));
//        console("Set Parent Code");
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(parentSql);
            if (rs.next()) {
                parentCode = rs.getString(1).trim();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String childrenSql = CHILDREN_CODE_SQL.replace("*!*", parentCode);
//        console("Returning CHILDREN_CODE_SQL");
        try {
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery(childrenSql);
            while (rs.next()) {
                theCodes.add(rs.getString(1).trim());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        int baseLength = theCodes.get(0).length();
        boolean foundIndex = false;
        int newIndex = 0;
        int prevIndex = 0;
        int currIndex = 0;
        for (String code : theCodes) {
            if (code.length() > baseLength) {
                String testCode = code.substring(baseLength);
                int currNumber = Integer.parseInt(testCode);
                if (prevIndex == 0) {
                    prevIndex = currNumber;
                    currIndex = currNumber;
                } else {
                    currIndex = currNumber;
                    if ((prevIndex + 1) == currIndex) {
                        prevIndex = currIndex;
                    } else {
                        if (!foundIndex) {
                            newIndex = prevIndex + 1;
                            foundIndex = true;
                        }
                    }
                }
            }
        }
        if (!foundIndex) {
            newIndex = currIndex + 1;
        }
        newCode = theCodes.get(0) + ("0000" + newIndex).substring(("" + newIndex).length());
        return newCode;
    }

    private String determineParentFolder(String currFolder) {
        if (currFolder.endsWith("/")) {
            currFolder = currFolder.substring(0, currFolder.length() - 2);
        }
//        console("Current Folder is "+currFolder+" for determining Parent Folder");
        int lastIndex = currFolder.lastIndexOf("\\");
//        console("Set ParentFolder");
        String parentFolder = currFolder.substring(0, lastIndex);
//        console("Return Parent Folder");
        return parentFolder;
    }

    public void addNewDocument(String directory, String docName, String itemNumber, String itemType, String docType) {
        String pathID = "";
        directory = directory.replace("/", "\\");
        String codeSQL = PARENT_CODE_SQL.replace("*!*", directory);
        ResultSet rs = null;
        Statement stmt = null;
		Boolean alreadyExists = false;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(codeSQL);
            console("Directory:"+codeSQL);
            if (rs.next()) {
                console("SQL Returned an entry for "+directory);
                pathID = rs.getString(1).trim();
                alreadyExists = true;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (!alreadyExists) {
            createPathID(docName);
        }
        String justDocName = docName.substring(docName.lastIndexOf('\\')  + 1).replace("\\", "");
//        console("DocNameSplit:" + docName.substring(docName.lastIndexOf('\\')  + 1).replace("\\", ""));
        String sql = INSERT_DOC_SQL;
        sql = sql.replace("*!*", pathID);
        console("pathID: " + pathID);
        sql = sql.replace("*!!*", justDocName);
        sql = sql.replace("*!!!*", itemNumber);
        sql = sql.replace("*!!!!*", itemType);
        sql = sql.replace("*!!!!!*", docType);
        if(System.getProperty("user.name") != null){
            sql = sql.replace("JAVA_FX", System.getProperty("user.name"));
        }
//        console("SQL to add Document:"+sql);
        try {
            stmt = conn.createStatement();
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewFolder(String newFolder) {
        if (!pathIDExist(newFolder)) {
            String parent = determineParentFolder(newFolder);
            parent = parent.replace("/", "\\");
            String newFolderCode = determineNewFolderCode(parent);
            String sql = INSERT_FOLDER_SQL.replace("*!*", newFolderCode);
            sql = sql.replace("!*!", newFolder.replace("/", "\\").replace("'", "''"));
            try {
//                console("Running add folder SQL");
                Statement stmt = conn.createStatement();
                stmt.executeUpdate(sql);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean pathIDExist(String absPath) {
        absPath = absPath.replace("/", "\\");
        String codeSQL = PARENT_CODE_SQL.replace("*!*", absPath.replace("'", "''"));
        ResultSet rs = null;
        Statement stmt = null;
        try {
            stmt = conn.createStatement();
            rs = stmt.executeQuery(codeSQL);
            if (rs.next()) {
//                console("Path Exists");
                return true;
            } else {
//                console("Path does not exist");
                return false;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    public void getCodeCategories() {
        Boolean success = true;
//        System.out.println("Getting Category & Sub Category Variables");
        HashMap<String,ArrayList> categoryNames = new HashMap<>();
        HashMap<String,ArrayList> categoryIDs = new HashMap<>();
        HashMap<String, Integer> categorySortOrder = new HashMap<>();
        HashMap<String, String> index = new HashMap<>();
        HashMap<String, String> directory = new HashMap<>();
        // Categories are mapped as follows
        // Names { CATEGORY_NAME : [SUBCATEGORY_NAMES] }
        // IDs { CATEGORY_ID : [SUBCATEGORY_IDS] }
        // Priority/SortOrder { SUBCATEGORY_NAME : PRIORITY }
        // Index {NAME : ID}
        // Directories {NAME : CATEGORY_PATH+SUBCATEGORY_PATH
        try {
            rs = stmt.executeQuery(CATEGORIES_SQL);
            while (rs.next()) {
                String categoryName = new String(valueOf(rs.getString("CATEGORY_NAME")).trim());
                String categoryID = new String(valueOf(rs.getString("CATEGORY_ID")).trim());
                String subCategoryName = new String(valueOf(rs.getString("SUBCATEGORY_NAME")).trim());
                String subCategoryID = new String(valueOf(rs.getString("SUBCATEGORY_ID")).trim());
                String categoryPath = new String(valueOf(rs.getString("CATEGORY_PATH")).trim());
                String subCategoryPath = new String(valueOf(rs.getString("SUBCATEGORY_PATH")).trim());
                String overridePath = new String(valueOf(rs.getString("OVERRIDE")).trim());
                int priority = rs.getInt("PRIORITY");
                if (categoryNames.containsKey(categoryName)) {
                    //if category already exists, then add subcategory to that key
                    categoryNames.get(categoryName).add(subCategoryName);
                    categoryIDs.get(categoryID).add(subCategoryID);
                } else {
                    index.put(categoryName,categoryID);
                    index.put(categoryID,categoryName);
                    //if category does not exist, create it and add the current subcategory
                    ArrayList subNameList = new ArrayList();
                    subNameList.add(subCategoryName);
                    categoryNames.put(categoryName,subNameList);
                    ArrayList subIDList = new ArrayList();
                    subIDList.add(subCategoryID);
                    categoryIDs.put(categoryID,subIDList);
                    directory.put(categoryID,categoryPath);
                }
                index.put(subCategoryName, subCategoryID);
                index.put(subCategoryID, subCategoryName);
                categorySortOrder.put(subCategoryName,priority);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            success = false;
        } finally {
            CodeScansController.categories[0] = categoryNames;
            CodeScansController.categories[1] = categoryIDs;
            CodeScansController.categories[2] = categorySortOrder;
            CodeScansController.categories[3] = index;
            CodeScansController.categories[4] = directory;
//            System.out.println("Data retrieved = "+success);
            closeQuery();
        }
    }

    public String destinationOverride(String categoryID, String subCategoryID) {
        String query = OVERRIDE.replace("*!*",categoryID).replace("*!!*",subCategoryID);
        try {
            rs = stmt.executeQuery(query);
            while (rs.next()) {
                if(rs.getString("OVERRIDE") != null) {
                    return rs.getString("OVERRIDE");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }

    private static void console(String msg) {
        System.out.println(msg);
    }
}
