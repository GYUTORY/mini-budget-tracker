package com.budgettracker.config

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application.yml")
abstract class TestConfig {
    // Base configuration for all integration tests
} 