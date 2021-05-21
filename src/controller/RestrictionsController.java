package controller;

import com.jfoenix.controls.JFXButton;
import definition.TTPDefinition;
import definition.state.CalendarState;
import execute.Executer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
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


    @FXML
    private Label noneRestriction;

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
        int maxHomeGamesBrokeRule =TTPDefinition.getInstance().penalizeLocalGames(calendar);

        lblLocalText.setVisible(false);
        lblVisitorText.setVisible(false);

        lblLocalNumber.setVisible(false);
        lblVisitorNumber.setVisible(false);

        noneRestriction.setVisible(false);

        lblLocalText.setText(lblLocalText.getText().replace("#",Integer.toString(TTPDefinition.getInstance().getCantVecesLocal())));
        lblVisitorText.setText(lblVisitorText.getText().replace("#",Integer.toString(TTPDefinition.getInstance().getCantVecesVisitante())));

        lblLocalNumber.setText(Integer.toString(maxHomeGamesBrokeRule));
        lblVisitorNumber.setText(Integer.toString(maxVisitorGamesBrokeRule));

        if(maxHomeGamesBrokeRule > 0 || maxVisitorGamesBrokeRule > 0){
            lblLocalText.setVisible(true);
            lblVisitorText.setVisible(true);

            lblLocalNumber.setVisible(true);
            lblVisitorNumber.setVisible(true);

            noneRestriction.setVisible(false);
        }
        else{
            lblLocalText.setVisible(false);
            lblVisitorText.setVisible(false);

            lblLocalNumber.setVisible(false);
            lblVisitorNumber.setVisible(false);

            noneRestriction.setVisible(true);
        }
    }



}
