import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.*;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import javafx.scene.shape.Shape;
import javafx.scene.paint.Color;

/**
 * JavaFX Map panel controller class.
 *
 * @author Daniel VC (K19012373), Skye Macdonald (k19015078) and Fatlir Topalli (K1921543) and Kevin Quah (K1921877)
 * @version 2020.03.28
 */
public class MapController implements Initializable
{
    @FXML private AnchorPane mapContainer;
    
    private ListingsFilter currentFilter = SharedData.listingsFilter.clone();
    
    private Map<String, Integer> counts;
    int highest;
    
    /**
     * Initialize JavaFX controller
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        counts = currentFilter.getCountOfPropertiesPerBorough();
        highest = Collections.max(counts.values());
        // Wrapped in a try/catch block, because it's very easy for something to go wrong here, and this is the only way to get the stack trace to print...
        try {
            // We start with getting all the things in the map panel...
            mapContainer.lookupAll("*").stream()
                    // Then filter it based the length of the node ID. We know that all boroughs have a node ID length of 4.
                    .filter(node -> node.getId() != null && node.getId().length() == 4)
                    // We only want to affect polygons...
                    .filter(Polygon.class::isInstance).map(Polygon.class::cast)
                    // Then attempt to set the borough colour on each hexagon...
                    .forEach(this::setBoroughColour);
        } catch (Exception e) {e.printStackTrace();}
    }

    /**
     * Set the colour of a borough, based on how many listings it has compared to the highest amount in the filter.
     * 
     * @param boroughHexagon The borough to set the colour of.
     */
    private void setBoroughColour(Shape boroughHexagon) {
        String boroughName = boroughLabelToName(boroughHexagon.getId());
        // Set the colour only if the ID of the shape is one of a borough...
        if (boroughName != null) boroughHexagon.setFill(Color.color(
                // Use a nice green colour...
                0, 1, 0.5,
                // We increase the opacity (up to 0.75 or 3/4) to represent how many properties in the borough there are compared to the borough with the highest number of properties...
                (double) counts.getOrDefault(boroughName, 0) / highest * 3 / 4));
    }
    
    /**
     * Gets borough name of button clicked...
     */
    @FXML
    private void displayBoroughListings(MouseEvent event)
    {
        String boroughLabel = event.toString().substring(event.toString().indexOf("id="),event.toString().indexOf(",")).substring(3);
        //System.out.println("Borough name: " + boroughLabel);
        
        String boroughName = boroughLabelToName(boroughLabel);

        //Should display property listings in the borough...
        // This part written by Skye Macdonald (k19015078)
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("listingsWindow.fxml"));
            stage.setScene(new Scene(loader.load()));
            
            ListingsController controller = loader.getController();
            ListingsFilter filter = currentFilter.clone();
            if (boroughName != null) filter.setBoroughFilter(boroughName);
            else filter.unsetBoroughFilter();;
            controller.setFilter(filter);
            
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Convert a borough label (from the map hexagons) to a name (used by the data and listings).
     * 
     * @param boroughLabel A borough label from the map hexagons
     * @return A borough name used by the listings.
     */
    private static String boroughLabelToName(String boroughLabel) {
        // Null check! If the label is null then there is obviously no name!
        if (boroughLabel == null) return null;
        
        // Retrieve full borough name
        // This part written by Kevin Quah (K1921877)
        String boroughName;
        switch (boroughLabel){
            case "KING":
                boroughName = "Kingston upon Thames";
                break;
            case "CROY":
                boroughName = "Croydon";
                break;
            case "BROM":
                boroughName = "Bromley";
                break;
            case "HOUN":
                boroughName = "Hounslow";
                break;
            case "EALI":
                boroughName = "Ealing";
                break;
            case "HAVE":
                boroughName = "Havering";
                break;
            case "HILL":
                boroughName = "Hillingdon";
                break;
            case "HRRW":
                boroughName = "Harrow";
                break;
            case "BREN":
                boroughName = "Brent";
                break;
            case "BARN":
                boroughName = "Barnet";
                break;
            case "ENFI":
                boroughName = "Enfield";
                break;
            case "WALT":
                boroughName = "Waltham Forest";
                break;
            case "REDB":
                boroughName = "Redbridge";
                break;
            case "SUTT":
                boroughName = "Sutton";
                break;
            case "LAMB":
                boroughName = "Lambeth";
                break;
            case "STHW":
                boroughName = "Southwark";
                break;
            case "LEWS":
                boroughName = "Lewisham";
                break;
            case "GWCH":
                boroughName = "Greenwich";
                break;
            case "BEXL":
                boroughName = "Bexley";
                break;
            case "RICH":
                boroughName = "Richmond upon Thames";
                break;
            case "MERT":
                boroughName = "Merton";
                break;
            case "WAND":
                boroughName = "Wandsworth";
                break;
            case "HAMM":
                boroughName = "Hammersmith and Fulham";
                break;
            case "KENS":
                boroughName = "Kensington and Chelsea";
                break;
            case "CITY":
                boroughName = "City of London";
                break;
            case "WSTM":
                boroughName = "Westminster";
                break;
            case "CAMD":
                boroughName = "Camden";
                break;
            case "TOWH":
                boroughName = "Tower Hamlets";
                break;
            case "ISLI":
                boroughName = "Islington";
                break;
            case "HACK":
                boroughName = "Hackney";
                break;
            case "HRGY":
                boroughName = "Haringey";
                break;
            case "NEWH":
                boroughName = "Newham";
                break;
            case "BARK":
                boroughName = "Barking and Dagenham";
                break;
            default:
                boroughName = null;
                break;
        }
        return boroughName;
    }

    /**
     * Highlights boroughs in different ways according to the event type detected.
     * @author Fatlir Topalli (K1921543)
     * @param event Mouse Event detected. 
     */
    @FXML
    private void highlight(MouseEvent event)
    {
        Shape borough = (Shape) event.getSource();
        if(event.getEventType() == MouseEvent.MOUSE_ENTERED)
        {
            borough.setStroke(Color.YELLOW);
        }
        else if(event.getEventType() == MouseEvent.MOUSE_EXITED)
        {
            borough.setStroke(Color.BLACK);
            setBoroughColour(borough);
        }
        else if(event.getEventType() == MouseEvent.MOUSE_PRESSED)
        {
            borough.setFill(Color.YELLOW);
        }
        else if(event.getEventType() == MouseEvent.MOUSE_RELEASED)
        {
            setBoroughColour(borough);
        }
        
    }
}
