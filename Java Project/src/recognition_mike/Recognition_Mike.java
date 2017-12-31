/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package recognition_mike;

import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.opencv.core.Core;

/**
 *
 * @author Michael Pan (zpan1@andrew.cmu.edu);
 */
public class Recognition_Mike extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLDocument.fxml"));

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.setTitle("Student Facial Recognition Program");
        stage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
       // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
       // launch(args);   
      
      String username = "nwells";
        
        ArrayList<File> fileNames = new ArrayList<File>();
        File filename;
        File file = new File("resources/trainingset/combined");
        File[] fileList = file.listFiles();
        
        for(File f:fileList){
            
            if(f.getName().contains(username)){
                fileNames.add(f);
               // System.out.println("File Name: " + s);
            }
        }  
        
        filename = fileNames.get(4);
        
        
       // System.out.println(filename);
        
        Image image = new Image("file:" + filename.getAbsolutePath().toString());


      
    }

}
