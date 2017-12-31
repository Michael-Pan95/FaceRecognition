package Database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Michael Pan (zpan1@andrew.cmu.edu);
 */
/*
 table1: STUDENT :          ID/NAME/GENDER/PROGRAM/Nationality/TIMES 
 table2: VISIT:                   VISITTIME/ID/REASON 
 table3:  Announcement:   UPLOADTIMEProgram/Announcement:
 table4:  Recog:                 ID/Img
 */
class DatabaseController {

    private final static String DRIVER_URL = "org.apache.derby.jdbc.ClientDriver";
    private final static String DB_URL = "jdbc:derby://localhost:1527/JavaGroupProject;create = true;";
    private final static String USER_NAME = "groupAdmin";
    private final String PASSWORD = "123456789";
    private Connection connection;
    private PreparedStatement pStatement;
    private Statement statement;
    private ResultSet resultSet;

    //build connection to the database. 
    private void databaseConnection() {
        //Load JDBC driver
        try {
            Class.forName(DRIVER_URL);
            System.out.println("Driver Loaded");
        } catch (ClassNotFoundException e) {
            System.out.println("Error in Class.forName.");
            e.getStackTrace();
        }

        //connect to a database
        try {
            connection = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);
            statement = connection.createStatement();
            System.out.println("Database Connected");
        } catch (SQLException e) {
            System.out.println("Connection Failed");
            e.getStackTrace();
        }
    }

    //close all the source
    private void databaseClose() {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            System.out.println("Fail To Close Database");
            e.getStackTrace();
        }

    }

    //data storage
    /**
     *
     * @param tableName
     * @param data
     * @return
     */
    public boolean dataStorage(String tableName, ArrayList data) {
        databaseConnection();
        //validation check
        if ((tableName.equals("STUDENT") && data.size() == 6)
                || (tableName.equals("VISIT") && data.size() == 3)
                || (tableName.equals("ANNOUNCEMENT") && data.size() == 2)
                || (tableName.equals("RECOG") && data.size() == 2)) {
        } else {
            //table name is invalid or data length is incompatible
            System.out.println("Invalid storage data");
            databaseClose();
            return false;
        }

        //if database is disconnected, do the connection
        if (statement == null) {
            databaseConnection();
        }

        try {
            if (tableName.equals("STUDENT")) {//table student
                pStatement = connection.prepareStatement("insert into STUDENT values(?,?,?,?,?,?)");
                pStatement.setString(1, (String) data.get(0)); //ID
                pStatement.setString(2, (String) data.get(1)); //Name
                pStatement.setString(3, (String) data.get(2)); //Gender
                pStatement.setString(4, (String) data.get(3));//Program
                pStatement.setString(5, (String) data.get(4));//Nationality
                pStatement.setInt(6, (Integer) data.get(5));//Times
                pStatement.executeUpdate();
            } else if (tableName.equals("VISIT")) {//table visit
                pStatement = connection.prepareStatement("insert into VISIT values(?,?,?)");
                pStatement.setTimestamp(1, (java.sql.Timestamp) data.get(0));//Visit Time
                pStatement.setString(2, (String) data.get(1));//ID
                pStatement.setString(3, (String) data.get(2));//Reason
                pStatement.executeUpdate();
            } else if (tableName.equals("ANNOUNCEMENT")) {//table announcement
                pStatement = connection.prepareStatement("Insert into Announcement values(?,?,?)");
                pStatement.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis())); //Database decides upload time
                pStatement.setString(2, (String) data.get(0));
                pStatement.setString(3, (String) data.get(1));//Annoucement
                pStatement.executeUpdate();
            } else if (tableName.equals("RECOG")) {//table recog
                pStatement = connection.prepareStatement("Insert into RECOG values(?,?)");
                pStatement.setString(1, (String) data.get(0));
                try {
                    //IMG process
                    File f = new File((String) data.get(1));
                    FileInputStream fis = new FileInputStream(f);
                    pStatement.setBinaryStream(2, fis, (int) f.length());
                } catch (FileNotFoundException e) {

                }
                pStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Data Storage Fail");
            databaseClose();
            return false;
        }
        //close database
        databaseClose();
        return true;
    }

    /**
     * check all table existence. If not, create one
     */
    public void checkTableExsistance() {
        databaseConnection();
        boolean exist1 = false;
        boolean exist2 = false;
        boolean exist3 = false;
        boolean exist4 = false;

        try (ResultSet rs1 = connection.getMetaData().getTables(null, null, "STUDENT", null);
                ResultSet rs2 = connection.getMetaData().getTables(null, null, "VISIT", null);
                ResultSet rs3 = connection.getMetaData().getTables(null, null, "ANNOUNCEMENT", null);
                ResultSet rs4 = connection.getMetaData().getTables(null, null, "RECOG", null);) {
            if (rs1.next()) {
                exist1 = true;
            }
            if (rs2.next()) {
                exist2 = true;
            }
            if (rs3.next()) {
                exist3 = true;
            }

            if (rs4.next()) {
                exist4 = true;
            }

            if (!exist1) {
                statement.executeUpdate("create table STUDENT(ID varchar(8) primary KEY,Name varchar(50),Gender varchar(7),Prgram varchar(50),Nationality varchar(50),Times Integer)");
            }

            if (!exist2) {
                statement.executeUpdate("create table VISIT(VisitTime Timestamp Primary KEY,ID varchar(8),Reason varchar(100))");
            }

            if (!exist3) {
                statement.executeUpdate("create table ANNOUNCEMENT(UPLOADTIME Timestamp primary KEY,Program varchar(50),Announcement varchar(30000))");
            }

            if (!exist4) {
                statement.executeUpdate("create table RECOG(ID varchar(8),IMG blob)");
            }

        } catch (Exception e) {
            System.out.println("Table Check Fail.");
        }

        //close database
        databaseClose();
    }

    /**
     * return data that has been retrieved.
     *
     * @param SQL
     * @param colnum
     * @param filePath only RECOG need this
     * @return
     */
    public ArrayList<String>[] retrieveData(String SQL, int colnum, String filePath) {
        databaseConnection();
        ArrayList[] result = Helper.ArrayListCreator(colnum);
        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL);
            //read img file is different from other data structures
            if (SQL.contains("RECOG")) {
                while (resultSet.next()) {
                    result[0].add(resultSet.getString("ID"));
                    result[1].add(filePath);
                    try {
                        File file = new File(filePath);
                        FileOutputStream output = new FileOutputStream(file);
                        InputStream input = resultSet.getBinaryStream("IMG");
                        int temp = 0;
                        while ((temp = input.read()) != -1) {
                            output.write(temp);
                        }
                    } catch (IOException | SQLException e) {

                    }
                }

            } else {

                while (resultSet.next()) {
                    for (int i = 0; i < colnum; i++) {
                        result[i].add(resultSet.getString(i + 1));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("retrieve data failed");
        }
        databaseClose();

        return result;
    }

    /**
     * we only need to modify table 1,2,3. Table 4 should not be changed
     *
     * @param tableName
     * @param set
     * @param where
     * @return
     */
    public boolean updateData(String tableName, String set, String where) {
        databaseConnection();
        try {
            statement = connection.createStatement();
            statement.executeUpdate("update " + tableName + " set " + set + " where " + where);
        } catch (SQLException e) {
            System.out.println("Update Data fail");
            databaseClose();
            return false;
        }
        databaseClose();
        return true;

    }

    /**
     * delete a data from table 1,2,3,4
     *
     * @param tableName
     * @param where
     * @return
     */
    public boolean deleteData(String tableName, String where) {
        databaseConnection();
        try {
            statement = connection.createStatement();
            statement.executeUpdate("Delete From " + tableName + " Where " + where);
        } catch (SQLException e) {
            System.out.println("delete data from" + tableName + "failed");
            databaseClose();
            return false;
        }
        databaseClose();
        return true;
    }

    public boolean reset() {
        databaseConnection();
        try {
            statement = connection.createStatement();
            statement.executeUpdate("drop Table STUDENT");
            statement.executeUpdate("drop Table VISIT");
            statement.executeUpdate("drop Table ANNOUNCEMENT");
            statement.executeUpdate("drop Table RECOG");
        } catch (SQLException e) {
            System.out.println("Fail to reset database");
            databaseClose();
            return false;
        }
        databaseClose();
        return true;
    }

}
