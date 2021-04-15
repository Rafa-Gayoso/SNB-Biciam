package controller;

import com.jfoenix.controls.JFXButton;
import definition.TTPDefinition;
import execute.Executer;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import problem.definition.State;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class RestrictionsController implements Initializable {

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



    @FXML
    public void initialize(URL location, ResourceBundle resources) {


        State calendar = Executer.getInstance().getResultStates().get(CalendarController.selectedCalendar);

        ArrayList<ArrayList<Integer>> itinerary = TTPDefinition.getInstance().teamsItinerary(calendar);


        int maxVisitorGamesBrokeRule = TTPDefinition.getInstance().penalizeVisitorGames(calendar);
        int maxHomeGamesBrokeRule =TTPDefinition.getInstance().penalizeLocalGames(calendar);


        lblLocalText.setText(lblLocalText.getText().replace("#",Integer.toString(TTPDefinition.getInstance().getCantVecesLocal())));
        lblVisitorText.setText(lblVisitorText.getText().replace("#",Integer.toString(TTPDefinition.getInstance().getCantVecesVisitante())));

        lblLocalNumber.setText(Integer.toString(maxHomeGamesBrokeRule));
        lblVisitorNumber.setText(Integer.toString(maxVisitorGamesBrokeRule));



    }

    @FXML
    void closeButtonAction(ActionEvent event) {
    // get a handle to the stage
        Stage stage = (Stage) closeButton.getScene().getWindow();
        // do what you have to do
        stage.close();
    }




}
