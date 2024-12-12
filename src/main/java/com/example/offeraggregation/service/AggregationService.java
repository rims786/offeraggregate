package com.example.offeraggregation.service;

import com.example.offeraggregation.model.Offer;
import com.example.offeraggregation.model.ProductAggregation;

/**
 * Service interface for aggregating offers.
 */
public interface AggregationService {
    /**
     * Adds a new offer to the aggregation.
     *
     * @param offer the offer to add
     */
    void addOffer(Offer offer);

    /**
     * Retrieves the aggregation for a specific product code.
     *
     * @param productCode the product code
     * @return the product aggregation
     */
    ProductAggregation getAggregation(String productCode);

    /**
     * Closes the aggregation for a specific product code.
     *
     * @param productCode the product code
     */
    void closeAggregation(String productCode);
}