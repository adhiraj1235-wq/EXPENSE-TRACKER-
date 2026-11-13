package com.expensetracker.transaction.service;

import com.expensetracker.transaction.dto.*;
import com.expensetracker.transaction.entity.Transaction;
import com.expensetracker.transaction.entity.TransactionType;
import com.expensetracker.transaction.exception.BadRequestException;
import com.expensetracker.transaction.exception.ResourceNotFoundException;
import com.expensetracker.transaction.repository.TransactionRepository;
import com.expensetracker.transaction.repository.TransactionSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    public TransactionResponse createTransaction(Long userId, TransactionRequest request) {
        if (userId == null) {
            throw new BadRequestException("User ID must not be null");
        }

        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory().trim());
        transaction.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        transaction.setDate(request.getDate());

        Transaction saved = transactionRepository.save(transaction);
        return TransactionResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public TransactionResponse updateTransaction(Long userId, Long transactionId, TransactionRequest request) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or access denied"));

        transaction.setType(request.getType());
        transaction.setAmount(request.getAmount());
        transaction.setCategory(request.getCategory().trim());
        transaction.setDescription(request.getDescription() != null ? request.getDescription().trim() : null);
        transaction.setDate(request.getDate());

        Transaction updated = transactionRepository.save(transaction);
        return TransactionResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or access denied"));

        transactionRepository.delete(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionResponse getTransactionById(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found or access denied"));

        return TransactionResponse.fromEntity(transaction);
    }

    @Override
    @Transactional(readOnly = true)
    public PaginatedResponse<TransactionResponse> getTransactions(
            Long userId,
            String search,
            TransactionType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            BigDecimal minAmount,
            BigDecimal maxAmount,
            int page,
            int size,
            String sortBy,
            String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(Math.max(0, page), Math.max(1, size), sort);

        Specification<Transaction> spec = TransactionSpecification.withFilters(
                userId, search, type, category, startDate, endDate, minAmount, maxAmount);

        Page<Transaction> transactionPage = transactionRepository.findAll(spec, pageable);

        List<TransactionResponse> dtoList = transactionPage.getContent().stream()
                .map(TransactionResponse::fromEntity)
                .toList();

        return new PaginatedResponse<>(
                dtoList,
                transactionPage.getNumber(),
                transactionPage.getSize(),
                transactionPage.getTotalElements(),
                transactionPage.getTotalPages(),
                transactionPage.isLast()
        );
    }

    @Override
    @Transactional(readOnly = true)
    public TransactionSummaryResponse getSummary(Long userId) {
        BigDecimal totalIncome = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.INCOME);
        BigDecimal totalExpense = transactionRepository.sumAmountByUserIdAndType(userId, TransactionType.EXPENSE);
        long count = transactionRepository.countByUserId(userId);

        return new TransactionSummaryResponse(totalIncome, totalExpense, count);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getCategoryExpense(Long userId, String category, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getCategoryExpense(userId, category, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryTotalDto> getCategoryBreakdown(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate) {
        return transactionRepository.getCategoryBreakdown(userId, type, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MonthlyTotalDto> getMonthlyTotals(Long userId, int months) {
        int safeMonths = Math.min(Math.max(months, 1), 24);
        List<MonthlyTotalDto> result = new ArrayList<>();

        YearMonth currentYearMonth = YearMonth.now();
        YearMonth startYearMonth = currentYearMonth.minusMonths(safeMonths - 1);

        for (int i = 0; i < safeMonths; i++) {
            YearMonth targetMonth = startYearMonth.plusMonths(i);
            LocalDate start = targetMonth.atDay(1);
            LocalDate end = targetMonth.atEndOfMonth();

            BigDecimal income = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                    userId, TransactionType.INCOME, start, end);
            BigDecimal expense = transactionRepository.sumAmountByUserIdAndTypeAndDateBetween(
                    userId, TransactionType.EXPENSE, start, end);

            String monthName = targetMonth.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH) + " " + targetMonth.getYear();
            result.add(new MonthlyTotalDto(targetMonth.getYear(), targetMonth.getMonthValue(), monthName, income, expense));
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAllUserTransactions(Long userId) {
        return transactionRepository.findByUserIdOrderByDateDesc(userId).stream()
                .map(TransactionResponse::fromEntity)
                .toList();
    }
}

