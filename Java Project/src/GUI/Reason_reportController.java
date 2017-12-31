/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import Database.Database_Main;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

/**
 * FXML Controller class
 *
 * @author nicwe
 */
public class Reason_reportController implements Initializable {

 @FXML
    private BarChart<?, ?> reason_chart;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        calling();
        

    }

    public void calling() {
        XYChart.Series set = new XYChart.Series<>();
        Database_Main A = new Database_Main();
        ArrayList[] ar = A.getStatisticReason();
        set.getData().add(new XYChart.Data<>(ar[0].get(0), ar[1].get(0)));
        set.getData().add(new XYChart.Data<>(ar[0].get(1), ar[1].get(1)));
         set.getData().add(new XYChart.Data<>(ar[0].get(2), ar[1].get(2)));
          set.getData().add(new XYChart.Data<>(ar[0].get(3), ar[1].get(3)));
          set.getData().add(new XYChart.Data<>(ar[0].get(4), ar[1].get(4)));
         set.getData().add(new XYChart.Data<>(ar[0].get(5), ar[1].get(5)));
        reason_chart.getData().addAll(set);
        reason_chart.lookupAll(".default-color0.chart-bar").forEach(n -> n.setStyle("-fx-bar-fill: #cc002b;"));
               
    }
}
