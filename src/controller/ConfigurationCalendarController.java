package controller;

import com.jfoenix.controls.*;
import definition.TTPDefinition;
import execute.Executer;
import operators.heuristics.HeuristicOperatorType;
import operators.mutation.MutationOperatorType;
import utils.DataFiles;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
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

public class ConfigurationCalendarController implements Initializable {

    private TrayNotification notification;
    private HomeController homeController;
    public static boolean secondRound = false;
    public static boolean ok = true;

    public static int teams;
    public static ArrayList<String> teamsNames;
    public static ArrayList<Integer> selectedIndexes;
    @FXML
    private JFXListView<String> teamsSelectionListView;

    @FXML
    private JFXToggleButton selectAll;

    @FXML
    private JFXButton select;

    @FXML
    private JFXToggleButton secondRoundButton;

    @FXML
    private Spinner<Integer> maxHomeGamesSpinner;

    @FXML
    private Spinner<Integer> maxVisitorGamesSpinner;


    @FXML
    private JFXButton advanceConfigurationBtn;


    @FXML
    void selectAllTeams(ActionEvent event) {
        if (selectAll.isSelected()) {

            teamsSelectionListView.getSelectionModel().selectAll();
        } else {
            teamsSelectionListView.getSelectionModel().clearSelection();
        }
    }



    @FXML
    void setSecondRound(ActionEvent event) {
        if (secondRoundButton.isSelected()) {
            secondRoundButton.setText("Sí");
        } else {
            secondRoundButton.setText("No");
        }
    }

    @FXML
    void selectTeams(ActionEvent event) throws IOException {
        validateData();

    }

    @FXML
    void advanceConfiguration(ActionEvent event) throws IOException {
        showAdvanceConfiguration();

    }

    private void validateData() throws IOException {

        selectedIndexes = new ArrayList<>(teamsSelectionListView.getSelectionModel().getSelectedIndices());
        teamsNames = new ArrayList<>();

        for (int index : selectedIndexes) {
            teamsNames.add(DataFiles.getSingletonDataFiles().getAcronyms().get(index));
        }

        if (selectedIndexes.size() <= 2) {
            showNotification("Debe escoger al menos dos equipos");
            ok = false;
        }

        if (ok) {
            HomeController.escogidos = true;
            teams = selectedIndexes.size();

            secondRound = secondRoundButton.isSelected();
            int localGames = maxHomeGamesSpinner.getValueFactory().getValue();
            int visitorGames = maxVisitorGamesSpinner.getValueFactory().getValue();

            TTPDefinition.getInstance().setTeamIndexes(selectedIndexes);
            TTPDefinition.getInstance().setDobleVuelta(secondRound);
            TTPDefinition.getInstance().setCantVecesLocal(localGames);
            TTPDefinition.getInstance().setCantVecesVisitante(visitorGames);
            ArrayList<MutationOperatorType> mutationsOperatorTypes = new ArrayList<>();
            mutationsOperatorTypes.add(MutationOperatorType.CHANGE_DATE_ORDER);
            mutationsOperatorTypes.add(MutationOperatorType.CHANGE_DATE_POSITION);
            mutationsOperatorTypes.add(MutationOperatorType.CHANGE_DUEL);
            mutationsOperatorTypes.add(MutationOperatorType.SWAP_DATES);
            ArrayList<HeuristicOperatorType> heuristicOperatorTypes = new ArrayList<>();
            heuristicOperatorTypes.add(HeuristicOperatorType.DUEL_HEURISTIC);
            heuristicOperatorTypes.add(HeuristicOperatorType.DATE_HEURISTIC);
            Executer.getInstance().setMutations(mutationsOperatorTypes);
            Executer.getInstance().setHeuristics(heuristicOperatorTypes);

            showTeamsMatrix();
        }
        ok = true;
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {


        //boolean existingConfiguration = false;

        /*if(controller.getLastSavedConfiguration() != null){
            existingConfiguration = true;
            configuration = controller.getLastSavedConfiguration();
        }*/

        List<String> teams = DataFiles.getSingletonDataFiles().getTeams();

        teamsSelectionListView.setItems(FXCollections.observableArrayList(teams));
        teamsSelectionListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        teamsSelectionListView.setOnMouseClicked(event -> {
            int indices = teamsSelectionListView.getSelectionModel().getSelectedIndices().size();
            if (indices > 1) {
                int valTempLocal = maxHomeGamesSpinner.getValue();
                int valTempVisitor = maxVisitorGamesSpinner.getValue();

                int maxGamesOther = indices / 2;
                maxHomeGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGamesOther));
                maxVisitorGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGamesOther));

                if(valTempLocal <= maxGamesOther){
                    maxHomeGamesSpinner.getValueFactory().setValue(valTempLocal);
                }
                else{
                    maxHomeGamesSpinner.getValueFactory().setValue(maxGamesOther);
                }

                if(valTempVisitor <= maxGamesOther){
                    maxVisitorGamesSpinner.getValueFactory().setValue(valTempVisitor);
                }
                else{
                    maxVisitorGamesSpinner.getValueFactory().setValue(maxGamesOther);
                }
            } else{
                maxHomeGamesSpinner.getValueFactory().setValue(4);
                maxVisitorGamesSpinner.getValueFactory().setValue(4);
            }


