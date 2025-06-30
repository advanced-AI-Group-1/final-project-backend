package com.example.finalproject.domain.report.service;

import com.example.finalproject.domain.report.entity.ReportEntity;
import com.example.finalproject.domain.report.repository.ReportRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;
    private static final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/reports";

    public String saveReportFromJsonString(String json) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> reportJson = objectMapper.readValue(json, Map.class);

        String corpName = (String) reportJson.getOrDefault("company_name", "알수없음");
        String safeCorpName = sanitizeDirectoryName(corpName);

        // 저장
        saveReportToFile(safeCorpName, reportJson);

        LocalDateTime createdAt = LocalDateTime.now();
        String reportUrl = "/api/report/download-json/" + safeCorpName;

        ReportEntity entity = ReportEntity.builder()
                .corpName(corpName)
                .dateCreated(createdAt)
                .reportUrl(reportUrl)
                .build();

        reportRepository.save(entity);
        return reportUrl;
    }

    public Map<String, Object> readReportFromFile(String safeCorpName) throws IOException {
        Path path = Paths.get(TEMP_DIR, safeCorpName, "report.json");
        if (!Files.exists(path)) {
            throw new FileNotFoundException("보고서 파일이 존재하지 않습니다.");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(path.toFile(), Map.class);
    }

    private void saveReportToFile(String safeCorpName, Map<String, Object> reportJson) throws IOException {
        Path dir = Paths.get(TEMP_DIR, safeCorpName);
        Files.createDirectories(dir);

        Path filePath = dir.resolve("report.json");
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(filePath.toFile(), reportJson);
    }

    public Optional<ReportEntity> findReportByCorpName(String corpName) {
        return reportRepository.findByCorpName(corpName);
    }

    public String sanitizeDirectoryName(String corpName) {
        return corpName.replace("/", "_");
    }

}
