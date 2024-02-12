package com.daisydata.codescans.codeuploadsfx;

import java.sql.*;
import java.util.*;

import static java.lang.String.valueOf;
//import static com.sun.tools.javac.jvm.ByteCodes.invokedynamic;

public class DatabaseConnection {
    static final String JDBC_DRIVER = "com.pervasive.jdbc.v2.Driver";
    static final String DB_URL = "jdbc:pervasive://GSS1/GLOBALDDD";
    static final String USER = "Master";
    static final String PASS = "master";
    //All private Static strings are used to commit SQL Queries, some are updates, some are data getters for grabbing the number and name associated with the entered item number
    private static final String ORDER_HEADER_SQL = "select a.CUSTOMER, b.NAME_CUSTOMER, a.ORDER_NO from (select ORDER_NO, CUSTOMER from ((select ORDER_NO, CUSTOMER from V_ORDER_HEADER WHERE ORDER_NO = '*!*') UNION ALL (select ORDER_NO, CUSTOMER from V_ORDER_HIST_HEAD WHERE ORDER_NO = '*!*')) c) a inner join V_CUSTOMER_MASTER b on a.CUSTOMER = b.CUSTOMER";
    private static final String PO_HEADER_SQL = "select a.VENDOR, b.NAME_VENDOR, a.PURCHASE_ORDER as PO_NUM from (select PURCHASE_ORDER, VENDOR from ((select PURCHASE_ORDER, VENDOR from V_PO_HEADER WHERE PURCHASE_ORDER = '*!*') UNION ALL (select PURCHASE_ORDER, VENDOR from V_PO_H_HEADER WHERE PURCHASE_ORDER = '*!*')) c) a inner join V_VENDOR_MASTER b on a.VENDOR = b.VENDOR ";
    //private static String RMA_HEADER_SQL = "SELECT CUSTOMER, NAME_CUSTOMER, RMA_ID, ORDER_NO FROM V_RMA_HIST_HEADER WHERE RMA_ID = '*!*' UNION ALL SELECT CUSTOMER, NAME_CUSTOMER, RMA_ID, ORDER_NO FROM V_RMA_HEADER WHERE RMA_ID = '*!*'";
    private static final String RMA_HEADER_SQL = "SELECT CUSTOMER, NAME_CUSTOMER, ORDER_NO FROM V_ORDER_HIST_HEAD WHERE order_no = '*!*' UNION ALL SELECT a.CUSTOMER, b.NAME_CUSTOMER, a.ORDER_NO FROM V_order_HEADER as a left join v_customer_master as b on a.customer = b.customer WHERE a.order_no = '*!*'";
    private static final String VENDOR_MASTER_SQL = "SELECT VENDOR, NAME_VENDOR FROM V_VENDOR_MASTER WHERE VENDOR = '*!*'";
    private static final String PATH_ID_SQL = "SELECT PATH_ID from D3_DMS_INDEX where ABS_PATH = '*!*'";
    private static final String FIND_REQUISITION_SQL = "SELECT PURCHASE_ORDER from V_PO_LINES where REQUISITION_NO = '*!*'";
    static private final String PARENT_CODE_SQL = "SELECT PATH_ID from D3_DMS_INDEX where ABS_PATH = '*!*'";
    private static final String CHILDREN_CODE_SQL = "SELECT PATH_ID from D3_DMS_INDEX where PATH_ID like '*!*____' ORDER BY PATH_ID ASC";
    private static final String INSERT_FOLDER_SQL = "INSERT INTO D3_DMS_INDEX (PATH_ID, ABS_PATH) VALUES ('*!*','!*!')";
    private static final String INSERT_DOC_SQL = "INSERT INTO D3_DMS_DOCS (PATH_ID, DOC_NAME, ITEM_NUM, ITEM_TYPE, DOC_TYPE, LAST_CHG_BY, LAST_CHANGE) VALUES ('*!*','*!!*','*!!!*','*!!!!*','*!!!!!*','JAVA_FX',now())";
    private static final String FIND_RECEIVER_SQL = "SELECT RECEIVER_NO, PURCHASE_ORDER, PO_LINE, DATE_RECEIVED, PART, PACK_LIST, EXTENDED_COST, QTY_RECEIVED FROM V_PO_RECEIVER where PURCHASE_ORDER = '*!*'";
    private static final String CATEGORIES_SQL = "SELECT * FROM D3_DMS_CATEGORIES WHERE ACTIVE = 1";
    private static final String OVERRIDE = "SELECT OVERRIDE FROM D3_DMS_CATEGORIES WHERE CATEGORY_ID = '*!*' AND SUBCATEGORY_ID '*!!*'";
    private static final String NCMR_SQL = "SELECT VENDOR, NAME, CUSTOMER from V_QUALITY WHERE CONTROL_NUMBER = '*!*'";
    private static final String CUSTOMER_SQL = "SELECT CUSTOMER, NAME_CUSTOMER FROM V_CUSTOMER_MASTER WHERE NAME_CUSTOMER != '' and CUSTOMER = '*!*'";
    private static final String WO_SQL = "SELECT JOB, PART FROM JOB_HEADER WHERE JOB = *!*";
    private static final String WO2_SQL = "Select b.JOB, b.SUFFIX, b.ORDER_NO from V_ORDER_TO_WO as b left join V_ORDER_HEADER as c on b.ORDER_NO = c.ORDER_NO where job = '*!*' order by b.JOB desc, b.SUFFIX, c.ORDER_NO desc";

