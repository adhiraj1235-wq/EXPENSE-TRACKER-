package com.expensetracker.budget;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * Budget & Analytics Service: manages monthly budgets with ₹ alerts,
 * spending predictions (3-month moving average), month-over-month comparisons,
 * and yearly financial reports. Calls transaction-service via OpenFeign.
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class BudgetAnalyticsApplication {

    public static void main(String[] args) {
        SpringApplication.run(BudgetAnalyticsApplication.class, args);
    }
}

