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
 * Unit tests for CSVReaderService.
 */
class CSVReaderServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(CSVReaderServiceTest.class);

    @Mock
    private AggregationService aggregationService;

    @InjectMocks
    private CSVReaderService csvReaderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test processing offers from a CSV file.
     */
    @Test
    void processOffers() {
        doNothing().when(aggregationService).addOffer(any(Offer.class));

        csvReaderService.processOffers();

        verify(aggregationService, atLeastOnce()).addOffer(any(Offer.class));
    }

    /**
     * Test processing a single offer.
     */
    @Test
    void processOffer() {
        Offer offer = new Offer("exampleProductCode", 100.0);
        doNothing().when(aggregationService).addOffer(offer);

        csvReaderService.processOffer(offer);

        verify(aggregationService, times(1)).addOffer(offer);
    }
}