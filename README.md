# Budget Tracker

가계부 관리 애플리케이션

## 기술 스택

- Java 21
- Spring Boot 3.2
- Spring Security
- MySQL 8.0
- MongoDB
- Redis
- Docker & Docker Compose

## 기능 명세

### 사용자 관리
- 회원가입
- 로그인/로그아웃
- 프로필 관리

### 거래 내역 관리
- 수입/지출 등록
- 거래 내역 조회
- 거래 내역 수정/삭제

### 카테고리 관리
- 카테고리 생성
- 카테고리 수정/삭제
- 카테고리별 통계

### 통계 및 분석
- 월별 통계
- 기간별 추이
- 예산 대비 지출 분석

### 데이터 백업 및 복구
- 자동 백업
- 수동 백업
- 백업 데이터 복구

### 알림 기능
- 예산 초과 알림
- 정기 거래 알림
- 시스템 알림

## 실행 방법

### Docker를 사용한 실행

1. Docker와 Docker Compose 설치
   - [Docker 설치 가이드](https://docs.docker.com/get-docker/)
   - [Docker Compose 설치 가이드](https://docs.docker.com/compose/install/)

2. 프로젝트 클론
   ```bash
   git clone https://github.com/GYUTORY/mini-budget-tracker.git
   cd mini-budget-tracker
   ```

3. Docker Compose로 실행
   ```bash
   docker-compose up -d
   ```

4. 서비스 접속
   - 애플리케이션: http://localhost:8080
   - MySQL: localhost:3306
   - MongoDB: localhost:27017
   - Redis: localhost:6379

### 개발 환경 설정

1. JDK 21 설치
2. Maven 설치
3. 프로젝트 빌드
   ```bash
   mvn clean package
   ```
4. 애플리케이션 실행
   ```bash
   java -jar target/*.jar
   ```

## API 문서

- [Swagger UI](http://localhost:8080/swagger-ui.html)
- [OpenAPI 문서](http://localhost:8080/v3/api-docs)

## 환경 변수

### 애플리케이션
- `SPRING_DATASOURCE_URL`: MySQL 데이터베이스 URL
- `SPRING_DATASOURCE_USERNAME`: MySQL 사용자 이름
- `SPRING_DATASOURCE_PASSWORD`: MySQL 비밀번호
- `SPRING_DATA_MONGODB_URI`: MongoDB 연결 URI
- `SPRING_REDIS_HOST`: Redis 호스트
- `SPRING_REDIS_PORT`: Redis 포트

### MySQL
- `MYSQL_ROOT_PASSWORD`: root 사용자 비밀번호
- `MYSQL_DATABASE`: 데이터베이스 이름
- `MYSQL_USER`: 일반 사용자 이름
- `MYSQL_PASSWORD`: 일반 사용자 비밀번호

### MongoDB
- `MONGO_INITDB_ROOT_USERNAME`: root 사용자 이름
- `MONGO_INITDB_ROOT_PASSWORD`: root 사용자 비밀번호
- `MONGO_INITDB_DATABASE`: 데이터베이스 이름

## 라이센스

MIT License
