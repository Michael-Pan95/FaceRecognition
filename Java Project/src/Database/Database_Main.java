/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Database;

import java.util.ArrayList;

/**
 * database interfaces
 *
 * @author Michael Pan (zpan1@andrew.cmu.edu);
 */
public class Database_Main {

    private DatabaseController db;

    public Database_Main() {
        db = new DatabaseController();
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
    public boolean addRowInVISIT(String ID, String reason) {
        db.checkTableExsistance();
        ArrayList temp = new ArrayList();
        temp.add(new java.sql.Timestamp(System.currentTimeMillis()));
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
        db.checkTableExsistance();
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
        db.checkTableExsistance();
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
        db.checkTableExsistance();
        ArrayList<String>[] data = db.retrieveData("select TIMES from STUDENT where ID = '" + ID + "'", 1, null);
        if (data[0].get(0) == null) {
            return 0;
        } else {
            return Integer.parseInt(data[0].get(0));
        }
    }

    //add times to specific student
    public boolean studenAddTimes(String ID, String reason) {
        db.checkTableExsistance();
        int currentTimes = studentGetTimes(ID);
        return db.updateData("STUDENT", "TIMES = " + (currentTimes + 1), "ID = '" + ID + "'") && addRowInVISIT(ID, reason);

    }

    //retrieve all img
    public boolean imgRetrieve(String filePath) {
        db.checkTableExsistance();
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
        db.checkTableExsistance();
        ArrayList<String>[] temp = db.retrieveData("select * from STUDENT where ID = '" + ID + "'   ", 6, null);
        ArrayList<String> result = new ArrayList();
        for (int i = 0; i < temp.length; i++) {
            result.add(temp[i].get(0));
        }
        return result;
    }

    //get student visit reason and time
    public ArrayList<String> getStudentReasonTime(String ID) {
        db.checkTableExsistance();
        ArrayList<String>[] temp = db.retrieveData("select ID,TIMES from STUDENT where ID = '" + ID + "'", 2, null);
        ArrayList<String>[] temp2 = db.retrieveData("select REASON,VISITTIME from VISIT where ID = '" + ID + "'", 2, null);

        ArrayList<String> result = new ArrayList();
//        for (int p = 0; p < temp2[0].size(); p++) {
//            for (int i = 0; i < temp.length; i++) {
//                result.add(temp[i].get(0));
//            }
//        }
//        for (int i = 0; i < temp2.length; i++) {
//            result.add(temp2[i].get(0));
//        }

        for (int i = 0; i < temp2[0].size(); i++) {
            result.add(temp[0].get(0) + " " + temp[1].get(0) + " " + temp2[0].get(i) + " " + temp2[1].get(i));
        }
        return result;
    }

    //get specific announcement
    public ArrayList<String> getAnnouncement(java.sql.Timestamp uploadTime) {
        db.checkTableExsistance();
        ArrayList<String>[] temp = db.retrieveData("select * from ANNOUNCEMENT where ID = '" + uploadTime + "'", 2, null);
        ArrayList<String> result = new ArrayList();
        for (int i = 0; i < temp.length; i++) {
            result.add(temp[i].get(0));
        }
        return result;
    }

    //delete specific announcement
    public boolean deleteAnnouncement(java.sql.Timestamp uploadTime) {
        db.checkTableExsistance();
        return db.deleteData("ANNOUNCEMENT", "ID = '" + uploadTime + "'");
    }

    //delete drop a student
    public boolean deleteStudent(String ID) {
        db.checkTableExsistance();
        return db.deleteData("STUDENT", "ID = '" + ID + "'") && db.deleteData("VISIT", "ID = '" + ID + "'") && db.deleteData("RECOG", "ID = '" + ID + "'");
    }

    //count number of student, announcement group by program
    public String[] statisticInfo() {
        db.checkTableExsistance();
        ArrayList[] result_student = db.retrieveData("select count(ID) from Student", 1, null);
        ArrayList[] result_announcement = db.retrieveData("select count(ANNOUNCEMENT),PROGRAM from ANNOUNCEMENT Group by PROGRAM", 2, null);
        String[] result = new String[1 + result_announcement[0].size()];
        result[0] = "Total Number of Students: " + result_student[0].get(0);
        for (int i = 0; i < result_announcement[0].size(); i++) {
            result[i + 1] = "Total Number of Announcement for " + result_announcement[1].get(i) + " is " + result_announcement[0].get(i);
        }
        return result;
    }

    //get number of each gender
    public ArrayList[] getStatisticGende() {
        db.checkTableExsistance();
        System.out.println("run");
        ArrayList[] result_maleNum = db.retrieveData("select count(ID) from Student where GENDER = 'Male'", 1, null);
        ArrayList[] result_femaleNum = db.retrieveData("select count(ID) from Student where GENDER = 'Female'", 1, null);

        System.out.println(result_femaleNum);
        ArrayList<String> result_Gender = new ArrayList();
        ArrayList<Integer> result_Num = new ArrayList();
        ArrayList[] result = {result_Gender, result_Num};

        result_Gender.add("Male");
        result_Gender.add("Female");
        result_Num.add(Integer.parseInt((String)result_maleNum[0].get(0)));
        result_Num.add(Integer.parseInt((String)result_femaleNum[0].get(0)));

        return result;
    }

    //get frequency of each reason
    public ArrayList[] getStatisticReason() {
        db.checkTableExsistance();
        ArrayList[] result_R1 = db.retrieveData("select count(ID) from VISIT where REASON = 'Collect Mail'", 1, null);
        ArrayList[] result_R2 = db.retrieveData("select count(ID) from VISIT where REASON = 'Pay Fees'", 1, null);
        ArrayList[] result_R3 = db.retrieveData("select count(ID) from VISIT where REASON = 'Meeting w/Staff'", 1, null);
        ArrayList[] result_R4 = db.retrieveData("select count(ID) from VISIT where REASON = 'Meeting w/Faculty'", 1, null);
        ArrayList[] result_R5 = db.retrieveData("select count(ID) from VISIT where REASON = 'General Questions'", 1, null);
        ArrayList[] result_R6 = db.retrieveData("select count(ID) from VISIT where REASON = 'Lost and Found'", 1, null);

        ArrayList<String> result_Reason = new ArrayList();
        ArrayList<Integer> result_Num = new ArrayList();
        ArrayList[] result = {result_Reason, result_Num};
       
        result_Reason.add("Collect Mail");
        result_Reason.add("Pay Fees");
        result_Reason.add("Meeting w/Staff");
        result_Reason.add("Meeting w/Faculty");
        result_Reason.add("General Questions");
        result_Reason.add("Lost and Found");       
        
        result_Num.add(Integer.parseInt((String)result_R1[0].get(0)));
        result_Num.add(Integer.parseInt((String)result_R2[0].get(0)));
        result_Num.add(Integer.parseInt((String)result_R3[0].get(0)));
        result_Num.add(Integer.parseInt((String)result_R4[0].get(0)));
        result_Num.add(Integer.parseInt((String)result_R5[0].get(0)));
        result_Num.add(Integer.parseInt((String)result_R6[0].get(0)));

        return result;
    }

    //do not use this method  unless michael consents
    public boolean resetTable() {
        return db.reset();
    }

}
