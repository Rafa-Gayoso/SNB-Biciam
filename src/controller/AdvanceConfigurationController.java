package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXRadioButton;
import com.jfoenix.controls.JFXToggleButton;
import execute.Executer;
import javafx.scene.control.*;
import operators.heuristics.HeuristicOperatorType;
import operators.mutation.MutationOperatorType;
import utils.DataFiles;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdvanceConfigurationController implements Initializable {

    private HomeController homeController;

    private TrayNotification notification;

    @FXML
    private JFXListView<String> mutationListView;
    @FXML
    private JFXListView<String> heuristicsListView;

    @FXML
    private Spinner<Integer> iterationsSpinner;

    @FXML
    private Spinner<Integer> executionsSpinner;


    @FXML
    private JFXButton select;

    public static boolean ok = true;


    @FXML
    private JFXRadioButton radioHC;

    @FXML
    private ToggleGroup metaheuristics;

    @FXML
    private JFXRadioButton radioEE;

    @FXML
    private JFXRadioButton radioRS;


    public AdvanceConfigurationController() {
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        iterationsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE, Executer.getInstance().getITERATIONS()));
        executionsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1,Integer.MAX_VALUE, Executer.getInstance().getEXECUTIONS()));
        List<String> mutations = DataFiles.getSingletonDataFiles().getMutations();

        mutationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        mutationListView.setItems(FXCollections.observableList(mutations));

        mutationListView.getSelectionModel().selectAll();

        List<String> heuristics = DataFiles.getSingletonDataFiles().getHeuristics();

        heuristicsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        heuristicsListView.setItems(FXCollections.observableList(heuristics));

        heuristicsListView.getSelectionModel().selectAll();

        formatSpinner(iterationsSpinner);

        formatSpinner(executionsSpinner);
    }

    private void formatSpinner(Spinner<Integer> spinner) {
        spinner.getEditor().setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d{0,9}?")) ? change : null));

        spinner.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                spinner.getValueFactory().setValue(Integer.parseInt(spinner.getEditor().getText()));
            }
        });
    }


    @FXML
    void saveNewAdvancesConfigurations(ActionEvent event) throws IOException {

        ArrayList<Integer> indexesMutations = new ArrayList<>(mutationListView.getSelectionModel().getSelectedIndices());
        ArrayList<Integer> indexesHeuristics = new ArrayList<>(heuristicsListView.getSelectionModel().getSelectedIndices());
        if (indexesMutations.isEmpty()) {
            notification = getNotification();
            notification.setTitle("Selecci\u00f3n de cambios");
            notification.setMessage("Debe escoger al menos una mutaci\u00f3n");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(1));
        }
        else if(indexesHeuristics.isEmpty()){
            notification = getNotification();
            notification.setTitle("Selecci\u00f3n de cambios");
            notification.setMessage("Debe escoger al menos una heur\u00edstica");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(1));
        }
        else{
            //Crear la lista de mutationes a utilizar
            ArrayList<MutationOperatorType> mutationOperatorTypes = new ArrayList<>();
            //Quedarse con todos los valores del enum de las mutaciones
            MutationOperatorType [] mutationsOperators = MutationOperatorType.values();
            //for por cada indice seleccionado de la listview
            for (int i: indexesMutations) {
                //for para buscar de todas las mutationces disponibles cual se va a añadir
                for (int j = 0; j < mutationsOperators.length; j++) {
                    //Quedarse con la mutaticon en la poscion J
                    MutationOperatorType operatorType = mutationsOperators[j];
                    //pedirle el ordinal a la mutatcion para ver si coincide con el indice de la lista
                    if(operatorType.ordinal() == i){
                        mutationOperatorTypes.add(operatorType);
                        //para romper el ciclo cuando encuentre la mutacion
                        break;
                    }
                }

            }
            Executer.getInstance().setMutations(mutationOperatorTypes);

            ArrayList<HeuristicOperatorType> heuristicOperatorTypes = new ArrayList<>();
            HeuristicOperatorType [] heuristicOperators = HeuristicOperatorType.values();

            for (int i: indexesHeuristics) {
                for (int j = 0; j < heuristicOperators.length; j++) {

                    HeuristicOperatorType operatorType = heuristicOperators[j];
                    if(operatorType.ordinal() == i){
                        heuristicOperatorTypes.add(operatorType);
                        break;
                    }
                }

            }



            int selectedMH = -1;
            if(radioHC.isSelected()){
                selectedMH = 0;
            }else if(radioEE.isSelected()){
                selectedMH = 1;
            }
            Executer.getInstance().setSelectedMH(selectedMH);
            Executer.getInstance().setHeuristics(heuristicOperatorTypes);
            Executer.getInstance().setITERATIONS(iterationsSpinner.getValueFactory().getValue());
            Executer.getInstance().setEXECUTIONS(executionsSpinner.getValueFactory().getValue());

            AnchorPane structureOver = homeController.getPrincipalPane();
            homeController.createPage(new ConfigurationCalendarController(), structureOver, "/visual/ConfigurationCalendar.fxml");
            homeController.getButtonReturnSelectionTeamConfiguration().setVisible(false);
        }
    }

    private TrayNotification getNotification() {
        return new TrayNotification();
    }

    void showTeamsMatrix() throws IOException {
        AnchorPane structureOver = homeController.getPrincipalPane();
        homeController.createPage(new SelectGridController(), structureOver, "/visual/SelectGrid.fxml");
        homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }
}
