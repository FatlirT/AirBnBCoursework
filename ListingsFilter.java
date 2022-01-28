import java.util.*;
import java.util.stream.Collectors;

/**
 * This class contains the loaded data.
 * You can change the filtering, which changes what listings are returned.
 * The original listings are still kept, so changing the filter starts from the original listings.
 * 
 * @author Skye Macdonald (k19015078)
 * @version 2020-03-27
 */
public class ListingsFilter implements Cloneable {
    // The original listings will which be filtered, this doesn't change.
    private final Collection<AirbnbListing> originalListings;
    // A cache of the listings after being filtered. If null it means a filter has changed.
    private Collection<AirbnbListing> filteredListings;
    // A cache of the statistics of the listings after being filtered. If null it means a filter has changed.
    private Statistics statistics;
    // A cache of the count of properties per borough
    private Map<String, Integer> propertiesPerBorough;
    // Used by the price filter...
    private boolean priceFilter = false;
    private int priceFilterLower = 0;
    private int priceFilterUpper = Integer.MAX_VALUE;
    // The borough filter... null means there is no borough filter.
    private String boroughFilter = null;

    /**
     * The public constructor. This is used by external code to load the data.
     * This will automatically use the {@link AirbnbDataLoader} to load the listings.
     */
    public ListingsFilter() {
        originalListings = new AirbnbDataLoader().load();
    }

    /**
     * Private constructor used by the clone method.
     * This copies all the fields of the original object given.
     * 
     * @param original The object to copy the fields from
     */
    private ListingsFilter(ListingsFilter original) {
        originalListings = original.originalListings;
        filteredListings = original.filteredListings;
        statistics = original.statistics;
        priceFilter = original.priceFilter;
        priceFilterUpper = original.priceFilterUpper;
        priceFilterLower = original.priceFilterLower;
        boroughFilter = original.boroughFilter;
        propertiesPerBorough = original.propertiesPerBorough;
    }

    /**
     * Set the price filter, given a lower and upper bound.
     * 
     * @param lower The lowest price allowed. (inclusive)
     * @param upper The highest price limit. (inclusive)
     */
    public void setPriceFilter(int lower, int upper) {
        // Clear the cache if anything changed!
        if (priceFilter == false || priceFilterUpper != upper || priceFilterUpper != lower) clearCache();
        
        priceFilter = true;
        priceFilterLower = lower;
        priceFilterUpper = upper;
    }

    /**
     * Get the upper price limit used by the filter.
     * 
     * @return The upper price limit used by the filter. (Integer.MAX_VALUE if filter is disabled.)
     */
    public int getUpperPriceFilter() {
        if (priceFilter) return priceFilterUpper;
        else return Integer.MAX_VALUE;
    }

    /**
     * Get the lower price limit used by the filter.
     * 
     * @return The lowest price limit used by the filter. (0 if price filter is disabled).
     */
    public int getLowerPriceFilter() {
        if (priceFilter) return priceFilterLower;
        else return 0;
    }
    
    /**
     * Remove the price filter.
     */
    public void unsetPriceFilter() {
        // Clear the cache if anything changed!
        if (priceFilter) clearCache();
        
        priceFilter = false;
    }

    /**
     * Set the borough filter.
     * 
     * @param borough The borough to filter to. MUST NOT BE NULL!
     */
    public void setBoroughFilter(String borough) {
        // If the filter changed, then clear the cache...
        if (!borough.equals(boroughFilter)) clearCache();
        
        boroughFilter = borough;
    }

    /**
     * Remove the borough filter.
     */
    public void unsetBoroughFilter() {
        // If the filter changed, then we need to clear the cache...
        if (boroughFilter != null) clearCache();
        
        boroughFilter = null;
    }

    /**
     * Get the statistics based off of the filtered listings.
     * 
     * @return The {@link Statistics} object of the filtered listings
     */
    public Statistics getStatistics() {
        // If the statistics cache was cleared, then 
        if (statistics == null) statistics = new Statistics(getListings());
        
        return statistics;
    }

