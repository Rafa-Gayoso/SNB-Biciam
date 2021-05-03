package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import definition.TTPDefinition;
import execute.Executer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Paint;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

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
    void saveLocations(ActionEvent event) throws IOException{
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
            error=false;
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
        int     cantRow   = 0;
        int     cantLocal = (int) Math.floor((SIZE - 1) / 2) + 1;

        for (int n = 0; n < SIZE && symmetric; n++) {
            for (int m = 0; m < SIZE && symmetric; m++) {
                if (matrixCalendar[n][m] == 1) {
                    cantRow++;
                }
                if (cantRow > cantLocal) {
                    symmetric = false;
                }
            }
            if (cantRow == 0 || cantRow < cantLocal-1) {
                symmetric = false;
            }
            cantRow = 0;
        }
        return symmetric;
    }

   
   void showCalendar() {

        try{
            TTPDefinition.getInstance().setDuelMatrix(matrixCalendar);
            Executer.getInstance().executeEC();
            AnchorPane structureOver = homeController.getPrincipalPane();
            homeController.createPage(new CalendarController(), structureOver, "/visual/Calendar.fxml");
            homeController.getButtonReturnSelectionTeamConfiguration().setVisible(true);
        }catch (IOException | ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e){
            e.printStackTrace();
        }

    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

}
