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
 * 🔧 데이터 소스 설정 클래스
 * <p>
 * 이 클래스는 Spring Boot 애플리케이션에서 사용할 `DataSource` 빈을 구성한다.
 * 기본적으로 MySQL 데이터베이스에 연결을 시도하고, 연결에 실패할 경우
 * 자동으로 H2 데이터베이스로 전환된다.
 * <p>
 * 주요 기능:
 * - `application.yml` 또는 `application.properties` 파일에 정의된 DB 접속 정보(@Value)를 읽어온다.
 * - MySQL 연결을 우선 시도하며, 성공 시 해당 커넥션 풀(DataSource)을 빈으로 등록한다.
 * - MySQL 연결에 실패할 경우, H2 TCP 서버를 시작하고 H2 데이터베이스로 전환하여 DataSource를 설정한다.
 * - `@Primary` 어노테이션을 통해 스프링에서 기본적으로 주입되는 DataSource로 지정된다.
 * <p>
 * 로깅: `@Log4j2`를 통해 연결 성공/실패 여부 및 상태 정보를 로그로 출력한다.
 * <p>
 * 참고:
 * - HikariCP를 사용하여 커넥션 풀을 관리한다.
 * - H2는 TCP 모드로 실행되며, 외부 접속이 가능하도록 설정되어 있다.
 */

@Log4j2
@Configuration
public class DataSourceConfig {

    @Value("${database.mysql.url}")
    private String mysqlUrl;

    @Value("${database.mysql.username}")
    private String mysqlUsername;

    @Value("${database.mysql.password}")
    private String mysqlPassword;

    @Value("${database.h2.url}")
    private String h2Url;

    @Value("${database.h2.username}")
    private String h2Username;

    @Value("${database.h2.password}")
    private String h2Password;

    private Server h2TcpServer;  // H2 TCP 서버 참조

    @Bean
    @Primary
    public DataSource dataSource() {
        //먼저 mysql에 연결 시도
        try {
            HikariDataSource mysqlDataSource = new HikariDataSource();
            mysqlDataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            mysqlDataSource.setJdbcUrl(mysqlUrl);
            mysqlDataSource.setUsername(mysqlUsername);
            mysqlDataSource.setPassword(mysqlPassword);

            //연결 테스트
            Connection conn = mysqlDataSource.getConnection();
            conn.close();

            log.info("🔌 MySQL 연결 성공");
            return mysqlDataSource;
        } catch (Exception e) {
            log.info("mysql 연결 실패, h2로 자동 전환", e);

            try {
                // H2 TCP 서버 시작
                h2TcpServer = Server.createTcpServer(
                        "-tcp", "-tcpAllowOthers", "-tcpPort", "9092"
                );
                h2TcpServer.start();
                log.info("🚀 H2 TCP 서버 시작됨 (포트: 9092)");

            } catch (SQLException serverEx) {
                log.warn("H2 TCP 서버 시작 실패: {}", serverEx.getMessage());
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

