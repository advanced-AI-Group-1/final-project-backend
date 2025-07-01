package com.example.finalproject.config;

import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.log4j.Log4j2;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * 데이터베이스 연결을 관리하는 설정 클래스입니다.
 * 애플리케이션의 데이터 소스(DataSource)를 구성하고 관리합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>MySQL 데이터베이스에 대한 연결 구성 (기본 설정)</li>
 *   <li>MySQL 연결 실패 시 H2 데이터베이스로 자동 전환</li>
 *   <li>HikariCP를 사용한 고성능 커넥션 풀 관리</li>
 *   <li>H2 데이터베이스의 TCP 서버 자동 시작</li>
 * </ul>
 *
 * <p>동작 방식:
 * <ul>
 *   <li>애플리케이션 시작 시 MySQL 연결 시도</li>
 *   <li>MySQL 연결 실패 시 H2 데이터베이스로 자동 전환</li>
 *   <li>H2는 TCP 모드로 실행되어 외부 도구에서 접근 가능</li>
 * </ul>
 *
 * <p>주의사항:
 * <ul>
 *   <li>프로덕션 환경에서는 적절한 데이터베이스 연결 정보를 설정해야 함</li>
 *   <li>H2는 개발/테스트 용도로만 사용하는 것을 권장</li>
 * </ul>
 *
 * <p>로깅:
 * <ul>
 *   <li>데이터베이스 연결 상태 및 전환 사항을 상세히 로깅</li>
 *   <li>문제 발생 시 원인 파악을 위한 오류 메시지 제공</li>
 * </ul>
 */

@Log4j2
@Configuration
public class DataSourceConfig {

    // ✅ 테스트용 MySQL 설정 (application.yml의 spring.datasource.* 참조)
    @Value("${spring.datasource.url}")
    private String mysqlUrl;

    @Value("${spring.datasource.username}")
    private String mysqlUsername;

    @Value("${spring.datasource.password}")
    private String mysqlPassword;

    // ✅ H2 fallback 설정
    @Value("${database.h2.url}")
    private String h2Url;

    @Value("${database.h2.username}")
    private String h2Username;

    @Value("${database.h2.password}")
    private String h2Password;

    private Server h2TcpServer;

    @Bean
    @Primary
    public DataSource dataSource() {
        try {
            // MySQL 연결 시도
            HikariDataSource mysqlDataSource = new HikariDataSource();
            mysqlDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            mysqlDataSource.setJdbcUrl(mysqlUrl);
            mysqlDataSource.setUsername(mysqlUsername);
            mysqlDataSource.setPassword(mysqlPassword);

            Connection conn = mysqlDataSource.getConnection();
            conn.close();

            log.info("🔌 MySQL 연결 성공");
            return mysqlDataSource;
        } catch (Exception e) {
            log.warn("❌ MySQL 연결 실패, H2로 자동 전환", e);

            try {
                h2TcpServer = Server.createTcpServer(
                        "-tcp", "-tcpAllowOthers", "-tcpPort", "9092"
                ).start();
                log.info("🚀 H2 TCP 서버 시작됨 (포트: 9092)");
            } catch (SQLException ex) {
                log.error("⚠️ H2 TCP 서버 시작 실패: {}", ex.getMessage());
            }

            HikariDataSource h2DataSource = new HikariDataSource();
            h2DataSource.setDriverClassName("org.h2.Driver");
            h2DataSource.setJdbcUrl(h2Url);
            h2DataSource.setUsername(h2Username);
            h2DataSource.setPassword(h2Password);

            return h2DataSource;
        }
    }
}