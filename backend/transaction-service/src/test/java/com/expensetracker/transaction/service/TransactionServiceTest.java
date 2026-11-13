package com.expensetracker.transaction.service;

import com.expensetracker.transaction.dto.TransactionRequest;
import com.expensetracker.transaction.dto.TransactionResponse;
import com.expensetracker.transaction.dto.TransactionSummaryResponse;
import com.expensetracker.transaction.entity.Transaction;
import com.expensetracker.transaction.entity.TransactionType;
import com.expensetracker.transaction.exception.ResourceNotFoundException;
import com.expensetracker.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private Transaction sampleExpense;

    @BeforeEach
    void setUp() {
        sampleExpense = new Transaction(
                10L, 1L, TransactionType.EXPENSE,
                new BigDecimal("1500.00"), "Groceries", "Weekly shopping", LocalDate.now());
    }

    @Test
    void createTransaction_Success() {
        TransactionRequest request = new TransactionRequest(
                TransactionType.EXPENSE, new BigDecimal("1500.00"), "Groceries", "Weekly shopping", LocalDate.now());

        when(transactionRepository.save(any(Transaction.class))).thenReturn(sampleExpense);

        TransactionResponse response = transactionService.createTransaction(1L, request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals(TransactionType.EXPENSE, response.getType());
        assertEquals(new BigDecimal("1500.00"), response.getAmount());
        assertEquals("Groceries", response.getCategory());
        verify(transactionRepository).save(any(Transaction.class));
    }

    @Test
    void getTransactionById_Success() {
        when(transactionRepository.findByIdAndUserId(10L, 1L)).thenReturn(Optional.of(sampleExpense));

        TransactionResponse response = transactionService.getTransactionById(1L, 10L);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("Groceries", response.getCategory());
    }

    @Test
    void getTransactionById_ThrowsNotFound_WhenIdDoesNotExist() {
        when(transactionRepository.findByIdAndUserId(99L, 1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> transactionService.getTransactionById(1L, 99L));
    }

    @Test
    void getSummary_CalculatesBalanceAccurately() {
        when(transactionRepository.sumAmountByUserIdAndType(1L, TransactionType.INCOME))
                .thenReturn(new BigDecimal("75000.00"));
        when(transactionRepository.sumAmountByUserIdAndType(1L, TransactionType.EXPENSE))
                .thenReturn(new BigDecimal("25000.00"));
        when(transactionRepository.countByUserId(1L)).thenReturn(15L);

        TransactionSummaryResponse summary = transactionService.getSummary(1L);

        assertNotNull(summary);
        assertEquals(new BigDecimal("75000.00"), summary.getTotalIncome());
        assertEquals(new BigDecimal("25000.00"), summary.getTotalExpense());
        assertEquals(new BigDecimal("50000.00"), summary.getBalance());
        assertEquals(15L, summary.getTotalTransactions());
    }
}

