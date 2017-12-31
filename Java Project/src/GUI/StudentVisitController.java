/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import Database.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author nicwe
 */
public class StudentVisitController implements Initializable {

    private String andrewId;

    @FXML
    private Button bt_saveVisit;

    @FXML
    private Button bt_returnToMain;

    @FXML
    private TextArea ta_reason;

    @FXML
    private ChoiceBox<String> cb_reason;

    @FXML
    void returnToMain(ActionEvent event) {
        actualReturnToMain();
    }

    @FXML
    void saveVisit(ActionEvent event) {
        if (cb_reason.getValue() != null) {
            Database_Main db = new Database_Main();
            db.studenAddTimes(andrewId, cb_reason.getValue());
            //  System.out.println(andrewId);
            
            actualReturnToMain();
        }
    }

    public StudentVisitController(String andrewId) {
        this.andrewId = andrewId;
    }

    private void actualReturnToMain() {
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
        // TODO

        // Set Values into Choice box for visit Reasons
        ObservableList<String> visitReasons = FXCollections.observableArrayList("Collect Mail", "Pay Fees", "Meeting w/Staff", "Meeting w/Faculty", "General Questions", "Lost and Found");
        cb_reason.setItems(visitReasons);
    }

}
