package com.example.offeraggregation.service;

import com.example.offeraggregation.model.Offer;

/**
 * Common interface for processing offers.
 */
public interface OfferProcessorService {
    /**
     * Processes multiple offers.
     */
    void processOffers();

    /**
     * Processes a single offer.
     *
     * @param offer the offer to process
     */
    void processOffer(Offer offer);
}