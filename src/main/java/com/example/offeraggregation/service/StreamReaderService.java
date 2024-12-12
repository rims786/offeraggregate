package com.example.offeraggregation.service;

import com.example.offeraggregation.model.Offer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for processing offers from a stream.
 */
@Service
public class StreamReaderService implements OfferProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(StreamReaderService.class);

    @Autowired
    private AggregationService aggregationService;

    private final ConcurrentMap<String, Object> locks = new ConcurrentHashMap<>();

    /**
     * Processes offers from a simulated stream.
     */
    @Override
    public void processOffers() {
        logger.info("Streaming processing started");
        // Example: Simulate processing a stream of offers
        Offer offer1 = new Offer("exampleProductCode1", 100.0);
        Offer offer2 = new Offer("exampleProductCode2", 200.0);
        processOffer(offer1);
        processOffer(offer2);
        logger.info("Streaming processing completed");
    }

    /**
     * Processes a single offer.
     *
     * @param offer the offer to process
     */
    @Override
    public void processOffer(Offer offer) {
        if (offer.productCode() == null) {
            logger.warn("Offer with null product code: {}", offer);
            return;
        }
        // Use a lock specific to the product code to ensure thread safety
        Object lock = locks.computeIfAbsent(offer.productCode(), k -> new Object());
        synchronized (lock) {
            try {
                aggregationService.addOffer(offer);
            } catch (IllegalStateException e) {
                logger.warn("Failed to add offer for product code {}: {}", offer.productCode(), e.getMessage());
            }
        }
    }
}