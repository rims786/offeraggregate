package com.example.offeraggregation.service;

import com.example.offeraggregation.model.Offer;
import com.opencsv.CSVReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for reading and processing offers from a CSV file.
 */
@Service
public class CSVReaderService implements OfferProcessorService {

    private static final Logger logger = LoggerFactory.getLogger(CSVReaderService.class);

    @Autowired
    private AggregationService aggregationService;

    private final ConcurrentMap<String, Object> locks = new ConcurrentHashMap<>();

    /**
     * Processes offers from a CSV file.
     */
    @Override
    public void processOffers() {
        try (CSVReader reader = new CSVReader(new InputStreamReader(new ClassPathResource("data/offerdata.csv").getInputStream()))) {
            String[] line;
            reader.readNext(); // Skip header line
            while ((line = reader.readNext()) != null) {
                String productCode = line[3];
                double price = Double.parseDouble(line[1]);
                Offer offer = new Offer(productCode, price);
                processOffer(offer);
            }
            logger.info("CSV file processed successfully");
        } catch (Exception e) {
            logger.error("Error processing CSV file", e);
        }
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
        // uses a synchronized block to ensure that only one thread can process
        // an offer for a specific product code at a time.
        // This prevents race conditions and ensures thread safety.
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