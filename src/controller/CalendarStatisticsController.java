package controller;

import com.jfoenix.controls.JFXButton;
import definition.TTPDefinition;
import definition.state.CalendarState;
import execute.Executer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import problem.definition.State;
import utils.Distance;
import utils.Statistics;

import java.util.ArrayList;
import java.util.List;

public class CalendarStatisticsController {

    @FXML
    private JFXButton backButton;

    private HomeController homeController;

    @FXML
    private BarChart<String, Float> barChartCalendar;

    @FXML
    private CategoryAxis xAxisCalendar;

    @FXML
    private NumberAxis yAxisCalendar;

    @FXML
    private BarChart<String, Float> barChartLessTeam;

    @FXML
    private CategoryAxis xAxisLessTeam;

    @FXML
    private NumberAxis yAxisLessTeam;

    @FXML
    private BarChart<String, Float> barChartMoreTeam;

    @FXML
    private CategoryAxis xAxisMoreTeam;

    @FXML
    private NumberAxis yAxisMoreTeam;


    @FXML
    private JFXButton btnStatisticsResume;


   /* @FXML
    private BarChart<String, Float> barChart;

    @FXML
    private CategoryAxis xAxis;

    @FXML
    private NumberAxis yAxis;*/


    private ObservableList<String> calendarData = FXCollections.observableArrayList();
    private ObservableList<String> lessTeamData = FXCollections.observableArrayList();
    private ObservableList<String> moreTeamData = FXCollections.observableArrayList();

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */



    @FXML
    private void initialize() {


        //barChart.setTitle("Estadísticas");
        yAxisCalendar.setLabel("Distancia en KM");
        yAxisLessTeam.setLabel("Distancia en KM");
        yAxisMoreTeam.setLabel("Distancia en KM");

        List<State> calendarsList = Executer.getInstance().getResultStates();

        ArrayList<String> xAxisCalendarData = new ArrayList<>();
        ArrayList<String> xAxisLessTeamData = new ArrayList<>();
        ArrayList<String> xAxisMoreTeamData = new ArrayList<>();

        for(int i =0; i < calendarsList.size();i++){
            
            //Distancias de los calendarios
            CalendarState calendar = (CalendarState) calendarsList.get(i);



            xAxisCalendarData.add(calendar.getConfiguration().getCalendarId());
            calendarData.add(xAxisCalendarData.get(i));
            XYChart.Series<String, Float>seriesCalendar = new XYChart.Series<String, Float>();
            seriesCalendar.setName(calendarData.get(i));
            seriesCalendar.getData().add(new XYChart.Data(xAxisCalendarData.get(i), calendar.getDistance()));
            barChartCalendar.getData().addAll(seriesCalendar);
            
            //Estadísticas de los calendarios
            
            //estadisticas de los equipos que menos distancias recorren
            ArrayList<ArrayList<Double>> itineraryDistance = Distance.getInstance().itineraryDistances(calendar);
            Statistics.getInstance().lessStatistics(itineraryDistance);
            xAxisLessTeamData.add(Statistics.getInstance().getTeam() + " - "+ calendar.getConfiguration().getCalendarId());
            lessTeamData.addAll(xAxisLessTeamData);
            XYChart.Series<String, Float> seriesLessTeam = new XYChart.Series<String,Float>();

            seriesLessTeam.setName("Equipo que menor distancia recorre del "+ xAxisCalendarData.get(i));
            seriesLessTeam.getData().add(new XYChart.Data(xAxisLessTeamData.get(i), Statistics.getInstance().getDistance()));
            barChartLessTeam.getData().addAll(seriesLessTeam);

           Statistics.getInstance().moreStatistics(itineraryDistance);
            xAxisMoreTeamData.add(Statistics.getInstance().getTeam() + " - "+ calendar.getConfiguration().getCalendarId());
            moreTeamData.addAll(xAxisMoreTeamData);
            XYChart.Series<String, Float> seriesMoreTeam = new XYChart.Series<String,Float>();

            seriesMoreTeam.setName("Equipo que mayor distancia recorre del "+ xAxisCalendarData.get(i));
            seriesMoreTeam.getData().add(new XYChart.Data(xAxisMoreTeamData.get(i),  Statistics.getInstance().getDistance()));
            barChartMoreTeam.getData().addAll(seriesMoreTeam);
            
        }

        setTooltipToChart(barChartCalendar);

        setTooltipToChart(barChartLessTeam);

        setTooltipToChart(barChartMoreTeam);

        if(calendarsList.size() == 1){
            barChartCalendar.setBarGap(0);
            barChartCalendar.setCategoryGap(900);
            barChartLessTeam.setBarGap(0);
            barChartLessTeam.setCategoryGap(900);
            barChartMoreTeam.setBarGap(0);
            barChartMoreTeam.setCategoryGap(900);
        }
        else {
            barChartCalendar.setBarGap(-120);

            barChartLessTeam.setBarGap(-120);

            barChartMoreTeam.setBarGap(-120);

        }
    }



    static void setTooltipToChart(BarChart<String, Float> barChart) {
        for (XYChart.Series<String, Float> series : barChart.getData()) {
            for (XYChart.Data<String, Float> item : series.getData()) {
                item.getNode().addEventHandler(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        Tooltip.install(item.getNode(), new Tooltip(item.getXValue() + ":\n" + item.getYValue()));
                    }
                });

            }
        }
    }


    //@FXML
    @FXML
    private void returnButton(/*ActionEvent event*/) {
        try {
            AnchorPane structureOver = homeController.getPrincipalPane();
            homeController.createPage(new CalendarController(), structureOver, "/visual/Calendar.fxml");
            homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @FXML
    void statisticsResume(ActionEvent event) {
        try {
            AnchorPane structureOver = homeController.getPrincipalPane();
            homeController.createPage(new StatisticsResumeController(), structureOver, "/visual/StatisticsResume.fxml");
            homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
