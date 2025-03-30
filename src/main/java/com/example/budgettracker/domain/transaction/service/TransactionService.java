package com.example.budgettracker.domain.transaction.service;

import com.example.budgettracker.domain.transaction.dto.TransactionRequest;
import com.example.budgettracker.domain.transaction.dto.TransactionResponse;
import com.example.budgettracker.domain.transaction.entity.Transaction;
import com.example.budgettracker.domain.transaction.repository.TransactionRepository;
import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.repository.UserRepository;
import com.example.budgettracker.global.exception.CustomException;
import com.example.budgettracker.global.exception.ErrorCode;
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
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Transaction transaction = Transaction.builder()
                .user(user)
                .amount(request.getAmount())
                .type(request.getType())
                .category(request.getCategory())
                .description(request.getDescription())
                .date(request.getDate())
                .build();

        transaction = transactionRepository.save(transaction);
        return TransactionResponse.from(transaction);
    }

    public List<TransactionResponse> getUserTransactions(String userId) {
        return transactionRepository.findByUserId(Long.parseLong(userId)).stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
    }

    public TransactionResponse getTransaction(String userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!transaction.getUser().getId().toString().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        return TransactionResponse.from(transaction);
    }

    @Transactional
    public TransactionResponse updateTransaction(String userId, Long transactionId, TransactionRequest request) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!transaction.getUser().getId().toString().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        transaction.update(request);
        return TransactionResponse.from(transaction);
    }

    @Transactional
    public void deleteTransaction(String userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new CustomException(ErrorCode.TRANSACTION_NOT_FOUND));

        if (!transaction.getUser().getId().toString().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        transactionRepository.delete(transaction);
    }
} 