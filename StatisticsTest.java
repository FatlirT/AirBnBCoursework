import static org.junit.Assert.*;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * The test class StatisticsTest.
 * This tests the Statistics Class, using a mix of real and made-up data.
 * This should ensure that the calculations are correct and are consistent.
 * It also tests a case where there are no listings to ensure that the program won't crash if that happens.
 *
 * @author  Skye Macdonald (k19015078)
 * @version 2020-03-15
 */
public class StatisticsTest
{
    // The full listings used in the real program, used for the regression test.
    private static Collection<AirbnbListing> fullListings = new AirbnbDataLoader().load();
    // Create some dummy listings to test the statistics with since it's possible to calculate the statistics for these by hand.
    private static Collection<AirbnbListing> simpleListings = Arrays.asList(
            new AirbnbListing("l1", "Test 1", "h1", "Blah", "Walford", 0, 0, "Private room", 25, 2, 50, "03/12/2016", 0.2, 1, 20),
            new AirbnbListing("l2", "Test 2", "h2", "Bleh", "Walford", 1, 0, "Private room", 20, 3, 40, "05/12/2016", 0.25, 1, 0),
            new AirbnbListing("l3", "Test 3", "h3", "Bluh", "Walford", 1, 1, "Entire home/apt", 500, 10, 25, "11/12/2016", 0.1, 1, 60),
            new AirbnbListing("l4", "Test 4", "h4", "Blih", "Leytown", 2, 0, "Private room", 20, 1, 100, "30/12/2016", 0.5, 1, 150),
            new AirbnbListing("l5", "Test 5", "h6", "Bloh", "Leytown", 2, 1, "Entire home/apt", 300, 5, 25, "11/09/2016", 0.1, 1, 120));

    /**
     * Test to make sure that nothing weird happens when given an empty collection of listings.
     */
    @Test
    public void noListings() {
        Statistics statistics = new Statistics(Collections.emptyList());
        assertEquals("There should be homes/apartments in an empty listing",0, statistics.getEntireHomesOrApartments());
        assertNull("As there are no listings, there should be no most expensive borough", statistics.getMostExpensiveBorough());
        assertEquals("No reviews, and no properties should mean no reviews per property.", 0, statistics.getReviewsPerProperty(), 0);
        assertEquals("As there are no listings, there should be no available properties",0, statistics.getTotalAvailableProperties());
        assertEquals("No listings, so there should be no properties per borough", 0, statistics.getPropertiesPerBorough(), 0);
        assertEquals("There are no listings, so no reviews, so there should be no reviews per month", 0, statistics.getTotalReviewsPerMonth(), 0);
        assertNull("No listings, so there should be no borough with the most reviews", statistics.getMostReviewedBorough());
        assertNull("No listings, so there should be no most actively reviewed borough", statistics.getMostActivelyReviewedBorough());
    }

    /**
     * Test to make sure that there are no exceptions when calculating statistics for the dummy listings
     */
    @Test
    public void calculatedStatisticsNoExceptions() {
        new Statistics(simpleListings);
    }
    
    /**
     * Test to make sure that there are no exceptions when calculating statistics from the full listings
     */
    @Test
    public void regressionStatisticsNoExceptions() {
        new Statistics(fullListings);
    }

    /**
     * Test to make sure that the average number of reviews per property is calculated correctly, compared against a hand calculated value.
     */
    @Test
    public void calculatedReviewsPerProperty() {
        Statistics simpleStatistics = new Statistics(simpleListings);
        assertEquals("The average number of reviews per property hasn't been calculated correctly",48, simpleStatistics.getReviewsPerProperty(), 0);
    }

    /**
     * Test to make sure that the total number of available properties is counted correctly, compared against a hand calculated value.
     */
    @Test
    public void calculatedTotalAvailableProperties() {
        Statistics simpleStatistics = new Statistics(simpleListings);
        assertEquals("The number of available properties isn't counted correctly", 4, simpleStatistics.getTotalAvailableProperties());
    }

    /**
     * Test to make sure that the number of entire homes or apartments is counted correctly, compared against a hand calculated value.
     */
    @Test
    public void calculatedEntireHomesOrApartments() {
        Statistics simpleStatistics = new Statistics(simpleListings);
        assertEquals("The number of entire homes/apartments isn't counted correctly", 2, simpleStatistics.getEntireHomesOrApartments());
    }

    /**
     * Test to make sure that the most expensive borough is worked out correctly, compared against a hand calculated value.
     */
    @Test
    public void calculatedMostExpensiveBorough() {
        Statistics simpleStatistics = new Statistics(simpleListings);
        assertEquals("The most expensive borough was not worked out correctly", "Walford", simpleStatistics.getMostExpensiveBorough());
    }

