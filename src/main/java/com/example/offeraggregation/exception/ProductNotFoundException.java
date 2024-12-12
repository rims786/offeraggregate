package com.example.offeraggregation.exception;

/**
 * Custom exception thrown when a product is not found.
 */
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(String message) {
        super(message);
    }
}