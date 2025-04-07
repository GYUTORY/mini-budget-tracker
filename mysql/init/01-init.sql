-- 사용자 생성 및 권한 부여
CREATE USER IF NOT EXISTS 'budget_user'@'%' IDENTIFIED BY 'budget_password';
GRANT ALL PRIVILEGES ON budget_tracker.* TO 'budget_user'@'%';
FLUSH PRIVILEGES;

-- 데이터베이스 선택
USE budget_tracker;

-- 사용자 테이블 생성
CREATE TABLE IF NOT EXISTS users (
    id VARCHAR(36) PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 카테고리 테이블 생성
CREATE TABLE IF NOT EXISTS categories (
    id VARCHAR(36) PRIMARY KEY,
    name VARCHAR(50) NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 거래 내역 테이블 생성
CREATE TABLE IF NOT EXISTS transactions (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    type ENUM('INCOME', 'EXPENSE') NOT NULL,
    description TEXT,
    date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- 예산 테이블 생성
CREATE TABLE IF NOT EXISTS budgets (
    id VARCHAR(36) PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    category_id VARCHAR(36) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    year_month VARCHAR(7) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (category_id) REFERENCES categories(id),
    UNIQUE KEY unique_budget (user_id, category_id, year_month)
); 