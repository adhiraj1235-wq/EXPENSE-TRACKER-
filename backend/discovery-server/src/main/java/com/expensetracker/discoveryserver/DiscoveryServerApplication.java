package com.expensetracker.discoveryserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

/**
 * Service Discovery Registry using Netflix Eureka.
 * All microservices register their network locations (host and port) here,
 * allowing the API Gateway and OpenFeign clients to discover and load-balance
 * requests dynamically without hardcoded URLs.
 */
@SpringBootApplication
@EnableEurekaServer
public class DiscoveryServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DiscoveryServerApplication.class, args);
    }
}

