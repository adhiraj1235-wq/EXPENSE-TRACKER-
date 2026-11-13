package com.expensetracker.budget.service;

import com.expensetracker.budget.client.TransactionServiceClient;
import com.expensetracker.budget.dto.*;
import com.expensetracker.budget.entity.Budget;
import com.expensetracker.budget.exception.BadRequestException;
import com.expensetracker.budget.exception.ResourceNotFoundException;
import com.expensetracker.budget.repository.BudgetRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionServiceClient transactionClient;

    public BudgetService(BudgetRepository budgetRepository, TransactionServiceClient transactionClient) {
        this.budgetRepository = budgetRepository;
        this.transactionClient = transactionClient;
    }

    // =========================================================================
    // Budget CRUD
    // =========================================================================

    @Transactional
    public BudgetResponse createBudget(Long userId, BudgetRequest request) {
        // Prevent duplicate budget for same user+category+month+year
        boolean exists = budgetRepository
                .findByUserIdAndCategoryIgnoreCaseAndMonthAndYear(
                        userId, request.getCategory(), request.getMonth(), request.getYear())
                .isPresent();

        if (exists) {
            throw new BadRequestException("A budget for '" + request.getCategory() + "' in "
                    + request.getMonth() + "/" + request.getYear() + " already exists. Update it instead.");
        }

        Budget budget = new Budget();
        budget.setUserId(userId);
        budget.setCategory(request.getCategory().trim());
        budget.setLimitAmount(request.getLimitAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        Budget saved = budgetRepository.save(budget);
        return enrichBudgetResponse(saved);
    }

    @Transactional
    public BudgetResponse updateBudget(Long userId, Long budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found or access denied"));

        budget.setCategory(request.getCategory().trim());
        budget.setLimitAmount(request.getLimitAmount());
        budget.setMonth(request.getMonth());
        budget.setYear(request.getYear());

        Budget updated = budgetRepository.save(budget);
        return enrichBudgetResponse(updated);
    }

    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findByIdAndUserId(budgetId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Budget not found or access denied"));
        budgetRepository.delete(budget);
    }

    @Transactional(readOnly = true)
    public List<BudgetResponse> getBudgets(Long userId, int month, int year) {
        List<Budget> budgets = budgetRepository.findByUserIdAndMonthAndYear(userId, month, year);
        return budgets.stream()
                .map(this::enrichBudgetResponse)
                .collect(Collectors.toList());
    }

    /**
     * Enriches a budget entity with real-time spending data via Feign call,
     * and calculates status/alert message.
     */
    private BudgetResponse enrichBudgetResponse(Budget budget) {
        YearMonth ym = YearMonth.of(budget.getYear(), budget.getMonth());
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        // Call transaction-service to get actual spent amount for this category
        BigDecimal spent = transactionClient.getCategoryExpense(
                budget.getUserId(), budget.getCategory(), start, end);
        if (spent == null) spent = BigDecimal.ZERO;

        BigDecimal limit = budget.getLimitAmount();
        BigDecimal remaining = limit.subtract(spent);

        // Calculate percent used (safe division)
        double percentUsed = 0.0;
        if (limit.compareTo(BigDecimal.ZERO) > 0) {
            percentUsed = spent.divide(limit, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .doubleValue();
        }

        // Determine alert status and message
        String status;
        String message;
        if (spent.compareTo(limit) > 0) {
            BigDecimal overage = spent.subtract(limit).setScale(2, RoundingMode.HALF_UP);
            status = "EXCEEDED";
            message = "Budget exceeded by ₹" + overage + " for " + budget.getCategory();
        } else if (percentUsed >= 80.0) {
            status = "WARNING";
            message = String.format("%.0f%% of budget used for %s. Only ₹%.2f remaining.",
                    percentUsed, budget.getCategory(), remaining);
        } else {
            status = "OK";
            message = String.format("%.0f%% of budget used for %s.", percentUsed, budget.getCategory());
        }

        BudgetResponse response = new BudgetResponse();
        response.setId(budget.getId());
        response.setUserId(budget.getUserId());
        response.setCategory(budget.getCategory());
        response.setLimitAmount(limit);
        response.setSpentAmount(spent.setScale(2, RoundingMode.HALF_UP));
        response.setRemainingAmount(remaining.setScale(2, RoundingMode.HALF_UP));
        response.setPercentUsed(Math.round(percentUsed * 100.0) / 100.0);
        response.setMonth(budget.getMonth());
        response.setYear(budget.getYear());
        response.setStatus(status);
        response.setMessage(message);
        response.setCreatedAt(budget.getCreatedAt());
        return response;
    }

    // =========================================================================
    // Analytics
    // =========================================================================

    /**
     * Overall summary: total income, expense, balance and savings rate.
     */
    public AnalyticsSummaryResponse getSummary(Long userId) {
        List<TransactionResponse> all = transactionClient.getAllUserTransactions(userId);

        BigDecimal totalIncome = all.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .map(TransactionResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = all.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .map(TransactionResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal balance = totalIncome.subtract(totalExpense);
        double savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? balance.divide(totalIncome, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        // Top expense categories from last 30 days
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(1);
        List<CategoryTotalDto> topCategories = transactionClient
                .getCategoryBreakdown(userId, "EXPENSE", start, end);

        AnalyticsSummaryResponse summary = new AnalyticsSummaryResponse();
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpense(totalExpense);
        summary.setBalance(balance);
        summary.setSavingsRate(Math.round(savingsRate * 100.0) / 100.0);
        summary.setTotalTransactions(all.size());
        summary.setTopExpenseCategories(topCategories != null ? topCategories : Collections.emptyList());
        return summary;
    }

    /**
     * Monthly income vs expense data for charts (last N months).
     */
    public List<MonthlyTotalDto> getMonthlyData(Long userId, int months) {
        return transactionClient.getMonthlyTotals(userId, months);
    }

    /**
     * Category breakdown for a given month/year.
     */
    public List<CategoryTotalDto> getCategoryBreakdown(Long userId, String type, int month, int year) {
        YearMonth ym = YearMonth.of(year, month);
        return transactionClient.getCategoryBreakdown(userId, type, ym.atDay(1), ym.atEndOfMonth());
    }

    /**
     * Month-over-month comparison: current vs previous month per expense category.
     */
    public List<CategoryComparisonResponse> getMonthComparison(Long userId) {
        YearMonth current = YearMonth.now();
        YearMonth previous = current.minusMonths(1);

        List<CategoryTotalDto> currentBreakdown = transactionClient.getCategoryBreakdown(
                userId, "EXPENSE", current.atDay(1), current.atEndOfMonth());
        List<CategoryTotalDto> previousBreakdown = transactionClient.getCategoryBreakdown(
                userId, "EXPENSE", previous.atDay(1), previous.atEndOfMonth());

        Map<String, BigDecimal> prevMap = previousBreakdown.stream()
                .collect(Collectors.toMap(
                        c -> c.getCategory().toLowerCase(),
                        CategoryTotalDto::getTotalAmount,
                        (a, b) -> a));

        String currentLabel = current.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + current.getYear();
        String previousLabel = previous.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + previous.getYear();

        List<CategoryComparisonResponse> comparisons = new ArrayList<>();
        for (CategoryTotalDto curr : (currentBreakdown != null ? currentBreakdown : Collections.<CategoryTotalDto>emptyList())) {
            BigDecimal currAmount = curr.getTotalAmount() != null ? curr.getTotalAmount() : BigDecimal.ZERO;
            BigDecimal prevAmount = prevMap.getOrDefault(curr.getCategory().toLowerCase(), BigDecimal.ZERO);
            BigDecimal change = currAmount.subtract(prevAmount);

            double changePercent = 0.0;
            if (prevAmount.compareTo(BigDecimal.ZERO) > 0) {
                changePercent = change.divide(prevAmount, 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100)).doubleValue();
            }

            String trend = change.compareTo(BigDecimal.ZERO) > 0 ? "UP"
                    : change.compareTo(BigDecimal.ZERO) < 0 ? "DOWN" : "SAME";

            CategoryComparisonResponse comp = new CategoryComparisonResponse();
            comp.setCategory(curr.getCategory());
            comp.setCurrentMonthLabel(currentLabel);
            comp.setCurrentMonthAmount(currAmount);
            comp.setPreviousMonthLabel(previousLabel);
            comp.setPreviousMonthAmount(prevAmount);
            comp.setChange(change.setScale(2, RoundingMode.HALF_UP));
            comp.setChangePercent(Math.round(changePercent * 100.0) / 100.0);
            comp.setTrend(trend);
            comparisons.add(comp);
        }
        return comparisons;
    }

    /**
     * 3-month moving average spending prediction per expense category.
     * Prediction = average of the last 3 months' actual spending.
     */
    public List<PredictionResponse> getSpendingPredictions(Long userId) {
        YearMonth now = YearMonth.now();
        YearMonth m1 = now.minusMonths(3); // 3 months ago
        YearMonth m2 = now.minusMonths(2); // 2 months ago
        YearMonth m3 = now.minusMonths(1); // last month

        List<CategoryTotalDto> cats1 = transactionClient.getCategoryBreakdown(
                userId, "EXPENSE", m1.atDay(1), m1.atEndOfMonth());
        List<CategoryTotalDto> cats2 = transactionClient.getCategoryBreakdown(
                userId, "EXPENSE", m2.atDay(1), m2.atEndOfMonth());
        List<CategoryTotalDto> cats3 = transactionClient.getCategoryBreakdown(
                userId, "EXPENSE", m3.atDay(1), m3.atEndOfMonth());

        // Collect all unique categories across all 3 months
        Set<String> allCategories = new LinkedHashSet<>();
        if (cats1 != null) cats1.forEach(c -> allCategories.add(c.getCategory()));
        if (cats2 != null) cats2.forEach(c -> allCategories.add(c.getCategory()));
        if (cats3 != null) cats3.forEach(c -> allCategories.add(c.getCategory()));

        Map<String, BigDecimal> map1 = toMap(cats1);
        Map<String, BigDecimal> map2 = toMap(cats2);
        Map<String, BigDecimal> map3 = toMap(cats3);

        List<PredictionResponse> predictions = new ArrayList<>();
        for (String category : allCategories) {
            BigDecimal a1 = map1.getOrDefault(category.toLowerCase(), BigDecimal.ZERO);
            BigDecimal a2 = map2.getOrDefault(category.toLowerCase(), BigDecimal.ZERO);
            BigDecimal a3 = map3.getOrDefault(category.toLowerCase(), BigDecimal.ZERO);

            BigDecimal predicted = a1.add(a2).add(a3)
                    .divide(BigDecimal.valueOf(3), 2, RoundingMode.HALF_UP);

            PredictionResponse pred = new PredictionResponse();
            pred.setCategory(category);
            pred.setMonth1Actual(a1);
            pred.setMonth2Actual(a2);
            pred.setMonth3Actual(a3);
            pred.setPredictedAmount(predicted);
            pred.setPredictionNote("Predicted next month spend based on 3-month moving average");
            predictions.add(pred);
        }

        // Sort by predicted amount descending (highest risk first)
        predictions.sort((a, b) -> b.getPredictedAmount().compareTo(a.getPredictedAmount()));
        return predictions;
    }

    /**
     * Full yearly financial report.
     */
    public YearlyReportResponse getYearlyReport(Long userId, int year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year, 12, 31);

        List<TransactionResponse> all = transactionClient.getAllUserTransactions(userId);

        // Filter only transactions in the given year
        List<TransactionResponse> yearTx = all.stream()
                .filter(t -> t.getDate() != null && t.getDate().getYear() == year)
                .collect(Collectors.toList());

        BigDecimal totalIncome = yearTx.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .map(TransactionResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalExpense = yearTx.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .map(TransactionResponse::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalSavings = totalIncome.subtract(totalExpense);
        double savingsRate = totalIncome.compareTo(BigDecimal.ZERO) > 0
                ? totalSavings.divide(totalIncome, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue()
                : 0.0;

        // Monthly breakdown for the year (12 months)
        List<MonthlyTotalDto> monthlyBreakdown = new ArrayList<>();
        String bestMonth = "";
        String worstMonth = "";
        BigDecimal bestSavings = null;
        BigDecimal worstExpense = null;

        for (int m = 1; m <= 12; m++) {
            YearMonth ym = YearMonth.of(year, m);
            final int finalM = m;
            String monthName = ym.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + year;

            BigDecimal inc = yearTx.stream()
                    .filter(t -> "INCOME".equals(t.getType()) && t.getDate().getMonthValue() == finalM)
                    .map(TransactionResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal exp = yearTx.stream()
                    .filter(t -> "EXPENSE".equals(t.getType()) && t.getDate().getMonthValue() == finalM)
                    .map(TransactionResponse::getAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            MonthlyTotalDto dto = new MonthlyTotalDto();
            dto.setYear(year);
            dto.setMonth(m);
            dto.setMonthName(monthName);
            dto.setIncome(inc);
            dto.setExpense(exp);
            dto.setNetSavings(inc.subtract(exp));
            monthlyBreakdown.add(dto);

            BigDecimal savings = inc.subtract(exp);
            if (bestSavings == null || savings.compareTo(bestSavings) > 0) {
                bestSavings = savings;
                bestMonth = monthName;
            }
            if (worstExpense == null || exp.compareTo(worstExpense) > 0) {
                worstExpense = exp;
                worstMonth = monthName;
            }
        }

        // Top expense categories for the year
        List<CategoryTotalDto> topCategories = transactionClient.getCategoryBreakdown(
                userId, "EXPENSE", start, end);

        YearlyReportResponse report = new YearlyReportResponse();
        report.setYear(year);
        report.setTotalIncome(totalIncome);
        report.setTotalExpense(totalExpense);
        report.setTotalSavings(totalSavings);
        report.setSavingsRate(Math.round(savingsRate * 100.0) / 100.0);
        report.setMonthlyBreakdown(monthlyBreakdown);
        report.setTopExpenseCategories(topCategories != null ? topCategories : Collections.emptyList());
        report.setBestMonth(bestMonth);
        report.setWorstMonth(worstMonth);
        return report;
    }

    private Map<String, BigDecimal> toMap(List<CategoryTotalDto> list) {
        if (list == null) return Collections.emptyMap();
        return list.stream().collect(Collectors.toMap(
                c -> c.getCategory().toLowerCase(),
                CategoryTotalDto::getTotalAmount,
                (a, b) -> a
        ));
    }
}

