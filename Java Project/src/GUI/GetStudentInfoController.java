/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Database.Database_Main;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author nicwe
 */
public class GetStudentInfoController implements Initializable {

    String username;

    @FXML
    private TextField tf_studentName;

    @FXML
    private TextField tf_studentAndrewID;

//    @FXML
//    private TextField tf_studentGender;

//    @FXML
//    private TextArea tb_reasonForVisit;

//    @FXML
//    private TextField tf_studentProgram;

    @FXML
    private TextField tf_studentCountry;

    @FXML
    private Label lb_results;
    
    @FXML
    private AnchorPane pane_studentInfo;
    
    @FXML
    private ChoiceBox<String> cb_gender;
    
    @FXML
    private ChoiceBox<String> cb_program;
    
    @FXML
    private ChoiceBox<String> cb_reason;

    @FXML
    void returnToMainPage(ActionEvent event) {
        actualGoToMainPage();
    }

    @FXML
    void saveStudent(ActionEvent event) {
        Database_Main db = new Database_Main();

        try {
            db.addRowInSTUDENT(tf_studentAndrewID.getText(), tf_studentName.getText(), cb_gender.getValue(), cb_program.getValue(), tf_studentCountry.getText());
            if (cb_reason.getValue() != null) {
                db.addRowInVISIT(tf_studentAndrewID.getText(), cb_reason.getValue());
            }
            tf_studentAndrewID.clear();
            tf_studentName.clear();
           // tf_studentGender.clear();
          //  tf_studentProgram.clear();
            tf_studentCountry.clear();
            lb_results.setText("The student has been saved.");
        } catch (Exception e) {
            e.printStackTrace();
            lb_results.setText("The student could not be saved. Please check database connection.");
        }
        actualGoToMainPage();
    }
    
    private void actualGoToMainPage(){
        Stage s = (Stage) tf_studentName.getScene().getWindow();
        try {
            Parent studentInfo = FXMLLoader.load(getClass().getResource("/recognition_mike/FXMLDocument.fxml"));
            s.setScene(new Scene(studentInfo));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public GetStudentInfoController(){}

    public AnchorPane getPane_studentInfo() {
        return pane_studentInfo;
    }
    
    public GetStudentInfoController(String username) {
        this.username = username;
    }

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        tf_studentAndrewID.setText(username);
        
        // Gender Drop Down
        ObservableList<String> gender = FXCollections.observableArrayList("Female", "Male");
        cb_gender.setItems(gender);
        
        // Program Drop Down
        ObservableList<String> program = FXCollections.observableArrayList("Global MISM", "MSIT", "MSPPM");
        cb_program.setItems(program);
        
        // Set Values into Choice box for visit Reasons
        ObservableList<String> visitReasons = FXCollections.observableArrayList("Collect Mail", "Pay Fees", "Meeting w/Staff", "Meeting w/Faculty", "General Questions", "Lost and Found");
        cb_reason.setItems(visitReasons);
    }

}
