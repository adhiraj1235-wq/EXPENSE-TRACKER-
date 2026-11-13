package com.expensetracker.transaction.service;

import com.expensetracker.transaction.dto.*;
import com.expensetracker.transaction.entity.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionResponse createTransaction(Long userId, TransactionRequest request);

    TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionRequest request);

    void deleteTransaction(Long userId, Long transactionId);

    TransactionResponse getTransactionById(Long userId, Long transactionId);

    PaginatedResponse<TransactionResponse> getTransactions(
            Long userId,
            String search,
            TransactionType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int page,
            int size,
            String sortBy,
            String sortDir);

    TransactionSummaryResponse getSummary(Long userId);

    BigDecimal getCategoryExpense(Long userId, String category, LocalDate startDate, LocalDate endDate);

    List<CategoryTotalDto> getCategoryBreakdown(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate);

    List<MonthlyTotalDto> getMonthlyTotals(Long userId, int months);

    List<TransactionResponse> getAllUserTransactions(Long userId);
}

