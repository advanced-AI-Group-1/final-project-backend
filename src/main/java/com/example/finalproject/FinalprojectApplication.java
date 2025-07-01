package com.example.finalproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 프로젝트의 메인 애플리케이션 클래스입니다.
 * Spring Boot 애플리케이션의 진입점(Entry Point) 역할을 합니다.
 *
 * <p>이 클래스는 다음의 주요 기능을 수행합니다:
 * <ul>
 *   <li>Spring Boot 애플리케이션을 시작하는 main 메서드 포함</li>
 *   <li>자동 구성, 컴포넌트 스캔, 속성 로드 등의 스프링 부트 자동 설정 수행</li>
 *   <li>내장형 톰캣 서버를 실행하여 웹 애플리케이션으로 동작</li>
 * </ul>
 * 
 * <p>@SpringBootApplication 어노테이션은 다음 세 가지 어노테이션을 조합한 것입니다:
 * <ul>
 *   <li>@Configuration: 스프링 설정 클래스임을 나타냄</li>
 *   <li>@EnableAutoConfiguration: 스프링 부트의 자동 구성 활성화</li>
 *   <li>@ComponentScan: 컴포넌트 스캔을 통해 빈을 등록</li>
 * </ul>
 */
@SpringBootApplication
public class FinalprojectApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinalprojectApplication.class, args);
    }

}
