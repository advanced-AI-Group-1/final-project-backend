package com.example.finalproject.domain.query.controller;

import com.example.finalproject.domain.report.entity.ReportEntity;
import com.example.finalproject.domain.report.service.ReportService;
import com.example.finalproject.exception.error.AIServerUnavailableException;
import com.example.finalproject.exception.error.FinancialDataParseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

/**
 * [QueryController 클래스 설명]
 * <p>
 * 이 컨트롤러는 사용자(프론트엔드)로부터 질의 또는 재무정보를 입력받아,
 * Agent AI 서버에게 해당 데이터를 전달하고, 받은 응답을 그대로 사용자에게 반환하는 역할을 한다.
 * <p>
 * 주요 기능:
 * - POST /api/query/ask: 사용자의 자연어 질의를 AI 서버로 전달하고, 응답을 반환
 * - POST /api/query/financial: 사용자가 직접 입력한 재무제표 데이터를 AI 서버로 전달하고, 분석 결과를 반환
 * <p>
 * 내부 구현:
 * - 두 API 모두 JSON 형식의 데이터를 받으며, 각각 "query" 또는 "financialData" 필드를 사용
 * - RestTemplate을 이용하여 외부 AI 서버와 통신
 * - 공통 전송 로직은 sendToAiServer() 메서드로 분리하여 중복 제거
 * <p>
 * 예외 처리:
 * - AI 서버가 응답하지 않거나 연결 실패 시 AIServerUnavailableException 발생
 * - 재무데이터가 없거나 형식이 잘못되었을 경우 FinancialDataParseException 발생
 * - 기타 예외는 GlobalExceptionHandler 를 통해 처리
 * <p>
 * 보안:
 * - AI 서버 주소는 application.yml 설정 파일을 통해 주입받으며, 외부에 노출되지 않도록 관리
 * <p>
 * 확장 가능성:
 * - 사용자 인증 및 세션 기반 처리
 * - 질의 및 응답 로그 저장
 * - 결과 캐싱 또는 VectorDB 연동
 */


@RestController
@RequestMapping("/api/query")
@RequiredArgsConstructor
@Slf4j
public class QueryController {

    @Value("${ai.server.url:http://localhost:8000}")
    private String aiServerUrl;

    private final RestTemplate restTemplate;
    private final ReportService reportService;

    /**
     * 공통적으로 AI 서버에 요청을 보내는 메서드
     */
    private ResponseEntity<String> sendToAiServer(Object payload, String endpoint) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> requestEntity = new HttpEntity<>(payload, headers);

        try {
            return restTemplate.postForEntity(aiServerUrl + endpoint, requestEntity, String.class);
        } catch (Exception e) {
            throw new AIServerUnavailableException("AI 서버와 통신 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * 1. 일반 텍스트 쿼리 처리 (예: "삼성전자 등급 알려줘")
     * 실제 AI 서버에 POST 요청을 보냄
     */
    @PostMapping("/ask")
    public ResponseEntity<?> forwardQuery(@RequestBody Map<String, Object> payload) {
        // 예시: { "prompt": "매출액이 높고 부채비율이 낮은 기업", "top_k": 5 }
        if (!payload.containsKey("prompt")) {
            throw new IllegalArgumentException("prompt 파라미터가 필요합니다.");
        }
        log.info("VectorDB(AI 서버)로 전송할 질의: " + payload.get("prompt"));

        ResponseEntity<String> response = sendToAiServer(payload, "/api/ai/v1/financial-data/search");

        log.info("AI 서버로부터 응답: " + response.getBody());

        return ResponseEntity.ok(response.getBody());
    }

    /**
     * AI 서버에 요청해서 보고서 저장 및 JSON 반환
     */
    private Map<String, Object> fetchAndSaveReportFromAi(Map<String, Object> payload, String companyName) throws IOException {
        ResponseEntity<String> response = sendToAiServer(payload, "/api/ai/v1/report/generate-from-financial-data");
        String savedUrl = reportService.saveReportFromJsonString(response.getBody());
        com.fasterxml.jackson.databind.ObjectMapper objectMapper = new com.fasterxml.jackson.databind.ObjectMapper();
        return objectMapper.readValue(response.getBody(), Map.class);
    }

    /**
     * 2. 재무제표 직접 입력 처리
     * 실제 AI 서버에 POST 요청을 보냄
     */
    @PostMapping("/financial")
    public ResponseEntity<?> forwardFinancialData(@RequestBody Map<String, Object> payload) {
        if (!payload.containsKey("financial_data")) {
            throw new FinancialDataParseException("financial_data가 누락되었거나 형식이 올바르지 않습니다.");
        }
        log.info("VectorDB(AI 서버)로 전송할 재무제표: " + payload.get("financial_data"));
        String companyName = extractCompanyName(payload);
        if (companyName == null || companyName.isBlank()) {
            return ResponseEntity.badRequest().body("company_name 또는 financial_data.corp_name이 필요합니다.");
        }

        try {
            Optional<ReportEntity> optional = reportService.findReportByCorpName(companyName);
            String safeCorpName = reportService.sanitizeDirectoryName(companyName);

            if (optional.isPresent()) {
                try {
                    Map<String, Object> reportJson = reportService.readReportFromFile(safeCorpName);
                    return ResponseEntity.ok(reportJson);
                } catch (java.io.FileNotFoundException fileNotFound) {
                    log.warn("DB에는 있지만 JSON 파일이 없어 AI 서버에 재요청: {}", companyName);
                    Map<String, Object> reportJson = fetchAndSaveReportFromAi(payload, companyName);
                    return ResponseEntity.ok(reportJson);
                }
            }

            // DB에도 없으면 AI 서버 호출 후 저장
            Map<String, Object> reportJson = fetchAndSaveReportFromAi(payload, companyName);
            return ResponseEntity.ok(reportJson);

        } catch (IOException e) {
            log.error("파일 처리 중 오류", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("보고서 파일 오류");
        } catch (Exception e) {
            log.error("AI 호출 또는 저장 실패", e);
            return ResponseEntity.internalServerError().body("보고서 생성 오류");
        }
    }

    private String extractCompanyName(Map<String, Object> payload) {
        String name = (String) payload.getOrDefault("company_name", null);
        if (name != null && !name.isBlank()) return name;

        Map<String, Object> financialData = (Map<String, Object>) payload.get("financial_data");
        return (String) financialData.getOrDefault("corp_name", null);
    }


}


