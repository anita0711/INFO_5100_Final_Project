/**
 * @author Anita Ganani
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {

    /**
     * @param args Method to the entry point of the project.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * @param primaryStage Method to load mainUI FXML, that is main screen.
     */
    @Override
    public void start(Stage primaryStage) {
        Parent root = null;
        try {
            root = FXMLLoader.load(getClass().getResource("mainUI.fxml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.setTitle("Transform your images");
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        scene.getStylesheets().add("/styles/Styles.css");
        primaryStage.show();
    }
}
