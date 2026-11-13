package com.expensetracker.budget.dto;

import java.math.BigDecimal;
import java.util.List;

/** Yearly financial report for a given year */
public class YearlyReportResponse {
    private int year;
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal totalSavings;
    private double savingsRate;             // percentage of income saved
    private List<MonthlyTotalDto> monthlyBreakdown;
    private List<CategoryTotalDto> topExpenseCategories;
    private String bestMonth;              // month name with highest savings
    private String worstMonth;             // month name with highest expense

    public YearlyReportResponse() {}

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
    public BigDecimal getTotalSavings() { return totalSavings; }
    public void setTotalSavings(BigDecimal totalSavings) { this.totalSavings = totalSavings; }
    public double getSavingsRate() { return savingsRate; }
    public void setSavingsRate(double savingsRate) { this.savingsRate = savingsRate; }
    public List<MonthlyTotalDto> getMonthlyBreakdown() { return monthlyBreakdown; }
    public void setMonthlyBreakdown(List<MonthlyTotalDto> monthlyBreakdown) { this.monthlyBreakdown = monthlyBreakdown; }
    public List<CategoryTotalDto> getTopExpenseCategories() { return topExpenseCategories; }
    public void setTopExpenseCategories(List<CategoryTotalDto> topExpenseCategories) { this.topExpenseCategories = topExpenseCategories; }
    public String getBestMonth() { return bestMonth; }
    public void setBestMonth(String bestMonth) { this.bestMonth = bestMonth; }
    public String getWorstMonth() { return worstMonth; }
    public void setWorstMonth(String worstMonth) { this.worstMonth = worstMonth; }
}

