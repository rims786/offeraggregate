package com.example.offeraggregation.service;

import com.example.offeraggregation.exception.ProductNotFoundException;
import com.example.offeraggregation.model.AggregationState;
import com.example.offeraggregation.model.Offer;
import com.example.offeraggregation.model.ProductAggregation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AggregationServiceImpl.
 */
class AggregationServiceImplTest {

    private static final Logger logger = LoggerFactory.getLogger(AggregationServiceImplTest.class);

    private AggregationServiceImpl aggregationService;

    @BeforeEach
    void setUp() {
        aggregationService = new AggregationServiceImpl();
    }

    /**
     * Test adding offers and closing the aggregation.
     */
    @Test
    void addOfferAndCloseAggregation() {
        Offer offer1 = new Offer("7392158007783", 100.0);
        Offer offer2 = new Offer("7392158007783", 200.0);
        Offer offer3 = new Offer("7392158007783", 300.0);

        aggregationService.addOffer(offer1);
        aggregationService.addOffer(offer2);
        aggregationService.addOffer(offer3);

        ProductAggregation aggregation = aggregationService.getAggregation("7392158007783");
        assertNotNull(aggregation);
        assertEquals(3, aggregation.offerCount());
        assertEquals(200.0, aggregation.averagePrice());

        // Verify that the aggregation is closed
        assertEquals(AggregationState.CLOSED, aggregation.state());

        // Attempt to add another offer after closing
        Offer offer4 = new Offer("7392158007783", 400.0);
        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> aggregationService.addOffer(offer4));
        assertEquals("Aggregation for product code 7392158007783 is closed. Offer not added.", exception.getMessage());

        aggregation = aggregationService.getAggregation("7392158007783");
        assertNotNull(aggregation);
        assertEquals(3, aggregation.offerCount()); // Offer count should remain 3
        assertEquals(200.0, aggregation.averagePrice()); // Average price should remain unchanged
    }

    /**
     * Test getting aggregation with no offers.
     */
    @Test
    void getAggregationWithNoOffers() {
        ProductAggregation aggregation = aggregationService.getAggregation("nonexistentProductCode");
        assertNotNull(aggregation);
        assertEquals(0, aggregation.offerCount());
        assertEquals(0.0, aggregation.averagePrice());
        assertEquals(AggregationState.OPEN, aggregation.state());
    }

    /**
     * Test closing aggregation with not enough offers.
     */
    @Test
    void closeAggregationWithNotEnoughOffers() {
        Offer offer1 = new Offer("7392158007783", 100.0);
        aggregationService.addOffer(offer1);

        assertThrows(IllegalStateException.class, () -> aggregationService.closeAggregation("7392158007783"));
    }

    /**
     * Test closing aggregation with no offers.
     */
    @Test
    void closeAggregationWithNoOffers() {
        assertThrows(ProductNotFoundException.class, () -> aggregationService.closeAggregation("nonexistentProductCode"));
    }

    /**
     * Test adding offer with null product code.
     */
    @Test
    void addOfferWithNullProductCode() {
        Offer offer = new Offer(null, 100.0);
        assertThrows(NullPointerException.class, () -> aggregationService.addOffer(offer));
    }

    /**
     * Test adding offer with negative price.
     */
    @Test
    void addOfferWithNegativePrice() {
        Offer offer = new Offer("7392158007783", -100.0);
        assertThrows(IllegalArgumentException.class, () -> aggregationService.addOffer(offer));
    }
}