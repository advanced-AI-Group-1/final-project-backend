package com.example.finalproject.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;
import org.springframework.util.FileSystemUtils;

import java.io.File;

/**
 * 세션 종료 시 임시 파일을 자동으로 정리하는 리스너 클래스입니다.
 *
 * <p>이 클래스는 HttpSessionListener를 구현하여 사용자 세션과 연관된 임시 디렉토리를 정리합니다.
 * 파일 업로드 시 /tmp/reports/{sessionId}/ 디렉토리에 저장된 임시 파일들을 세션 종료 시 자동으로 제거하여
 * 시스템 자원을 확보하고 보안을 강화합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>세션 만료 또는 무효화 시점 자동 감지</li>
 *   <li>세션별 임시 디렉토리 자동 정리</li>
 *   <li>파일 시스템 자원의 효율적 관리</li>
 *   <li>보안 강화를 위한 임시 파일 자동 삭제</li>
 * </ul>
 *
 * <p>동작 방식:
 * <ol>
 *   <li>세션이 종료되면 sessionDestroyed() 메소드가 자동 호출됨</li>
 *   <li>세션 ID를 기반으로 한 임시 디렉토리 경로 생성</li>
 *   <li>해당 디렉토리와 모든 하위 파일/디렉토리 재귀적 삭제</li>
 *   <li>삭제 완료 시 로그 출력</li>
 * </ol>
 *
 * <p>주의사항:
 * <ul>
 *   <li>이 리스너는 Spring의 @Component로 등록되어 자동으로 동작함</li>
 *   <li>임시 파일은 시스템의 기본 임시 디렉토리 하위에 저장됨</li>
 *   <li>세션 타임아웃은 Spring Security 또는 application.properties에서 설정 필요</li>
 *   <li>파일 삭제 실패 시 로그만 출력하고 예외는 무시됨</li>
 * </ul>
 *
 * @see jakarta.servlet.http.HttpSessionListener
 * @see org.springframework.util.FileSystemUtils
 * @see org.springframework.stereotype.Component
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
