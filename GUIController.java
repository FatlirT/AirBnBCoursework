import javafx.event.ActionEvent;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.Event;
import javafx.fxml.Initializable;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import java.util.ArrayList;
import javafx.scene.Parent;
import javafx.stage.Stage;

import java.io.*;
import java.text.*;

/**
 * JavaFX GUI Controller class.
 *
 * @author Fatlir Topalli (K1921543), Daniel VC (K19012373) and Skye Macdonald (k19015078)
 * @version 2020.03.27
 */
public class GUIController implements Initializable
{
    // main panel
    @FXML private BorderPane panelContainer;
    @FXML private ComboBox toMenu, fromMenu;
    @FXML private Button goPrevious, goNext;
    @FXML private Label invalidRangeLabel;
    @FXML private BorderPane navigationButtons;
    @FXML private Button popOut;
    private ArrayList<String> panelList;
    private String currentPanel;
    
    /**
     * Initialize JavaFX controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        for(int i=0; i<=1000; i+=100)
        {
            fromMenu.getItems().add("£" + i);
            
            if(!(i==0))
                toMenu.getItems().add("£" + i);
            
        }
        toMenu.getItems().add(">£1000");
        
        // the order of the following add statements is the order of the panel keys in the ArrayList
        // and thus is the order they will be cycled through
        panelList = new ArrayList<String>();
        
        // names added to this ArrayList must be identical to the panel's file name
        // initial panel to be displayed on launch must be added to the arrayList first, and so must have index 0
        panelList.add("welcomePanel"); // initial panel
        panelList.add("mapPanel");
        panelList.add("statisticsPanel");
        panelList.add("chartPanel");

        loadPanel(panelList.get(0));
    }
    
    /**
    * Navigation through the panels
    */
    @FXML
    private void navigatePanels(ActionEvent event)
    {
        int panelIndex = panelList.indexOf(currentPanel); // panelList index of panel on display when event occurred
        if(event.getSource() == goNext)
        {
            if(panelIndex == panelList.size() - 1)
            {
                panelIndex = 0;
            }
            else
            {
                panelIndex += 1;
            }
        }
        else if(event.getSource() == goPrevious)
        {
            if(panelIndex == 0)
            {
                panelIndex = panelList.size() - 1;
            }
            else
            {
                panelIndex -= 1;
            }
        }
        loadPanel(panelList.get(panelIndex));
    }
    
    /**
     * Used to load a panel on to the stage
     * @param panel The name of the FXML panel file
     */
    private void loadPanel(String panel)
    {
        Parent loadedPanel  = null;
        try
        {
            loadedPanel = FXMLLoader.load(getClass().getResource(panel + ".fxml"));
        }
        catch(IOException ex)
        {
            throw new RuntimeException(ex);
        }
        panelContainer.setCenter(loadedPanel);
        currentPanel = panel;
        popOut.setDisable("welcomePanel".equals(currentPanel));
    }
    
    /**
     * Checks validity of chosen value in ComboBox compared to value chosen in other ComboBox
     */
    @FXML
    private void checkComboBoxValues(ActionEvent event) throws ParseException
    {
        int fromMenuValue = 0;
        if(fromMenu.getValue() != null)
            fromMenuValue = NumberFormat.getCurrencyInstance().parse((String)fromMenu.getValue()).intValue();
        
        int toMenuValue = 0;
        if(toMenu.getValue() != null) {
            // if it's an option that means "greater than" then it means there is no upper limit.
            if (toMenu.getValue().toString().startsWith(">")) toMenuValue = Integer.MAX_VALUE;
            else toMenuValue = NumberFormat.getCurrencyInstance().parse((String) toMenu.getValue()).intValue();
        }
        
        if((fromMenuValue > toMenuValue) && fromMenu.getValue() != null && toMenu.getValue() != null)
        {
            invalidRangeLabel.setVisible(true);
            navigationButtons.setDisable(true);
            loadPanel(panelList.get(0)); // 0 corresponds to the index of the initial panel, the welcomePanel
        }
        else if(fromMenu.getValue() != null && toMenu.getValue() != null)
        {
            // valid range selected
            invalidRangeLabel.setVisible(false);
            navigationButtons.setDisable(false);
            
            // pass range values to data filter
            SharedData.listingsFilter.setPriceFilter(fromMenuValue, toMenuValue);
            
            // reload the panel to display the data for the newly selected range
            loadPanel(currentPanel);
        }
    }

    /**
     * Pops out a panel and makes it appear in a separate window.
     * 
     * @param event Ignored.
     * @throws IOException If the panel cannot be loaded.
     */
    @FXML
    private void popOut(Event event) throws IOException
    {
        Stage stage = new Stage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource(currentPanel + ".fxml"));
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        // Get the controller...
        var controller = loader.getController();
        // If it is the statistics controller, tell it not to save panels...
        if (controller instanceof SharedStateController) ((SharedStateController) controller).disableSharedState();
        // Let the user know it won't update...
        stage.setTitle("AirBnB Viewer - Pop Out (Non-Updating) - " + SharedData.listingsFilter.getDescription());
        stage.show();
    }
}
