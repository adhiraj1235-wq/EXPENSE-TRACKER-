package com.expensetracker.budget.dto;

import java.math.BigDecimal;

/**
 * Spending prediction for a category using 3-month moving average.
 * predictedAmount = average of last 3 months' actual spending for the category.
 */
public class PredictionResponse {
    private String category;
    private BigDecimal month1Actual;    // 3 months ago
    private BigDecimal month2Actual;    // 2 months ago
    private BigDecimal month3Actual;    // last month
    private BigDecimal predictedAmount; // 3-month average
    private String predictionNote;

    public PredictionResponse() {}

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getMonth1Actual() { return month1Actual; }
    public void setMonth1Actual(BigDecimal month1Actual) { this.month1Actual = month1Actual; }
    public BigDecimal getMonth2Actual() { return month2Actual; }
    public void setMonth2Actual(BigDecimal month2Actual) { this.month2Actual = month2Actual; }
    public BigDecimal getMonth3Actual() { return month3Actual; }
    public void setMonth3Actual(BigDecimal month3Actual) { this.month3Actual = month3Actual; }
    public BigDecimal getPredictedAmount() { return predictedAmount; }
    public void setPredictedAmount(BigDecimal predictedAmount) { this.predictedAmount = predictedAmount; }
    public String getPredictionNote() { return predictionNote; }
    public void setPredictionNote(String predictionNote) { this.predictionNote = predictionNote; }
}

