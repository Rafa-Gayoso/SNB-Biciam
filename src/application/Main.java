package application;

import eu.mihosoft.scaledfx.ScalableContentPane;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.*;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        try {
            ScalableContentPane scale = new ScalableContentPane();
            Parent root = FXMLLoader.load(getClass().getResource("/visual/Home.fxml"));
            scale.setContent(root);
            Scene scene = new Scene(scale);
            scene.getStylesheets().add(getClass().getResource("/styles/application.css").toExternalForm());

            Rectangle rectangle = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
            primaryStage.setScene(scene);
            primaryStage.setMaxHeight(rectangle.height);
            primaryStage.setMaximized(true);
            primaryStage.setResizable(true);
            primaryStage.setMinHeight(768);
            primaryStage.setMinWidth(1360);
            primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/resources/snb.png")));
            primaryStage.show();
            primaryStage.setOnCloseRequest(event ->
                    System.exit(0));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
