package com.expensetracker.budget.controller;

import com.expensetracker.budget.dto.BudgetRequest;
import com.expensetracker.budget.dto.BudgetResponse;
import com.expensetracker.budget.exception.BadRequestException;
import com.expensetracker.budget.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/budgets")
@Tag(name = "Budgets", description = "Monthly category budgets with ₹ alerts and status tracking")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    private Long requireUserId(Long userId) {
        if (userId == null) throw new BadRequestException("Missing X-User-Id header");
        return userId;
    }

    @PostMapping
    @Operation(summary = "Create a new monthly budget for a category")
    public ResponseEntity<BudgetResponse> createBudget(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(budgetService.createBudget(requireUserId(userId), request));
    }

    @GetMapping
    @Operation(summary = "Get all budgets with live spending for a given month/year")
    public ResponseEntity<List<BudgetResponse>> getBudgets(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(value = "month", required = false) Integer month,
            @RequestParam(value = "year", required = false) Integer year) {

        int m = (month != null) ? month : LocalDate.now().getMonthValue();
        int y = (year != null) ? year : LocalDate.now().getYear();
        return ResponseEntity.ok(budgetService.getBudgets(requireUserId(userId), m, y));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing budget")
    public ResponseEntity<BudgetResponse> updateBudget(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable(value = "id") Long id,
            @Valid @RequestBody BudgetRequest request) {
        return ResponseEntity.ok(budgetService.updateBudget(requireUserId(userId), id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a budget")
    public ResponseEntity<Void> deleteBudget(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @PathVariable(value = "id") Long id) {
        budgetService.deleteBudget(requireUserId(userId), id);
        return ResponseEntity.noContent().build();
    }
}

