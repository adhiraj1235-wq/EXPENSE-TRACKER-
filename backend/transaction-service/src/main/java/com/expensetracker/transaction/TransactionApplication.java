package com.expensetracker.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Transaction Service: Manages incomes, expenses, multi-filter queries,
 * and user account balance calculations. Backed by transaction_db.
 */
@SpringBootApplication
@EnableDiscoveryClient
public class TransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
}

