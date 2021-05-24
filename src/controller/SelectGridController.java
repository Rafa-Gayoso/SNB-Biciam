package controller;

import com.jfoenix.controls.*;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import definition.TTPDefinition;
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
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utils.ServiceCalendar;
import utils.ServiceOccidentOrientCalendar;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class SelectGridController implements Initializable {


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
                notification.setMessage("Sedes guardadas con éxito");
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

        /*AnchorPane structureOver = homeController.getPrincipalPane();
        try {
            TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
            //Executer.getInstance().executeEC();
            Executer.getInstance().executeOCC();
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
        homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);*/

        StackPane stackPane = new StackPane();

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
                    notification.setTitle("Generar Caeldnarios");
                    notification.setMessage("Ocurrió un error y no se pudo generar los calendarios");
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
                    notification.setTitle("Generar Caeldnarios");
                    notification.setMessage("Ocurrió un error y no se pudo generar los calendarios");
                    notification.setNotificationType(NotificationType.ERROR);
                    notification.setRectangleFill(Paint.valueOf("#2F2484"));
                    notification.setAnimationType(AnimationType.FADE);
                    notification.showAndDismiss(Duration.seconds(2));

                }
            });
            service.restart();
        }


    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

}
