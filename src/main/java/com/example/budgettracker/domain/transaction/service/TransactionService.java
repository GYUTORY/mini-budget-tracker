package com.example.budgettracker.domain.transaction.service;

import com.example.budgettracker.domain.transaction.Transaction;
import com.example.budgettracker.domain.transaction.dto.TransactionRequest;
import com.example.budgettracker.domain.transaction.dto.TransactionResponse;
import com.example.budgettracker.domain.transaction.repository.TransactionRepository;
import com.example.budgettracker.domain.user.User;
import com.example.budgettracker.domain.user.repository.UserRepository;
import com.example.budgettracker.global.error.BusinessException;
import com.example.budgettracker.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    @Transactional
    public TransactionResponse createTransaction(String userId, TransactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .user(user)
                .type(request.getType())
                .category(request.getCategory())
                .amount(request.getAmount())
                .transactionDate(request.getTransactionDate())
                .memo(request.getMemo())
                .build();

        transactionRepository.save(transaction);
        return TransactionResponse.from(transaction);
    }

    public List<TransactionResponse> getUserTransactions(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        return transactionRepository.findByUserOrderByTransactionDateDesc(user).stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }
} 