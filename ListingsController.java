import javafx.event.ActionEvent;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Insets;
import javafx.scene.input.*;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Controller for the Borough Window.
 * 
 * @author Skye Macdonald (k19015078) and Kevin Quah (k1921877)
 * @version 2020-03-27
 */
public class ListingsController implements Initializable {
    @FXML private ComboBox<String> sortingOptions;
    @FXML private TableView<AirbnbListing> table;
    @FXML private TableColumn<AirbnbListing, String> nameColumn;
    @FXML private TableColumn<AirbnbListing, Integer> priceColumn;
    @FXML private TableColumn<AirbnbListing, Integer> reviewsColumn;
    @FXML private TableColumn<AirbnbListing, Integer> nightsColumn;
    @FXML private CheckBox reverseSort;
    @FXML private Pane pane;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sortingOptions.getItems().addAll("Host Name", "Price per Night", "Number of Reviews", "Minimum Number of Nights");
        nameColumn.setCellValueFactory(data -> new ReadOnlyStringWrapper(data.getValue().getHost_name()));
        priceColumn.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getPrice()).asObject());
        reviewsColumn.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getNumberOfReviews()).asObject());
        nightsColumn.setCellValueFactory(data -> new ReadOnlyIntegerWrapper(data.getValue().getMinimumNights()).asObject());
    }

    /**
     * Set the filter used to display properties in this borough.
     * 
     * @param filter The filter this borough window will use to show the listings.
     */
    public void setFilter(ListingsFilter filter) {
        ((Stage) pane.getScene().getWindow()).setTitle(filter.getDescription());
        table.getItems().clear();
        table.getItems().addAll(filter.getListings());
    }
    
    /**
     * Sorts the table according to a specific column
     */
    @FXML
    private void onSortValueChange(ActionEvent event) {
        String sort = sortingOptions.getValue();
        // null/empty check
        if (sort == null || sort.equals("")) {
            reverseSort.setDisable(true);   
            return;
        }
        // Enable reversing the sorting direction...
        reverseSort.setDisable(false);
        
        // Get the sorted listings.
        List<AirbnbListing> sorted = sortListings(table.getItems(), sort, reverseSort.isSelected());
        // Replace the table with the sorted listings.
        table.getItems().clear();
        table.getItems().setAll(sorted);
    }
    
    /**
     * Merge sort for Airbnb listings
     */
    
    private List<AirbnbListing> sortListings(Collection<AirbnbListing> unsortedList, String sortBy, boolean reverse) {
        // Create the comparator used to sort the list...
        Comparator<AirbnbListing> comparator;
        switch (sortBy){
            case "Host Name":
                comparator = Comparator.comparing(AirbnbListing::getHost_name);
                break;
            case "Price per Night":
                comparator = Comparator.comparingInt(AirbnbListing::getPrice);
                break;
            case "Number of Reviews":
                comparator = Comparator.comparingInt(AirbnbListing::getNumberOfReviews);
                break;
            case "Minimum Number of Nights":
                comparator = Comparator.comparingInt(AirbnbListing::getMinimumNights);
                break;
            default:
                throw new IllegalArgumentException("Unexpected sorting type");
        }
        // Reverse it if needed...
        if (reverse) comparator = comparator.reversed();
        
        return unsortedList.stream().sorted(comparator).collect(Collectors.toUnmodifiableList());
    }
    
    /**
     * Displays the description of a property in a new window
     */
    @FXML
    private void viewProperty(MouseEvent event){
        // Retrieve selected record
        AirbnbListing record = table.getSelectionModel().getSelectedItem();
        
        // Create a new stage
        Stage stage = new Stage();
        
        // Create a new grid pane
        GridPane pane = new GridPane();
        pane.setPadding(new Insets(10, 10, 10, 10));
        
        // Set property values on to the label
        Label myLabel = new Label(
            "ID: " + record.getId() + "\n" +
            "Name: " + record.getName() + "\n" +
            "Host ID: " + record.getHost_id() + "\n" +
            "Host Name: " + record.getHost_name() + "\n" +
            "Neighbourhood: " + record.getNeighbourhood() + "\n" +
            "Latitude: " + record.getLatitude() + "\n" +
            "Longitude: " + record.getLongitude() + "\n" +
            "Room type: " + record.getRoom_type() + "\n" +
            "Price: " + record.getPrice() + "\n" +
            "Minimum nights: " + record.getMinimumNights() + "\n" +
            "Number of reviews: " + record.getNumberOfReviews() + "\n" +
            "Last review: " + record.getLastReview() + "\n" +
            "Reviews per month: " + record.getReviewsPerMonth() + "\n" +
            "Calculated host listings count: " + record.getCalculatedHostListingsCount() + "\n" +
            "Availability 365: " + record.getAvailability365());
        
        // Add the label into the pane
        pane.add(myLabel, 1, 0);
        
        Scene scene = new Scene(pane);
        stage.setTitle("Property");
        stage.setScene(scene);
        
        // Display the stage (window)
        stage.show();
    }
}
