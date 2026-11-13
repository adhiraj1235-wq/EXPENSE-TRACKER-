package com.expensetracker.budget.client;

import com.expensetracker.budget.dto.CategoryTotalDto;
import com.expensetracker.budget.dto.MonthlyTotalDto;
import com.expensetracker.budget.dto.TransactionResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * Fallback for TransactionServiceClient.
 * Returns safe empty/zero defaults when transaction-service is unavailable,
 * so budget-analytics-service continues operating in a degraded mode.
 */
@Component
public class TransactionServiceFallback implements TransactionServiceClient {

    private static final Logger log = LoggerFactory.getLogger(TransactionServiceFallback.class);

    @Override
    public BigDecimal getCategoryExpense(Long userId, String category, LocalDate startDate, LocalDate endDate) {
        log.warn("Fallback: transaction-service unavailable. Returning ZERO for category expense [{}/{}]", userId, category);
        return BigDecimal.ZERO;
    }

    @Override
    public List<CategoryTotalDto> getCategoryBreakdown(Long userId, String type, LocalDate startDate, LocalDate endDate) {
        log.warn("Fallback: transaction-service unavailable. Returning empty category breakdown for user [{}]", userId);
        return Collections.emptyList();
    }

    @Override
    public List<MonthlyTotalDto> getMonthlyTotals(Long userId, int months) {
        log.warn("Fallback: transaction-service unavailable. Returning empty monthly totals for user [{}]", userId);
        return Collections.emptyList();
    }

    @Override
    public List<TransactionResponse> getAllUserTransactions(Long userId) {
        log.warn("Fallback: transaction-service unavailable. Returning empty transactions for user [{}]", userId);
        return Collections.emptyList();
    }
}

