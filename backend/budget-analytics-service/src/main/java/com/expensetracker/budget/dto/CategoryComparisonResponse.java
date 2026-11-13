package com.expensetracker.budget.dto;

import java.math.BigDecimal;
import java.util.List;

/** Month-over-month comparison: current month vs last month per category */
public class CategoryComparisonResponse {
    private String category;
    private String currentMonthLabel;
    private BigDecimal currentMonthAmount;
    private String previousMonthLabel;
    private BigDecimal previousMonthAmount;
    private BigDecimal change;              // current - previous
    private double changePercent;           // ((current - previous) / previous) * 100
    private String trend;                   // "UP", "DOWN", "SAME"

    public CategoryComparisonResponse() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getCurrentMonthLabel() { return currentMonthLabel; }
    public void setCurrentMonthLabel(String currentMonthLabel) { this.currentMonthLabel = currentMonthLabel; }
    public BigDecimal getCurrentMonthAmount() { return currentMonthAmount; }
    public void setCurrentMonthAmount(BigDecimal currentMonthAmount) { this.currentMonthAmount = currentMonthAmount; }
    public String getPreviousMonthLabel() { return previousMonthLabel; }
    public void setPreviousMonthLabel(String previousMonthLabel) { this.previousMonthLabel = previousMonthLabel; }
    public BigDecimal getPreviousMonthAmount() { return previousMonthAmount; }
    public void setPreviousMonthAmount(BigDecimal previousMonthAmount) { this.previousMonthAmount = previousMonthAmount; }
    public BigDecimal getChange() { return change; }
    public void setChange(BigDecimal change) { this.change = change; }
    public double getChangePercent() { return changePercent; }
    public void setChangePercent(double changePercent) { this.changePercent = changePercent; }
    public String getTrend() { return trend; }
    public void setTrend(String trend) { this.trend = trend; }
}

