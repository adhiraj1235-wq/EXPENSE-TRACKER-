package com.expensetracker.budget.controller;

import com.expensetracker.budget.dto.*;
import com.expensetracker.budget.exception.BadRequestException;
import com.expensetracker.budget.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/analytics")
@Tag(name = "Analytics", description = "Charts, predictions, comparisons and yearly reports")
public class AnalyticsController {

    private final BudgetService budgetService;

    public AnalyticsController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    private Long requireUserId(Long userId) {
        if (userId == null) throw new BadRequestException("Missing X-User-Id header");
        return userId;
    }

    @GetMapping("/summary")
    @Operation(summary = "Overall financial summary: balance, income, expenses, savings rate")
    public ResponseEntity<AnalyticsSummaryResponse> getSummary(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(budgetService.getSummary(requireUserId(userId)));
    }

    @GetMapping("/monthly")
    @Operation(summary = "Monthly income vs expense for the last N months (default 6)")
    public ResponseEntity<List<MonthlyTotalDto>> getMonthlyData(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "months", defaultValue = "6") int months) {
        return ResponseEntity.ok(budgetService.getMonthlyData(requireUserId(userId), months));
    }

    @GetMapping("/categories")
    @Operation(summary = "Category breakdown for a given month/year")
    public ResponseEntity<List<CategoryTotalDto>> getCategoryBreakdown(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "type", defaultValue = "EXPENSE") String type,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year) {

        int m = (month != null) ? month : LocalDate.now().getMonthValue();
        int y = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(budgetService.getCategoryBreakdown(requireUserId(userId), type, m, y));
    }

    @GetMapping("/compare")
    @Operation(summary = "Month-over-month category comparison (current vs previous month)")
    public ResponseEntity<List<CategoryComparisonResponse>> getMonthComparison(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(budgetService.getMonthComparison(requireUserId(userId)));
    }

    @GetMapping("/predict")
    @Operation(summary = "3-month moving average spending prediction per category")
    public ResponseEntity<List<PredictionResponse>> getSpendingPredictions(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        return ResponseEntity.ok(budgetService.getSpendingPredictions(requireUserId(userId)));
    }

    @GetMapping("/report")
    @Operation(summary = "Yearly financial report: total income, expense, savings rate, monthly/category breakdown")
    public ResponseEntity<YearlyReportResponse> getYearlyReport(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "year", required = false) Integer year) {

        int y = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(budgetService.getYearlyReport(requireUserId(userId), y));
    }
}