    /**
     * Get a collection of filtered listings.
     * 
     * @return A read only collection of filtered listings.
     */
    public Collection<AirbnbListing> getListings() {
        // If the filtered listing cache is clear, then fill it. 
        if (filteredListings == null) filteredListings = filterListings(originalListings);
        return filteredListings;
    }

    /**
     * Get a description of the listings from this filter at the current settings.
     * It describes the borough, and the price range.
     * 
     * @return A description of the current filter settings.
     */
    public String getDescription() {
        StringBuilder stringBuilder = new StringBuilder("Listings ");
        if (boroughFilter != null) {
            stringBuilder.append("for ");
            stringBuilder.append(boroughFilter);
            stringBuilder.append(" ");
        }
        boolean lowerFilter = priceFilterLower >  0;
        boolean upperFilter = priceFilterUpper != Integer.MAX_VALUE;
        if (priceFilter && (lowerFilter || upperFilter)) {
            stringBuilder.append("with prices ");
            if (lowerFilter) {
                stringBuilder.append("from £");
                stringBuilder.append(priceFilterLower);
                if (upperFilter) stringBuilder.append(" ");
            }
            if (upperFilter) {
                stringBuilder.append("to £");
                stringBuilder.append(priceFilterUpper);
            }
        }
        return stringBuilder.toString();
    }

    /**
     * Get a map which has a count of properties for each borough name.
     * 
     * @return A map which has a count of properties for each borough name.
     */
    public Map<String, Integer> getCountOfPropertiesPerBorough() {
        if (propertiesPerBorough == null) {
            // If there is no value cached, then we need to recalculate it...
            propertiesPerBorough = getListings().stream().collect(
                    // We want an unmodifiable map...
                    Collectors.toUnmodifiableMap(
                            // The key is the neighbourhood...
                            AirbnbListing::getNeighbourhood,
                            // And to count it, every time to add 1. If it's new we add 1, and if it's not new we sum 1 + the existing value.
                            listing -> 1, Integer::sum));
        }
        
        return propertiesPerBorough;
    }
    
    /**
     * Private helper method.
     * Create an unmodifiable collection of listings, filtered using the rules in this object.
     * 
     * @param listings The listings to filter.
     * @return The filtered listings
     */
    private Collection<AirbnbListing> filterListings(Collection<AirbnbListing> listings) {
        return listings.stream()
                // The price filter
                .filter(listing -> {
                    if (!priceFilter) return true;
                    else {
                        int price = listing.getPrice();
                        return (price >= priceFilterLower) 
                            && (price <= priceFilterUpper); 
                    }
                })
                // The borough filter
                .filter(listing -> (boroughFilter == null) || listing.getNeighbourhood().equals(boroughFilter))
                // And we want to return an unmodifiable list...
                .collect(Collectors.toUnmodifiableList());
    }

    /**
     * Private helper method.
     * Clear the cache of the filtered listings.
     * Used when the filters change.
     */
    private void clearCache() {
        filteredListings = null;
        statistics = null;
        propertiesPerBorough = null;
    }

    /**
     * Create a copy of this object.
     * 
     * @return A copy of this object
     */
    @Override
    public ListingsFilter clone() {
        return new ListingsFilter(this);
    }

    // Code generated by IDE
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ListingsFilter that = (ListingsFilter) o;
        return priceFilter == that.priceFilter &&
                priceFilterLower == that.priceFilterLower &&
                priceFilterUpper == that.priceFilterUpper &&
                originalListings.equals(that.originalListings) &&
                Objects.equals(boroughFilter, that.boroughFilter);
    }

    // Code generated by IDE
    @Override
    public int hashCode() {
        return Objects.hash(originalListings, priceFilter, priceFilterLower, priceFilterUpper, boroughFilter);
    }
}
