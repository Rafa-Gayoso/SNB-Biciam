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
import javafx.scene.control.ScrollPane;
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
    private ScrollPane scroll;


    @FXML
    private JFXButton saveLocations;


    @FXML
    private JFXButton invertDuels;


    boolean error = false;
    JFXToggleButton[][] matrix;
    static int[][] matrixCalendar;
    private final int SIZE = ConfigurationCalendarController.teams;
    private ArrayList<String> names = ConfigurationCalendarController.teamsNames;


    @FXML
    void saveLocations(ActionEvent event) throws Exception {
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

        int calendarPosition = CalendarController.selectedCalendar;
        if (calendarPosition != -1) {
            CalendarConfiguration configuration = ((CalendarState) Executer.getInstance().getResultStates().get(calendarPosition)).getConfiguration();
            if (compareConfigurations(configuration)) {
                matrixCalendar = configuration.getDuelMatrix();
            } else {
                matrixCalendar = generateMatrix(SIZE);
            }
        } else
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


    void showCalendar() throws Exception {

        TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
        /*TTPDefinition.getInstance().setNumberOfDates(TTPDefinition.getInstance().getTeamsIndexes().size() - 1);
        Executer.getInstance().executeEC();
        AnchorPane structureOver = homeController.getPrincipalPane();
        homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);

        homeController.createPage(new CalendarController(), structureOver, "/visual/Calendar.fxml");*/
        StackPane stackPane = new StackPane();

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


    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }


    private boolean compareConfigurations(CalendarConfiguration configuration) {
        boolean compare = true;


        if (configuration.getTeamsIndexes().size() != TTPDefinition.getInstance().getTeamsIndexes().size()) {
            compare = false;
        } else if (configuration.isSecondRoundCalendar() && !TTPDefinition.getInstance().isSecondRound()) {
            compare = false;
        } else if (configuration.isSymmetricSecondRound() && !TTPDefinition.getInstance().isSymmetricSecondRound()) {
            compare = false;
        } else if (configuration.isInauguralGame() && !TTPDefinition.getInstance().isInauguralGame()) {
            compare = false;
        } else if (configuration.isInauguralGame() && TTPDefinition.getInstance().isInauguralGame()) {
            if (configuration.getChampion() != TTPDefinition.getInstance().getFirstPlace() ||
                    configuration.getSecondPlace() != TTPDefinition.getInstance().getSecondPlace()) {
                compare = false;
            }

        } else if (configuration.isChampionVsSecondPlace() && !TTPDefinition.getInstance().isChampionVsSub()) {

            compare = false;

        } else if (configuration.isChampionVsSecondPlace() && TTPDefinition.getInstance().isChampionVsSub()) {
            if (configuration.getChampion() != TTPDefinition.getInstance().getFirstPlace() ||
                    configuration.getSecondPlace() != TTPDefinition.getInstance().getSecondPlace()) {
                compare = false;
            }
        } else if (configuration.isOccidenteVsOriente() && !TTPDefinition.getInstance().isOccidentVsOrient()) {
            compare = false;
        }


        return compare;

    }


    @FXML
    void invertDuels(ActionEvent event) {
        int [][] newMatrix = new int[matrixCalendar.length][matrixCalendar.length];

        for(int i =0; i < matrixCalendar.length; i++){
            for (int j = 0; j < matrixCalendar.length; j++) {
                if(matrixCalendar[i][j] == 1){
                    newMatrix[i][j] = 2;
                }else if(matrixCalendar[i][j] == 2){
                    newMatrix [i][j] = 1;
                }
                else
                    newMatrix[i][j]=0;

            }
        }
        if(TTPDefinition.getInstance().isChampionVsSub()){
            int posChampion = TTPDefinition.getInstance().getFirstPlace();
            int posSecond = TTPDefinition.getInstance().getSecondPlace();
            newMatrix[posChampion][posSecond] = 2;
            newMatrix[posSecond][posChampion] = 1;
        }

        newMatrix = TTPDefinition.getInstance().readjustSymmetricCalendar(newMatrix);


        matrixCalendar = newMatrix;

        for(int i =0; i < matrixCalendar.length; i++){
            for (int j = 0; j < matrixCalendar.length; j++) {
                System.out.print(matrixCalendar[i][j] + " ");
            }
            System.out.println("");
        }
        boolean symmetric = checkSymetricMatrix();
        if (symmetric) {
            notBalanceCalendar.setVisible(false);
            balanceCalendar.setVisible(true);
            error = false;
        } else {
            int champion = TTPDefinition.getInstance().getFirstPlace();
            int second = TTPDefinition.getInstance().getSecondPlace();;
            int i =0;
            while (i < matrixCalendar.length && !symmetric){
                if(i != second){
                    if(matrixCalendar[champion][i] == 1){
                        matrixCalendar[champion][i] = 2;
                        matrixCalendar[i][champion] = 1;
                        symmetric= checkSymetricMatrix();
                        if(!symmetric){
                            matrixCalendar[champion][i] = 1;
                            matrixCalendar[i][champion] = 2;
                        }
                    }else if(matrixCalendar[champion][i] == 2){
                        matrixCalendar[champion][i] = 1;
                        matrixCalendar[i][champion] = 2;
                        symmetric= checkSymetricMatrix();
                        if(!symmetric){
                            matrixCalendar[champion][i] = 2;
                            matrixCalendar[i][champion] = 1;
                        }
                    }
                }

                i++;
            }
            /*for(int i = 0; i < matrixCalendar.length && !symmetric; i++){
                if(matrixCalendar[champion][i] == 1){
                    matrixCalendar[champion][i] = 2;
                    matrixCalendar[i][champion] = 1;
                    symmetric= checkSymetricMatrix();
                    if(!symmetric){

                    }
                }
            }*/
        }

        matrix = generateMatrixToggleButton(matrixCalendar.length);

        selectionGrid.setGridLinesVisible(false);

        selectionGrid.getChildren().removeAll(selectionGrid.getChildren());



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
        selectionGrid.setGridLinesVisible(true);


        //scroll.setContent(selectionGrid);
    }

    @FXML
    void exportConfiguration(ActionEvent event) {

        CalendarConfiguration configuration = new CalendarConfiguration();

        configuration.setInauguralGame(TTPDefinition.getInstance().isInauguralGame());
        configuration.setChampionVsSecondPlace(TTPDefinition.getInstance().isChampionVsSub());
        configuration.setChampion(TTPDefinition.getInstance().getFirstPlace());
        configuration.setSecondPlace(TTPDefinition.getInstance().getSecondPlace());
        configuration.setSecondRoundCalendar(TTPDefinition.getInstance().isSecondRound());
        configuration.setSymmetricSecondRound(TTPDefinition.getInstance().isSymmetricSecondRound());
        configuration.setOccidenteVsOriente(TTPDefinition.getInstance().isOccidentVsOrient());
        configuration.setMaxLocalGamesInARow(TTPDefinition.getInstance().getCantVecesLocal());
        configuration.setMaxVisitorGamesInARow(TTPDefinition.getInstance().getCantVecesVisitante());
        configuration.setTeamsIndexes(TTPDefinition.getInstance().getTeamsIndexes());
        configuration.setRestDates(TTPDefinition.getInstance().getRestIndexes());

        DataFiles.getSingletonDataFiles().exportConfiguration(configuration);
    }
}
