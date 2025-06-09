package com.example.finalproject.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;
/* [✅ 클래스 설명]
 * 이 클래스는 Spring Boot에서 HttpSessionListener를 구현하여,
 * 사용자의 세션이 종료될 때 자동으로 해당 세션의 임시 파일 디렉토리를 정리(clean-up)하는 역할을 한다.
 *
 * 사용자가 파일 업로드 시 /tmp/reports/{sessionId}/report_*.pdf 형식으로 저장되며,
 * 세션이 만료되면 이 디렉토리를 삭제하여 자원을 정리하고 보안을 강화한다.
 *
 * 구성 요소:
 * - TEMP_DIR: 시스템 임시 디렉토리의 기본 경로 (/tmp/reports 또는 Windows 임시 경로)
 * - sessionDestroyed(): 세션 종료 이벤트 발생 시 자동 호출되어 디렉토리를 삭제함
 *
 * 작동 방식:
 * - @Component로 등록되어 Spring이 자동으로 관리
 * - 세션 ID를 기준으로 사용자별 폴더를 구분
 * - FileSystemUtils.deleteRecursively()를 통해 하위 파일까지 전부 삭제
 *
 * 주의사항:
 * - 반드시 HttpSessionListener를 구현해야 작동함
 * - 이 클래스는 사용자 요청과 무관하게 세션 상태 변화에 반응함
 */
@Component
public class SessionCleanupListener implements HttpSessionListener {

    private final String TEMP_DIR = System.getProperty("java.io.tmpdir") + "/reports";

    // 세션이 종료되면 SpringBoot이 자동으로 호출
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // 종료된 세션 ID를 가져와서 해당 세션의 임시 디렉토리를 삭제
        String sessionId = se.getSession().getId();
        File sessionDir = new File(TEMP_DIR, sessionId);
        // 세션 디렉토리가 존재하면 재귀적으로 삭제(하위 파일 및 디렉토리 포함)
        if (sessionDir.exists()) {
            FileSystemUtils.deleteRecursively(sessionDir);
            System.out.println("🧹 세션 종료 - 디렉토리 삭제됨: " + sessionDir.getAbsolutePath());
        }
    }
}
