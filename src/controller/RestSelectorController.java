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

import java.net.URL;
import java.util.ArrayList;
import java.util.Observable;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class RestSelectorController implements Initializable {

    private HomeController homeController;

    @FXML
    private JFXListView<String> restList;

    @FXML
    private JFXButton saveBtn;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        int size = ConfigurationCalendarController.teams;

        ArrayList<String> dates = new ArrayList<>();

        for(int i=1; i < size-1; i++){
            dates.add("Fecha "+(i));
        }

        restList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        restList.setItems(FXCollections.observableArrayList(dates));


    }


    @FXML
    void save(ActionEvent event) {
        ArrayList<Integer> indices = new ArrayList<>(restList.getSelectionModel().getSelectedIndices());

        ArrayList<Integer> restIndices = (ArrayList<Integer>) indices.stream().map(i -> i+1).collect(Collectors.toList());
        System.out.println(restIndices);
        TTPDefinition.getInstance().setRestIndexes(restIndices);

        Stage stage = (Stage) restList.getScene().getWindow();
        stage.close();
    }




    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
