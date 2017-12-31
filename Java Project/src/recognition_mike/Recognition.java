/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recognition_mike;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.opencv.core.Core;
import Database.Database_Main;

/**
 *
 * @author Michael Pan (zpan1@andrew.cmu.edu);
 */
public class Recognition extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Student Facial Recognition Program");
        stage.show();
    }

    /**
     * This function initializes the program
     * Please follow the steps to run the program:-
     * 1.Add open CV package 3.1.0 with contrib folder.
     * 2.Remove any missing libraries.
     * 3.Add VMI path.
     * 4.Start server for JavaDB.
     * 5.Run the project.
     * 
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Database_Main dm = new Database_Main();
     
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        launch(args);
    }

}
