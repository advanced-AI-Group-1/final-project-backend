package com.example.finalproject.domain.report.controller;

import com.example.finalproject.domain.report.entity.ReportEntity;
import com.example.finalproject.domain.report.repository.ReportRepository;
import com.example.finalproject.exception.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * ReportController
 * <p>
 * 기업 보고서(JSON)를 저장하고 조회하는 REST 컨트롤러.
 * <p>
 * ✅ 주요 기능:
 * - 기업명 기반 JSON 보고서 저장 (POST /api/report/save-json)
 * - 기업명 기반 JSON 보고서 조회 (GET /api/report/download-json/{corpName})
 * <p>
 * ✅ 저장 구조:
 * - 저장 경로: /tmp/reports/{corpName}/report.json
 * - DB에는 corpName, 생성일, 접근 URL을 함께 저장
 * <p>
 * ✅ 특징:
 * - UUID 및 세션 기반이 아닌 기업명 기반 디렉토리 사용
 * - 전체 사용자 접근이 가능한 정적 URL 제공
 * - JSON 파일은 ObjectMapper를 이용해 저장/조회
 * <p>
 * 사용 환경: Linux 서버 기준 (디렉토리 이름 정제 시 '/' 문자만 제거)
 */

@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@Slf4j
public class ReportController {

    private final ReportRepository reportRepository;

    // 임시 디렉토리 경로 설정
    static private final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/reports";

    // 클래스 로딩 시 디렉토리 생성
    static {
        try {
            Files.createDirectories(Paths.get(TEMP_DIR));
        } catch (IOException e) {
            // 무시 (이미 존재할 수 있음)
            log.warn("임시 디렉토리 생성 실패", e);
        }
    }

    // 1. JSON 형식의 보고서를 로컬 서버에 .json파일로 저장, DB에 경로 저장 후 URI 반환, 기업명 기반
    @PostMapping(value = "/save-json")
    public ResponseEntity<ApiResponse<String>> saveJsonReport(@RequestBody Map<String, Object> reportJson) {
        String corpName = (String) reportJson.get("company_name");
        if (corpName == null || corpName.isBlank()) { // 회사 이름


            // 이 없을 경우 기본값 설정
            corpName = "알수없음";
        }

        log.info("JSON 보고서 저장 요청: 회사명 = {}", corpName);

        try {
            String safeCorpName = sanitizeDirectoryName(corpName);
            saveReportToFile(safeCorpName, reportJson); // JSON 파일 로컬에 저장
            String reportUrl = "/api/report/download-json/" + safeCorpName; // 파일 다운로드 URI 생성
            ReportEntity report = ReportEntity.builder()
                    .corpName(corpName)
                    .dateCreated(LocalDateTime.now())
                    .reportUrl(reportUrl)
                    .build();
            reportRepository.save(report); // DB에 보고서 정보 저장\
            return ResponseEntity.ok(ApiResponse.success(reportUrl));
        } catch (IOException e) {
            log.error("보고서 저장 실패", e);
            return ResponseEntity.internalServerError().body(ApiResponse.error("보고서 저장 중 오류 발생"));
        }
    }

    //2. 기업명 기반 JSON 보고서 반환 (ApiResponse 없이 JSON 그대로 반환)
    @GetMapping("/download-json/{corpName}")
    public ResponseEntity<Map<String, Object>> serveJsonReport(@PathVariable String corpName) {
        Optional<ReportEntity> optionalReport = reportRepository.findByCorpName(corpName);
        if (optionalReport.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        try {
            String safeCorpName = sanitizeDirectoryName(corpName);
            Map<String, Object> reportJson = readReportFromFile(safeCorpName); // 로컬 서버에 저장된 JSON 파일 읽기
            return ResponseEntity.ok(reportJson);
        } catch (IOException e) {
            log.error("보고서 파일 읽기 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 디렉토리 이름에서 "/" 제거 (Linux 기준)
     */
    private String sanitizeDirectoryName(String corpName) {
        return corpName.replace("/", "_");
    }

    /**
     * JSON 파일 저장
     */
    private void saveReportToFile(String safeCorpName, Map<String, Object> reportJson) throws IOException {
        Path corpDir = Paths.get(TEMP_DIR, safeCorpName);
        Files.createDirectories(corpDir);

        Path targetPath = corpDir.resolve("report.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(targetPath.toFile(), reportJson);
    }

    /**
     * JSON 파일 읽기
     */
    private Map<String, Object> readReportFromFile(String safeCorpName) throws IOException {
        Path filePath = Paths.get(TEMP_DIR, safeCorpName, "report.json");
        if (!Files.exists(filePath)) {
            throw new FileNotFoundException("파일이 존재하지 않음: " + filePath.toString());
        }
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(filePath.toFile(), Map.class);
    }
}
