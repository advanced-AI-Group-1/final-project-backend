package com.example.finalproject.domain.query.controller;

import com.example.finalproject.exception.error.AIServerUnavailableException;
import com.example.finalproject.exception.error.FinancialDataParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.util.Map;

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
public class QueryController {

    @Value("${ai.server.url:http://localhost:8000}")
    private String aiServerUrl;

    private final RestTemplate restTemplate = new RestTemplate();
	
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
		ResponseEntity<String> response = sendToAiServer(payload, "/api/ai/v1/financial-data/search");
		return ResponseEntity.ok(response.getBody());
	}
	
	/**
	 * 2. 재무제표 직접 입력 처리
	 * 실제 AI 서버에 POST 요청을 보냄
	 */
	@PostMapping("/financial")
	public ResponseEntity<?> forwardFinancialData(@RequestBody Map<String, Object> payload) {
		// 예시: { "company_name": ..., "financial_data": {...}, "report_type": ..., "additional_context": ... }
		if (!payload.containsKey("financial_data")) {
			throw new FinancialDataParseException("financial_data가 누락되었거나 형식이 올바르지 않습니다.");
		}
		// 선택적 필드: financial_data.corp_name, additional_context
		// (별도 검증 없이 그대로 전달)
		ResponseEntity<String> response = sendToAiServer(payload, "/api/ai/v1/report/generate-from-financial-data");
		return ResponseEntity.ok(response.getBody());
	}
}

