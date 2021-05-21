package controller;

import com.jfoenix.controls.JFXButton;
import definition.TTPDefinition;
import definition.state.CalendarState;
import execute.Executer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import operators.initialSolution.InitialSolutionType;
import problem.definition.State;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RestrictionsController {

    @FXML
    private JFXButton closeButton;

    @FXML
    private Label lblLocalText;

    @FXML
    private Label lblVisitorText;

    @FXML
    private Label lblLocalNumber;

    @FXML
    private Label lblVisitorNumber;

    @FXML
    private Label lblLongTripNumber;




    private HomeController homeController;

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */


    public void setData(State calendar){

        int maxVisitorGamesBrokeRule = TTPDefinition.getInstance().penalizeVisitorGames(calendar);
        int maxHomeGamesBrokeRule = TTPDefinition.getInstance().penalizeLocalGames(calendar);



        lblLocalText.setText(lblLocalText.getText().replace("#",Integer.toString(TTPDefinition.getInstance().getCantVecesLocal())));
        lblVisitorText.setText(lblVisitorText.getText().replace("#",Integer.toString(TTPDefinition.getInstance().getCantVecesVisitante())));

        lblLocalNumber.setText(Integer.toString(maxHomeGamesBrokeRule));
        lblVisitorNumber.setText(Integer.toString(maxVisitorGamesBrokeRule));

        if(maxHomeGamesBrokeRule > 0){
            lblLocalText.setTextFill(Color.web("#a52727"));
            lblLocalNumber.setTextFill(Color.web("#a52727"));

        }
        if(maxVisitorGamesBrokeRule > 0){
            lblVisitorText.setTextFill(Color.web("#a52727"));
            lblVisitorNumber.setTextFill(Color.web("#a52727"));
        }


    }



}
