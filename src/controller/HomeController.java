package controller;


import com.jfoenix.controls.JFXButton;
import definition.TTPDefinition;
import definition.state.CalendarState;
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
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private ContextMenu contextMenu;
    @FXML
    private MenuItem exportCalendar;

    @FXML
    private MenuItem exportAllCalendar;

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

    @FXML
    private ImageView imgSuperior;

    @FXML
    private Label lblSuperior;

    private boolean areVisible;


    public JFXButton getButtonReturnSelectionTeamConfiguration() {
        return buttonReturnSelectionTeamConfiguration;
    }

    public void setButtonReturnSelectionTeamConfiguration(JFXButton buttonReturnSelectionTeamConfiguration) {
        this.buttonReturnSelectionTeamConfiguration = buttonReturnSelectionTeamConfiguration;
    }

    @FXML
    void showCalendar(ActionEvent event) throws IOException {

        //DEBUG
        System.out.println("Homecontroller.showCalendar()");

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
    void showCalendar32(ActionEvent event) throws IOException {

        //DEBUG
        System.out.println("Homecontroller.showCalendar()");

        //NEW 12/03/2022
        TTPDefinition.getInstance().setLss(true);

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
    void showData(ActionEvent event) throws IOException {
        this.createPage(new CrudsController(), home, "/visual/Cruds.fxml");

    }

    @FXML
    void showReturnSelectionTeamConfiguration(ActionEvent event) throws IOException {
        this.createPage(new ConfigurationCalendarController(), home, "/visual/ConfigurationCalendar.fxml");
        buttonReturnSelectionTeamConfiguration.setVisible(false);
    }

    @FXML
    void importCalendar(ActionEvent event) {
        Stage stage = new Stage();
        FileChooser fc = new FileChooser();

        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Documento Excel", "*xlsx"));
        file = fc.showOpenDialog(stage);

        try {
            if (file != null) {
                CalendarState calendar = DataFiles.getSingletonDataFiles().readExcelItineraryToCalendar(file.toString());
                if(calendar.getCode().size()>0){
                    Executer.getInstance().getResultStates().add(calendar);

                    notification = getNotification();
                    notification.setTitle("Importaci\u00f3n de Calendario");
                    notification.setMessage("Calendario importado con \u00e9xito");
                    notification.setNotificationType(NotificationType.SUCCESS);
                    notification.setRectangleFill(Paint.valueOf("#2F2484"));
                    notification.setAnimationType(AnimationType.FADE);
                    notification.showAndDismiss(Duration.seconds(2));
                    buttonReturnSelectionTeamConfiguration.setVisible(false);
                    this.createPage(new CalendarController(),home, "/visual/Calendar.fxml");
                    this.buttonReturnSelectionTeamConfiguration.setVisible(true);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exportCalendar(ActionEvent event) {

       /*int calendarToExport = CalendarController.selectedCalendar;
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
        }*/
    }


    @FXML
    void showInformation(ActionEvent event) throws IOException{

        //DEBUG
        System.out.println("HomeCOntroller.showInformation()");

        Parent root = FXMLLoader.load(getClass().getResource("/visual/Help.fxml"));
        Stage stage = new Stage();

        stage.setTitle("Ayuda");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/snb.png")));
        stage.setResizable(true);
        stage.setScene(new Scene(root));

        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(primaryPane.getScene().getWindow());

        stage.show();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //DEBUG
        System.out.println("HomeCOntroller.initialize()");


        areVisible = false;lblSuperior.setVisible(false);
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

        //DEBUG
        System.out.println("HomeCOntroller.createPage()");

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(HomeController.class.getResource(loc));
        anchorPane = loader.load();
        if(!areVisible){
            lblSuperior.setVisible(true);
            FadeTransition ft = new FadeTransition(Duration.millis(2000));
            ft.setNode(lblSuperior);
            ft.setFromValue(0.1);
            ft.setToValue(1);
            ft.setCycleCount(1);
            ft.setAutoReverse(false);
            ft.play();
            areVisible = true;
        }

        if (object instanceof MutationsConfigurationController) {

            Parent root = FXMLLoader.load(getClass().getResource("/visual/MutationsConfiguration.fxml"));
            Stage stage = new Stage();
           /* ScalableContentPane scale = new ScalableContentPane();
            scale.setContent(anchorPane);*/

            stage.setTitle("Configuraci\u00f3n de las mutaciones");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/snb.png")));
            stage.setResizable(true);
            stage.setMinWidth(1152 );
            stage.setMinHeight(763);
            stage.setScene(new Scene(root));

            object = loader.getController();
            ((MutationsConfigurationController) object).setHomeController(this);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryPane.getScene().getWindow());

            stage.show();
        }
        else if (object instanceof RestSelectorController) {

            Parent root = FXMLLoader.load(getClass().getResource("/visual/RestSelector.fxml"));
            Stage stage = new Stage();
            /*ScalableContentPane scale = new ScalableContentPane();
            scale.setContent(anchorPane);*/

            stage.setTitle("Selecci\u00f3n de descansos");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/snb.png")));
            stage.setResizable(true);
            stage.setMinWidth(305 );
            stage.setMinHeight(382);
            stage.setScene(new Scene(root));

            object = loader.getController();
            ((RestSelectorController) object).setHomeController(this);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryPane.getScene().getWindow());
            stage.setResizable(false);
            stage.show();
        }else if (object instanceof TeamsItineraryController) {

            Parent root = FXMLLoader.load(getClass().getResource("/visual/TeamsItinerary.fxml"));
            Stage stage = new Stage();
            ScalableContentPane scale = new ScalableContentPane();
            scale.setContent(anchorPane);
            stage.setTitle("Itinerario de equipos");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/snb.png")));
            stage.setResizable(true);
            stage.setScene(new Scene(scale));
            stage.setMinWidth(1148);
            stage.setMinHeight(632);

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
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/snb.png")));
            stage.setResizable(true);
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

        else if (object instanceof CrudsController) {

            Parent root = FXMLLoader.load(getClass().getResource("/visual/Cruds.fxml"));
            Stage stage = new Stage();
            ScalableContentPane scale = new ScalableContentPane();
            scale.setContent(anchorPane);
            stage.setTitle("Gesti\u00f3n de datos");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/snb.png")));
            stage.setResizable(true);
            stage.setMinWidth(1015);
            stage.setMinHeight(506);
            stage.setScene(new Scene(scale));

            object = loader.getController();
            ((CrudsController) object).setHomeController(this);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryPane.getScene().getWindow());

            stage.show();
        }
        else if (object instanceof StatisticsResumeController) {

            Parent root = FXMLLoader.load(getClass().getResource("/visual/StatisticsResume.fxml"));
            Stage stage = new Stage();

            stage.setTitle("Resumen estad\u00edstico");
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/snb.png")));
            stage.setResizable(true);
            stage.setScene(new Scene(root));

            object = loader.getController();
            ((StatisticsResumeController) object).setHomeController(this);

            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(primaryPane.getScene().getWindow());
            stage.setMinWidth(350);
            stage.setMinHeight(300);
            stage.show();
        }
    }




    @FXML
    void exportSelectedCalendar(ActionEvent event) {
        /*int calendarToExport = CalendarController.selectedCalendar;
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
        }*/
        boolean all = false;
        if(Executer.getInstance().getResultStates().isEmpty()){
            notification = getNotification();
            notification.setTitle("Exportaci\u00f3n de Calendario");
            notification.setMessage("No hay calendarios para exportar");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(2));
        }
        else{
            DataFiles.getSingletonDataFiles().exportItinerary(all);
        }
    }

    @FXML
    void exportAllCalendar(ActionEvent event) {
        boolean all = true;
        if(Executer.getInstance().getResultStates().isEmpty()){
            notification = getNotification();
            notification.setTitle("Exportaci\u00f3n de Calendario");
            notification.setMessage("No hay calendarios para exportar");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(2));
        }
        else{
            DataFiles.getSingletonDataFiles().exportItinerary(all);
        }
    }




}
