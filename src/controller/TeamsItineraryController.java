package controller;


import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import definition.TTPDefinition;
import execute.Executer;
import utils.DataFiles;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import problem.definition.State;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class TeamsItineraryController implements Initializable {


    public static int selectedCalendar;
    @FXML
    private JFXListView<String> teamsListView;

    @FXML
    private TableView<ObservableList> itineraryTable;

    @FXML
    private JFXButton showItinerary;

    private HomeController homeController;

    private ObservableList<ObservableList> data ;

    private State calendar;


    @FXML
    private TableColumn<ObservableList, String> colDates;




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedCalendar = CalendarController.selectedCalendar;


        calendar = Executer.getInstance().getResultStates().get(selectedCalendar);
        ArrayList<Integer> teamsIndexes = TTPDefinition.getInstance().getTeamsIndexes();
        System.out.println(teamsIndexes);
        List<String> teams = new ArrayList<>() ;
        for (int teamsIndex : teamsIndexes) {
            teams.add(DataFiles.getSingletonDataFiles().getTeams().get(teamsIndex));
        }

        teamsListView.setItems(FXCollections.observableArrayList(teams));
        teamsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    @FXML
    void displayItinerary(ActionEvent event) {
        data = FXCollections.observableArrayList();


        itineraryTable.getColumns().removeAll(itineraryTable.getColumns());
        itineraryTable.setItems(FXCollections.observableArrayList(new ArrayList<>()));
        ArrayList<Integer> selectedTeams = new ArrayList<>(teamsListView.getSelectionModel().getSelectedIndices());

        ArrayList<ArrayList<Integer>> itinerary = TTPDefinition.getInstance().teamsItinerary(calendar);

        /*
        Poner el encabezado de las columnas y el valor que va dentro
         */
        colDates = new TableColumn("Fecha");
        colDates.setPrefWidth(92);


        int index=0;
        for (int selectedTeam : selectedTeams) {
            final int j= index;
            if(index == 0){
                itineraryTable.getColumns().add(colDates);
            }
            colDates.setCellValueFactory(
                   param1 -> new SimpleStringProperty(param1.getValue().get(0).toString()));
            TableColumn<ObservableList, String> col = new TableColumn(DataFiles.getSingletonDataFiles().getAcronyms().get(selectedTeam));
            col.setCellValueFactory(
                    param -> new SimpleStringProperty(param.getValue().get(j+1).toString()));

            index++;
            itineraryTable.getColumns().add(col);
        }

        for(int i=1; i < itinerary.size();i++){
            ObservableList<String> row = FXCollections.observableArrayList();
            row.add(Integer.toString(i));
            ArrayList<Integer> current = itinerary.get(i);
            for (int selectedTeam : selectedTeams) {
                row.add(DataFiles.getSingletonDataFiles().getAcronyms().get(current.get(selectedTeam)));
            }
            System.out.println(row);
            data.add(row);
        }

        itineraryTable.setItems(data);

    }

    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }


}
