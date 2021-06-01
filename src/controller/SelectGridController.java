package controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import definition.TTPDefinition;
import definition.state.CalendarState;
import definition.state.statecode.Date;
import execute.Executer;
import javafx.beans.property.Property;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import operators.initialSolution.InitialSolutionType;
import operators.interfaces.ICreateInitialSolution;
import operators.interfaces.IInauguralGame;
import problem.definition.State;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utils.CalendarConfiguration;
import utils.DataFiles;
import utils.ServiceCalendar;
import utils.ServiceOccidentOrientCalendar;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

public class SelectGridController implements Initializable, ICreateInitialSolution, IInauguralGame {


    private HomeController homeController;

    @FXML
    private AnchorPane panel;
    @FXML
    private FontAwesomeIconView balanceCalendar;
    @FXML
    private FontAwesomeIconView notBalanceCalendar;
    @FXML
    private GridPane selectionGrid;


    @FXML
    private JFXButton saveLocations;


    boolean error = false;
    JFXToggleButton[][] matrix;
    static int[][] matrixCalendar;
    private final int SIZE = ConfigurationCalendarController.teams;
    private ArrayList<String> names = ConfigurationCalendarController.teamsNames;


    @FXML
    void saveLocations(ActionEvent event) throws IOException {
        if (matrix != null) {
            if (!error) {
                HomeController.matrix = true;

                TrayNotification notification = new TrayNotification();
                notification.setTitle("Escoger sedes");
                notification.setMessage("Sedes guardadas con \u00e9xito");
                notification.setNotificationType(NotificationType.SUCCESS);
                notification.setRectangleFill(Paint.valueOf("#2F2484"));
                notification.setAnimationType(AnimationType.FADE);
                notification.showAndDismiss(Duration.seconds(2));

                showCalendar();
                HomeController.conf = true;
            } else {
                TrayNotification notification = new TrayNotification();
                notification.setTitle("Escoger sedes");
                notification.setMessage("El calendario no se encuentra balanceado");
                notification.setNotificationType(NotificationType.ERROR);
                notification.setRectangleFill(Paint.valueOf("#2F2484"));
                notification.setAnimationType(AnimationType.FADE);
                notification.showAndDismiss(Duration.seconds(2));
            }
        } else {
            System.out.println("No se ha creado la matriz");
        }
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

    private JFXToggleButton[][] generateMatrixToggleButton(int size) {

        JFXToggleButton[][] matrix = new JFXToggleButton[size][size];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (i != j) {
                    JFXToggleButton btn = new JFXToggleButton();
                    if (matrixCalendar[i][j] == 1) {
                        btn.setSelected(true);
                    }
                    matrix[i][j] = btn;
                    int finalI = i;
                    int finalJ = j;
                    matrix[i][j].setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            if (matrix[finalI][finalJ].isSelected()) {
                                matrixCalendar[finalJ][finalI] = 2;
                                matrixCalendar[finalI][finalJ] = 1;
                                matrix[finalJ][finalI].setSelected(false);
                                matrix[finalI][finalJ].setSelected(true);

                            } else {
                                matrixCalendar[finalJ][finalI] = 1;
                                matrixCalendar[finalI][finalJ] = 2;
                                matrix[finalJ][finalI].setSelected(true);
                                matrix[finalI][finalJ].setSelected(false);
                            }

                            boolean stop = checkSymetricMatrix();
                            if (stop) {
                                error = false;
                                balanceCalendar.setVisible(true);
                                notBalanceCalendar.setVisible(false);
                            } else {
                                error = true;
                                balanceCalendar.setVisible(false);
                                notBalanceCalendar.setVisible(true);
                            }

                        }
                    });
                }
            }
        }

        if (TTPDefinition.getInstance().isChampionVsSub()) {
            matrix[TTPDefinition.getInstance().getFirstPlace()][TTPDefinition.getInstance().getSecondPlace()].setDisable(true);
            matrix[TTPDefinition.getInstance().getSecondPlace()][TTPDefinition.getInstance().getFirstPlace()].setDisable(true);
        }
        return matrix;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        matrixCalendar = generateMatrix(SIZE);
        HomeController.matrix = false;

        HomeController.conf = false;
        boolean symmetric = checkSymetricMatrix();
        if (symmetric) {
            notBalanceCalendar.setVisible(false);
            balanceCalendar.setVisible(true);
            error = false;
        } else {
            notBalanceCalendar.setVisible(true);
            balanceCalendar.setVisible(false);
            error = true;
        }
        selectionGrid.setGridLinesVisible(true);
        matrix = generateMatrixToggleButton(SIZE);

        for (int i = 0; i < SIZE + 1; i++) {
            ColumnConstraints columnConstraints = new ColumnConstraints();
            columnConstraints.setHalignment(HPos.CENTER);
            columnConstraints.setFillWidth(true);
            columnConstraints.setPercentWidth(100.0 / SIZE);
            selectionGrid.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < SIZE + 1; i++) {
            RowConstraints rowConstraints = new RowConstraints();
            rowConstraints.setValignment(VPos.CENTER);
            rowConstraints.setFillHeight(true);
            rowConstraints.setPercentHeight(100.0 / SIZE);
            selectionGrid.getRowConstraints().add(rowConstraints);
        }
        for (int i = 1; i <= names.size(); i++) {
            Label label = new Label(names.get(i - 1));
            selectionGrid.add(label, i, 0);

        }
        for (int i = 1; i <= names.size(); i++) {
            Label label = new Label(names.get(i - 1));
            selectionGrid.add(label, 0, i);
        }

        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++) {
                if (i != j) {
                    selectionGrid.add(matrix[i][j], j + 1, i + 1);
                }
            }
        }
    }

    private boolean checkSymetricMatrix() {
        boolean symmetric = true;
        int cantRow = 0;
        int cantLocal = (int) Math.floor((SIZE - 1) / 2) + 1;

        for (int n = 0; n < SIZE && symmetric; n++) {
            for (int m = 0; m < SIZE && symmetric; m++) {
                if (matrixCalendar[n][m] == 1) {
                    cantRow++;
                }
                if (cantRow > cantLocal) {
                    symmetric = false;
                }
            }
            if (cantRow == 0 || cantRow < cantLocal - 1) {
                symmetric = false;
            }
            cantRow = 0;
        }
        return symmetric;
    }


    void showCalendar() throws IOException {

        AnchorPane structureOver = homeController.getPrincipalPane();
        try {
            TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
            if(!TTPDefinition.getInstance().isOccidentVsOrient()){
                TTPDefinition.getInstance().setNumberOfDates(TTPDefinition.getInstance().getTeamsIndexes().size()-1);
                Executer.getInstance().executeEC();
            }
            else{

                ArrayList<Integer> teamsOnlyOccident = new ArrayList<>();
                ArrayList<Integer> teamsOnlyOrient = new ArrayList<>();
                ArrayList<Integer> allTeams = (ArrayList<Integer>) TTPDefinition.getInstance().getTeamsIndexes().clone();

                int newMatrix[][] = TTPDefinition.getInstance().getDuelMatrix().clone();


                for (Integer index : TTPDefinition.getInstance().getTeamsIndexes()) {
                    if (DataFiles.getSingletonDataFiles().getLocations().get(index).equalsIgnoreCase("Occidental")) {
                        teamsOnlyOccident.add(index);
                    } else {
                        teamsOnlyOrient.add(index);
                    }
                }

                int[][] matrixOnlyOccident = new int[teamsOnlyOccident.size()][teamsOnlyOccident.size()];
                int[][] matrixOnlyOrient = new int[teamsOnlyOrient.size()][teamsOnlyOrient.size()];

                for (int k = 0; k < newMatrix.length; k++) {
                    int posIOccident = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getTeamsIndexes().get(k));
                    int posIOrient = teamsOnlyOrient.indexOf(TTPDefinition.getInstance().getTeamsIndexes().get(k));

                    for (int j = 0; j < newMatrix[k].length; j++) {
                        if (k < j) {
                            int posJOccident = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getTeamsIndexes().get(j));
                            int posJOrient = teamsOnlyOrient.indexOf(TTPDefinition.getInstance().getTeamsIndexes().get(j));
                            if (posIOccident != -1 && posJOccident != -1) {
                                matrixOnlyOccident[posIOccident][posJOccident] = newMatrix[k][j];
                                matrixOnlyOccident[posJOccident][posIOccident] = newMatrix[j][k];
                                newMatrix[k][j] = 0;
                                newMatrix[j][k] = 0;
                            } else if (posIOrient != -1 && posJOrient != -1) {
                                matrixOnlyOrient[posIOrient][posJOrient] = newMatrix[k][j];
                                matrixOnlyOrient[posJOrient][posIOrient] = newMatrix[j][k];
                                newMatrix[k][j] = 0;
                                newMatrix[j][k] = 0;
                            }
                        }
                    }
                }

                CalendarConfiguration originalConfiguration = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                        allTeams, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                        TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                        TTPDefinition.getInstance().isSymmetricSecondRound(), true, TTPDefinition.getInstance().getCantVecesLocal(),
                        TTPDefinition.getInstance().getCantVecesVisitante(),TTPDefinition.getInstance().getRestIndexes());


                CalendarConfiguration confOnlyOccident = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                        teamsOnlyOccident, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                        TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                        TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                        TTPDefinition.getInstance().getCantVecesVisitante(),TTPDefinition.getInstance().getRestIndexes());

                CalendarConfiguration confOnlyOrient = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                        teamsOnlyOrient, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                        TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                        TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                        TTPDefinition.getInstance().getCantVecesVisitante(),TTPDefinition.getInstance().getRestIndexes());

                CalendarConfiguration confOccVsOr = new CalendarConfiguration(TTPDefinition.getInstance().getCalendarId(),
                        allTeams, TTPDefinition.getInstance().isInauguralGame(), TTPDefinition.getInstance().isChampionVsSub(),
                        TTPDefinition.getInstance().getFirstPlace(), TTPDefinition.getInstance().getSecondPlace(), TTPDefinition.getInstance().isSecondRound(),
                        TTPDefinition.getInstance().isSymmetricSecondRound(), false, TTPDefinition.getInstance().getCantVecesLocal(),
                        TTPDefinition.getInstance().getCantVecesVisitante(),TTPDefinition.getInstance().getRestIndexes());

                if (TTPDefinition.getInstance().getFirstPlace() != -1){
                    int posChamp = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getFirstPlace());
                    int posSub = teamsOnlyOccident.indexOf(TTPDefinition.getInstance().getSecondPlace());

                    if(posChamp != -1 && posSub != -1){
                        confOnlyOrient.setChampionVsSecondPlace(false);
                        confOnlyOrient.setInauguralGame(false);
                        confOccVsOr.setChampionVsSecondPlace(false);
                    }
                    else if(posChamp == -1 && posSub == -1){
                        confOnlyOccident.setChampionVsSecondPlace(false);
                        confOnlyOccident.setInauguralGame(false);
                        confOccVsOr.setChampionVsSecondPlace(false);
                    }
                    else if(posChamp != -1 && posSub == -1 && TTPDefinition.getInstance().isInauguralGame()){
                        confOnlyOccident.setChampionVsSecondPlace(false);
                        confOnlyOccident.setInauguralGame(false);
                    }
                    else if(posChamp == -1 && posSub != -1 && TTPDefinition.getInstance().isInauguralGame()){
                        confOnlyOrient.setChampionVsSecondPlace(false);
                        confOnlyOrient.setInauguralGame(false);
                    }
                    else{
                        confOnlyOrient.setChampionVsSecondPlace(false);
                        confOnlyOccident.setChampionVsSecondPlace(false);
                    }
                    if (TTPDefinition.getInstance().isInauguralGame()){
                        confOccVsOr.setInauguralGame(false);
                    }
                }

                TTPDefinition.getInstance().setTeamIndexes(teamsOnlyOccident);
                TTPDefinition.getInstance().setDuelMatrix(matrixOnlyOccident);
                TTPDefinition.getInstance().setOccidentOrientConfiguration(confOnlyOccident);
                TTPDefinition.getInstance().setNumberOfDates(teamsOnlyOccident.size()-1);
                Executer.getInstance().executeEC();

                ArrayList<State> calendarOccList = new ArrayList<>();

                for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                    calendarOccList.add(0, Executer.getInstance().getResultStates().get(Executer.getInstance().getResultStates().size()-1));
                    Executer.getInstance().getResultStates().remove(Executer.getInstance().getResultStates().size()-1);
                }

                TTPDefinition.getInstance().setTeamIndexes(teamsOnlyOrient);
                TTPDefinition.getInstance().setDuelMatrix(matrixOnlyOrient);
                TTPDefinition.getInstance().setOccidentOrientConfiguration(confOnlyOrient);
                TTPDefinition.getInstance().setNumberOfDates(teamsOnlyOrient.size()-1);
                Executer.getInstance().executeEC();

                ArrayList<State> calendarOrList = new ArrayList<>();

                for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                    calendarOrList.add(0, Executer.getInstance().getResultStates().get(Executer.getInstance().getResultStates().size()-1));
                    Executer.getInstance().getResultStates().remove(Executer.getInstance().getResultStates().size()-1);
                }

                ArrayList<State> allTogetherList = new ArrayList<>();
                ArrayList<Date> dateToStarList = new ArrayList<>();

                if (originalConfiguration.isInauguralGame()){
                    for (int i = 0; i < calendarOccList.size(); i++) {
                        if (confOnlyOccident.isInauguralGame()){
                            deleteInauguralGame(calendarOccList.get(i));
                        }
                        else if(confOnlyOrient.isInauguralGame()){
                            deleteInauguralGame(calendarOrList.get(i));
                        }
                    }
                }


                for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {

                    State allTogether = new State();
                    for (int j = 0; j < calendarOccList.get(i).getCode().size(); j++) {

                        definition.state.statecode.Date dateOcc = new definition.state.statecode.Date();
                        dateOcc.setGames(((Date)(calendarOccList.get(i).getCode().get(j))).getGames());

                        definition.state.statecode.Date dateOr = new definition.state.statecode.Date();
                        dateOr.setGames(((Date)(calendarOrList.get(i).getCode().get(j))).getGames());

                        definition.state.statecode.Date dateTogether = new definition.state.statecode.Date();
                        dateTogether.getGames().addAll(dateOcc.getGames());
                        dateTogether.getGames().addAll(dateOr.getGames());

                        allTogether.getCode().add(dateTogether);

                        if (j == calendarOccList.get(i).getCode().size()-1){
                            dateToStarList.add(dateTogether);
                        }
                    }
                    allTogetherList.add(allTogether);
                }
                TTPDefinition.getInstance().getDateToStartList().addAll(dateToStarList);

                int numberOfDate = 0;
                if (TTPDefinition.getInstance().isSecondRound()){
                    numberOfDate = (allTeams.size()-1) - (calendarOccList.get(0).getCode().size()/2);
                }
                else{
                    numberOfDate = (allTeams.size()-1) - calendarOccList.get(0).getCode().size();
                }

                TTPDefinition.getInstance().setTeamIndexes(allTeams);
                TTPDefinition.getInstance().setDuelMatrix(newMatrix);
                TTPDefinition.getInstance().setOccidentOrientConfiguration(confOccVsOr);
                TTPDefinition.getInstance().setNumberOfDates(numberOfDate);



                ArrayList<Integer> originalRests = new ArrayList<>();
                originalRests.addAll(TTPDefinition.getInstance().getRestIndexes());

                int numberToModifyRests = allTogetherList.get(0).getCode().size();
                if (TTPDefinition.getInstance().isInauguralGame()){
                    numberToModifyRests += 1;
                }

                boolean setDateToStart = true;

                ArrayList<Integer> modifiedRests = new ArrayList<>();
                for (int i = 0; i < originalRests.size(); i++) {
                    if (originalRests.get(i) > numberToModifyRests){
                        int tempRest = originalRests.get(i);
                        tempRest -= numberToModifyRests;
                        modifiedRests.add(tempRest);
                    }
                    else if (originalRests.get(i) == numberToModifyRests){
                        setDateToStart = false;
                    }
                }
                TTPDefinition.getInstance().setRestIndexes(modifiedRests);

                if (setDateToStart){
                    Executer.getInstance().setTimeToSetDateToStart(true);
                    TTPDefinition.getInstance().setUseDateToStart(true);
                }

                Executer.getInstance().executeEC();

                TTPDefinition.getInstance().setRestIndexes(originalRests);

                if (setDateToStart){
                    Executer.getInstance().setTimeToSetDateToStart(false);
                    TTPDefinition.getInstance().setUseDateToStart(false);
                }
                
                ArrayList<State> calendarOccVsOrList = new ArrayList<>();

                for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                    calendarOccVsOrList.add(0, Executer.getInstance().getResultStates().get(Executer.getInstance().getResultStates().size()-1));
                    Executer.getInstance().getResultStates().remove(Executer.getInstance().getResultStates().size()-1);
                }

                if (originalConfiguration.isInauguralGame() && confOccVsOr.isInauguralGame()){
                    for (int i = 0; i < calendarOccVsOrList.size(); i++) {
                        deleteInauguralGame(calendarOccVsOrList.get(i));
                    }
                }

                for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                    allTogetherList.get(i).getCode().addAll(calendarOccVsOrList.get(i).getCode());
                }

                InitialSolutionType type = createSolutionType();

                for (int i = 0; i < Executer.getInstance().getEXECUTIONS(); i++) {
                    CalendarState tempState = new CalendarState();
                    tempState.getCode().addAll(allTogetherList.get(i).getCode());
                    CalendarConfiguration tempConfiguration = new CalendarConfiguration(originalConfiguration.getCalendarId(),
                            allTeams, originalConfiguration.isInauguralGame(), originalConfiguration.isChampionVsSecondPlace(),
                            originalConfiguration.getChampion(), originalConfiguration.getSecondPlace(), originalConfiguration.isSecondRoundCalendar(),
                            originalConfiguration.isSymmetricSecondRound(), originalConfiguration.isOccidenteVsOriente(), originalConfiguration.getMaxLocalGamesInARow(),
                            originalConfiguration.getMaxVisitorGamesInARow(), originalConfiguration.getRestDates());

                    tempState.setConfiguration(tempConfiguration);
                    tempState.setCalendarType(type.ordinal());
                    if (originalConfiguration.isInauguralGame()){
                        addInauguralGame(tempState);
                    }
                    Executer.getInstance().getResultStates().add(tempState);

                    if( Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId()) == null){
                        Executer.getInstance().getIdMaps().put(TTPDefinition.getInstance().getCalendarId(), 1);
                    }else{
                        ;
                        Executer.getInstance().getIdMaps().put(TTPDefinition.getInstance().getCalendarId(),
                                Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId())+1);

                    }
                    tempState.getConfiguration().setCalendarId(TTPDefinition.getInstance().getCalendarId() +"."+
                            Executer.getInstance().getIdMaps().get(TTPDefinition.getInstance().getCalendarId()));

                    if( Executer.getInstance().getIdMaps().get(tempState.getConfiguration().getCalendarId()) == null){
                        Executer.getInstance().getIdMaps().put(tempState.getConfiguration().getCalendarId(), 1);
                    }else{
                        Executer.getInstance().getIdMaps().put(tempState.getConfiguration().getCalendarId(),
                                Executer.getInstance().getIdMaps().get(tempState.getConfiguration().getCalendarId())+1);
                    }
                }
            }
            homeController.createPage(new CalendarController(), structureOver, "/visual/Calendar.fxml");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);

        /*StackPane stackPane = new StackPane();

        JFXDialog jfxDialog = new JFXDialog();
        JFXDialogLayout content = new JFXDialogLayout();
        FXMLLoader fxmlLoader =  new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("/visual/CalendarService.fxml"));
        AnchorPane progressContent = fxmlLoader.load();
        CalendarServiceController serviceController = fxmlLoader.getController();

        content.setBody(progressContent);

        jfxDialog.setContent(content);
        TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
        jfxDialog.setDialogContainer(stackPane);
        panel.getChildren().add(stackPane);
        stackPane.setLayoutX(400);
        stackPane.setLayoutY(200);
        jfxDialog.setPrefHeight(105);
        jfxDialog.setPrefWidth(432);
        jfxDialog.show();

        if(TTPDefinition.getInstance().isOccidentVsOrient()){
            ServiceOccidentOrientCalendar serviceOccidentOrientCalendar = new ServiceOccidentOrientCalendar();

            serviceOccidentOrientCalendar.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    AnchorPane structureOver = homeController.getPrincipalPane();
                    try {
                        TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
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
        else {
            ServiceCalendar service = new ServiceCalendar();

            service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent workerStateEvent) {
                    AnchorPane structureOver = homeController.getPrincipalPane();
                    try {
                        TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
                        //Executer.getInstance().executeEC();
                        homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
                        homeController.createPage(new CalendarController(), structureOver, "/visual/Calendar.fxml");
                    } catch (IOException e) {
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
        }*/


    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

}
