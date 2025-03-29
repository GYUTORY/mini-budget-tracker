package com.example.budgettracker.domain.transaction.controller;

import com.example.budgettracker.domain.transaction.dto.TransactionRequest;
import com.example.budgettracker.domain.transaction.dto.TransactionResponse;
import com.example.budgettracker.domain.transaction.service.TransactionService;
import com.example.budgettracker.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 거래 내역 관련 API 컨트롤러
 * 
 * @Tag: Swagger 문서에서 API 그룹을 나타냄
 * @RestController: REST API 컨트롤러임을 나타냄
 * @RequestMapping: 기본 URL 경로 설정
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@Tag(name = "거래 내역", description = "거래 내역 관리 API")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 거래 내역 생성 API
     * 
     * @Operation: API 엔드포인트 설명
     * @Parameter: API 파라미터 설명
     * @param authentication 인증 정보
     * @param request 거래 내역 생성 요청 데이터
     * @return 생성된 거래 내역 응답 데이터
     */
    @Operation(summary = "거래 내역 생성", description = "새로운 거래 내역을 등록합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "거래 내역 생성 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "거래 내역 정보", required = true)
            @Valid @RequestBody TransactionRequest request) {
        String userId = authentication.getName();
        return ResponseEntity.ok(transactionService.createTransaction(userId, request));
    }

    /**
     * 거래 내역 조회 API
     * 
     * @Operation: API 엔드포인트 설명
     * @param authentication 인증 정보
     * @return 거래 내역 목록 응답 데이터
     */
    @Operation(summary = "거래 내역 목록 조회", description = "사용자의 모든 거래 내역을 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "거래 내역 목록 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getTransactions(
            @Parameter(hidden = true)
            Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(transactionService.getUserTransactions(userId));
    }

    /**
     * 거래 내역 상세 조회 API
     * 
     * @Operation: API 엔드포인트 설명
     * @Parameter: API 파라미터 설명
     * @param authentication 인증 정보
     * @param id 거래 내역 ID
     * @return 거래 내역 상세 응답 데이터
     */
    @Operation(summary = "거래 내역 상세 조회", description = "특정 거래 내역의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "거래 내역 상세 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "거래 내역을 찾을 수 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransaction(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "거래 내역 ID", required = true)
            @PathVariable Long id) {
        String userId = authentication.getName();
        return ResponseEntity.ok(transactionService.getTransaction(userId, id));
    }

    /**
     * 거래 내역 수정 API
     * 
     * @Operation: API 엔드포인트 설명
     * @Parameter: API 파라미터 설명
     * @param authentication 인증 정보
     * @param id 거래 내역 ID
     * @param request 거래 내역 수정 요청 데이터
     * @return 수정된 거래 내역 응답 데이터
     */
    @Operation(summary = "거래 내역 수정", description = "기존 거래 내역을 수정합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "거래 내역 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "거래 내역을 찾을 수 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "거래 내역 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "수정할 거래 내역 정보", required = true)
            @Valid @RequestBody TransactionRequest request) {
        String userId = authentication.getName();
        return ResponseEntity.ok(transactionService.updateTransaction(userId, id, request));
    }

    /**
     * 거래 내역 삭제 API
     * 
     * @Operation: API 엔드포인트 설명
     * @Parameter: API 파라미터 설명
     * @param authentication 인증 정보
     * @param id 거래 내역 ID
     * @return 삭제 성공 여부
     */
    @Operation(summary = "거래 내역 삭제", description = "거래 내역을 삭제합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "거래 내역 삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "거래 내역을 찾을 수 없음")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "거래 내역 ID", required = true)
            @PathVariable Long id) {
        String userId = authentication.getName();
        transactionService.deleteTransaction(userId, id);
        return ResponseEntity.ok().build();
    }
} 