package com.expensetracker.budget.client;

import com.expensetracker.budget.dto.CategoryTotalDto;
import com.expensetracker.budget.dto.MonthlyTotalDto;
import com.expensetracker.budget.dto.TransactionResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * OpenFeign declarative HTTP client for transaction-service.
 * Uses Eureka service-discovery (lb://TRANSACTION-SERVICE) instead of a hardcoded URL.
 * The fallback method is invoked if the transaction-service is unreachable or slow.
 */
@FeignClient(
        name = "TRANSACTION-SERVICE",
        fallback = TransactionServiceFallback.class
)
public interface TransactionServiceClient {

    @GetMapping("/api/transactions/user/{userId}/category-expense")
    BigDecimal getCategoryExpense(
            @PathVariable("userId") Long userId,
            @RequestParam("category") String category,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("/api/transactions/user/{userId}/category-breakdown")
    List<CategoryTotalDto> getCategoryBreakdown(
            @PathVariable("userId") Long userId,
            @RequestParam("type") String type,
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate);

    @GetMapping("/api/transactions/user/{userId}/monthly-totals")
    List<MonthlyTotalDto> getMonthlyTotals(
            @PathVariable("userId") Long userId,
            @RequestParam("months") int months);

    @GetMapping("/api/transactions/user/{userId}/all")
    List<TransactionResponse> getAllUserTransactions(@PathVariable("userId") Long userId);
}

