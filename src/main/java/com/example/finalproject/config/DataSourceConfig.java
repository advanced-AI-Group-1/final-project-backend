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

