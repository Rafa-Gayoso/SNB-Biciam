package controller;


import com.jfoenix.controls.JFXButton;
import eu.mihosoft.scaledfx.ScalableContentPane;
import execute.Executer;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utils.DataFiles;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class HomeController implements Initializable {

    public static boolean conf = false;
    public static boolean escogidos = false;
    public static boolean matrix = false;
    private TrayNotification notification;
    private File file;
    private static HomeController singletonController;

    @FXML
    private MenuItem exportCalendar;

    @FXML
    private JFXButton buttonPrincipalMenu;


    @FXML
    private JFXButton buttonCalendarConfiguration;


    @FXML
    private JFXButton dataBtn;



    @FXML
    private JFXButton buttonImportCalendar;


    @FXML
    private AnchorPane pane;

    @FXML
    private AnchorPane primaryPane;


    private AnchorPane home;


    @FXML
    private JFXButton buttonReturnSelectionTeamConfiguration;

    @FXML
    private JFXButton buttonExportCalendar;


    @FXML
    private Label information;

    @FXML
    private JFXButton buttonInfromation;


    public JFXButton getButtonReturnSelectionTeamConfiguration() {
        return buttonReturnSelectionTeamConfiguration;
    }

    public void setButtonReturnSelectionTeamConfiguration(JFXButton buttonReturnSelectionTeamConfiguration) {
        this.buttonReturnSelectionTeamConfiguration = buttonReturnSelectionTeamConfiguration;
    }

    @FXML
    void showCalendar(ActionEvent event) throws IOException {
        if(Executer.getInstance().getResultStates().isEmpty()){
            this.createPage(new ConfigurationCalendarController(), home, "/visual/ConfigurationCalendar.fxml");
            this.buttonReturnSelectionTeamConfiguration.setVisible(false);
        }
        else{
            this.createPage(new CalendarController(), home, "/visual/Calendar.fxml");
            this.buttonReturnSelectionTeamConfiguration.setVisible(true);
        }
    }

    @FXML
    void showData(ActionEvent event) {

    }

    @FXML
    void showReturnSelectionTeamConfiguration(ActionEvent event) throws IOException {
        this.createPage(new ConfigurationCalendarController(), home, "/visual/ConfigurationCalendar.fxml");
        buttonReturnSelectionTeamConfiguration.setVisible(false);
    }

    @FXML
    void importCalendar(ActionEvent event) {

    }

    @FXML
    void showTeams(ActionEvent event) throws IOException {
        this.createPage(home, "/visual/Teams.fxml");
        notification = getNotification();
        notification.setTitle("Listado de Equipos");
        notification.setMessage("Equipos participantes");
        notification.setNotificationType(NotificationType.SUCCESS);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(2));
    }

    @FXML
    void exportCalendar(ActionEvent event) {

       int calendarToExport = CalendarController.selectedCalendar;
        System.out.println(calendarToExport+"EXPORTAR");
        if(Executer.getInstance().getResultStates().isEmpty()){
            notification = getNotification();
            notification.setTitle("Exportación de Calendario");
            notification.setMessage("No hay calendarios para exportar");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(2));
        }
        else{
            DataFiles.getSingletonDataFiles().exportItineraryInExcelFormat(Executer.getInstance().getResultStates().get(calendarToExport));
        }


    }


    @FXML
    void showInformation(ActionEvent event) throws IOException{
        File file = new File("files/help.pdf");

        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        //let's try to open PDF file
        if(file.exists()) desktop.open(file);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        int year = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
        information.setText(" " + (year - 1960) + " Serie Nacional de Be\u00edsbol");
        buttonReturnSelectionTeamConfiguration.setVisible(false);
    }


    public void setNode(Node node) {
        pane.getChildren().clear();
        pane.getChildren().add((Node) node);
        FadeTransition ft = new FadeTransition(Duration.millis(2000));
        ft.setNode(node);
        ft.setFromValue(0.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }


    public void createPage(AnchorPane home, String loc) throws IOException {
        home = FXMLLoader.load(getClass().getResource(loc));
        setNode(home);
    }

    private TrayNotification getNotification() {
        return new TrayNotification();
    }

    //********************DAVID CHaNGE
    public AnchorPane getPrincipalPane() {
        return this.pane;
    }

    public void createPage(Object object, AnchorPane anchorPane, String loc) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(HomeController.class.getResource(loc));
        anchorPane = loader.load();

       if (object instanceof TeamsItineraryController) {

            Parent root = FXMLLoader.load(getClass().getResource("/visual/TeamsItinerary.fxml"));
            Stage stage = new Stage();
            ScalableContentPane scale = new ScalableContentPane();
            scale.setContent(anchorPane);
            stage.setTitle("Itinerario de equipos");
            stage.setResizable(false);
            stage.setScene(new Scene(scale));

            object = loader.getController();
            ((TeamsItineraryController) object).setHomeController(this);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryPane.getScene().getWindow());

            stage.show();
        }else if (object instanceof RestrictionsController) {

            Parent root = FXMLLoader.load(getClass().getResource("/visual/Restrictions.fxml"));
            Stage stage = new Stage();

            stage.initModality(Modality.WINDOW_MODAL);
            stage.setTitle("Restricciones del calendario");
            stage.setResizable(false);
            stage.setScene(new Scene(anchorPane));

            object = loader.getController();
            ((RestrictionsController) object).setHomeController(this);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryPane.getScene().getWindow());

            stage.show();
        }else if (object instanceof CalendarController) {
            object = loader.getController();
            ((CalendarController) object).setHomeController(this);
            setNode(anchorPane);
        } else if (object instanceof SelectGridController) {
            object = loader.getController();
            ((SelectGridController) object).setHomeController(this);
            setNode(anchorPane);
        } else if (object instanceof ConfigurationCalendarController) {
            object = loader.getController();
            ((ConfigurationCalendarController) object).setHomeController(this);

            setNode(anchorPane);
        }else if (object instanceof AdvanceConfigurationController) {
            object = loader.getController();
            ((AdvanceConfigurationController) object).setHomeController(this);
            setNode(anchorPane);
        }

        else if (object instanceof CalendarStatisticsController) {
            object = loader.getController();

            ((CalendarStatisticsController) object).setHomeController(this);
            setNode(anchorPane);
        }

    }

    @FXML
    void exportSelectedCalendar(ActionEvent event) {
        int calendarToExport = CalendarController.selectedCalendar;
        System.out.println(calendarToExport+"EXPORTAR");
        if(Executer.getInstance().getResultStates().isEmpty()){
            notification = getNotification();
            notification.setTitle("Exportación de Calendario");
            notification.setMessage("No hay calendarios para exportar");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(2));
        }
        else{
            DataFiles.getSingletonDataFiles().exportItineraryInExcelFormat(Executer.getInstance().getResultStates().get(calendarToExport));
        }
    }
}
