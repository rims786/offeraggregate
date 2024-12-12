package com.example.offeraggregation.controller;

import com.example.offeraggregation.exception.ProductNotFoundException;
import com.example.offeraggregation.model.Offer;
import com.example.offeraggregation.model.ProductAggregation;
import com.example.offeraggregation.service.AggregationService;
import com.example.offeraggregation.service.CSVReaderService;
import com.example.offeraggregation.service.StreamReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST controller for managing offers and aggregations.
 */
@RestController
@RequestMapping("/api/offers")
public class OfferController {

    private static final Logger logger = LoggerFactory.getLogger(OfferController.class);

    @Autowired
    private AggregationService aggregationService;

    @Autowired
    private CSVReaderService csvReaderService;

    @Autowired
    private StreamReaderService streamReaderService;

    /**
     * Adds a new offer.
     *
     * @param offer the offer to add
     * @return a response entity with a success message
     */
    @PostMapping
    public ResponseEntity<String> addOffer(@RequestBody Offer offer) {
        logger.info("Adding new offer: {}", offer);
        try {
            aggregationService.addOffer(offer);
            return new ResponseEntity<>("Offer added successfully.", HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error adding offer: {}", offer, e);
            return new ResponseEntity<>("Failed to add offer.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Gets the aggregation for a specific product code.
     *
     * @param productCode the product code
     * @return a response entity with the product aggregation
     */
    @GetMapping("/{productCode}")
    public ResponseEntity<ProductAggregation> getAggregation(@PathVariable String productCode) {
        logger.info("Fetching aggregation for product code: {}", productCode);
        ProductAggregation aggregation = aggregationService.getAggregation(productCode);
        if (aggregation == null) {
            logger.warn("No aggregation found for product code: {}", productCode);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(aggregation, HttpStatus.OK);
    }

    /**
     * Closes the aggregation for a specific product code.
     *
     * @param productCode the product code
     * @return a response entity with a success message
     */
    @PostMapping("/{productCode}/close")
    public ResponseEntity<String> closeAggregation(@PathVariable String productCode) {
        logger.info("Closing aggregation for product code: {}", productCode);
        try {
            aggregationService.closeAggregation(productCode);
            return new ResponseEntity<>("Aggregation closed successfully.", HttpStatus.OK);
        } catch (ProductNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.error("Error closing aggregation for product code: {}", productCode, e);
            return new ResponseEntity<>("Failed to close aggregation.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Processes a CSV file to add offers.
     *
     * @return a response entity with a success message
     */
    @PostMapping("/process-csv")
    public ResponseEntity<String> processCSV() {
        logger.info("Processing CSV file");
        csvReaderService.processOffers();
        return new ResponseEntity<>("CSV processed successfully", HttpStatus.OK);
    }

    /**
     * Processes offers from a stream.
     *
     * @return a response entity with a success message
     */
    @PostMapping("/process-stream")
    public ResponseEntity<String> processStream() {
        logger.info("Processing stream of offers");
        streamReaderService.processOffers();
        return new ResponseEntity<>("Stream processed successfully", HttpStatus.OK);
    }
}