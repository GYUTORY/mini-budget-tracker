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

### Docker 상세 실행 가이드

1. Dockerfile 생성 (프로젝트 루트 디렉토리에 없는 경우)
   ```dockerfile
   FROM eclipse-temurin:21-jdk as build
   WORKDIR /workspace/app

   COPY mvnw .
   COPY .mvn .mvn
   COPY pom.xml .
   COPY src src

   RUN ./mvnw install -DskipTests
   RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)

   FROM eclipse-temurin:21-jre
   VOLUME /tmp
   ARG DEPENDENCY=/workspace/app/target/dependency
   COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
   COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
   COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
   ENTRYPOINT ["java","-cp","app:app/lib/*","com.example.budgettracker.BudgetTrackerApplication"]
   ```

2. docker-compose.yml 생성 (프로젝트 루트 디렉토리에 없는 경우)
   ```yaml
   version: '3.8'

   services:
     app:
       build: .
       ports:
         - "8080:8080"
       depends_on:
         - mysql
         - redis
         - mongodb
       environment:
         - SPRING_DATASOURCE_URL=jdbc:mysql://mysql:3306/budget_tracker?useSSL=false&serverTimezone=UTC&characterEncoding=UTF-8&allowPublicKeyRetrieval=true
         - SPRING_DATASOURCE_USERNAME=root
         - SPRING_DATASOURCE_PASSWORD=root
         - SPRING_DATA_MONGODB_URI=mongodb://root:root@mongodb:27017/budget_tracker?authSource=admin
         - SPRING_REDIS_HOST=redis
         - SPRING_REDIS_PORT=6379
       networks:
         - budget-network

     mysql:
       image: mysql:8.0
       ports:
         - "3306:3306"
       environment:
         - MYSQL_ROOT_PASSWORD=root
         - MYSQL_DATABASE=budget_tracker
       volumes:
         - mysql-data:/var/lib/mysql
       networks:
         - budget-network

     mongodb:
       image: mongo:latest
       ports:
         - "27017:27017"
       environment:
         - MONGO_INITDB_ROOT_USERNAME=root
         - MONGO_INITDB_ROOT_PASSWORD=root
         - MONGO_INITDB_DATABASE=budget_tracker
       volumes:
         - mongo-data:/data/db
       networks:
         - budget-network

     redis:
       image: redis:latest
       ports:
         - "6379:6379"
       volumes:
         - redis-data:/data
       networks:
         - budget-network

   networks:
     budget-network:
       driver: bridge

   volumes:
     mysql-data:
     mongo-data:
     redis-data:
   ```

3. Docker 환경에서 빌드 및 실행
   ```bash
   # 빌드만 실행
   docker-compose build

   # 모든 서비스 시작
   docker-compose up -d

   # 로그 확인
   docker-compose logs -f

   # 특정 서비스 로그 확인
   docker-compose logs -f app
   ```

4. Docker 컨테이너 및 리소스 관리
   ```bash
   # 실행 중인 컨테이너 확인
   docker-compose ps

   # 컨테이너 중지
   docker-compose stop

   # 컨테이너 재시작
   docker-compose restart

   # 컨테이너 및 네트워크 삭제 (볼륨은 유지)
   docker-compose down

   # 컨테이너, 네트워크 및 볼륨 모두 삭제
   docker-compose down -v
   ```

5. 데이터베이스 접속
   ```bash
   # MySQL 접속
   docker exec -it budget-tracker_mysql_1 mysql -u root -p

   # MongoDB 접속
   docker exec -it budget-tracker_mongodb_1 mongosh -u root -p
   
   # Redis 접속
   docker exec -it budget-tracker_redis_1 redis-cli
   ```

6. 문제 해결
   - 포트 충돌: 이미 사용 중인 포트가 있는 경우 docker-compose.yml 파일에서 포트 매핑 변경
   - 볼륨 마운트 문제: `docker volume ls` 및 `docker volume rm` 명령어로 볼륨 관리
   - 컨테이너 로그 확인: `docker-compose logs -f [서비스명]`으로 오류 확인

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
