package com.example.finalproject.domain.report.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;
/**
 * [📄 ReportController 클래스 설명]
 *
 * 이 컨트롤러는 Agent AI 서버가 생성한 PDF 보고서를 서버 측에서 세션별로 저장하고,
 * 사용자(프론트엔드)가 이후 다운로드할 수 있도록 제공하는 역할을 한다.
 *
 * 예 : /tmp/reports/{세션ID}/report_{fileId}.pdf
 *
 * 주요 흐름:
 * - POST /api/report/upload: Agent AI 서버가 생성한 PDF 파일을 HTTP body로 전송
 * - GET /api/report/download/{fileId}: 사용자에게 해당 PDF 파일을 다운로드 제공
 *
 * 세션 ID 기반으로 임시 저장소 디렉토리를 구분하며, UUID 기반으로 고유 파일명을 생성한다.
 * 세션 종료 시 해당 폴더는 자동 삭제된다 (SessionCleanupListener 참조).
 *
 * 보안상 세션ID는 사용자에게 노출되지 않으며, 프론트는 UUID만 사용한다.
 *
 * 확장 기능 :  보고서 목록 조회, 다운로드 유효시간 제한, 파일 자동 만료 기능, 남은 스토리지 용량 체크 로직 (향후 대량 저장 고려 시)
 */
@RestController
@RequestMapping("/api/report")
public class ReportController {

    // 임시 디렉토리 경로 설정
    private final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/reports";

    //1. uploadReport : PDF 파일 업로드를 위한 엔드포인트 (세션 + UUID 기반 ->
    // 사용자에게 세션을 노출하지 않기 위해 UUID를 사용하여 파일 이름을 생성)
    @PostMapping(value = "/upload", consumes = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<String> receiveGeneratedReport(
            HttpServletRequest request,
            HttpSession session) throws IOException {

        // session에서 사용자 정보 가져오기
        String sessionId = session.getId();
        // UUID를 사용하여 파일 이름 생성
        String fileId = UUID.randomUUID().toString();
        String fileName = "report_" + fileId + ".pdf"; // 파일 이름 형식: report_{UUID}.pdf
        // temp 경로 생성
        Path sessionDir = Paths.get(TEMP_DIR, sessionId);
        Files.createDirectories(sessionDir);

        // 파일을 세션 디렉토리에 저장 + OS에 따라 경로 구분자 처리
        File targetFile = new File(sessionDir.toFile(), fileName);

        try (InputStream inputStream = request.getInputStream();
        OutputStream outputStream = Files.newOutputStream(targetFile.toPath())) {
            inputStream.transferTo(outputStream);
        }

        return ResponseEntity.ok(fileId);
    }

    //2. downloadReport : PDF 파일 다운로드를 위한 엔드포인트 (세션 + UUID 기반)
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadReport(
            @PathVariable("fileId") String fileId,
            HttpSession session) {

        String sessionId = session.getId();
        String fileName = "report_" + fileId + ".pdf"; // 파일 이름 형식: report_{UUID}.pdf
        File file = Paths.get(TEMP_DIR, sessionId, fileName).toFile();

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Resource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }

}
