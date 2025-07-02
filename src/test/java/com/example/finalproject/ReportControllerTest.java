//package com.example.finalproject;
//
//import com.example.finalproject.domain.report.controller.ReportController;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockHttpSession;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.util.FileCopyUtils;
//import org.junit.jupiter.api.AfterEach;
//
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.util.UUID;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(ReportController.class)
//public class ReportControllerTest {
//
//    private static final Logger logger = LogManager.getLogger(ReportControllerTest.class);
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    private final MockHttpSession session = new MockHttpSession();
//
//    @Test
//    @DisplayName("📤 PDF 업로드 테스트")
//    void uploadReportTest() throws Exception {
//        // 테스트용 PDF 파일 로드 (src/test/resources/test.pdf)
//        byte[] pdfBytes;
//
//        try (InputStream is = getClass().getResourceAsStream("/test.pdf")) {
//            if (is != null) {
//                pdfBytes = is.readAllBytes();
//            } else {
//                pdfBytes = "Mock PDF content for testing".getBytes();
//            }
//        } catch (Exception e) {
//            pdfBytes = "Mock PDF content for testing".getBytes();
//        }
//
//        var result = mockMvc.perform(post("/api/report/upload")
//                        .contentType(MediaType.APPLICATION_PDF)
//                        .content(pdfBytes)
//                        .session(session))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String fileId = result.getResponse().getContentAsString();
//        logger.info("✅ 업로드된 파일 UUID: {}", fileId);
//        cleanUp();
//    }
//
//    @Test
//    @DisplayName("📥 PDF 다운로드 테스트")
//    void downloadReportTest() throws Exception {
//        // 먼저 업로드
//        byte[] pdfBytes;
//
//        try (InputStream is = getClass().getResourceAsStream("/test.pdf")) {
//            if (is != null) {
//                pdfBytes = is.readAllBytes();
//            } else {
//                pdfBytes = "Mock PDF content for testing".getBytes();
//            }
//        } catch (Exception e) {
//            pdfBytes = "Mock PDF content for testing".getBytes();
//        }
//
//        var upload = mockMvc.perform(post("/api/report/upload")
//                        .contentType(MediaType.APPLICATION_PDF)
//                        .content(pdfBytes)
//                        .session(session))
//                .andExpect(status().isOk())
//                .andReturn();
//
//        String fileId = upload.getResponse().getContentAsString();
//        logger.info("✅ 다운로드 테스트용 파일 UUID: {}", fileId);
//
//        // 그 UUID로 다운로드 시도
//        mockMvc.perform(get("/api/report/download/" + fileId)
//                        .session(session))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_PDF))
//                .andExpect(header().string("Content-Disposition", "attachment; filename=\"report_" + fileId + ".pdf\""));
//
//        logger.info("📥 다운로드 요청 성공: report_{}.pdf", fileId);
//        cleanUp();
//    }
//
//    @Test
//    @DisplayName("❌ 존재하지 않는 PDF 다운로드 시 404 반환")
//    void downloadNonExistingFileTest() throws Exception {
//        String fakeId = UUID.randomUUID().toString();
//
//        mockMvc.perform(get("/api/report/download/" + fakeId)
//                        .session(session))
//                .andExpect(status().isNotFound());
//
//        logger.warn("❌ 존재하지 않는 UUID 요청: {}", fakeId);
//    }
//
//
//    @AfterEach
//    void cleanUp() {
//        String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/reports";
//        File sessionDir = new File(TEMP_DIR, session.getId());
//        if (sessionDir.exists()) {
//            for (File file : sessionDir.listFiles()) {
//                file.delete();
//            }
//            sessionDir.delete();
//            logger.info("🧹 테스트 후 임시 디렉토리 정리 완료: {}", sessionDir.getPath());
//        }
//    }
//
//}
