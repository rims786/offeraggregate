package com.example.offeraggregation.controller;

import com.example.offeraggregation.model.AggregationState;
import com.example.offeraggregation.model.Offer;
import com.example.offeraggregation.model.ProductAggregation;
import com.example.offeraggregation.service.AggregationService;
import com.example.offeraggregation.service.CSVReaderService;
import com.example.offeraggregation.service.StreamReaderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OfferController.
 */
class OfferControllerTest {

    private static final Logger logger = LoggerFactory.getLogger(OfferControllerTest.class);

    @Mock
    private AggregationService aggregationService;

    @Mock
    private CSVReaderService csvReaderService;

    @Mock
    private StreamReaderService streamReaderService;

    @InjectMocks
    private OfferController offerController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Test for adding an offer.
     */
    @Test
    void addOffer() {
        Offer offer = new Offer("7392158007783", 1095.00);
        doNothing().when(aggregationService).addOffer(offer);

        ResponseEntity<String> response = offerController.addOffer(offer);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("Offer added successfully.", response.getBody());
        verify(aggregationService, times(1)).addOffer(offer);
    }

    /**
     * Test for getting an aggregation.
     */
    @Test
    void getAggregation() {
        ProductAggregation aggregation = new ProductAggregation("7392158007783", 1095.00, 1200.00, 1147.50, 2, AggregationState.OPEN);
        when(aggregationService.getAggregation("7392158007783")).thenReturn(aggregation);

        ResponseEntity<ProductAggregation> response = offerController.getAggregation("7392158007783");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(aggregation, response.getBody());
    }

    /**
     * Test for closing an aggregation.
     */
    @Test
    void closeAggregation() {
        doNothing().when(aggregationService).closeAggregation("7392158007783");

        ResponseEntity<String> response = offerController.closeAggregation("7392158007783");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Aggregation closed successfully.", response.getBody());
        verify(aggregationService, times(1)).closeAggregation("7392158007783");
    }

    /**
     * Test for processing offers from a CSV file.
     */
    @Test
    void processCSV() {
        doNothing().when(csvReaderService).processOffers();

        ResponseEntity<String> response = offerController.processCSV();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("CSV processed successfully", response.getBody());
        verify(csvReaderService, times(1)).processOffers();
    }

    /**
     * Test for processing offers from a stream.
     */
    @Test
    void processStream() {
        doNothing().when(streamReaderService).processOffers();

        ResponseEntity<String> response = offerController.processStream();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Stream processed successfully", response.getBody());
        verify(streamReaderService, times(1)).processOffers();
    }
}