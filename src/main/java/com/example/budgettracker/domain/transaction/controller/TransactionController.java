package com.example.budgettracker.domain.transaction.controller;

import com.example.budgettracker.domain.transaction.dto.TransactionRequest;
import com.example.budgettracker.domain.transaction.dto.TransactionResponse;
import com.example.budgettracker.domain.transaction.service.TransactionService;
import com.example.budgettracker.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 거래 등록 API
     * POST /api/transactions
     *
     * @param request 거래 등록 요청 정보
     * @return 등록된 거래 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<TransactionResponse>> createTransaction(
            @Valid @RequestBody TransactionRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        TransactionResponse response = transactionService.createTransaction(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "거래가 등록되었습니다."));
    }

    /**
     * 사용자의 거래 목록 조회 API
     * GET /api/transactions
     *
     * @return 거래 목록
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TransactionResponse>>> getUserTransactions() {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        List<TransactionResponse> response = transactionService.getUserTransactions(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "거래 목록 조회가 완료되었습니다."));
    }
} 