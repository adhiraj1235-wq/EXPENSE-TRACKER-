package com.expensetracker.budget.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Budget response DTO with real-time spending status and alert messages.
 * Status values: OK, WARNING (>=80% used), EXCEEDED
 */
public class BudgetResponse {

    private Long id;
    private Long userId;
    private String category;
    private BigDecimal limitAmount;
    private BigDecimal spentAmount;
    private BigDecimal remainingAmount;
    private double percentUsed;
    private int month;
    private int year;
    private String status;      // "OK", "WARNING", "EXCEEDED"
    private String message;     // e.g. "Budget exceeded by ₹2000.00" or "80% of budget used"
    private LocalDateTime createdAt;

    public BudgetResponse() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getLimitAmount() { return limitAmount; }
    public void setLimitAmount(BigDecimal limitAmount) { this.limitAmount = limitAmount; }
    public BigDecimal getSpentAmount() { return spentAmount; }
    public void setSpentAmount(BigDecimal spentAmount) { this.spentAmount = spentAmount; }
    public BigDecimal getRemainingAmount() { return remainingAmount; }
    public void setRemainingAmount(BigDecimal remainingAmount) { this.remainingAmount = remainingAmount; }
    public double getPercentUsed() { return percentUsed; }
    public void setPercentUsed(double percentUsed) { this.percentUsed = percentUsed; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

