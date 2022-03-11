package controller;

import com.jfoenix.controls.*;
import definition.TTPDefinition;
import definition.state.CalendarState;
import execute.Executer;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import operators.heuristics.HeuristicOperatorType;
import operators.interfaces.IChampionGame;
import operators.interfaces.ICreateInitialSolution;
import operators.interfaces.IInauguralGame;
import operators.interfaces.ISecondRound;
import operators.mutation.MutationOperatorType;
import org.controlsfx.control.CheckListView;
import utils.CalendarConfiguration;
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
import utils.ServiceCalendar;
import utils.ServiceOccidentOrientCalendar;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ConfigurationCalendarController implements Initializable, ISecondRound, IInauguralGame, IChampionGame, ICreateInitialSolution {

    private TrayNotification notification;
    private HomeController homeController;
    public static boolean secondRound = true;
    public static boolean inaugural = true;
    public static boolean ok = true;
    public static boolean savedConfiguration = false;
    private CalendarConfiguration lastConfiguration =new CalendarConfiguration();

    //For 3-2 Calendar
    public static boolean series32 = false;

    private int posChampion = -1, posSub = -2;

    private ObservableList<String> listComboChamp;
    private ObservableList<String> listComboSub;

    public static int teams;
    public static ArrayList<String> teamsNames;
    public static ArrayList<Integer> selectedIndexes;


    @FXML
    private CheckListView<String> teamCheckList;



    @FXML
    private AnchorPane panel;

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
    private JFXComboBox<String> comboChamp;

    @FXML
    private JFXTextField calendarId;

    @FXML
    private JFXComboBox<String> comboSub;

    @FXML
    private JFXToggleButton champVsSub;

    @FXML
    private JFXToggleButton inauguralGame;

    @FXML
    private JFXButton btnSwap;

    @FXML
    private Label lblSymmetricSecondRound;

    @FXML
    private JFXToggleButton symmetricSecondRound;

    @FXML
    private Label lblOccidenteVsOriente;

    @FXML
    private JFXToggleButton occidenteVsOrienteToggle;

    @FXML
    private JFXButton restBtn;


    @FXML
    void selectAllTeams(ActionEvent event) {
        if (selectAll.isSelected()) {

            teamCheckList.getCheckModel().checkAll();
        } else {
            teamCheckList.getCheckModel().clearChecks();
        }
    }

    @FXML
    void selectTeams(ActionEvent event) throws Exception {

        //DEBUG
        System.out.println("ConfigurationCalendarController.selectTeams()");

        validateData();
    }

    @FXML
    void advanceConfiguration(ActionEvent event) throws IOException {
        showAdvanceConfiguration();

    }

    private void validateData() throws Exception {

        //DEBUG
        System.out.println("ConfigurationCalendarController.validateData()");

        selectedIndexes = new ArrayList<>(teamCheckList.getCheckModel().getCheckedIndices());
        teamsNames = new ArrayList<>();

        int occAmount = 0;
        int orAmount = 0;


        for (int index : selectedIndexes) {
            teamsNames.add(DataFiles.getSingletonDataFiles().getAcronyms().get(index));

            if (occidenteVsOrienteToggle.isSelected()) {
                if (DataFiles.getSingletonDataFiles().getLocations().get(index).equalsIgnoreCase("Occidental")) {
                    occAmount++;
                } else {
                    orAmount++;
                }
            }
        }

        if (calendarId.getText().equalsIgnoreCase(" ") || calendarId.getText().equalsIgnoreCase("")) {
            showNotification("Debe Introducir el identificador del calendario");
            ok = false;
        } else if (selectedIndexes.size() <= 2) {
            showNotification("Debe escoger al menos dos equipos");
            ok = false;
        } else if (selectedIndexes.size() % 2 != 0) {
            showNotification("Debe escoger una cantidad par de equipos");
            ok = false;
        } else if (occidenteVsOrienteToggle.isSelected() && (occAmount != orAmount)) {
            showNotification("Las cantidades de equipos seleccionados" + "\n" + " de Oriente y Occidente deben ser iguales");
            ok = false;
        } else if (inauguralGame.isSelected()) {
            if (champVsSub.isSelected()) {
                validateChampionAndSubchampion();
            } else {
                showNotification("Debe escoger al campe\u00f3n y subcampe\u00f3n.");
                ok = false;
            }
        } else if (champVsSub.isSelected()) {
            validateChampionAndSubchampion();
        }

        if(ok) {

            HomeController.escogidos = true;    //Ya los equipos están seleccionados
            teams = selectedIndexes.size();     //

            int posChampion = -1;
            int posSub = -1;
            if (champVsSub.isSelected()) {      //Si hay juego de campeon contra subcampeon
                String champion = comboChamp.getSelectionModel().getSelectedItem();     //Leer el campoeon seleccionado
                String subchampion = comboSub.getSelectionModel().getSelectedItem();
                posChampion = DataFiles.getSingletonDataFiles().getTeams().indexOf(champion);   //Obtener indice del campeon
                posSub = DataFiles.getSingletonDataFiles().getTeams().indexOf(subchampion);     //Obtener indice del subcampeon
            }

            secondRound = secondRoundButton.isSelected();
            int localGames = maxHomeGamesSpinner.getValueFactory().getValue();
            int visitorGames = maxVisitorGamesSpinner.getValueFactory().getValue();

            TTPDefinition.getInstance().setTeamIndexes(selectedIndexes);
            TTPDefinition.getInstance().setSymmetricSecondRound(symmetricSecondRound.isSelected());
            TTPDefinition.getInstance().setSecondRound(secondRound);
            TTPDefinition.getInstance().setCantVecesLocal(localGames);
            TTPDefinition.getInstance().setCantVecesVisitante(visitorGames);
            TTPDefinition.getInstance().setChampionVsSub(champVsSub.isSelected());
            TTPDefinition.getInstance().setFirstPlace(posChampion);
            TTPDefinition.getInstance().setSecondPlace(posSub);
            TTPDefinition.getInstance().setInauguralGame(inauguralGame.isSelected());
            TTPDefinition.getInstance().setOccidentVsOrient(occidenteVsOrienteToggle.isSelected());
            TTPDefinition.getInstance().setCalendarId(calendarId.getText());
            TTPDefinition.getInstance().setLss(series32);


            if (Executer.getInstance().getMutations().isEmpty()) {
                ArrayList<MutationOperatorType> mutationsOperatorTypes = new ArrayList<>();
                mutationsOperatorTypes.add(MutationOperatorType.CHANGE_DATE_ORDER);
                mutationsOperatorTypes.add(MutationOperatorType.CHANGE_DATE_POSITION);
                mutationsOperatorTypes.add(MutationOperatorType.CHANGE_DUEL);
                mutationsOperatorTypes.add(MutationOperatorType.SWAP_DATES);

                if (TTPDefinition.getInstance().isSecondRound() && !TTPDefinition.getInstance().isSymmetricSecondRound()) {
                    mutationsOperatorTypes.add(MutationOperatorType.CHANGE_TEAMS_OPERATOR);
                    mutationsOperatorTypes.add(MutationOperatorType.CHANGE_DATE_DUELS_ORDER_OPERATOR);
                    mutationsOperatorTypes.add(MutationOperatorType.CHANGE_DATE_SINGLE_DUEL_ORDER_OPERATOR);
                    mutationsOperatorTypes.add(MutationOperatorType.CHANGE_LOCAL_VISITOR_SINGLE_TEAM_OPERATOR);
                }


                Executer.getInstance().setMutations(mutationsOperatorTypes);
            } else {
                if ((TTPDefinition.getInstance().isSecondRound() && TTPDefinition.getInstance().isSymmetricSecondRound())
                        || !TTPDefinition.getInstance().isSecondRound()) {
                    MutationOperatorType[] types = MutationOperatorType.values();

                    for (int i = 4; i < types.length; i++) {
                        if (Executer.getInstance().getMutations().contains(types[i])) {
                            Executer.getInstance().getMutations().remove(types[i]);
                        }
                    }

                }
            }

            if (Executer.getInstance().getHeuristics().isEmpty()) {
                ArrayList<HeuristicOperatorType> heuristicOperatorTypes = new ArrayList<>();
                heuristicOperatorTypes.add(HeuristicOperatorType.DUEL_HEURISTIC);
                heuristicOperatorTypes.add(HeuristicOperatorType.DATE_HEURISTIC);
                Executer.getInstance().setHeuristics(heuristicOperatorTypes);
            }
        }
            showTeamsMatrix();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        //DEBUG
        System.out.println("ConfigurationCalendarController.initialize()");



        lastConfiguration = new CalendarConfiguration();
        int calendarPosition = CalendarController.selectedCalendar;

        DataFiles.getSingletonDataFiles().readTeams();
        List<String> teams = DataFiles.getSingletonDataFiles().getTeams();

        teamCheckList.setItems(FXCollections.observableArrayList(teams));
        //teamsSelectionListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        teamCheckList.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
            @Override
            public void onChanged(Change<? extends String> change) {
                {
                    try{
                        int indices = teamCheckList.getCheckModel().getCheckedIndices().size();
                        if (indices > 1) {
                            int valTempLocal = maxHomeGamesSpinner.getValue();
                            int valTempVisitor = maxVisitorGamesSpinner.getValue();

                            int maxGamesOther = indices / 2;
                            maxHomeGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGamesOther));
                            maxVisitorGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGamesOther));

                            if (valTempLocal <= maxGamesOther) {
                                maxHomeGamesSpinner.getValueFactory().setValue(valTempLocal);
                            } else {
                                maxHomeGamesSpinner.getValueFactory().setValue(maxGamesOther);
                            }

                            if (valTempVisitor <= maxGamesOther) {
                                maxVisitorGamesSpinner.getValueFactory().setValue(valTempVisitor);
                            } else {
                                maxVisitorGamesSpinner.getValueFactory().setValue(maxGamesOther);
                            }
                        } else {
                            maxHomeGamesSpinner.getValueFactory().setValue(4);
                            maxVisitorGamesSpinner.getValueFactory().setValue(4);
                        }
                        comboChamp.getSelectionModel().clearSelection();
                        comboSub.getSelectionModel().clearSelection();

                        listComboChamp.clear();
                        listComboChamp.addAll(teamCheckList.getCheckModel().getCheckedItems());

                        listComboSub.clear();
                        listComboSub.addAll(teamCheckList.getCheckModel().getCheckedItems());

                        if (indices == DataFiles.getSingletonDataFiles().getTeams().size()) {
                            selectAll.setSelected(true);
                        } else {
                            selectAll.setSelected(false);
                        }
                    }catch (Exception e){

                    }

                }
            }
        });

        teamCheckList.setOnMouseClicked(event -> {
            int indices = teamCheckList.getCheckModel().getCheckedIndices().size();
            if (indices > 1) {
                int valTempLocal = maxHomeGamesSpinner.getValue();
                int valTempVisitor = maxVisitorGamesSpinner.getValue();

                int maxGamesOther = indices / 2;
                maxHomeGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGamesOther));
                maxVisitorGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGamesOther));

                if (valTempLocal <= maxGamesOther) {
                    maxHomeGamesSpinner.getValueFactory().setValue(valTempLocal);
                } else {
                    maxHomeGamesSpinner.getValueFactory().setValue(maxGamesOther);
                }

                if (valTempVisitor <= maxGamesOther) {
                    maxVisitorGamesSpinner.getValueFactory().setValue(valTempVisitor);
                } else {
                    maxVisitorGamesSpinner.getValueFactory().setValue(maxGamesOther);
                }
            } else {
                maxHomeGamesSpinner.getValueFactory().setValue(4);
                maxVisitorGamesSpinner.getValueFactory().setValue(4);
            }
            comboChamp.getSelectionModel().clearSelection();
            comboSub.getSelectionModel().clearSelection();

            listComboChamp.clear();
            listComboChamp.addAll(teamCheckList.getCheckModel().getCheckedItems());

            listComboSub.clear();
            listComboSub.addAll(teamCheckList.getCheckModel().getCheckedItems());

            if (indices == DataFiles.getSingletonDataFiles().getTeams().size()) {
                selectAll.setSelected(true);
            } else {
                selectAll.setSelected(false);
            }
        });

        calendarId.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[A-Za-z0-9 _.]*$")) ? change : null));

        //if(!existingConfiguration){
        if (calendarPosition == -1 && TTPDefinition.getInstance().getTeamsIndexes() == null) {
            HomeController.escogidos = false;
            selectAll.setSelected(true);

            secondRoundButton.setSelected(true);
            teamCheckList.getCheckModel().checkAll();

            int maxGames = teamCheckList.getCheckModel().getCheckedIndices().size() / 2;
            maxHomeGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));
            maxVisitorGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));

            lblSymmetricSecondRound.setVisible(true);
            symmetricSecondRound.setVisible(true);
            secondRoundButton.setSelected(true);
            teamCheckList.getCheckModel().checkAll();
            this.teams = teamCheckList.getCheckModel().getCheckedIndices().size();
            listComboChamp = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());

            listComboSub = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());
            comboChamp.setItems(listComboChamp);
            //comboChamp.getSelectionModel().select(5);
            comboSub.setItems(listComboSub);
            //comboSub.getSelectionModel().select(10);
            occidenteVsOrienteToggle.setSelected(false);

            champVsSub.setSelected(true);
            comboChamp.setVisible(true);
            comboSub.setVisible(true);
            btnSwap.setVisible(true);
        } else if(TTPDefinition.getInstance().getTeamsIndexes() != null && calendarPosition == -1){
            HomeController.escogidos = true;

            calendarId.setText(TTPDefinition.getInstance().getCalendarId());

            if (TTPDefinition.getInstance().isInauguralGame()) {
                inauguralGame.setSelected(true);
                inauguralGame.setText("S\u00ed");
            } else {
                inauguralGame.setSelected(false);
                inauguralGame.setText("No");
            }

            if (TTPDefinition.getInstance().getTeamsIndexes().size() == DataFiles.getSingletonDataFiles().getTeams().size()) {
                selectAll.setSelected(true);
                teamCheckList.getCheckModel().checkAll();
                listComboChamp = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());
                listComboSub = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());
                comboChamp.setItems(listComboChamp);
                comboSub.setItems(listComboSub);
            } else {
                selectAll.setSelected(false);

                teamCheckList.getCheckModel().clearChecks();
                int[] array = new int[TTPDefinition.getInstance().getTeamsIndexes().size()];
                for (int i = 0; i < TTPDefinition.getInstance().getTeamsIndexes().size(); i++) {
                    array[i] = TTPDefinition.getInstance().getTeamsIndexes().get(i);
                }
                teamCheckList.getCheckModel().checkIndices(array);
                listComboChamp = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());
                listComboSub = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());
                comboChamp.setItems(listComboChamp);
                comboSub.setItems(listComboSub);
            }


            if (TTPDefinition.getInstance().isSecondRound()) {
                secondRoundButton.setSelected(true);
                secondRoundButton.setText("S\u00ed");
                lblSymmetricSecondRound.setVisible(true);
                symmetricSecondRound.setVisible(true);
                if (TTPDefinition.getInstance().isSymmetricSecondRound()) {
                    symmetricSecondRound.setSelected(true);
                    symmetricSecondRound.setText("S\u00ed");
                } else {
                    symmetricSecondRound.setSelected(false);
                    symmetricSecondRound.setText("No");
                }
            } else {
                symmetricSecondRound.setSelected(false);
                secondRoundButton.setSelected(false);
                secondRoundButton.setText("No");
                lblSymmetricSecondRound.setVisible(false);
                symmetricSecondRound.setVisible(false);
                symmetricSecondRound.setText("No");
            }

            if (TTPDefinition.getInstance().isChampionVsSub()) {
                champVsSub.setSelected(true);

                champVsSub.setText("S\u00ed");
                comboChamp.setVisible(true);
                comboSub.setVisible(true);
                btnSwap.setVisible(true);

                int champion = TTPDefinition.getInstance().getFirstPlace();
                int second = TTPDefinition.getInstance().getSecondPlace();
                if(champion!=-1 && second !=-1 ){
                    comboChamp.setValue(teams.get(TTPDefinition.getInstance().getFirstPlace()));
                    comboSub.setValue(teams.get(TTPDefinition.getInstance().getSecondPlace()));
                    listComboSub.remove(teams.get(TTPDefinition.getInstance().getFirstPlace()));
                }
                else if( champion == -1 && second !=-1){
                    comboChamp.getSelectionModel().select(-1);
                    comboSub.setValue(teams.get(TTPDefinition.getInstance().getSecondPlace()));
                    //listComboSub.remove(teams.get(TTPDefinition.getInstance().getFirstPlace()));
                }else if( champion != -1 && second ==-1){
                    comboChamp.setValue(teams.get(TTPDefinition.getInstance().getFirstPlace()));
                    comboSub.getSelectionModel().select(-1);
                    //listComboSub.remove(teams.get(TTPDefinition.getInstance().getFirstPlace()));
                }else{
                    comboChamp.getSelectionModel().select(-1);
                    comboSub.getSelectionModel().select(-1);
                }

            } else {
                champVsSub.setText("No");
                champVsSub.setSelected(false);
                comboChamp.setVisible(false);
                comboSub.setVisible(false);
                btnSwap.setVisible(false);
            }

            int maxGames = teamCheckList.getCheckModel().getCheckedItems().size() / 2;
            maxHomeGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));
            maxVisitorGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));
            maxHomeGamesSpinner.getValueFactory().setValue(4);
            maxVisitorGamesSpinner.getValueFactory().setValue(4);
            ConfigurationCalendarController.teams = TTPDefinition.getInstance().getTeamsIndexes().size();
            if (TTPDefinition.getInstance().isOccidentVsOrient()) {
                occidenteVsOrienteToggle.setSelected(true);
                occidenteVsOrienteToggle.setText("S\u00ed");

            } else {
                occidenteVsOrienteToggle.setSelected(false);
                occidenteVsOrienteToggle.setText("No");
            }
        }else {
            HomeController.escogidos = true;
            CalendarConfiguration configuration = ((CalendarState) Executer.getInstance().getResultStates().get(calendarPosition)).getConfiguration();
            calendarId.setText(configuration.getCalendarId());

            if (configuration.isInauguralGame()) {
                inauguralGame.setSelected(true);
                inauguralGame.setText("S\u00ed");
            } else {
                inauguralGame.setSelected(false);
                inauguralGame.setText("No");
            }

            if (configuration.getTeamsIndexes().size() == DataFiles.getSingletonDataFiles().getTeams().size()) {
                selectAll.setSelected(true);
                teamCheckList.getCheckModel().checkAll();
                listComboChamp = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());
                listComboSub = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());
                comboChamp.setItems(listComboChamp);
                comboSub.setItems(listComboSub);
            } else {
                selectAll.setSelected(false);

                teamCheckList.getCheckModel().clearChecks();
                int[] array = new int[configuration.getTeamsIndexes().size()];
                for (int i = 0; i < configuration.getTeamsIndexes().size(); i++) {
                    array[i] = configuration.getTeamsIndexes().get(i);
                }
                teamCheckList.getCheckModel().checkIndices( array);
                listComboChamp = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());
                listComboSub = FXCollections.observableArrayList(teamCheckList.getCheckModel().getCheckedItems());
                comboChamp.setItems(listComboChamp);
                comboSub.setItems(listComboSub);
            }


            if (configuration.isSecondRoundCalendar()) {
                secondRoundButton.setSelected(true);
                secondRoundButton.setText("S\u00ed");
                lblSymmetricSecondRound.setVisible(true);
                symmetricSecondRound.setVisible(true);
                if (configuration.isSymmetricSecondRound()) {
                    symmetricSecondRound.setSelected(true);
                    symmetricSecondRound.setText("S\u00ed");
                } else {
                    symmetricSecondRound.setSelected(false);
                    symmetricSecondRound.setText("No");
                }
            } else {
                secondRoundButton.setSelected(false);
                secondRoundButton.setText("No");
                symmetricSecondRound.setSelected(false);
                lblSymmetricSecondRound.setVisible(false);
                symmetricSecondRound.setVisible(false);
            }

            if (configuration.isChampionVsSecondPlace()) {
                champVsSub.setSelected(true);

                champVsSub.setText("S\u00ed");
                comboChamp.setVisible(true);
                comboSub.setVisible(true);
                btnSwap.setVisible(true);

                comboChamp.setValue(teams.get(configuration.getChampion()));
                comboSub.setValue(teams.get(configuration.getSecondPlace()));
                listComboSub.remove(teams.get(configuration.getChampion()));
            } else {
                champVsSub.setText("No");
                champVsSub.setSelected(false);
                comboChamp.setVisible(false);
                comboSub.setVisible(false);
                btnSwap.setVisible(false);
            }

            int maxGames =teamCheckList.getCheckModel().getCheckedItems().size() / 2;
            maxHomeGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));
            maxVisitorGamesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, maxGames));
            maxHomeGamesSpinner.getValueFactory().setValue(4);
            maxVisitorGamesSpinner.getValueFactory().setValue(4);
            ConfigurationCalendarController.teams = configuration.getTeamsIndexes().size();
            if (configuration.isOccidenteVsOriente()) {
                occidenteVsOrienteToggle.setSelected(true);
                occidenteVsOrienteToggle.setText("S\u00ed");

            } else {
                occidenteVsOrienteToggle.setSelected(false);
                occidenteVsOrienteToggle.setText("No");
            }
        }


        notification = new TrayNotification();

        //if(!existingConfiguration){
        maxHomeGamesSpinner.getValueFactory().setValue(4);
        maxVisitorGamesSpinner.getValueFactory().setValue(4);

        //}
    }

    private void validateChampionAndSubchampion() {
        String champion = comboChamp.getSelectionModel().getSelectedItem();
        String subchampion = comboSub.getSelectionModel().getSelectedItem();
        //posChampion = comboChamp.getSelectionModel().getSelectedIndex();
        //posSub = comboSub.getSelectionModel().getSelectedIndex();
        if (champion == null || subchampion == null) {
            //ok = false;
            showNotification("Debe escoger al campe�n y subcampe�n.");
            ok = false;
        } else if (champion.equalsIgnoreCase(subchampion)) {
            showNotification("El campe�n y subcampe�n deben ser diferentes");
            ok = false;
        } else {
            ok = true;
            posChampion = DataFiles.getSingletonDataFiles().getTeams().indexOf(champion);
            posSub = DataFiles.getSingletonDataFiles().getTeams().indexOf(subchampion);
        }
    }

    @FXML
    void swapTeams(ActionEvent event) {
        String teamSwap = comboSub.getSelectionModel().getSelectedItem();
        comboSub.getSelectionModel().select(comboChamp.getSelectionModel().getSelectedItem());
        comboChamp.getSelectionModel().select(teamSwap);
    }

    @FXML
    void selectTeamChamp(ActionEvent event) {

        listComboSub.clear();
        listComboSub.addAll(teamCheckList.getCheckModel().getCheckedItems());
        listComboSub.remove(comboChamp.getSelectionModel().getSelectedItem());
    }

    @FXML
    void setChampVsSub(ActionEvent event) {
        if (champVsSub.isSelected()) {
            comboChamp.setVisible(true);
            comboSub.setVisible(true);
            btnSwap.setVisible(true);
            champVsSub.setText("S\u00ed");

        } else {
            comboChamp.setVisible(false);
            comboSub.setVisible(false);
            btnSwap.setVisible(false);
            inauguralGame.setSelected(false);
            inauguralGame.setText("No");
            champVsSub.setText("No");
        }
    }

    @FXML
    void setSecondRound(ActionEvent event) {
        if (secondRoundButton.isSelected()) {
            secondRoundButton.setText("S\u00ed");
            lblSymmetricSecondRound.setVisible(true);
            symmetricSecondRound.setVisible(true);
        } else {
            secondRoundButton.setText("No");
            lblSymmetricSecondRound.setVisible(false);
            symmetricSecondRound.setVisible(false);
            symmetricSecondRound.setSelected(false);
            symmetricSecondRound.setText("No");
        }
    }

    @FXML
    void setSymmetricSecondRound(ActionEvent event) {
        if (symmetricSecondRound.isSelected()) {
            symmetricSecondRound.setText("S\u00ed");
        } else {
            symmetricSecondRound.setText("No");
        }
    }

    @FXML
    void setOccidenteVsOriente(ActionEvent event) {
        if (occidenteVsOrienteToggle.isSelected()) {
            occidenteVsOrienteToggle.setText("S\u00ed");
        } else {
            occidenteVsOrienteToggle.setText("No");
        }
    }

    @FXML
    void setInauguralGame(ActionEvent event) {
        if (inauguralGame.isSelected()) {
            inauguralGame.setText("S\u00ed");
            champVsSub.setSelected(true);
            champVsSub.setText("S\u00ed");
            comboChamp.setVisible(true);
            comboSub.setVisible(true);
            btnSwap.setVisible(true);
        } else {
            inauguralGame.setText("No");
        }
    }

    @FXML
    void selectTeamSubChamp(ActionEvent event) {
        //System.out.println("Sub-Champion Team Selected => " + comboSub.getSelectionModel().getSelectedItem());

        //comentario



        /*
        if (comboSub.getSelectionModel().getSelectedItem().equalsIgnoreCase(comboChamp.getSelectionModel().getSelectedItem())) {
            comboChamp.getSelectionModel().clearSelection();
        }

         */
    }

    private void showNotification(String message) {
        notification.setTitle("Selecci\u00f3n de equipos.");
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.ERROR);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(1));
    }


    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    void showTeamsMatrix() throws Exception {

        //DEBUG
        System.out.println("ConfigurationCalendarController.showTeamsMatrix()");

        //System.out.println(restIndices);
        //TTPDefinition.getInstance().setRestIndexes(restIndices);
        if (TTPDefinition.getInstance().isSecondRound() && !TTPDefinition.getInstance().isSymmetricSecondRound()) {
            /*TTPDefinition.getInstance().setNumberOfDates(TTPDefinition.getInstance().getTeamsIndexes().size() - 1);
            TTPDefinition.getInstance().setDuelMatrix(generateMatrix(TTPDefinition.getInstance().getCantEquipos()));
            Executer.getInstance().executeEC();
            AnchorPane structureOver = homeController.getPrincipalPane();
            homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);

            homeController.createPage(new CalendarController(), structureOver, "/visual/Calendar.fxml");*/
            StackPane stackPane = new StackPane();
            TTPDefinition.getInstance().setDuelMatrix(generateMatrix(TTPDefinition.getInstance().getCantEquipos())); //generar matriz
            JFXDialog jfxDialog = new JFXDialog();
            JFXDialogLayout content = new JFXDialogLayout();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("/visual/CalendarService.fxml"));
            AnchorPane progressContent = fxmlLoader.load();
            CalendarServiceController serviceController = fxmlLoader.getController();

            content.setBody(progressContent);

            jfxDialog.setContent(content);
            //TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
            jfxDialog.setDialogContainer(stackPane);
            panel.getChildren().add(stackPane);
            stackPane.setLayoutX(400);
            stackPane.setLayoutY(200);
            jfxDialog.setPrefHeight(105);
            jfxDialog.setPrefWidth(432);
            jfxDialog.show();
            if (!TTPDefinition.getInstance().isOccidentVsOrient()) {
                TTPDefinition.getInstance().setNumberOfDates(TTPDefinition.getInstance().getTeamsIndexes().size() - 1);


                ServiceCalendar service = new ServiceCalendar();

                service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        AnchorPane structureOver = homeController.getPrincipalPane();
                        try {
                            //TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
                            //Executer.getInstance().executeEC();
                            homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
                            homeController.createPage(new CalendarController(), structureOver, "/visual/Calendar.fxml");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                });

                service.setOnRunning(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        serviceController.getProgress().progressProperty().bind(service.progressProperty());
                        serviceController.getLblProgress().textProperty().bindBidirectional((Property<String>) service.messageProperty());


                    }
                });


                service.setOnFailed(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        TrayNotification notification = new TrayNotification();
                        notification.setTitle("Generar Calendarios");
                        notification.setMessage("Ocurri\u00f3 un error y no se pudo generar los calendarios");
                        notification.setNotificationType(NotificationType.ERROR);
                        notification.setRectangleFill(Paint.valueOf("#2F2484"));
                        notification.setAnimationType(AnimationType.FADE);
                        notification.showAndDismiss(Duration.seconds(2));

                    }
                });
                service.restart();
                //Executer.getInstance().executeEC();
            } else {

                ServiceOccidentOrientCalendar serviceOccidentOrientCalendar = new ServiceOccidentOrientCalendar();
                //TTPDefinition.getInstance().setDuelMatrix(generateMatrix(TTPDefinition.getInstance().getCantEquipos()));

                serviceOccidentOrientCalendar.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        AnchorPane structureOver = homeController.getPrincipalPane();
                        try {
                            //TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
                            //Executer.getInstance().executeEC();
                            homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
                            homeController.createPage(new CalendarController(), structureOver, "/visual/Calendar.fxml");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

                serviceOccidentOrientCalendar.setOnRunning(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        serviceController.getProgress().progressProperty().bind(serviceOccidentOrientCalendar.progressProperty());
                        serviceController.getLblProgress().textProperty().bindBidirectional((Property<String>) serviceOccidentOrientCalendar.messageProperty());


                    }
                });


                serviceOccidentOrientCalendar.setOnFailed(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent workerStateEvent) {
                        TrayNotification notification = new TrayNotification();
                        notification.setTitle("Generar Calendarios");
                        notification.setMessage("Ocurri\u00f3 un error y no se pudo generar los calendarios");
                        notification.setNotificationType(NotificationType.ERROR);
                        notification.setRectangleFill(Paint.valueOf("#2F2484"));
                        notification.setAnimationType(AnimationType.FADE);
                        notification.showAndDismiss(Duration.seconds(2));

                    }
                });
                serviceOccidentOrientCalendar.restart();
            }


        } else {


            AnchorPane structureOver = homeController.getPrincipalPane();
            homeController.createPage(new SelectGridController(), structureOver, "/visual/SelectGrid.fxml");

            homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
        }

    }

    void showAdvanceConfiguration() throws IOException {
        savedConfiguration = true;
        selectedIndexes = new ArrayList<>(teamCheckList.getCheckModel().getCheckedIndices());
        teamsNames = new ArrayList<>();
        teams = selectedIndexes.size();

        int posChampion = -1;
        int posSub = -1;
        if (champVsSub.isSelected()) {
            String champion = comboChamp.getSelectionModel().getSelectedItem();
            String subchampion = comboSub.getSelectionModel().getSelectedItem();
            posChampion = DataFiles.getSingletonDataFiles().getTeams().indexOf(champion);
            posSub = DataFiles.getSingletonDataFiles().getTeams().indexOf(subchampion);
        }

        secondRound = secondRoundButton.isSelected();
        int localGames = maxHomeGamesSpinner.getValueFactory().getValue();
        int visitorGames = maxVisitorGamesSpinner.getValueFactory().getValue();

        TTPDefinition.getInstance().setTeamIndexes(selectedIndexes);
        TTPDefinition.getInstance().setSymmetricSecondRound(symmetricSecondRound.isSelected());
        TTPDefinition.getInstance().setSecondRound(secondRound);
        TTPDefinition.getInstance().setCantVecesLocal(localGames);
        TTPDefinition.getInstance().setCantVecesVisitante(visitorGames);
        TTPDefinition.getInstance().setChampionVsSub(champVsSub.isSelected());
        TTPDefinition.getInstance().setFirstPlace(posChampion);
        TTPDefinition.getInstance().setSecondPlace(posSub);
        TTPDefinition.getInstance().setInauguralGame(inauguralGame.isSelected());
        TTPDefinition.getInstance().setOccidentVsOrient(occidenteVsOrienteToggle.isSelected());
        TTPDefinition.getInstance().setCalendarId(calendarId.getText());
        AnchorPane structureOver = homeController.getPrincipalPane();
        homeController.createPage(new AdvanceConfigurationController(), structureOver, "/visual/AdvanceConfiguration.fxml");
        homeController.getButtonReturnSelectionTeamConfiguration().setVisible(false);
    }

    private int[][] generateMatrix(int size) {
        //false el equipo no se ha cogido
        int[][] matrix = new int[size][size];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                matrix[i][j] = 0;
            }
        }
        matrix = TTPDefinition.getInstance().symmetricCalendar(matrix);
        return matrix;
    }

    @FXML
    void showRest(ActionEvent event) throws IOException {
        teams = teamCheckList.getCheckModel().getCheckedItems().size();
        secondRound = secondRoundButton.isSelected();
        inaugural = inauguralGame.isSelected();
        AnchorPane structureOver = homeController.getPrincipalPane();
        homeController.createPage(new RestSelectorController(), structureOver, "/visual/RestSelector.fxml");

        homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
    }

    @FXML
    void importConfiguration(ActionEvent event) throws IOException {

        Stage stage = new Stage();
        FileChooser fc = new FileChooser();

        fc.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Documento Excel", "*xlsx"));
        File file = fc.showOpenDialog(stage);

        try {
            if (file != null) {
               DataFiles.getSingletonDataFiles().importConfiguration(file.toString());

                AnchorPane structureOver = homeController.getPrincipalPane();
                homeController.createPage(new ConfigurationCalendarController(), structureOver, "/visual/ConfigurationCalendar.fxml");

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    void exportConfiguration(ActionEvent event) {

        String champion = comboChamp.getSelectionModel().getSelectedItem();
        String subchampion = comboSub.getSelectionModel().getSelectedItem();
        int posChampionExport = DataFiles.getSingletonDataFiles().getTeams().indexOf(champion);
        int posSubExport = DataFiles.getSingletonDataFiles().getTeams().indexOf(subchampion);

        CalendarConfiguration configuration = new CalendarConfiguration();

        configuration.setCalendarId(calendarId.getText());
        configuration.setInauguralGame(inauguralGame.isSelected());
        configuration.setChampionVsSecondPlace(champVsSub.isSelected());
        configuration.setChampion(posChampionExport);
        configuration.setSecondPlace(posSubExport);
        configuration.setSecondRoundCalendar(secondRoundButton.isSelected());
        configuration.setSymmetricSecondRound(symmetricSecondRound.isSelected());
        configuration.setOccidenteVsOriente(occidenteVsOrienteToggle.isSelected());
        configuration.setMaxLocalGamesInARow(maxHomeGamesSpinner.getValue());
        configuration.setMaxVisitorGamesInARow(maxVisitorGamesSpinner.getValue());
        configuration.setTeamsIndexes(new ArrayList<>(teamCheckList.getCheckModel().getCheckedIndices()));
        configuration.setRestDates(TTPDefinition.getInstance().getRestIndexes());

        DataFiles.getSingletonDataFiles().exportConfiguration(configuration);
    }
}