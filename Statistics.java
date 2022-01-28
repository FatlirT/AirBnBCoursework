import java.util.*;

/**
 * This class calculates statistics on creation, and then allows those calculated statistics to be accessed. 
 * 
 * @author Skye Macdonald (k19015078)
 * @version 2020-03-28
 */
public class Statistics {
    // Instance fields that will contain the calculated statistics
    private double reviewsPerProperty = 0;
    private int totalAvailableProperties = 0;
    private int entireHomesOrApartments = 0;
    private String mostExpensiveBorough;
    private double propertiesPerBorough = 0;
    private double totalReviewsPerMonth = 0;
    private String mostReviewedBorough;
    private String mostActivelyReviewedBorough;

    /**
     * Create a new statistics object using the data source given.
     * The statistics calculated will be stored in the object, and accessed with the public methods.
     * 
     * @param dataSource A collection of {@link AirbnbListing}s that will be processed to get the statistics 
     */
    public Statistics(Collection<AirbnbListing> dataSource) {
        int totalReviews = 0;
        Map<String, List<AirbnbListing>> boroughs = new HashMap<>();
        for (AirbnbListing listing : dataSource) {
            totalReviews += listing.getNumberOfReviews();
            // Note: This seems to be what it is referring to when it means "available" listings...
            if (listing.getAvailability365() > 0) totalAvailableProperties++;
            if (listing.getRoom_type().equals("Entire home/apt")) entireHomesOrApartments++;
            String borough = listing.getNeighbourhood();
            // If the borough isn't in the set, then add it ready for the next part...
            boroughs.putIfAbsent(borough, new ArrayList<>());
            // Add the minimum price to the borough...
            boroughs.get(listing.getNeighbourhood()).add(listing);
            // Add the number of reviews per month to the total count of reviews per month
            totalReviewsPerMonth += listing.getReviewsPerMonth();
        }
        
        mostExpensiveBorough = boroughs.entrySet().stream()
                // Turn every entry of borough & list of minimum prices into an entry of borough & average minimum price
                .map(entry -> Map.entry(entry.getKey(),entry.getValue().stream().mapToInt(listing -> listing.getMinimumNights() * listing.getPrice()).sum() / entry.getValue().size()))
                // Get the maximum average minimum price, and figure out which borough it was from, then return that. Will be null in the case of no listings.
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        
        mostReviewedBorough = boroughs.entrySet().stream()
                // Map the list of listings to total number of reviews per borough...
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().stream().mapToInt(AirbnbListing::getNumberOfReviews).sum()))
                // Take the maximum number of reviews per borough
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        
        mostActivelyReviewedBorough = boroughs.entrySet().stream()
                // Map the list of listings to average number of reviews per month per borough...
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().stream().mapToDouble(AirbnbListing::getReviewsPerMonth).sum() / entry.getValue().size()))
                // Take the maximum number of reviews per month per borough
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(null);
        
        // Calculate the average reviews per property
        if (totalReviews > 0) reviewsPerProperty = ((double) totalReviews) / ((double) dataSource.size());
        
        // Calculate the average number of properties per borough...
        // We use this to get the number of boroughs as it has already been keeping count of them...
        int numberOfBoroughs = boroughs.size();
        // So we don't divide by zero!
        if (numberOfBoroughs > 0) {
            // We work out the total number of properties by counting the minimum prices...
            int totalNumberOfProperties = boroughs.values().stream().mapToInt(List::size).sum();
            // Then we calculate and set the average...
            propertiesPerBorough = ((double) totalNumberOfProperties) / ((double) numberOfBoroughs);
        }
    }

    /**
     * Get the average (mean) number of reviews per property.
     * (count of all reviews) / (number of listings)
     * 
     * @return the average number of reviews per property
     */
    public double getReviewsPerProperty() {
        return reviewsPerProperty;
    }

    /**
     * Get the total number of available properties.
     * These are properties that are available for at least 1 day a year.
     * 
     * @return the total number of available properties
     */
    public int getTotalAvailableProperties() {
        return totalAvailableProperties;
    }

    /**
     * Get the number of listings that are entire homes or apartments.
     * 
     * @return the number of listings that are entire homes or apartments
     */
    public int getEntireHomesOrApartments() {
        return entireHomesOrApartments;
    }

    /**
     * Get the most expensive borough.
     * This is the borough with the highest average minimum price.
     * The highest average minimum price is the average of all minimum prices of listings in the borough.
     * The minimum price is the lowest price a listing can be used for. Which is: (minimum days) * (daily price).
     * 
     * @return the most expensive borough or null if no listings.
     */
    public String getMostExpensiveBorough() {
        return mostExpensiveBorough;
    }

    /**
     * Get the average (mean) number of properties per borough.
     * 
     * @return The average number of properties per borough.
     */
    public double getPropertiesPerBorough() {
        return propertiesPerBorough;
    }

    /**
     * Get the total number of reviews per month. 
     * his is effectively how "busy" the AirBnB dataset is.
     * 
     * @return the total number of reviews per month
     */
    public double getTotalReviewsPerMonth() {
        return totalReviewsPerMonth;
    }

    /**
     * Get the borough with the most total reviews.
     * 
     * @return The borough with the most total reviews, or null if there are no listings.
     */
    public String getMostReviewedBorough() {
        return mostReviewedBorough;
    }

    /**
     * Get the most actively reviewed borough.
     * This is the borough with the most reviews per month per property.
     * 
     * @return The most actively reviewed borough, or null if there are no listings.
     */
    public String getMostActivelyReviewedBorough() {
        return mostActivelyReviewedBorough;
    }
}