            if (indices == DataFiles.getSingletonDataFiles().getTeams().size()) {
                selectAll.setSelected(true);
            } else {
                selectAll.setSelected(false);
            }
        });

        //if(!existingConfiguration){
            HomeController.escogidos = false;
            selectAll.setSelected(true);

            secondRoundButton.setSelected(true);
            teamsSelectionListView.getSelectionModel().selectAll();

            int maxGames = teamsSelectionListView.getSelectionModel().getSelectedIndices().size() / 2;
            maxHomeGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));
            maxVisitorGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));
        /*}
        else{
            HomeController.escogidos = true;



            if(configuration.getTeamsIndexes().size() == DataFiles.getSingletonDataFiles().getTeams().size()){
                selectAll.setSelected(true);
                teamsSelectionListView.getSelectionModel().selectAll();
            }
            else{
                selectAll.setSelected(false);

                teamsSelectionListView.getSelectionModel().clearSelection();
                int[] array = new int[configuration.getTeamsIndexes().size()];
                for (int i = 0; i < configuration.getTeamsIndexes().size(); i++){
                    array[i] = configuration.getTeamsIndexes().get(i);
                }
                teamsSelectionListView.getSelectionModel().selectIndices(-1, array);

            }


            if(configuration.isSecondRoundCalendar()){
                secondRoundButton.setSelected(true);
                secondRoundButton.setText("Sí");
            }
            else{
                secondRoundButton.setSelected(false);
                secondRoundButton.setText("No");
            }



            int maxGames = teamsSelectionListView.getSelectionModel().getSelectedIndices().size() / 2;
            maxHomeGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));
            maxVisitorGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));
            maxHomeGamesSpinner.getValueFactory().setValue(4);
            maxVisitorGamesSpinner.getValueFactory().setValue(4);
            ConfigurationCalendarController.teams = configuration.getTeamsIndexes().size();
        }*/


        notification = new TrayNotification();

        //if(!existingConfiguration){
            maxHomeGamesSpinner.getValueFactory().setValue(4);
            maxVisitorGamesSpinner.getValueFactory().setValue(4);
            ConfigurationCalendarController.teams = 0;
        //}

    }

    private void showNotification(String message) {
        notification.setTitle("Selección de equipos.");
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.ERROR);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(1));
    }


    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    void showTeamsMatrix() throws IOException {
        AnchorPane structureOver = homeController.getPrincipalPane();
        homeController.createPage(new SelectGridController(), structureOver, "/visual/SelectGrid.fxml");

        homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
    }

    void showAdvanceConfiguration() throws IOException {
        AnchorPane structureOver = homeController.getPrincipalPane();
        homeController.createPage(new AdvanceConfigurationController(), structureOver, "/visual/AdvanceConfiguration.fxml");
        homeController.getButtonReturnSelectionTeamConfiguration().setVisible(false);
    }
}