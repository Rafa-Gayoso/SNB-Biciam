package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import definition.TTPDefinition;
import definition.state.statecode.Date;
import execute.Executer;
import utils.DataFiles;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import problem.definition.State;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CalendarController implements Initializable {

    private ArrayList<Date> calendar;
    private ArrayList<TableView<Duel>> tables;
    private HomeController homeController;
    public static int selectedCalendar;
    private TrayNotification notification;

    @FXML
    private JFXTabPane calendarsTabPane;


    @FXML
    private JFXButton statisticsBtn;

    @FXML
    private JFXButton configurationBtn;

    @FXML
    private JFXButton exportBtn;

    @FXML
    private JFXTabPane calendarTabPane;

    @FXML
    private JFXButton itineraryBtn;

    @FXML
    private JFXButton stadiumItineraryBtn;

    @FXML
    private JFXButton restrictionsBtn;

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        notification = new TrayNotification();
        selectedCalendar = 0;

        tables = new ArrayList<>();
        List<State> calendarsList = Executer.getInstance().getResultStates();

        for(int i=0; i < calendarsList.size();i++){
            State calendar = calendarsList.get(i);
            ArrayList<Object> calendarDates = calendar.getCode();
            JFXTabPane currentCalendarTabPane = new JFXTabPane();


            //currentCalendarTabPane.setPrefHeight(calendarsTabPane.getHeight());
            for(int j=0; j < calendarDates.size();j++){
                TableView<Duel> table = new TableView<Duel>();
                TableColumn<Duel, String> col = new TableColumn<>("Local");
                TableColumn<Duel, String> col2 = new TableColumn<>("Visitante");

                col.setCellValueFactory(new PropertyValueFactory<>("local"));
                col2.setCellValueFactory(new PropertyValueFactory<>("visitor"));

                ObservableList<TableColumn<Duel, ?>> columns = table.getColumns();
                columns.add(col);
                columns.add(col2);
                Date date = (Date) calendarDates.get(j);
                for (int k = 0; k < date.getGames().size(); k++) {
                    int posLocal = date.getGames().get(k).get(0);
                    int posVisitor = date.getGames().get(k).get(1);
                    table.getItems().add(new Duel(DataFiles.getSingletonDataFiles().getTeams().get(posLocal),
                            DataFiles.getSingletonDataFiles().getTeams().get(posVisitor)));
                }


                Tab tab = new Tab("Fecha " + (j + 1));
                tab.setContent(table);
                tables.add(table);
                currentCalendarTabPane.getTabs().add(tab);

                if(calendarDates.size()-j == 1){
                   ArrayList<Integer> rest = addRestToCalendar(calendar);
                    for (Integer restDate:
                         rest) {
                        Label label = new Label("Descanso");
                        Tab t = new Tab("Descanso");
                        t.setContent(label);
                        currentCalendarTabPane.getTabs().add(restDate,t);
                    }
                }
            }
            Tab tab =  new Tab("Calendario "+(i+1));
            tab.setContent(currentCalendarTabPane);
            calendarsTabPane.getTabs().add(tab);
        }

        calendarsTabPane.getSelectionModel().selectedItemProperty().addListener(
                new ChangeListener<Tab>() {
                    @Override
                    public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                        selectedCalendar = calendarsTabPane.getTabs().indexOf(t1);
                    }
                }
        );

    }

    private ArrayList<Integer> addRestToCalendar(State calendar){
        ArrayList<Integer> rest = new ArrayList<>();
        ArrayList<ArrayList<Integer>> itinerary = TTPDefinition.getInstance().teamsItinerary(calendar);
        ArrayList<Integer> teams= (ArrayList<Integer>) itinerary.get(0).clone();

        for(int i=1; i < itinerary.size()-1;i++){
           if(itinerary.get(i).containsAll(teams)){
               rest.add(i - 1);
           }
        }
        return rest;
    }



    @FXML
    void showStatistics(ActionEvent event) {
        if(!calendarsTabPane.getTabs().isEmpty()) {
            try {
                homeController.createPage(new CalendarStatisticsController(), null, "/visual/CalendarStatistics.fxml");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            showNotification("No existe ningún calendario", "Debe crear o importar al menos un calendario.");
        }
    }

    @FXML
    void showItinerary(ActionEvent event) {
        if(!calendarsTabPane.getTabs().isEmpty()) {
            try {
                homeController.createPage(new TeamsItineraryController(), null, "/visual/TeamsItinerary.fxml");
                // Hide this current window (if this is what you want)
                // ((Node)(event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            showNotification("No existe ningún calendario", "Debe crear o importar al menos un calendario.");
        }
    }

    @FXML
    void showRestrictions(ActionEvent event) {
        if(!calendarsTabPane.getTabs().isEmpty()){
            try {
                homeController.createPage(new RestrictionsController(), null, "/visual/Restrictions.fxml");
                // Hide this current window (if this is what you want)
                // ((Node)(event.getSource())).getScene().getWindow().hide();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            showNotification("No existe ningún calendario", "Debe crear o importar al menos un calendario.");
        }
    }

    /*@FXML
    void closeSelectedTab(ActionEvent event) {
            controller.getCalendarList().remove(selectedCalendar);
            calendarsTabPane.getTabs().remove(selectedCalendar);

            if(calendarsTabPane.getTabs().isEmpty()){
                Tab tab  =  new Tab("No existen calendarios para mostrar");
                Label label = new Label("No hay datos para mostrar");
                tab.setContent(label);
                calendarsTabPane.getTabs().add(tab);
            }

    }*/

    private void showNotification(String title, String message) {
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.ERROR);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(1));
    }
}



