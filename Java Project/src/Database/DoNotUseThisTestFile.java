/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.util.ArrayList;

/**
 *
 * @author Michael Pan (zpan1@andrew.cmu.edu);
 */

/*
 table1: STUDENT :          ID/NAME/GENDER/PROGRAM/Nationality/TIMES 
 table2: VISIT:                   VISIT/TIME/ID/REASON 
 table3:  Announcement:   Program/Announcement:
 table4:  Recog:                 ID/Img
 */
public class DoNotUseThisTestFile {

    private DatabaseController db;

    //constructor
    public DoNotUseThisTestFile() {
        db = new DatabaseController();
    }

    public static void main() {
        DoNotUseThisTestFile main = new DoNotUseThisTestFile();
        main.resetTable();
        main.db.checkTableExsistance();
        main.addRowInSTUDENT("000002", "Mike", "Male", "MISM", "China");
        main.addRowInRECOG("000002", "C:\\Users\\Michael\\Desktop\\Mike.png");
        main.addRowInANNOUNCEMENT("MISM", "Haha");
        main.addRowInVISIT(new java.sql.Timestamp(System.currentTimeMillis()), "000002", "No reason. I'm Mike");

        System.out.println(main.studentGetTimes("000002"));
        main.studenAddTimes("000002");
        System.out.println(main.studentGetTimes("000002"));
        main.imgRetrieve("D:/mike.png");
        main.deleteStudent("000002");
    }

    /**
     * save new student
     *
     * @param ID
     * @param name
     * @param gender only be male or female
     * @param program
     * @param nationality
     * @return
     */
    public boolean addRowInSTUDENT(String ID, String name, String gender, String program, String nationality) {
        db.checkTableExsistance();
        ArrayList temp = new ArrayList();
        temp.add(ID);
        temp.add(name);
        temp.add(gender);
        temp.add(program);
        temp.add(nationality);
        temp.add(1);
        return db.dataStorage("STUDENT", temp);
    }

    /**
     *
     * save visit info
     *
     * @param ID
     * @param visitTime sql.TimeStamp !
     * @param reason reason to visit
     * @return save successfully or not
     */
    public boolean addRowInVISIT(java.sql.Timestamp visitTime, String ID, String reason) {
        ArrayList temp = new ArrayList();
        temp.add(visitTime);
        temp.add(ID);
        temp.add(reason);
        return db.dataStorage("VISIT", temp);
    }

    /**
     * save new announcement
     *
     * @param program
     * @param announcement
     * @return
     */
    public boolean addRowInANNOUNCEMENT(String program, String announcement) {
        ArrayList temp = new ArrayList();
        temp.add(program);
        temp.add(announcement);
        return db.dataStorage("ANNOUNCEMENT", temp);
    }

    /**
     * save new img data
     *
     * @param ID
     * @param filePath
     * @return
     */
    public boolean addRowInRECOG(String ID, String filePath) {
        // path validation
        if (!filePath.substring(filePath.length() - 4, filePath.length()).equals(".png")) {
            System.out.println("Wrong file path. Should be a path to png file");
            return false;
        }

        ArrayList temp = new ArrayList();
        temp.add(ID);
        temp.add(filePath);
        return db.dataStorage("RECOG", temp);
    }

    //get current number of times a student has visited
    /**
     *
     * @param ID
     * @return
     */
    public int studentGetTimes(String ID) {

        ArrayList<String>[] data = db.retrieveData("select TIMES from STUDENT where ID = '" + ID + "'", 1, null);
        if (data[0].get(0) == null) {
            return 0;
        } else {
            return Integer.parseInt(data[0].get(0));
        }
    }

    //add times to specific student
    public boolean studenAddTimes(String ID) {
        int currentTimes = studentGetTimes(ID);
        return db.updateData("STUDENT", "TIMES = " + (currentTimes + 1), "ID = '" + ID + "'");
    }

    //retrieve all img
    public boolean imgRetrieve(String filePath) {
        try {
            db.retrieveData("select * from RECOG", 2, filePath);
        } catch (Exception e) {
            System.out.println("IMG load fail! FATOL ERROR. Program terminated");
            System.exit(1);
        }
        return true;
    }

    //get student basic info
    public ArrayList<String> getStudentInfo(String ID) {
        ArrayList<String>[] temp = db.retrieveData("select * from STUDENT where ID = '" + ID + ";", 6, null);
        ArrayList<String> result = new ArrayList();
        for (int i = 0; i < temp.length; i++) {
            result.add(temp[i].get(0));
        }
        return result;
    }

    //get student visit reason and time
    public ArrayList<String> getStudentReasonTime(String ID) {
        ArrayList<String>[] temp = db.retrieveData("select ID,NAME,REASON,TIME from STUDENT,VISIT where ID = '" + ID + ";", 4, null);
        ArrayList<String> result = new ArrayList();
        for (int i = 0; i < temp.length; i++) {
            result.add(temp[i].get(0));
        }
        return result;
    }

    //get specific announcement
    public ArrayList<String> getAnnouncement(java.sql.Timestamp uploadTime) {
        ArrayList<String>[] temp = db.retrieveData("select * from ANNOUNCEMENT where ID = '" + uploadTime + "'", 2, null);
        ArrayList<String> result = new ArrayList();
        for (int i = 0; i < temp.length; i++) {
            result.add(temp[i].get(0));
        }
        return result;
    }

    //delete specific announcement
    public boolean deleteAnnouncement(java.sql.Timestamp uploadTime) {
        return db.deleteData("ANNOUNCEMENT", "ID = '" + uploadTime + "'");
    }

    //delete drop a student
    public boolean deleteStudent(String ID) {
        return db.deleteData("STUDENT", "ID = '" + ID + "'") && db.deleteData("VISIT", "ID = '" + ID + "'") && db.deleteData("RECOG", "ID = '" + ID + "'");
    }

    private boolean resetTable() {
        return db.reset();
    }

}
