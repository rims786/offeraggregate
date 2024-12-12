package com.example.offeraggregation.model;

/**
 * Represents the aggregation of offers for a product.
 */
public record ProductAggregation(String productCode, double minPrice, double maxPrice,
                                 double averagePrice, int offerCount, AggregationState state) {}