package com.example.budgettracker.domain.budget.service;

import com.example.budgettracker.domain.budget.dto.BudgetRequest;
import com.example.budgettracker.domain.budget.entity.Budget;
import com.example.budgettracker.domain.budget.repository.BudgetRepository;
import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Testcontainers
@Transactional
class BudgetServiceIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
            .withDatabaseName("budget_tracker_test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private BudgetRepository budgetRepository;

    @Autowired
    private UserRepository userRepository;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("password")
                .name("Test User")
                .build();
        userRepository.save(testUser);
    }

    @Test
    @DisplayName("예산 생성 - 성공")
    void createBudget_Success() {
        // given
        BudgetRequest request = new BudgetRequest();
        request.setAmount(BigDecimal.valueOf(1000000));
        request.setYearMonth(YearMonth.now());
        request.setCategoryId(1L);

        // when
        Long budgetId = budgetService.createBudget(testUser.getId(), request);

        // then
        Budget savedBudget = budgetRepository.findById(budgetId).orElseThrow();
        assertThat(savedBudget.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000000));
        assertThat(savedBudget.getUser().getId()).isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("예산 생성 - 중복 기간 실패")
    void createBudget_DuplicatePeriod() {
        // given
        BudgetRequest request = new BudgetRequest();
        request.setAmount(BigDecimal.valueOf(1000000));
        request.setYearMonth(YearMonth.now());
        request.setCategoryId(1L);

        budgetService.createBudget(testUser.getId(), request);

        // when & then
        assertThrows(IllegalStateException.class, () -> {
            budgetService.createBudget(testUser.getId(), request);
        });
    }
} 