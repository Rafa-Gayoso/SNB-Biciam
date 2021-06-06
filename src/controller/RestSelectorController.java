package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import definition.TTPDefinition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.stage.Stage;
import org.controlsfx.control.CheckListView;

import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RestSelectorController implements Initializable {

    private HomeController homeController;


    @FXML
    private JFXButton saveBtn;

    @FXML
    private CheckListView<String> checkBoxListView;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int size = ConfigurationCalendarController.teams - 1;

        ArrayList<String> dates = new ArrayList<>();

        int i=0;
        for(i=1; i < size; i++){
            dates.add("Fecha "+(i));
        }

        if(ConfigurationCalendarController.inaugural){
            dates.add("Fecha "+(i));
        }

        if (ConfigurationCalendarController.secondRound){
            size = size*2;
            if (ConfigurationCalendarController.inaugural){
                size += 1;
            }
            //i++;
            for(; i < size; i++){
                dates.add("Fecha "+(i));
            }
        }



        checkBoxListView.setItems(FXCollections.observableArrayList(dates));

    }


    @FXML
    void save(ActionEvent event) {
        ArrayList<Integer> indices = new ArrayList<>(checkBoxListView.getCheckModel().getCheckedIndices());

        ArrayList<Integer> restIndices = (ArrayList<Integer>) indices.stream().map(i -> i+1).collect(Collectors.toList());
        System.out.println(restIndices);
        TTPDefinition.getInstance().setRestIndexes(restIndices);

        Stage stage = (Stage) checkBoxListView.getScene().getWindow();
        stage.close();
    }




    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
