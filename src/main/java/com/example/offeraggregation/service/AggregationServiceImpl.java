package com.example.offeraggregation.service;

import com.example.offeraggregation.exception.ProductNotFoundException;
import com.example.offeraggregation.model.AggregationState;
import com.example.offeraggregation.model.Offer;
import com.example.offeraggregation.model.ProductAggregation;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of the AggregationService interface.
 * This service handles the business logic for aggregating offers.
 */
@Service
public class AggregationServiceImpl implements AggregationService {

    private static final Logger logger = LoggerFactory.getLogger(AggregationServiceImpl.class);

    private final Map<String, List<Offer>> offerMap = new ConcurrentHashMap<>();
    private final Map<String, AggregationState> aggregationState = new ConcurrentHashMap<>();
    // Current default value before offer is closed
    private static final int MIN_OFFERS_TO_CLOSE = 3;

    /**
     * Adds a new offer to the aggregation.
     * If the aggregation is closed, the offer is not added.
     * If the number of offers reaches the threshold, the aggregation is closed.
     *
     * @param offer the offer to add
     */
    @Override
    public void addOffer(Offer offer) {
        if (offer.price() < 0) {
            logger.warn("Offer with negative price: {}", offer);
            throw new IllegalArgumentException("Price cannot be negative.");
        }
        if (AggregationState.CLOSED.equals(aggregationState.get(offer.productCode()))) {
            logger.warn("Aggregation for product code {} is closed. Offer not added.", offer.productCode());
            throw new IllegalStateException("Aggregation for product code " + offer.productCode() + " is closed. Offer not added.");
        }
        offerMap.computeIfAbsent(offer.productCode(), k -> new ArrayList<>()).add(offer);
        logger.info("Offer added: {}", offer);
        logger.debug("Current offers for product code {}: {}", offer.productCode(), offerMap.get(offer.productCode()));

        // Check if the number of offers has reached the threshold to close the aggregation
        if (offerMap.get(offer.productCode()).size() >= MIN_OFFERS_TO_CLOSE) {
            closeAggregation(offer.productCode());
        }
    }

    /**
     * Retrieves the aggregation for a specific product code.
     *
     * @param productCode the product code
     * @return the product aggregation
     */
    @Override
    public ProductAggregation getAggregation(String productCode) {
        List<Offer> offers = offerMap.getOrDefault(productCode, Collections.emptyList());
        if (offers.isEmpty()) {
            logger.warn("No offers found for product code: {}", productCode);
            return new ProductAggregation(productCode, 0, 0, 0, 0, AggregationState.OPEN);
        }

        double minPrice = offers.stream().mapToDouble(Offer::price).min().orElse(0);
        double maxPrice = offers.stream().mapToDouble(Offer::price).max().orElse(0);
        double averagePrice = offers.stream().mapToDouble(Offer::price).average().orElse(0);
        int offerCount = offers.size();
        AggregationState state = aggregationState.getOrDefault(productCode, AggregationState.OPEN);

        logger.info("Aggregation for product code {}: minPrice={}, maxPrice={}, averagePrice={}, offerCount={}, state={}",
                productCode, minPrice, maxPrice, averagePrice, offerCount, state);

        return new ProductAggregation(productCode, minPrice, maxPrice, averagePrice, offerCount, state);
    }

    /**
     * Closes the aggregation for a specific product code.
     * The aggregation can only be closed if the number of offers meets the threshold.
     *
     * @param productCode the product code
     */
    @Override
    public void closeAggregation(String productCode) {
        List<Offer> offers = offerMap.get(productCode);
        if (offers != null && offers.size() >= MIN_OFFERS_TO_CLOSE) {
            aggregationState.put(productCode, AggregationState.CLOSED);
            logger.info("Aggregation for product code {} is now closed.", productCode);
        } else if (offers == null) {
            logger.warn("No offers found for product code: {}", productCode);
            throw new ProductNotFoundException("No offers found for product code: " + productCode);
        } else {
            logger.warn("Aggregation for product code {} cannot be closed. Not enough offers.", productCode);
            throw new IllegalStateException("Not enough offers to close aggregation for product code: " + productCode);
        }
    }
}