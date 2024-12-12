
# Offer Aggregation Application

## Overview
The Offer Aggregation Application is designed to aggregate offers for products. 
It provides a RESTful API to add offers, query aggregations, and close aggregations. 
The application processes offers from both CSV files and streams.

## Features
- Add offers for products
- Query aggregations for a given product code
- Close aggregations
- Process offers from CSV files
- Process offers from streams
- Provides REST APIs to add offers, query aggregations, and close aggregations.
- Ensures aggregations with more than N offers can be closed (N is configurable and defaults to 3).

## Environment Requirements
- Java 17
- Spring Boot
- Maven

## Installing and Running from Zip File
1. Download and extract the zip file:
   unzip offeraggregation.zip
   cd offeraggregation

2. Build and run the application with a single command:
   mvn clean install && mvn spring-boot:run
 
3. TODO: (Future)
   - GitHub Repository: Clone the repository:https://github.com/rims786/<future-project>
   	- (Currently not applicable due to the assignment requirements)
   - Deploy application using Docker:
   	- (Currently not applicable due to the assignment requirements)
       - docker-compose up --build
       - docker-compose ps

### Running the Application
    Build the Application:    
    	mvn clean install
    Run the Application:    
    	java -jar offeraggregation-0.0.1-SNAPSHOT.jar

## API Example Usage
    Adding an Offer
    	curl -X POST -H "Content-Type: application/json" -d '{"productCode": "<productCode>", "price": <0.0>}' http://localhost:8080/api/offers
    		curl -X POST -H "Content-Type: application/json" -d '{"productCode": "7392158007783", "price": 1095.00}' http://localhost:8080/api/offers
    Querying an Aggregation
    	curl -X GET http://localhost:8080/api/offers/<productCode>
    		curl -X GET http://localhost:8080/api/offers/7392158007783

    Closing an Aggregation
    	curl -X POST http://localhost:8080/api/offers/<productCode>/close
    		curl -X POST http://localhost:8080/api/offers/7392158007783/close

    Processing Offers from CSV
    	curl -X POST http://localhost:8080/api/offers/process-csv

    Processing Offers from Stream
    	curl -X POST http://localhost:8080/api/offers/process-stream

    Closing Offers from Stream
    	curl -X POST http://localhost:8080/api/offers/<productCode>/close
    	curl -X POST http://localhost:8080/api/offers/7392158007783/close


## Acceptance Criteria Fulfillment

### 1. A closed aggregation does not accept any more offers

The `AggregationServiceImpl` class ensures that once an aggregation is closed, no more offers can be added. 
This is handled in the `addOffer` method, which throws an `IllegalStateException` if the aggregation state is `CLOSED`.

### 2. The service's API explanations:
- **Querying for an aggregation for a given product code:**
    - The `OfferController` class has a `getAggregation` method that handles GET requests to `/api/offers/{productCode}`. 
    - This method calls `aggregationService.getAggregation(productCode)` to retrieve the aggregation details.
- **Closing the aggregation:**
    - The `OfferController` class has a `closeAggregation` method that handles POST requests to `/api/offers/{productCode}/close`. 
    - This method calls `aggregationService.closeAggregation(productCode)` to close the aggregation.
- **Supplying offers for the aggregation:**
    - The `OfferController` class has an `addOffer` method that handles POST requests to `/api/offers`. 
    - This method calls `aggregationService.addOffer(offer)` to add a new offer.

### 3. The offers to aggregate should be batched or streamed
- **Batched Offers:**
    - The `CSVReaderService` class processes offers from a CSV file. 
    - The `processOffers` method reads the CSV file and calls `processOffer` for each offer.
- **Streamed Offers:**
    - The `StreamReaderService` class simulates processing offers from a stream. 
    - The `processOffers` method demonstrates adding offers in a streaming manner.

## Contact
	- For any questions or clarification, please contact:
   Name: Rahim R Uddin
   Email: rimmy2008@gmail.com
