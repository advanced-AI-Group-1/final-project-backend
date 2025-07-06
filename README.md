# Final Project Backend

Spring Boot 기반의 백엔드 애플리케이션으로, AI 서버와의 통신을 통해 재무제표 분석 및 보고서 생성을 지원합니다.

## 주요 기능

### 1. 기존 REST API

-   **POST /api/query/ask**: 자연어 질의 처리
-   **POST /api/query/financial**: 재무제표 데이터 분석 (동기 방식)

### 2. 새로운 보고서 관리 API

-   **GET /api/query/report/{companyName}**: 특정 기업의 보고서 조회
-   **POST /api/query/save-report**: AI 서버에서 생성된 보고서를 저장

## API 상세 설명

### 보고서 조회 API

```
GET /api/query/report/{companyName}
```

**응답 예시:**

```json
// 보고서가 존재하는 경우
{
  "exists": true,
  "company_name": "삼성전자",
  "report": {
    "company_name": "삼성전자",
    "credit_rating": "AAA",
    "analysis": "..."
  }
}

// 보고서가 존재하지 않는 경우
{
  "exists": false,
  "company_name": "삼성전자",
  "message": "해당 기업의 보고서가 존재하지 않습니다."
}
```

### 보고서 저장 API

```
POST /api/query/save-report
```

**요청 예시:**

```json
{
	"company_name": "삼성전자",
	"report": {
		"company_name": "삼성전자",
		"credit_rating": "AAA",
		"analysis": "...",
		"financial_data": {
			"revenue": 3000000000000,
			"net_income": 500000000000
		}
	}
}
```

**응답 예시:**

```json
{
	"success": true,
	"company_name": "삼성전자",
	"saved_url": "/reports/samsung_electronics.json",
	"message": "보고서가 성공적으로 저장되었습니다."
}
```

## 사용 시나리오

### 1. 보고서 생성 및 저장 플로우

1. 프론트엔드에서 AI 서버로 직접 SSE 연결하여 보고서 생성
2. AI 서버에서 보고서 생성 완료 시 프론트엔드로 결과 전송
3. 프론트엔드에서 Spring Boot 백엔드의 `/api/query/save-report` API 호출하여 보고서 저장
4. 저장 완료 후 사용자에게 결과 표시

### 2. 기존 보고서 조회 플로우

1. 사용자가 특정 기업의 보고서를 요청
2. 프론트엔드에서 `/api/query/report/{companyName}` API 호출
3. 보고서가 존재하면 즉시 반환, 없으면 AI 서버로 새로 생성 요청

## 설정

### 의존성

-   `spring-boot-starter-web`: 웹 기능
-   `spring-boot-starter-data-jpa`: JPA ORM
-   `spring-boot-starter-security`: Spring Security

### 보안 설정

-   보고서 조회/저장 엔드포인트들은 인증 없이 접근 가능하도록 설정됨
-   `/api/query/report/**`, `/api/query/save-report` 경로 허용

## 개발 환경

-   Java 17
-   Spring Boot 3.5.0
-   Gradle
-   MySQL 8.0

## 실행 방법

1. 의존성 설치:

```bash
./gradlew build
```

2. 애플리케이션 실행:

```bash
./gradlew bootRun
```

3. API 테스트:

```bash
# 보고서 조회
curl http://localhost:8080/api/query/report/삼성전자

# 보고서 저장
curl -X POST http://localhost:8080/api/query/save-report \
  -H "Content-Type: application/json" \
  -d '{"company_name":"삼성전자","report":{"company_name":"삼성전자","credit_rating":"AAA"}}'
```

## 파일 구조

```
src/main/java/com/example/finalproject/
├── config/
│   └── SecurityConfig.java           # 보안 설정 (보고서 API 허용)
├── domain/
│   └── query/
│       └── controller/
│           └── QueryController.java   # REST API + 보고서 관리 API
└── ...
```

## 주의사항

1. **보고서 저장**: AI 서버에서 생성된 보고서는 반드시 백엔드에 저장하여 재사용 가능
2. **에러 처리**: 보고서 조회/저장 시 적절한 에러 메시지 반환
3. **데이터 검증**: 저장 시 필수 필드(company_name, report) 검증
4. **파일 관리**: 보고서 파일은 안전한 디렉토리명으로 저장
