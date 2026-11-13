package com.expensetracker.budget.dto;

import java.math.BigDecimal;
import java.util.List;

/** Analytics summary: total income, expenses, savings rate and category breakdown */
public class AnalyticsSummaryResponse {
    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private double savingsRate;             // (balance / totalIncome) * 100
    private long totalTransactions;
    private List<CategoryTotalDto> topExpenseCategories;

    public AnalyticsSummaryResponse() {}

    public BigDecimal getTotalIncome() { return totalIncome; }
    public void setTotalIncome(BigDecimal totalIncome) { this.totalIncome = totalIncome; }
    public BigDecimal getTotalExpense() { return totalExpense; }
    public void setTotalExpense(BigDecimal totalExpense) { this.totalExpense = totalExpense; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public double getSavingsRate() { return savingsRate; }
    public void setSavingsRate(double savingsRate) { this.savingsRate = savingsRate; }
    public long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(long totalTransactions) { this.totalTransactions = totalTransactions; }
    public List<CategoryTotalDto> getTopExpenseCategories() { return topExpenseCategories; }
    public void setTopExpenseCategories(List<CategoryTotalDto> topExpenseCategories) { this.topExpenseCategories = topExpenseCategories; }
}

