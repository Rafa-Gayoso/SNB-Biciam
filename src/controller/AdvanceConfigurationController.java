package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXListView;
import execute.Executer;
import operators.heuristics.HeuristicOperatorType;
import operators.mutation.MutationOperatorType;
import utils.DataFiles;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
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
    private final String MUTATTIONS_ADDRESS = "src\\files\\Mutations.xlsx";
    private final String HEURISTICS_ADDRESS = "src\\files\\Heuristics.xlsx";

    private TrayNotification notification;

    @FXML
    private JFXListView<String> mutationListView;
    @FXML
    private JFXListView<String> heuristicsListView;


    @FXML
    private JFXButton select;

    public static boolean ok = true;




    public AdvanceConfigurationController() {
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        List<String> dataRead = DataFiles.getSingletonDataFiles().readExcelFiles(MUTATTIONS_ADDRESS);
        List<String> mutations = new ArrayList<>();
        for (String s : dataRead) {
            String[] mutation = s.split("\\.");
            mutations.add(mutation[0]);
        }
        mutationListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        mutationListView.setItems(FXCollections.observableList(mutations));

        mutationListView.getSelectionModel().selectAll();

        dataRead = DataFiles.getSingletonDataFiles().readExcelFiles(HEURISTICS_ADDRESS);

        heuristicsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        heuristicsListView.setItems(FXCollections.observableList(dataRead));

        heuristicsListView.getSelectionModel().selectAll();


    }


    @FXML
    void saveNewAdvancesConfigurations(ActionEvent event) throws IOException {

        ArrayList<Integer> indexesMutations = new ArrayList<>(mutationListView.getSelectionModel().getSelectedIndices());
        ArrayList<Integer> indexesHeuristics = new ArrayList<>(heuristicsListView.getSelectionModel().getSelectedIndices());
        if (indexesMutations.isEmpty()) {
            notification = getNotification();
            notification.setTitle("Selección de cambios");
            notification.setMessage("Debe escoger al menos una mutación");
            notification.setNotificationType(NotificationType.ERROR);
            notification.setRectangleFill(Paint.valueOf("#2F2484"));
            notification.setAnimationType(AnimationType.FADE);
            notification.showAndDismiss(Duration.seconds(1));
        }
        else if(indexesHeuristics.isEmpty()){
            notification = getNotification();
            notification.setTitle("Selección de cambios");
            notification.setMessage("Debe escoger al menos una heurística");
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
            Executer.getInstance().setHeuristics(heuristicOperatorTypes);

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
