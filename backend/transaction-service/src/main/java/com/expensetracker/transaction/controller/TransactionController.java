package com.expensetracker.transaction.controller;

import com.expensetracker.transaction.dto.*;
import com.expensetracker.transaction.entity.TransactionType;
import com.expensetracker.transaction.exception.UnauthorizedException;
import com.expensetracker.transaction.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transactions", description = "CRUD and filtering operations for Incomes and Expenses")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private Long validateAndGetUserId(Long userId) {
        if (userId == null) {
            throw new UnauthorizedException("Unauthorized: Missing user identity header (X-User-Id)");
        }
        return userId;
    }

    @PostMapping
    @Operation(summary = "Add a new transaction (Income or Expense)")
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody TransactionRequest request) {
        Long authenticatedUserId = validateAndGetUserId(userId);
        TransactionResponse response = transactionService.createTransaction(authenticatedUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get paginated transactions with multi-criteria filtering")
    public ResponseEntity<PaginatedResponse<TransactionResponse>> getTransactions(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "type", required = false) TransactionType type,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "minAmount", required = false) BigDecimal minAmount,
            @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "sortBy", defaultValue = "date") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        Long authenticatedUserId = validateAndGetUserId(userId);

        // If month and year provided, override startDate and endDate
        if (month != null && year != null) {
            YearMonth ym = YearMonth.of(year, month);
            startDate = ym.atDay(1);
            endDate = ym.atEndOfMonth();
        }

        PaginatedResponse<TransactionResponse> response = transactionService.getTransactions(
                authenticatedUserId, search, type, category, startDate, endDate, minAmount, maxAmount,
                page, size, sortBy, sortDir);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/summary")
    @Operation(summary = "Get overall balance, total income, and total expense")
    public ResponseEntity<TransactionSummaryResponse> getSummary(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Long authenticatedUserId = validateAndGetUserId(userId);
        return ResponseEntity.ok(transactionService.getSummary(authenticatedUserId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a single transaction by ID")
    public ResponseEntity<TransactionResponse> getTransactionById(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable(value = "id") Long id) {
        Long authenticatedUserId = validateAndGetUserId(userId);
        return ResponseEntity.ok(transactionService.getTransactionById(authenticatedUserId, id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing transaction")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody TransactionRequest request) {
        Long authenticatedUserId = validateAndGetUserId(userId);
        return ResponseEntity.ok(transactionService.updateTransaction(authenticatedUserId, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a transaction")
    public ResponseEntity<Void> deleteTransaction(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable(value = "id") Long id) {
        Long authenticatedUserId = validateAndGetUserId(userId);
        transactionService.deleteTransaction(authenticatedUserId, id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // Internal Service-to-Service Endpoints for Budget & Analytics Service
    // =========================================================================

    @GetMapping("/user/{userId}/category-expense")
    @Operation(summary = "Internal: Get category expense total in date range")
    public ResponseEntity<BigDecimal> getCategoryExpense(
            @PathVariable(value = "userId") Long userId,
            @RequestParam(value = "category") String category,
            @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(transactionService.getCategoryExpense(userId, category, startDate, endDate));
    }

    @GetMapping("/user/{userId}/category-breakdown")
    @Operation(summary = "Internal: Get spending/income breakdown by category")
    public ResponseEntity<List<CategoryTotalDto>> getCategoryBreakdown(
            @PathVariable(value = "userId") Long userId,
            @RequestParam(value = "type", defaultValue = "EXPENSE") TransactionType type,
            @RequestParam(value = "startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(transactionService.getCategoryBreakdown(userId, type, startDate, endDate));
    }

    @GetMapping("/user/{userId}/monthly-totals")
    @Operation(summary = "Internal: Get month-by-month totals for trend analysis")
    public ResponseEntity<List<MonthlyTotalDto>> getMonthlyTotals(
            @PathVariable(value = "userId") Long userId,
            @RequestParam(value = "months", defaultValue = "6") int months) {
        return ResponseEntity.ok(transactionService.getMonthlyTotals(userId, months));
    }

    @GetMapping("/user/{userId}/all")
    @Operation(summary = "Internal: Get all transactions for a user")
    public ResponseEntity<List<TransactionResponse>> getAllUserTransactions(@PathVariable(value = "userId") Long userId) {
        return ResponseEntity.ok(transactionService.getAllUserTransactions(userId));
    }
}