    private static Connection conn;
    private static Statement stmt = null;
    private ResultSet rs = null;


    public static void main(String[] args) {
    }

    public DatabaseConnection() {
        try {
            Class.forName("com.pervasive.jdbc.v2.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt =  conn.createStatement();
        } catch (ClassNotFoundException | SQLException | NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void checkAndReopenConnection() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.pervasive.jdbc.v2.Driver");
                conn = DriverManager.getConnection(DB_URL, USER, PASS);
                stmt = conn.createStatement();
                System.out.println("Connection reopened successfully.");
            }
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    public void deconstruct() {
        if (this.rs != null) {
            try {
                this.rs.close();
                System.out.println("ResultSet Closed");
                stmt.close();
                System.out.println("Statement Closed");
                conn.close();
                System.out.println("Connection Closed");
            } catch (SQLException e) {
                System.out.println("Attempted to close connection and encountered an error");
                e.printStackTrace();
            }
        }
    }

    public void closeQuery() {
        try {
            rs.close();
            System.out.println("ResultSet Closed");
        } catch (SQLException e) {
            System.out.println("Attempted to close query and encountered an error");
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
        } finally {
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
//                result = gui.receiverPicker(this.rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String[] findFolderName(String docType, String itemNumber, boolean isWO) {
        checkAndReopenConnection();
        String sql = "";
        String num = "";
        String name = "";
        String itemCat = "";
        String[] result = new String[5];


        if ( isWO ) {
            if (docType.equalsIgnoreCase("so")) {
                console("Changed itemCat to so");
                itemCat = "so";
            } else if (docType.equalsIgnoreCase("rma")) {
                console("Changed itemCat to rma");
                itemCat = "rma";
            }
            docType = "workorder";
            System.out.println("DOCTYPE IS a workorder");
        }

        //Insert proper item number into respective SQL query
        switch (docType.toLowerCase()) {
            case "rma" -> {
                sql = RMA_HEADER_SQL.replace("*!*", itemNumber);
                System.out.println("running rma sql");
            }
            case "po" -> {
                System.out.println("running po sql");
                sql = PO_HEADER_SQL.replace("*!*", itemNumber);
            }
            case "cust" -> {
                System.out.println("running cust sql");
                sql = CUSTOMER_SQL.replace("*!*", itemNumber);
            }
            case "vend" -> {
                System.out.println("running vend sql");
                sql = VENDOR_MASTER_SQL.replace("*!*", itemNumber);
            }
            case "so" -> {
                System.out.println("running order header sql");
                sql = ORDER_HEADER_SQL.replace("*!*", itemNumber);
            }
            case "ncmr" -> {
                System.out.println("running ncmr sql");
                sql = NCMR_SQL.replace("*!*", itemNumber);
            }
            case "req" -> {
                System.out.println("running req sql");
                sql = PO_HEADER_SQL.replace("*!*", findReqPo(itemNumber));
            }
            case "workorder" -> {
                sql = WO2_SQL.replace("*!*", itemNumber.substring(0, 6));
                System.out.println("RUNNING WO2: " + sql);
            }
            case "wo" -> {
                System.out.println("running wo sql");
                sql = WO_SQL.replace("*!*", itemNumber);
            }
        }

        try {
            //Attempt to execute query, returning a number and name associated with the respective object type
            if (docType.equalsIgnoreCase("workorder")) {
                this.rs = stmt.executeQuery(sql);

                if (this.rs.next()) {
                    result[0] = this.rs.getString(1).trim(); //job
                    console("Result [0]: " + result[0]);
                    if (!this.rs.getString(2).trim().equals("")) {
                        result[1] = "000";
                    } else {
                        result[1] = this.rs.getString(2).trim(); //suffix
                    }
                    console("Result [1]: " + result[1]);
                    result[2] = this.rs.getString(3).trim(); //order number
                    console("Result [2]: " + result[2]);
                    result[3] = ""; // Customer Number
                    result[4] = ""; // Customer Name

                    String additionalSql = ORDER_HEADER_SQL.replace("*!*", result[2]);
                    PreparedStatement additionalStmt = conn.prepareStatement(additionalSql);

                    ResultSet additionalRs = additionalStmt.executeQuery();
                    if (additionalRs.next()) {
                        result[3] = additionalRs.getString(1);
                        result[4] = additionalRs.getString(2);
                    }
                    additionalRs.close();
                    additionalStmt.close();
                    console("results: 0: " + result[0] + "| 1: " + result[1] + "| 2: " + result[2] + "| 3: " + result[3] + "| 4: " + result[4]);


                }
            } else {
                this.rs = stmt.executeQuery(sql);
                while (this.rs.next()) {
                    num = this.rs.getString(1).trim();
                    name = this.rs.getString(2).trim();
                    console("Number: "+num);
                    console("Name: "+name);
                    result[0] = name;
                    result[1] = num;
                }
                if (!this.rs.next()){
                    this.rs = stmt.executeQuery(ORDER_HEADER_SQL.replace("*!*", itemNumber));
                    if (this.rs.next()) {
                        console("Made it here");
                        num = this.rs.getString(1).trim();
                        name = this.rs.getString(2).trim();
                        console("Number: "+num);
                        console("Name: "+name);
                        result[0] = name;
                        result[1] = num;
                    }
                }
            }
            this.rs.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }

    public String createPathID(String fullPath) {
        console("createPathID was called.");
        String pathID = null;
        //Check if path exists
        try {
            stmt.executeQuery(PATH_ID_SQL.replace("*!*", fullPath));
            if (this.rs.next()) {
                //Return the path_id of the existing record
                pathID = rs.getString("PATH_ID");
                console("PATH_ID: "+pathID);
            } else {
                console("Path ID does not exist - creating new");
                //Split path into parts, then rebuild
                String[] rawPathParts = fullPath.split("\\\\");
                String[] pathParts = Arrays.copyOfRange(rawPathParts,2,rawPathParts.length-1);
                //ArrayList<String> pathPartsLess = new ArrayList<String>(pathParts.length - 2);
                //String nextParent = null;
                //Try next parent directory for matches in Path_ID
                //Collections.addAll(pathPartsLess, pathParts);
                //join the array into a string with slashes
                //nextParent = String.join("\\", pathPartsLess);
                //Get path ID of parent directory
                //stmt.executeQuery(PATH_ID_SQL.replace("*!*", nextParent));
                //Determine how many directories do *NOT* exist
                int[] pathIDIdentifier = {4,8,12,16};
                //int parentsMissing = (pathParts.length-pathPartsLess.toArray().length);
                String newPathID = "";
                String incPath = "";
                //Gather MAX existing path_id parts for all missing/non-existent parts of the path_id
                int pathCounter = 0;
                for (int i = 0;i<(pathParts.length);i++){

                    console("NEXT PARENT: "+pathParts[i]);
                    //Incrementally build the path to test the MAX current ID
                    if (i==0) {
                        incPath += "\\\\"+pathParts[i];
                    } else {
                        incPath += "\\"+pathParts[i];
                    }
                    String retrieveMaxID = "select substring(PATH_ID,"+3+","+pathIDIdentifier[pathCounter]+") as 'MAX_ID' from D3_DMS_INDEX where ABS_PATH like '%"+incPath+"%' ORDER BY 'MAX_ID' DESC";
                    console(retrieveMaxID);
                    this.rs = stmt.executeQuery(retrieveMaxID);
                    if (!rs.next()) {
                                console("Adding new folder for parent directory "+pathParts[i]);
                        addNewFolder(incPath);
                        console("Added new folder for parent directory "+pathParts[i]);
                    }
                    if (this.rs.getString(1) != null) {
                        console("Adding String to Path");
                        newPathID+=this.rs.getString(1);
                    }
                    console("New Path ID: "+newPathID);
                    if (pathCounter<3) {
                        pathCounter++;
                    }
                }

                pathID = newPathID;
                console("FINAL PATH_ID: "+pathID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (pathID == null) {
            console("No matches for any part of PATH_ID in D3_DMS_INDEX");
        }
        return pathID;
    }

    private String determineNewFolderCode(String parent) {
        String newCode = "";
        String parentCode = "";
        ArrayList<String> theCodes = new ArrayList<>();
        ResultSet rs = null;
        console("Start determineNewFolderCode");
        String parentSql = PARENT_CODE_SQL.replace("*!*", parent.replace("'", "''"));
        console("Set Parent Code");
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
        console("Returning CHILDREN_CODE_SQL");
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
        console("Current Folder is "+currFolder+" for determining Parent Folder");
        int lastIndex = currFolder.lastIndexOf("\\");
        console("Set ParentFolder");
        String parentFolder = currFolder.substring(0, lastIndex);
        console("Return Parent Folder");
        return parentFolder;
    }

    public void addNewDocument(String directory, String docName, String itemNumber, String itemType, String docType) {
        String pathID = "";
        directory = directory.replace("/", "\\");
        String codeSQL = PARENT_CODE_SQL.replace("*!*", directory);
        ResultSet rs = null;
        Statement stmt = null;
        boolean alreadyExists = false;
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
        String justDocName = docName.replace("\\", "/"); // Replacing backslashes with forward slashes
        String fileName = justDocName.substring(justDocName.lastIndexOf("/") + 1); // Extracting the file name from the path
        justDocName = fileName.replace("/", "\\"); // Replacing forward slashes with backslashes

        System.out.println("JUSTDOCNAME: " + justDocName);
        String sql = INSERT_DOC_SQL;
        sql = sql.replace("*!*", pathID);
        console(pathID);
        sql = sql.replace("*!!*", fileName);
        sql = sql.replace("*!!!*", itemNumber);
        sql = sql.replace("*!!!!*", itemType);
        sql = sql.replace("*!!!!!*", docType);
        sql = sql.replace("*!!!!!!!*", "NOW()");
        console("SQL to add Document:"+sql);
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
                console("Running add folder SQL");
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
            console("Running pathIDExist");
            stmt = conn.createStatement();
            rs = stmt.executeQuery(codeSQL);
            if (rs.next()) {
                console("Path Exists");
                return true;
            } else {
                console("Path does not exist");
                return false;
            }
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return false;
    }

    public void getCodeCategories() {
        boolean success = true;
        System.out.println("Getting Category & Sub Category Variables");
        HashMap<String, ArrayList<String>> categoryNames = new HashMap<>();
        HashMap<String, ArrayList<String>> categoryIDs = new HashMap<>();
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
                String categoryName = valueOf(rs.getString("CATEGORY_NAME")).trim();
                String categoryID = valueOf(rs.getString("CATEGORY_ID")).trim();
                String subCategoryName = valueOf(rs.getString("SUBCATEGORY_NAME")).trim();
                String subCategoryID = valueOf(rs.getString("SUBCATEGORY_ID")).trim();
                String categoryPath = valueOf(rs.getString("CATEGORY_PATH")).trim();
                String subCategoryPath = valueOf(rs.getString("SUBCATEGORY_PATH")).trim();
                String overridePath = valueOf(rs.getString("OVERRIDE")).trim();
                int priority = rs.getInt("PRIORITY");
                if (categoryNames.containsKey(categoryName)) {
                    //if category already exists, then add subcategory to that key
                    categoryNames.get(categoryName).add(subCategoryName);
                    categoryIDs.get(categoryID).add(subCategoryID);
                } else {
                    index.put(categoryName,categoryID);
                    index.put(categoryID,categoryName);
                    //if category does not exist, create it and add the current subcategory
                    ArrayList<String> subNameList = new ArrayList<>();
                    subNameList.add(subCategoryName);
                    categoryNames.put(categoryName,subNameList);
                    ArrayList<String> subIDList = new ArrayList<>();
                    subIDList.add(subCategoryID);
                    categoryIDs.put(categoryID,subIDList);
                    directory.put(categoryID,categoryPath);
                }
                index.put(subCategoryName, subCategoryID);
                index.put(subCategoryID, subCategoryName);
//                Removed because subCategoryPath was overwriting categoryPath in categories[4] to null ???
//                if(overridePath.length() > 0) {
//                    directory.put(subCategoryID,overridePath);
//                } else if(subCategoryPath.length() > 0) {
//                    directory.put(subCategoryID,(subCategoryPath).replace("///","/"));
//                }
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
            System.out.println("Data retrieved = "+success);
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