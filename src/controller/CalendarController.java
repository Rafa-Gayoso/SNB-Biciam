package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTabPane;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import execute.Executer;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.DataFiles;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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


    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        notification = new TrayNotification();
        selectedCalendar = 0;

        tables = new ArrayList<>();
        List<State> calendarsList = Executer.getInstance().getResultStates();

        try{
            for(int i=0; i < calendarsList.size();i++){
                CalendarState calendar = (CalendarState) calendarsList.get(i);
                AnchorPane allContent = new AnchorPane();

                FXMLLoader fxmlLoader =  new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("/visual/Restrictions.fxml"));
                AnchorPane restrictionsContent = fxmlLoader.load();
                RestrictionsController restrictionsController = fxmlLoader.getController();
                restrictionsController.setData(calendar);
                ArrayList<Object> calendarDates = calendar.getCode();
                JFXTabPane currentCalendarTabPane = new JFXTabPane();


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
                        table.getItems().add(new Duel(DataFiles.getSingletonDataFiles().getAcronyms().get(posLocal),
                                DataFiles.getSingletonDataFiles().getAcronyms().get(posVisitor)));
                    }

                    if(!calendar.getConfiguration().getRestDates().isEmpty()){

                            if(calendar.getConfiguration().getRestDates().contains(j)){
                                Label label = new Label("Descanso");
                                Tab t = new Tab("Descanso");
                                t.setContent(label);
                                currentCalendarTabPane.getTabs().add(t);
                            }


                    }

                    table.setPrefWidth(restrictionsContent.getPrefWidth());
                    Tab tab = new Tab("Fecha " + (j + 1));
                    tab.setContent(table);
                    tables.add(table);
                    currentCalendarTabPane.getTabs().add(tab);

                    /*if(calendarDates.size()-j == 1){
                        ArrayList<Integer> rest = addRestToCalendar(calendar);
                        for (Integer restDate:
                                rest) {
                            Label label = new Label("Descanso");
                            Tab t = new Tab("Descanso");
                            t.setContent(label);
                            currentCalendarTabPane.getTabs().add(restDate,t);
                        }
                    }*/
                }

                currentCalendarTabPane.setPrefWidth(restrictionsContent.getPrefWidth());
                HBox hboxCalendarContent = new HBox();
                HBox hboxRestrictionContent = new HBox();
                hboxCalendarContent.getChildren().addAll(currentCalendarTabPane);

                hboxRestrictionContent.getChildren().addAll(restrictionsContent);

                allContent.getChildren().addAll(hboxCalendarContent,hboxRestrictionContent);
                allContent.setLeftAnchor(hboxCalendarContent, 0.0);
                allContent.setLeftAnchor(hboxRestrictionContent, currentCalendarTabPane.getPrefWidth());
                Tab tab =  new Tab(calendar.getConfiguration().getCalendarId());
                tab.setContent(allContent);
                calendarsTabPane.getTabs().add(tab);
            }

            calendarsTabPane.getSelectionModel().selectedItemProperty().addListener(
                    new ChangeListener<Tab>() {
                        @Override
                        public void changed(ObservableValue<? extends Tab> ov, Tab t, Tab t1) {
                            selectedCalendar = calendarsTabPane.getTabs().indexOf(t1);
                            System.out.println(selectedCalendar);
                        }
                    }
            );

            calendarsTabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.SELECTED_TAB);
        }catch(Exception e){
            e.printStackTrace();
        }


    }

    private void/*ArrayList<Integer>*/ addRestToCalendar(JFXTabPane currentCalendarTabPane, CalendarState calendar){


        ArrayList<Integer> rest = calendar.getConfiguration().getRestDates();
        for(int i=0; i < rest.size(); i++){
            int descanso = rest.get(i);
            Label label = new Label("Descanso");
            Tab t = new Tab("Descanso");
            t.setContent(label);
            currentCalendarTabPane.getTabs().add(descanso,t);
        }

        /*ArrayList<Integer> rest = new ArrayList<>();
        State state = calendar.clone();
        ArrayList<ArrayList<Integer>> itinerary = TTPDefinition.getInstance().teamsItinerary(state);
        ArrayList<Integer> teams= (ArrayList<Integer>) itinerary.get(0).clone();

        for(int i=1; i < itinerary.size()-1;i++){
           if(itinerary.get(i).containsAll(teams)){
               rest.add(i - 1);
           }
        }
        return rest;*/
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
            showNotification("No existe ning\u00fan calendario", "Debe crear o importar al menos un calendario.");
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
            showNotification("No existe ning\u00fan calendario", "Debe crear o importar al menos un calendario.");
        }
    }



    @FXML
    void closeSelectedTab(ActionEvent event) {
            Executer.getInstance().getResultStates().remove(selectedCalendar);
            calendarsTabPane.getTabs().remove(selectedCalendar);

            if(calendarsTabPane.getTabs().isEmpty()){
                Tab tab  =  new Tab("No existen calendarios para mostrar");
                Label label = new Label("No hay datos para mostrar");
                tab.setContent(label);
                calendarsTabPane.getTabs().add(tab);
            }

    }

    @FXML
    void showConfiguration(ActionEvent event) {
        if(!calendarsTabPane.getTabs().isEmpty()) {
            try {
                homeController.createPage(new MutationsConfigurationController(), null, "/visual/MutationsConfiguration.fxml");
                // Hide this current window (if this is what you want)

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            showNotification("No existe ningún calendario", "Debe crear o importar al menos un calendario.");
        }
    }

    private void showNotification(String title, String message) {
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.ERROR);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(1));
    }
}



