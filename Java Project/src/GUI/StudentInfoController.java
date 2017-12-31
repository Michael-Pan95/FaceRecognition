/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Database.Database_Main;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author nicwe
 */
public class StudentInfoController implements Initializable {

    private String username;


    @FXML
    private Label lb_name;

    @FXML
    private Label lb_andrewID;

    @FXML
    private Label lb_gender;

    @FXML
    private Label lb_program;

    @FXML
    private Label lb_home;

    @FXML
    private ListView<String> lv_visitReasons;

    @FXML
    private Button bt_recordNewVisit;

    @FXML
    private Button bt_returnToMain;

    @FXML
    private ImageView studentImageView;

    public StudentInfoController(String username) {
        this.username = username;
    }

    @FXML
    void recordNewVisit(ActionEvent event) {
        Stage s = (Stage) bt_recordNewVisit.getScene().getWindow();

        try {
            // Parent studentInfo = FXMLLoader.load(getClass().getResource("GetStudentInfo.fxml"));
            FXMLLoader studentInfo = new FXMLLoader(getClass().getResource("/GUI/StudentVisit.fxml"));
            StudentVisitController controller = new StudentVisitController(username);
            studentInfo.setController(controller);
            s.setScene(new Scene(studentInfo.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void setPicture(ActionEvent event) {
        studentImageView.setImage(getStudentImage(username));
    }

    @FXML
    void returnToMain(ActionEvent event) {
        Stage s = (Stage) bt_returnToMain.getScene().getWindow();
        try {
            Parent studentInfo = FXMLLoader.load(getClass().getResource("/recognition_mike/FXMLDocument.fxml"));
            s.setScene(new Scene(studentInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        Database_Main db = new Database_Main();
        // get info about student.
        ArrayList<String> studentInfo = db.getStudentInfo(username);
        lb_name.setText(studentInfo.get(1));//name
        lb_andrewID.setText(studentInfo.get(0));//ID
        lb_gender.setText(studentInfo.get(2));//Gender
        lb_program.setText(studentInfo.get(3));//Program
        lb_home.setText(studentInfo.get(4));//Nationality

        // get info about the students visit.
        ArrayList<String> studentReasonTime = db.getStudentReasonTime(username);
         studentReasonTime = formatVisitString(studentReasonTime, studentInfo.get(1)); // Format Student visit information 
        ObservableList<String> data = FXCollections.observableList(studentReasonTime);
//        ObservableList<String> data = FXCollections.observableArrayList("Student", "Student1", "Student2", "Student3", "Student4");
        lv_visitReasons.setItems(data);
    }

    private Image getStudentImage(String username) {
        ArrayList<File> fileNames = new ArrayList();
        File filename;
        File file = new File("resources/trainingset/combined");
        File[] fileList = file.listFiles();

        for (File f : fileList) {

            if (f.getName().contains(username)) {
                fileNames.add(f);
                // System.out.println("File Name: " + s);
            }
        }

        filename = fileNames.get(7);

        System.out.println(filename);

        Image image = new Image("file:" + filename.getAbsolutePath().toString());

        return image;
    }

    
    private ArrayList<String> formatVisitString(ArrayList<String> raw, String name){
        ArrayList<String> formatted = new ArrayList<>();
        formatted.add(String.format("%-10s%-40s%-20s%-20s","Visit", "Reason", "Date", "Time"));
        
        int i = 0;
        for(String s:raw){
            System.out.println(s);
            
            int length = s.length();
            System.out.println("Length: " + length);
            
            // Number of Visits
            i++;
            System.out.println("Visit Number: " + i);
            
            // Name
            System.out.println("Name: " + name);
            
            // Time
            String time = s.substring(length-12);
            System.out.println("Time: " + time);
            
            // Date
            System.out.println(length);
            String date = s.substring(length-23, length-13);
            System.out.println("Date: " + date);
            
            String[] parsed = s.split(" ");
            
            // AndrewId
            String andrewId = parsed[0]; 
            System.out.println("Id: " + andrewId);
            
            // Total Visits
            String visits = parsed[1];
            System.out.println("Visit: " + visits);
            
            // Reason
            String reason = s.substring((andrewId.length()+visits.length()+2),length-24);
            System.out.println("Reason: " + reason);
           
            
            formatted.add(String.format("%-10s%-40s%-20s%-20s",String.valueOf(i), reason, date, time));
        }
        
        return formatted;
    }
}
