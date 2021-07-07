package controller;

import com.jfoenix.controls.JFXTextField;
import definition.TTPDefinition;
import definition.state.CalendarState;
import execute.Executer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import problem.definition.State;
import utils.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class StatisticsResumeController implements Initializable {

    private HomeController homeController;


    @FXML
    private TableView<TableStatisticsData> statisticsTable;

    @FXML
    private TableColumn<TableStatisticsData, String> calendarColumn;

    @FXML
    private TableColumn<TableStatisticsData, Double> calendarDistanceColumn;

    @FXML
    private TableColumn<TableStatisticsData, String> teamLessNameColumn;

    @FXML
    private TableColumn<TableStatisticsData, Double> teamLessDistanceColumn;

    @FXML
    private TableColumn<TableStatisticsData, String> teamMoreNameColumn;

    @FXML
    private TableColumn<TableStatisticsData, Double> teamMoreDistanceColumn;

    @FXML
    private TableColumn<TableStatisticsData, Integer> localRestrictionsColum;

    @FXML
    private TableColumn<TableStatisticsData, Integer> visitorRestrictionsColum;

    private ObservableList<TableStatisticsData> data = FXCollections.observableArrayList();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {


        calendarColumn.setCellValueFactory(new PropertyValueFactory<TableStatisticsData, String>("calendarId"));
        calendarDistanceColumn.setCellValueFactory(new PropertyValueFactory<TableStatisticsData,Double>("calendarDistance"));
        teamLessNameColumn.setCellValueFactory(new PropertyValueFactory<TableStatisticsData, String>("lessTeam"));
        teamLessDistanceColumn.setCellValueFactory(new PropertyValueFactory<TableStatisticsData,Double>("lessTeamDistance"));
        teamMoreNameColumn.setCellValueFactory(new PropertyValueFactory<TableStatisticsData, String>("moreTeam"));
        teamMoreDistanceColumn.setCellValueFactory(new PropertyValueFactory<TableStatisticsData,Double>("moreTeamDistance"));
        localRestrictionsColum.setCellValueFactory(new PropertyValueFactory<TableStatisticsData,Integer>("localRestrictionsVioleted"));
        visitorRestrictionsColum.setCellValueFactory(new PropertyValueFactory<TableStatisticsData,Integer>("visitorRestrictionsVioleted"));
        fillColumns();
    }

    private void fillColumns () {

        List<State> calendars = Executer.getInstance().getResultStates();

        for (int i = 0; i < calendars.size(); i++) {
            CalendarState calendar = (CalendarState) calendars.get(i);
            ArrayList<ArrayList<Double>> itineraryDistance = Distance.getInstance().itineraryDistances(calendar);
            Statistics.getInstance().lessStatistics(itineraryDistance);
            String teamLessDistance = Statistics.getInstance().getTeam();
            double lessDistance = Statistics.getInstance().getDistance();
            Statistics.getInstance().moreStatistics(itineraryDistance);
            String teamMoreDistance = Statistics.getInstance().getTeam();
            double moreDistance = Statistics.getInstance().getDistance();
            int maxVisitorGamesBrokeRule = TTPDefinition.getInstance().penalizeVisitorGames(calendar);
            int maxHomeGamesBrokeRule = TTPDefinition.getInstance().penalizeLocalGames(calendar);
            data.add(new TableStatisticsData(calendar.getConfiguration().getCalendarId(), teamLessDistance, teamMoreDistance, calendar.getDistance(), lessDistance, moreDistance,
                    maxHomeGamesBrokeRule,maxVisitorGamesBrokeRule));

        }
        statisticsTable.setItems(data);


    }


    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @FXML
    void exportStatistics(ActionEvent event) {
        DataFiles.getSingletonDataFiles().exportsStatistics(statisticsTable);
    }

    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) statisticsTable.getScene().getWindow();
        stage.close();
    }

}
