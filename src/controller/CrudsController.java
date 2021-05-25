package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextFormatter;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;
import javafx.util.Duration;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;
import utils.DataFiles;
import utils.Distance;
import utils.TableCrudsData;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class CrudsController implements Initializable {

    private TrayNotification notification;
    private HomeController homeController;
    private ObservableList<TableCrudsData> data = FXCollections.observableArrayList();


    @FXML
    private JFXTextField nameTextField;

    @FXML
    private JFXTextField acronymTextField;

    @FXML
    private JFXComboBox<String> locationComboBox;

    @FXML
    private Spinner<Integer> distanceSpinner;

    @FXML
    private JFXButton modifyBtn;

    @FXML
    private JFXButton deleteBtn;

    @FXML
    private JFXButton addBtn;

    @FXML
    private JFXButton showDataBtn;

    @FXML
    private JFXButton closeBtn;

    @FXML
    private TableView<TableCrudsData> table;

    @FXML
    private TableColumn<TableCrudsData, String> columnName;

    @FXML
    private TableColumn<TableCrudsData, String> columnAcro;

    @FXML
    private TableColumn<TableCrudsData, String> columnLocation;

    @FXML
    private TableColumn<TableCrudsData, JFXTextField> columnDistance;


    @FXML
    private JFXComboBox<String> teamsComboBox;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        notification = new TrayNotification();
        columnName.setCellValueFactory(new PropertyValueFactory<TableCrudsData, String>("name"));
        columnAcro.setCellValueFactory(new PropertyValueFactory<TableCrudsData, String>("acronym"));
        columnLocation.setCellValueFactory(new PropertyValueFactory<TableCrudsData, String>("location"));
        columnDistance.setCellValueFactory(new PropertyValueFactory<TableCrudsData, JFXTextField>("distance"));

        nameTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[A-Za-zñÑáéíóúÁÉÍÓÚ ]*$")) ? change : null));

        acronymTextField.setTextFormatter(new TextFormatter<>(change ->
                (change.getControlNewText().matches("^[A-Za-zÑñ]{0,3}$")) ? change : null));

        ObservableList<String> comboTeams = FXCollections.observableArrayList(DataFiles.getSingletonDataFiles().getTeams());
        teamsComboBox.setItems(comboTeams);
        locationComboBox.setItems(FXCollections.observableArrayList("Occidental","Oriental"));

        setListenerForComboBox();
        modifyBtn.setDisable(true);
        deleteBtn.setDisable(true);
        fillColumns();
    }

    @FXML
    void close(ActionEvent event) {
        Stage stage = (Stage) nameTextField.getScene().getWindow();
        stage.close();
    }

    @FXML
    void showData(ActionEvent event) throws IOException {
        File file = new File("src/files/Data.xlsx");

        //first check if Desktop is supported by Platform or not
        if(!Desktop.isDesktopSupported()){
            System.out.println("Desktop is not supported");
            return;
        }

        Desktop desktop = Desktop.getDesktop();

        //let's try to open PDF file
        if(file.exists()) desktop.open(file);
    }

    private void fillColumns () {

        ArrayList<String> namesList = DataFiles.getSingletonDataFiles().getTeams();
        ArrayList<String> acroList = DataFiles.getSingletonDataFiles().getAcronyms();
        ArrayList<String> locationsList = DataFiles.getSingletonDataFiles().getLocations();

        for (int i = 0; i < namesList.size(); i++) {
            JFXTextField textField = new JFXTextField();

            textField.setTextFormatter(new TextFormatter<>(change ->
                    (change.getControlNewText().matches("\\d{0,9}(\\d[.]\\d{0,2})?")) ? change : null));

            textField.setText("0.0");
            data.add(new TableCrudsData(namesList.get(i), acroList.get(i), locationsList.get(i), textField));
        }
        table.setItems(data);
    }

    @FXML
    void addNewTeam() throws IOException {
        String messageTitle = "A\u00f1adir Equipo";

        String teamName = nameTextField.getText();
        String acro = acronymTextField.getText();
        String  location = locationComboBox.getSelectionModel().getSelectedItem();
        acro = acro.toUpperCase();
        Double[] distances = new Double[DataFiles.getSingletonDataFiles().getTeams().size()];

        for (int i = 0; i < data.size(); i++){
            String temp = ((JFXTextField)(data.get(i).getDistance())).getText();
            if(!temp.isEmpty()){
                distances[i] = Double.parseDouble(temp);
            }else{
                distances[i] = 0.0;
            }
        }

        if(!(teamName.equalsIgnoreCase("") || teamName.equalsIgnoreCase(" "))){
            if(!(acro.equalsIgnoreCase("") || teamName.equalsIgnoreCase(" "))){
                if (acro.length() == 3){
                    if (!DataFiles.getSingletonDataFiles().getAcronyms().contains(acro)){

                        boolean exists = false;

                        int i = 0;
                        while (!exists && i < DataFiles.getSingletonDataFiles().getTeams().size()){

                            if (DataFiles.getSingletonDataFiles().getTeams().get(i).equalsIgnoreCase(teamName)){
                                exists = true;
                            }
                            i++;
                        }
                        if (!exists){

                            boolean hasZeroDistance = false;
                            int j = 0;
                            while (!hasZeroDistance && j < distances.length){
                                if(distances[j] == 0.0){
                                    hasZeroDistance = true;
                                }
                                j++;
                            }

                            if (!hasZeroDistance){
                                JFXTextField textField = new JFXTextField();
                                textField.setTextFormatter(new TextFormatter<>(change ->
                                        (change.getControlNewText().matches("\\d{0,9}(\\d[.]\\d{0,2})?")) ? change : null));

                                textField.setText("0.0");
                                TableCrudsData newTableRow = new TableCrudsData(teamName, acro, location, textField);
                                data.add(newTableRow);

                                //addModifyDistancesToController(distances,distances.length);

                                int pos = DataFiles.getSingletonDataFiles().getTeams().size() + 1;
                                DataFiles.getSingletonDataFiles().addModifyTeamToData(teamName, acro, location, distances, pos);
                                DataFiles.getSingletonDataFiles().addTeamToFXML(teamName, acro, location);

                                DataFiles.getSingletonDataFiles().readTeams();
                                Distance.getInstance().fillMatrixDistance();

                                ObservableList<String> comboTeams = FXCollections.observableArrayList(DataFiles.getSingletonDataFiles().getTeams());
                                teamsComboBox.setItems(comboTeams);
                                setListenerForComboBox();

                                restore();
                                showSuccessfulMessage(messageTitle, "Equipo añadido correctamente.");

                            }else{
                                showNotification(messageTitle, "Ninguna distancia puede valer cero.");
                            }
                        }else{
                            showNotification(messageTitle, "Ya existe un equipo con ese nombre");
                        }
                    }else{
                        showNotification(messageTitle,"Ya existe un equipo con ese acr\u00f3nimo");
                    }
                }else{
                    showNotification(messageTitle,"El acr\u00f3nimo debe tener tres caracteres");
                }
            }else{
                showNotification(messageTitle,"Debe llenar el campo de acr\u00f3nimo");
            }
        }else{
            showNotification(messageTitle,"Debe llenar el campo de nombre");
        }
    }

    @FXML
    void modifyTeam(ActionEvent event) throws IOException {

        String messageTitle = "Modificar Equipo";
        int pos = teamsComboBox.getSelectionModel().getSelectedIndex();
        String teamName = nameTextField.getText();
        String acro = acronymTextField.getText();
        String location = locationComboBox.getSelectionModel().getSelectedItem();
        acro = acro.toUpperCase();
        Double[] distances = new Double[DataFiles.getSingletonDataFiles().getTeams().size()];

        for (int i = 0; i < data.size(); i++){
            String temp = ((JFXTextField)(data.get(i).getDistance())).getText();
            if(!temp.isEmpty()){
                distances[i] = Double.parseDouble(temp);
            }else{
                distances[i] = 0.0;
            }
        }

        if(!(teamName.equalsIgnoreCase("") || teamName.equalsIgnoreCase(" "))){
            if(!(acro.equalsIgnoreCase("") || teamName.equalsIgnoreCase(" "))){
                if (acro.length() == 3){

                    int posCheck =DataFiles.getSingletonDataFiles().getAcronyms().indexOf(acro);

                    if (posCheck == -1 || posCheck == pos){


                        boolean correct = true;
                        int i = 0;
                        while (correct && i < DataFiles.getSingletonDataFiles().getTeams().size()){

                            if (DataFiles.getSingletonDataFiles().getTeams().get(i).equalsIgnoreCase(teamName) && (i != pos)){
                                correct = false;
                            }
                            i++;
                        }
                        if (correct){

                            data.get(pos).setName(teamName);
                            data.get(pos).setAcronym(acro);
                            data.get(pos).setLocation(location);

                            //addModifyDistancesToController(distances,distances.length);


                            DataFiles.getSingletonDataFiles().addModifyTeamToData(teamName, acro, location, distances, pos+1);
                            DataFiles.getSingletonDataFiles().modifyTeamFXML(teamName, acro, location, pos);

                            DataFiles.getSingletonDataFiles().readTeams();
                            Distance.getInstance().fillMatrixDistance();

                            teamsComboBox.getItems().set(pos, teamName);
                            showSuccessfulMessage(messageTitle, "Equipo modificado correctamente.");

                            /*
                            for(int j = 0; j < Controller.getSingletonController().getMatrixDistance().length; j++){
                                for(int k = 0; k < Controller.getSingletonController().getMatrixDistance().length; k++){
                                    System.out.print(Controller.getSingletonController().getMatrixDistance()[j][k] + " ");
                                }
                                System.out.println();
                            }
                            */
                        }else{
                            showNotification(messageTitle,"Ya existe un equipo con ese nombre");
                        }
                    }else{
                        showNotification(messageTitle,"Ya existe un equipo con ese acr\u00f3nimo");
                    }
                }else{
                    showNotification(messageTitle,"El acr\u00f3nimo debe tener tres caracteres");
                }
            }else{
                showNotification(messageTitle,"El campo de acr\u00f3nimo no puede estar vac\u00edo");
            }
        }else{
            showNotification(messageTitle,"El campo de nombre no puede estar vac\u00edo");
        }
    }

    @FXML
    void removeTeam(ActionEvent event) throws IOException {

        int pos = teamsComboBox.getSelectionModel().getSelectedIndex();
        DataFiles.getSingletonDataFiles().removeTeamFromData(pos + 1);
        DataFiles.getSingletonDataFiles().removeTeamFXML(pos);
        data.remove(pos);
        teamsComboBox.getSelectionModel().clearSelection();
        teamsComboBox.getItems().remove(pos);

        DataFiles.getSingletonDataFiles().readTeams();
        Distance.getInstance().fillMatrixDistance();

        restore();
        showSuccessfulMessage("Eliminar Equipo", "El equipo se elimin\u00f3 correctamente");
    }

    @FXML
    void restore() {
        nameTextField.clear();
        acronymTextField.clear();
        locationComboBox.getSelectionModel().clearSelection();
        teamsComboBox.getSelectionModel().clearSelection();
        data.clear();
        fillColumns();
        modifyBtn.setDisable(true);
        deleteBtn.setDisable(true);
        addBtn.setDisable(false);
    }

    public HomeController getHomeController() {
        return homeController;
    }

    public void setHomeController(HomeController homeController) {
        this.homeController = homeController;
    }

    private void setListenerForComboBox(){
        teamsComboBox.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {

                if(newValue != null){
                    addBtn.setDisable(true);
                    deleteBtn.setDisable(false);
                    modifyBtn.setDisable(false);
                    int pos =   DataFiles.getSingletonDataFiles().getTeams().indexOf(newValue);
                    nameTextField.setText(DataFiles.getSingletonDataFiles().getTeams().get(pos));
                    acronymTextField.setText(DataFiles.getSingletonDataFiles().getAcronyms().get(pos));
                    locationComboBox.getSelectionModel().select(DataFiles.getSingletonDataFiles().getLocations().get(pos));

                    double[] distancesRow = Distance.getInstance().getMatrixDistance()[pos];

                    for(int i = 0; i < data.size(); i++){
                        ((JFXTextField)(data.get(i).getDistance())).setText(Double.toString(distancesRow[i]));
                        if(distancesRow[i] == 0.0){
                            ((JFXTextField)(data.get(i).getDistance())).setDisable(true);
                            if(oldValue != null){
                                int posToReset = DataFiles.getSingletonDataFiles().getTeams().indexOf(oldValue);
                                ((JFXTextField)(data.get(posToReset).getDistance())).setDisable(false);
                            }
                        }
                    }
                }
            }
        });
    }

    private void showNotification(String title, String message) {
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.ERROR);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(1));
    }

    private static void showSuccessfulMessage(String title, String message) {
        TrayNotification notification = new TrayNotification();
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setNotificationType(NotificationType.SUCCESS);
        notification.setRectangleFill(Paint.valueOf("#2F2484"));
        notification.setAnimationType(AnimationType.FADE);
        notification.showAndDismiss(Duration.seconds(2));
    }

    private void addModifyDistancesToController(Double[] distances, int pos){

        for(int i = 0; i < distances.length; i++){

            if(i != pos){
                Distance.getInstance().getMatrixDistance()[i][pos] = distances[i];
                Distance.getInstance().getMatrixDistance()[pos][i] = distances[i];
            }else {
                Distance.getInstance().getMatrixDistance()[i][pos] = 0.0;
            }

        }
    }
}
