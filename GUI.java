import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.fxml.*;

import java.net.URL;
import javafx.scene.Parent;

/**
 * Main JavaFX GUI class
 *
 * @author Fatlir Topalli (K1921543)
 * @version 2020.03.15
 */
public class GUI extends Application
{
    /**
     * The start method is the main entry point for every JavaFX application. 
     * It is called after the init() method has returned and after 
     * the system is ready for the application to begin running.
     *
     * @param  stage the primary stage for this application.
     */
    @Override
    public void start(Stage stage) throws Exception
    {
        // Load the listings ready for filtering. 
        SharedData.listingsFilter = new ListingsFilter();
        
        URL url = getClass().getResource("GUI.fxml");
        Parent root = FXMLLoader.load(url);
        Scene scene = new Scene (root);
        
        stage.setTitle("AirBnB Viewer");
        stage.setScene(scene);
        stage.setMinWidth(720);
        stage.setMinHeight(480);
        stage.show();
    }
}
