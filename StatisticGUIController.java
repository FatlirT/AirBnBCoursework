

import javafx.event.ActionEvent;

import java.net.URL;
import java.util.*;

import javafx.fxml.Initializable;
import javafx.fxml.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;


/**
 * GUI controller class for statistics
 *
 * @author Kevin Quah (K1921877) with modifications by Skye Macddonald (k19015078)
 * @version 2020-03-28
 */
public class StatisticGUIController implements Initializable, SharedStateController
{
    private enum Panel {
        FIRST, SECOND, THIRD, FOURTH
    }
    // We want to save this when this is reloaded, so it's static...
    public static Map<Panel, String> statisticPanels = null;
    // Sometimes we don't want to do this though...
    private boolean doNotSavePanels = false;
    
    // statistic panel
    private Statistics statistics;
    @FXML private Label statTitle1;
    @FXML private Label statInfo1;
    @FXML private Button statBack1;
    @FXML private Button statForward1;
    @FXML private Label statTitle2;
    @FXML private Label statInfo2;
    @FXML private Button statBack2;
    @FXML private Button statForward2;
    @FXML private Label statTitle3;
    @FXML private Label statInfo3;
    @FXML private Button statBack3;
    @FXML private Button statForward3;
    @FXML private Label statTitle4;
    @FXML private Label statInfo4;
    @FXML private Button statBack4;
    @FXML private Button statForward4;
    private Deque<String> statDeque;
    
    /**
     * Initialize JavaFX controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Create a new statistic object which stores info about the whole dataset
        statistics = SharedData.listingsFilter.getStatistics();

        // Create and store statistic options
        statDeque = new ArrayDeque<>();
        statDeque.add("Reviews per property");
        statDeque.add("Total available properties");
        statDeque.add("Entire homes or apartments");
        statDeque.add("Most expensive borough");
        statDeque.add("Properties per borough");
        statDeque.add("Total reviews per month");
        statDeque.add("Most reviewed borough");
        statDeque.add("Most actively reviewed borough");

        if (statisticPanels == null) {
            // Make sure we can save the panels
            if (!doNotSavePanels) statisticPanels = Collections.synchronizedMap(new EnumMap<>(Panel.class));
            
            // Setup the initial settings of the panels...
            setStatPanel(Panel.FIRST, statDeque.removeFirst());
            setStatPanel(Panel.SECOND, statDeque.removeFirst());
            setStatPanel(Panel.THIRD, statDeque.removeFirst());
            setStatPanel(Panel.FOURTH, statDeque.removeFirst());
        } else {
            // Restore from the saved panels...
            for (var entry : statisticPanels.entrySet()) {
                String name = entry.getValue();
                setStatPanel(entry.getKey(), name);
                statDeque.remove(name);
            }
        }
    }

    /**
    * Navigation through a specific statistics options
    */
    @FXML
    private void navigateStatPanel(ActionEvent event)
    {
        Panel panel = null;
        String title = "";
        
        Object control = event.getSource();
        if (control == statBack1)
        {
            statDeque.addFirst(statTitle1.getText());
            title = statDeque.removeLast();
            panel = Panel.FIRST;
        }
        else if (control == statForward1)
        {
            statDeque.addLast(statTitle1.getText());
            title = statDeque.removeFirst();
            panel = Panel.FIRST;
        }
        else if (control == statBack2)
        {
            statDeque.addFirst(statTitle2.getText());
            title = statDeque.removeLast();
            panel = Panel.SECOND;
        }
        else if (control == statForward2)
        {
            statDeque.addLast(statTitle2.getText());
            title = statDeque.removeFirst();
            panel = Panel.SECOND;
        }
        else if (control == statBack3)
        {
            statDeque.addFirst(statTitle3.getText());
            title = statDeque.removeLast();
            panel = Panel.THIRD;
        }
        else if (control == statForward3)
        {
            statDeque.add(statTitle3.getText());
            title = statDeque.removeFirst();
            panel = Panel.THIRD;
        }
        else if (control == statBack4)
        {
            statDeque.addFirst(statTitle4.getText());
            title = statDeque.removeLast();
            panel = Panel.FOURTH;
        }
        else if (control == statForward4)
        {
            statDeque.add(statTitle4.getText());
            title = statDeque.removeFirst();
            panel = Panel.FOURTH;
        }
        
        setStatPanel(panel, title);
    }
    
    /**
     * Updates the values stored within the statistics panel.
     * @param panel Identifies the panel to be updated
     * @param stat The statistic to be displayed
     */
    private void setStatPanel(Panel panel, String stat)
    {
        String info;
        // Save this for when we reload...
        if (!doNotSavePanels) statisticPanels.put(panel, stat);
        
        // Retrieve the information
        switch(stat)
        {
            case "Reviews per property":
                info = roundedString(statistics.getReviewsPerProperty());
                break;
            case "Total available properties":
                info = String.valueOf(statistics.getTotalAvailableProperties());
                break;
            case "Entire homes or apartments":
                info = String.valueOf(statistics.getEntireHomesOrApartments());
                break;
            case "Most expensive borough":
                info = statistics.getMostExpensiveBorough();
                break;
            case "Properties per borough":
                info = roundedString(statistics.getPropertiesPerBorough());
                break;
            case "Total reviews per month":
                info = roundedString(statistics.getTotalReviewsPerMonth());
                break;
            case "Most reviewed borough":
                info = statistics.getMostReviewedBorough();
                break;
            case "Most actively reviewed borough":
                info = statistics.getMostActivelyReviewedBorough();
                break;
            default:
                info = "error";
                break;
        }
        
        // Set the information into the controls
        switch(panel)
        {
            case FIRST:
                statInfo1.setText(info);
                statTitle1.setText(stat);
                break;
            case SECOND:
                statInfo2.setText(info);
                statTitle2.setText(stat);
                break;
            case THIRD:
                statInfo3.setText(info);
                statTitle3.setText(stat);
                break;
            case FOURTH:
                statInfo4.setText(info);
                statTitle4.setText(stat);
                break;
        }
    }

    /**
     * Convert a double to a string rounding to a reasonable number of decimal places..
     * 
     * @param number The number to round and convert into a string.
     * @return a string of the number given rounded to a reasonable number of decimal places
     * @author Skye Macdonald (k19015078)
     */
    private String roundedString(double number) {
        return String.format("%.3f", number);
    }

    /**
     * Disable the saving panels. Used when making pop-out windows.
     */
    public void disableSharedState() {
        doNotSavePanels = true;
    }
}