    /**
     * Test to make sure that the average number of properties per borough is being calculated correctly.
     */
    @Test
    public void calculatedPropertiesPerBorough() {
        Statistics simpleStatistics = new Statistics(simpleListings);
        assertEquals("The average number of properties per borough was not calculated correctly", 2.5, simpleStatistics.getPropertiesPerBorough(), 0);
    }

    /**
     * Test to make sure that the total count of reviews per month is calculated correctly.
     */
    @Test
    public void calculatedTotalReviewsPerMonth() {
        Statistics statistics = new Statistics(simpleListings);
        // Due to the nature of floating point numbers, there is a difference compared to the hand calculated value. The code is fine, it's just how floating point works, so we use the delta.
        assertEquals("The total number of reviews per month was not calculated correctly", 1.15, statistics.getTotalReviewsPerMonth(), 0.000000000000001);
    }
    
    /**
     * Test to make sure that the borough with the most reviews is worked out correctly, compared against a value figured out by hand.
     */
    @Test
    public void calculatedMostReviewedBorough() {
        Statistics simpleStatistics = new Statistics(simpleListings);
        assertEquals("The borough with the most reviews was not worked out correctly", "Leytown", simpleStatistics.getMostReviewedBorough());
    }

    /**
     * Test to make sure that the borough with the most reviews per month per property is worked out correctly, compared against a value figured out by hand.
     */
    @Test
    public void calculatedMostActivelyReviewedBorough() {
        Statistics simpleStatistics = new Statistics(simpleListings);
        assertEquals("The borough with the most actively reviewed listings was not worked out correctly", "Leytown", simpleStatistics.getMostActivelyReviewedBorough());
    }
    
    /**
     * Test to make sure that the count of entire homes or apartments hasn't changed from when we made this test.
     */
    @Test
    public void regressionEntireHomesOrApartments() {
        Statistics fullStatistics = new Statistics(fullListings);
        assertEquals("Count of full homes or apartments has changed from a previously known correct value.", 27175, fullStatistics.getEntireHomesOrApartments());
    }

    /**
     * Test to make sure that the count of entire homes or apartments hasn't changed from when we made this test.
     */
    @Test
    public void regressionMostExpensiveBorough() {
        Statistics fullStatistics = new Statistics(fullListings);
        assertEquals("Calculated most expensive borough has changed from a previously known correct value.", "Richmond upon Thames", fullStatistics.getMostExpensiveBorough());
    }

    /**
     * Test to make sure that the count of total available properties hasn't changed from when we made this test.
     */
    @Test
    public void regressionTotalAvailableProperties() {
        Statistics fullStatistics = new Statistics(fullListings);
        assertEquals("Count of full homes or apartments has changed from a previously known correct value.", 41941, fullStatistics.getTotalAvailableProperties());
    }

    /**
     * Test to make sure that the average number of reviews per properties hasn't changed from when we made this test.
     */
    @Test
    public void regressionReviewsPerProperty() {
        Statistics fullStatistics = new Statistics(fullListings);
        assertEquals("Calculated average number of reviews per property has changed from a previously known correct value.", 12.469185960225586, fullStatistics.getReviewsPerProperty(), 0);
    }

    /**
     * Test to make sure that the calculated average of properties per borough hasn't changed from when we made this test.
     */
    @Test
    public void regressionPropertiesPerBorough() {
        Statistics fullStatistics = new Statistics(fullListings);
        assertEquals("Calculated number of properties per borough has changed from a previously known correct value", 1633.4545454545455, fullStatistics.getPropertiesPerBorough(), 0);
    }

    /**
     * Test to make sure that the total count of reviews per month is calculated the same and hasn't changed from when we made this test.
     */
    @Test
    public void regressionTotalReviewsPerMonth() {
        Statistics statistics = new Statistics(fullListings);
        assertEquals("The calculated total number of reviews per month has changed from a previously known correct value", 32364.63999999979, statistics.getTotalReviewsPerMonth(), 0);
    }

    /**
     * Test to make sure that the borough with the most reviews is worked out the same as it was when we made this test.
     */
    @Test
    public void regressionMostReviewedBorough() {
        Statistics statistics = new Statistics(fullListings);
        assertEquals("The calculated borough with the most reviews has changed from the previously known correct value", "Westminster", statistics.getMostReviewedBorough());
    }

    /**
     * Test to make sure that the borough with the most reviews per month per property is worked out the same as it was when we made this test.
     */
    @Test
    public void regressionMostActivelyReviewedBorough() {
        Statistics statistics = new Statistics(fullListings);
        assertEquals("The calculated borough with the most reviews per month per property has changed from the previously known correct value", "Westminster", statistics.getMostActivelyReviewedBorough());
    }

}

