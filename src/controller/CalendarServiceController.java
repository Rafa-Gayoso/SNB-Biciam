package controller;

import com.jfoenix.controls.JFXProgressBar;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class CalendarServiceController {



    @FXML
    private JFXProgressBar progress;

    @FXML
    private Label lblProgress;


    public JFXProgressBar getProgress() {
        return progress;
    }

    public void setProgress(JFXProgressBar progress) {
        this.progress = progress;
    }

    public Label getLblProgress() {
        return lblProgress;
    }

    public void setLblProgress(Label lblProgress) {
        this.lblProgress = lblProgress;
    }

}
