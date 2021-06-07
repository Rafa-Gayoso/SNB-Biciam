package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXListView;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import execute.Executer;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import operators.mutation.CombineMutationOperator;
import operators.mutation.MutationOperatorType;
import org.controlsfx.control.CheckListView;
import tray.notification.TrayNotification;
import utils.CalendarConfiguration;
import utils.DataFiles;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MutationsConfigurationController implements Initializable {


    private int selectedCalendar;
    private HomeController homeController;

    private ArrayList<ArrayList<Boolean>> booleanValues;//lista de boolean para saber que componentes activar o no

    private ArrayList<ArrayList<Integer>> configurationsList;

    private ArrayList<Integer> positionsMutationsSelected;
    private List<String> mutationsToAdd;

    @FXML
    private AnchorPane pane;

    @FXML
    private JFXListView<String> mutationsListView;

    @FXML
    private JFXComboBox<String> comboDate1;

    @FXML
    private JFXComboBox<String> comboDate2;

    @FXML
    private JFXComboBox<String> comboDuel1;

    @FXML
    private JFXComboBox<String> comboDuel2;

    @FXML
    private JFXComboBox<String> comboTeam1;

    @FXML
    private JFXComboBox<String> comboTeam2;

    @FXML
    private JFXListView<String> selectedMutationListView;

    @FXML
    private JFXButton selectMutations;

    @FXML
    private JFXButton removeMutations;

    @FXML
    private JFXButton buttonApplyMuttations;

    @FXML
    private Spinner<Integer> iterations;

    private CalendarState calendar;

    private CalendarConfiguration configuration;

    private CombineMutationOperator operatorSelector;

    public static int currentMutationPostion;

    private List<String> mutationsReaded;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.operatorSelector = new CombineMutationOperator();
        currentMutationPostion = -1;
        this.selectedCalendar = CalendarController.selectedCalendar;
        calendar = (CalendarState) Executer.getInstance().getResultStates().get(selectedCalendar);
        configuration = calendar.getConfiguration();
        iterations.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, Integer.MAX_VALUE));
        iterations.getValueFactory().setValue(20000);
        mutationsToAdd = new ArrayList<>();
        positionsMutationsSelected = new ArrayList<>();
        booleanValues = new ArrayList<>();
        selectMutations.setVisible(false);
        removeMutations.setVisible(false);
        comboDate1.setVisible(false);
        comboDate2.setVisible(false);
        comboDuel1.setVisible(false);
        comboDuel2.setVisible(false);
        comboTeam1.setVisible(false);
        comboTeam2.setVisible(false);

        iterations.getEditor().setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("\\d{0,9}?")) ? change : null));

        iterations.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                iterations.getValueFactory().setValue(Integer.parseInt(iterations.getEditor().getText()));
            }
        });

        //lleno la lista de mutaciones y separo los datos de configuracion
        mutationsReaded = DataFiles.getSingletonDataFiles().getMutations();
        int mutationSize = DataFiles.getSingletonDataFiles().getMutations().size();
        ArrayList<ArrayList<String>> mutationsConfigurations = DataFiles.getSingletonDataFiles().getMutationsConfiguration();



        for (int i = 0; i < mutationsConfigurations.size(); i++) {
            ArrayList<Boolean> booleans = new ArrayList<>();
            ArrayList<String> config = mutationsConfigurations.get(i);
            for (int j = 0; j < config.size(); j++) {

                String[] currentConfig = config.get(j).split(",");
                for (int k = 0; k < currentConfig.length; k++) {
                    if (currentConfig[k].equalsIgnoreCase("V")) {
                        booleans.add(true);
                    } else {
                        booleans.add(false);
                    }
                }

            }

            booleanValues.add(booleans);
        }

        if(!configuration.isSymmetricSecondRound()){
            for(int i=mutationSize-1; i >=4 ; i--){
                mutationsReaded.remove(i);
                booleanValues.remove(i);
            }
        }

        mutationsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        mutationsListView.setItems(FXCollections.observableList(mutationsReaded));

        configurationsList = new ArrayList<>();


        // Ciclo para crear las listas dependiendo las mutaciones que hayan
        for (int i = 0; i < mutationsListView.getItems().size(); i++) {
            ArrayList<Integer> configuration = new ArrayList<>();
            //metodo para llenar las listas dependiedno la cantidad de componentes de configuracion que hayan
            for (int j = 0; j < pane.getChildren().size(); j++) {
                configuration.add(-1);
            }
            configurationsList.add(configuration);
        }



        int dates = calendar.getCode().size();
        boolean inaugural = configuration.isInauguralGame();
        boolean symmetric = configuration.isSymmetricSecondRound();

        if(symmetric){
            for (int i = 0; i < dates/2; i++) {
                if (inaugural && (i == 0)) {
                    i++;
                }
                String date = "Fecha " + (i + 1);
                comboDate1.getItems().add(date);
                comboDate2.getItems().add(date);
            }
        }else {
            for (int i = 0; i < dates; i++) {
                if (inaugural && (i == 0)) {
                    i++;
                }
                String date = "Fecha " + (i + 1);
                comboDate1.getItems().add(date);
                comboDate2.getItems().add(date);
            }
        }


        for (int i = 0; i < configuration.getTeamsIndexes().size(); i++) {

            String team = DataFiles.getSingletonDataFiles().getTeams().get(configuration.getTeamsIndexes().get(i));
            comboTeam1.getItems().add(team);
            comboTeam2.getItems().add(team);
        }

        comboBoxValidation(comboDate1, comboDuel1);
        comboBoxValidation(comboDate2, comboDuel2);
        comboBoxTeamValidation(comboTeam1,comboTeam2);

        mutationsListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ((int) newValue != -1) {
                    selectMutations.setVisible(true);
                    removeMutations.setVisible(true);
                }
            }
        });

        selectedMutationListView.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {

                if ((int) newValue != -1) {
                    int positionNew = positionsMutationsSelected.get((int) newValue);
                    ArrayList<Boolean> values = booleanValues.get(positionNew);
                    comboDate1.setVisible(values.get(0));
                    comboDate2.setVisible(values.get(1));
                    comboDuel1.setVisible(values.get(2));
                    comboDuel2.setVisible(values.get(3));
                    comboTeam1.setVisible(values.get(4));
                    comboTeam2.setVisible(values.get(5));

                    if ((int) oldValue != -1) {
                        int positionOld = positionsMutationsSelected.get((int) oldValue);
                        configurationsList.get(positionOld).set(0, comboDate1.getSelectionModel().getSelectedIndex());
                        configurationsList.get(positionOld).set(1, comboDate2.getSelectionModel().getSelectedIndex());
                        configurationsList.get(positionOld).set(2, comboDuel1.getSelectionModel().getSelectedIndex());
                        configurationsList.get(positionOld).set(3, comboDuel2.getSelectionModel().getSelectedIndex());
                        configurationsList.get(positionOld).set(4, comboTeam1.getSelectionModel().getSelectedIndex());
                        configurationsList.get(positionOld).set(5, comboTeam2.getSelectionModel().getSelectedIndex());
                    }
                    System.out.println("Lista de configuraciones");
                    System.out.println(configurationsList);
                    comboDate1.getSelectionModel().select(configurationsList.get(positionNew).get(0));
                    comboDate2.getSelectionModel().select(configurationsList.get(positionNew).get(1));
                    comboDuel1.getSelectionModel().select(configurationsList.get(positionNew).get(2));
                    comboDuel2.getSelectionModel().select(configurationsList.get(positionNew).get(3));
                    comboTeam1.getSelectionModel().select(configurationsList.get(positionNew).get(4));
                    comboTeam2.getSelectionModel().select(configurationsList.get(positionNew).get(5));
                }
            }
        });

    }

    private void comboBoxTeamValidation(JFXComboBox<String> comboTeam1, JFXComboBox<String> comboTeam2) {
        comboTeam1.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ((int) newValue != -1) {
                    comboTeam2.getItems().clear();
                    int position = (int) newValue;

                    for(int i = 0; i < calendar.getConfiguration().getTeamsIndexes().size();i++){
                        if(i != position){
                            comboTeam2.getItems().add(DataFiles.getSingletonDataFiles().getTeams().get(calendar.getConfiguration().getTeamsIndexes().get(i)));
                        }
                    }


                }

            }
        });
    }

    private void comboBoxValidation(JFXComboBox<String> comboDate, JFXComboBox<String> comboDuel) {

        comboDate.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if ((int) newValue != -1) {
                    comboDuel.getItems().clear();
                    int position = (int) newValue;

                    Date date = (Date) calendar.getCode().get(position);
                    for (int j = 0; j < date.getGames().size(); j++) {
                        int posLocal = date.getGames().get(j).get(0);
                        int posVisitor = date.getGames().get(j).get(1);
                        String element = "" + DataFiles.getSingletonDataFiles().getTeams().get(posLocal) + " - " + DataFiles.getSingletonDataFiles().getTeams().get(posVisitor);
                        //table.getItems().add(new Duel(controller.getTeams().get(posLocal), controller.getTeams().get(posVisitor)));
                        comboDuel.getItems().add(element);
                    }
                }

            }
        });
    }




    @FXML
    void selectMutations(ActionEvent event) {
        //positionsMutationsSelected = (ArrayList<Integer>) mutationsListView.getSelectionModel().getSelectedIndices();


        for (int i = 0; i < mutationsListView.getSelectionModel().getSelectedItems().size(); i++) {
            String mutation = mutationsListView.getSelectionModel().getSelectedItems().get(i);
            int pos = mutationsListView.getSelectionModel().getSelectedIndices().get(i);
            if (!mutationsToAdd.contains(mutation))
                mutationsToAdd.add(mutation);

            if (!positionsMutationsSelected.contains(pos))
                positionsMutationsSelected.add(pos);
        }


        selectedMutationListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        selectedMutationListView.setItems(FXCollections.observableList(mutationsToAdd));


        /*System.out.println(mutationsToAdd);
        System.out.println(positionsMutationsSelected);*/
    }


    @FXML
    void removeMutations(ActionEvent event) {
        int index = selectedMutationListView.getSelectionModel().getSelectedIndex();
        if (index != -1) {
            selectedMutationListView.getItems().remove(index);
            positionsMutationsSelected.remove(index);
        }
        System.out.println(positionsMutationsSelected);
    }

    @FXML
    void applyMutations(ActionEvent event) throws IOException {
        System.out.println(positionsMutationsSelected);
        System.out.println(selectedMutationListView.getSelectionModel().getSelectedIndex());

        ArrayList<String> stringMutations = new ArrayList<>(selectedMutationListView.getItems());
        int atLestOneMutationSelected = selectedMutationListView.getItems().size();


        //Crear la lista de mutationes a utilizar
        ArrayList<MutationOperatorType> mutationOperatorTypes = new ArrayList<>();
        //Quedarse con todos los valores del enum de las mutaciones
        MutationOperatorType[] mutationsOperators = MutationOperatorType.values();
        //for por cada indice seleccionado de la listview
        for (String currentMutations : stringMutations) {
            //for para buscar de todas las mutationces disponibles cual se va a añadir
            int posMutation = mutationsReaded.indexOf(currentMutations);
            mutationOperatorTypes.add(mutationsOperators[posMutation]);

        }


        if (atLestOneMutationSelected != 0) {

            operatorSelector.setTypes(mutationOperatorTypes);
            if (selectedMutationListView.getSelectionModel().getSelectedIndex() != -1) {
                int posSelectedMutation = positionsMutationsSelected.get(selectedMutationListView.getSelectionModel().getSelectedIndex());

                configurationsList.get(posSelectedMutation).set(0, comboDate1.getSelectionModel().getSelectedIndex());
                configurationsList.get(posSelectedMutation).set(1, comboDate2.getSelectionModel().getSelectedIndex());
                configurationsList.get(posSelectedMutation).set(2, comboDuel1.getSelectionModel().getSelectedIndex());
                configurationsList.get(posSelectedMutation).set(3, comboDuel2.getSelectionModel().getSelectedIndex());
                configurationsList.get(posSelectedMutation).set(4, comboTeam1.getSelectionModel().getSelectedIndex());
                configurationsList.get(posSelectedMutation).set(5, comboTeam2.getSelectionModel().getSelectedIndex());

                if (configuration.isInauguralGame()) {
                    for (int i = 0; i < configurationsList.size(); i++) {
                        for (int j = 0; j < configurationsList.get(i).size(); j++) {
                            if (configurationsList.get(i).get(j) != -1) {
                                configurationsList.get(i).set(j, configurationsList.get(i).get(j) + 1);
                            }
                        }
                    }
                }
                System.out.println("Lista de configuraciones");
                System.out.println(configurationsList);
            }

            TTPDefinition.getInstance().setMutationsConfigurationsList(configurationsList);
            /*Controller.getSingletonController().setMutationsIndexes(positionsMutationsSelected);*/
            System.out.println("mutaciones seleccionadas: " + positionsMutationsSelected);

            CalendarState newState = (CalendarState) calendar.clone();

            for (int j = 0; j < iterations.getValueFactory().getValue(); j++) {
                for (int i = 0; i < positionsMutationsSelected.size(); i++) {
                    currentMutationPostion = positionsMutationsSelected.get(i);
                    newState = (CalendarState) operatorSelector.applyMutation(newState);
                }

            }



            int numeration = Executer.getInstance().getIdMaps().get(newState.getConfiguration().getCalendarId());

            Executer.getInstance().getIdMaps().put(calendar.getConfiguration().getCalendarId(),
                    Executer.getInstance().getIdMaps().get(calendar.getConfiguration().getCalendarId())+1);

            newState.getConfiguration().setCalendarId(newState.getConfiguration().getCalendarId()+"."+numeration);

            TTPDefinition.getInstance().setMutationsConfigurationsList(new ArrayList<>());

            if( Executer.getInstance().getIdMaps().get(newState.getConfiguration().getCalendarId()) == null){
                Executer.getInstance().getIdMaps().put(newState.getConfiguration().getCalendarId(), 1);
            }else{

                Executer.getInstance().getIdMaps().put(newState.getConfiguration().getCalendarId(),
                        Executer.getInstance().getIdMaps().get(newState.getConfiguration().getCalendarId())+1);
            }
            Executer.getInstance().getResultStates().add(newState);



            Stage stage = (Stage) selectMutations.getScene().getWindow();
            stage.close();

            AnchorPane structureOver = homeController.getPrincipalPane();
            homeController.createPage(new CalendarController(), structureOver, "/visual/Calendar.fxml");
            homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);

        } else {
            TrayNotification notification = new TrayNotification();
            notification.setMessage("Debe elegir al menos una mutaci\u00f3n");
            notification.showAndDismiss(Duration.seconds(1));
        }
    }

    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) selectMutations.getScene().getWindow();
        stage.close();
    }

}
