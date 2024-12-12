package com.example.offeraggregation.service;

import com.example.offeraggregation.model.Offer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.mockito.Mockito.*;

/**
 * Unit tests for StreamReaderService.
 */
class StreamReaderServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(StreamReaderServiceTest.class);

    @Mock
    private AggregationService aggregationService;

    @InjectMocks
    private StreamReaderService streamReaderService;

    @BeforeEach
    void setUp() {
        // Initialize mocks before each test
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test processing offers from a stream.
     */
    @Test
    void processOffers() {
        // Mock the behavior of aggregationService.addOffer
        doNothing().when(aggregationService).addOffer(any(Offer.class));

        // Call the method to test
        streamReaderService.processOffers();

        // Verify that addOffer was called at least once
        verify(aggregationService, atLeastOnce()).addOffer(any(Offer.class));
    }

    /**
     * Test processing a single offer.
     */
    @Test
    void processOffer() {
        Offer offer = new Offer("exampleProductCode", 100.0);
        // Mock the behavior of aggregationService.addOffer
        doNothing().when(aggregationService).addOffer(offer);

        // Call the method to test
        streamReaderService.processOffer(offer);

        // Verify that addOffer was called exactly once with the specified offer
        verify(aggregationService, times(1)).addOffer(offer);
    }
}