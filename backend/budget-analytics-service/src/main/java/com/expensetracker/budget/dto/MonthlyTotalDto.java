package com.expensetracker.budget.dto;

import java.math.BigDecimal;

/** Mirrors MonthlyTotalDto from transaction-service for Feign deserialization */
public class MonthlyTotalDto {
    private int year;
    private int month;
    private String monthName;
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal netSavings;

    public MonthlyTotalDto() {
        this.income = BigDecimal.ZERO;
        this.expense = BigDecimal.ZERO;
        this.netSavings = BigDecimal.ZERO;
    }

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }
    public int getMonth() { return month; }
    public void setMonth(int month) { this.month = month; }
    public String getMonthName() { return monthName; }
    public void setMonthName(String monthName) { this.monthName = monthName; }
    public BigDecimal getIncome() { return income != null ? income : BigDecimal.ZERO; }
    public void setIncome(BigDecimal income) { this.income = income; }
    public BigDecimal getExpense() { return expense != null ? expense : BigDecimal.ZERO; }
    public void setExpense(BigDecimal expense) { this.expense = expense; }
    public BigDecimal getNetSavings() { return netSavings != null ? netSavings : BigDecimal.ZERO; }
    public void setNetSavings(BigDecimal netSavings) { this.netSavings = netSavings; }
}

