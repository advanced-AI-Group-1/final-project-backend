package com.example.finalproject.domain.user.controller;

import com.example.finalproject.domain.user.dto.LoginRequestDTO;
//import com.example.finalproject.domain.user.dto.PasswordResetDTO;
import com.example.finalproject.domain.user.dto.UserRegisterDTO;
import com.example.finalproject.domain.user.dto.UserResponseDTO;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.security.CustomOAuth2User;
import com.example.finalproject.domain.user.service.UserService;
import com.example.finalproject.exception.ApiResponse;
import com.example.finalproject.exception.error.DuplicateUserException;
import com.example.finalproject.exception.error.UnAuthorizedException;
import com.example.finalproject.exception.error.UserNotFoundException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import com.example.finalproject.config.jwt.JwtProvider;
import java.util.Optional;

/**
 * 사용자 관련 API 요청을 처리하는 REST 컨트롤러입니다.
 *
 * <p>제공 기능:
 * <ul>
 *   <li>사용자 단건 조회 (GET /api/users/{userId})</li>
 *   <li>사용자 회원가입 (POST /api/users/register)</li>
 * </ul>
 *
 * <p>응답 형식:
 * 모든 응답은 {@link com.example.finalproject.exception.ApiResponse} 형식으로 감싸서 반환됩니다.
 * <ul>
 *   <li>성공 시: {@code ApiResponse.success(data)}</li>
 *   <li>실패 시: {@code ApiResponse.error("오류 메시지")}</li>
 * </ul>
 *
 * <p>예외 처리:
 * <ul>
 *   <li>{@code UserNotFoundException} - 사용자 조회 실패</li>
 *   <li>{@code DuplicateUserException} - 이미 존재하는 사용자로 인한 회원가입 실패</li>
 *   <li>{@code Exception} - 그 외 서버 내부 오류</li>
 * </ul>
 *
 * <p>사용 예:
 * <pre>
 * GET /api/users/gildong123
 * → ApiResponse<UserResponseDTO>
 *
 * POST /api/users/register
 * {
 *   "userId": "gildong123",
 *   "password": "password123!",
 *   "isDirectSignup": true
 * }
 * → ApiResponse<String>
 * </pre>
 *
 * @author
 * @version 1.0
 * @since 2025-06-24
 */

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final JwtProvider jwtProvider;

    private final UserService userService;

    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<String>> handleValidationErrors(MethodArgumentNotValidException ex) {
            String errorMessage = ex.getBindingResult().getFieldError().getDefaultMessage();
            return ResponseEntity.badRequest().body(ApiResponse.error(errorMessage));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("✅ /test 엔드포인트에 도달함");
        return ResponseEntity.ok("hello");
    }


    // 로그인
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequestDTO request) {
        System.out.println("📩 POST /login 요청 도착: " + request.getUserId()); // (테스트를 위해 추가됨)
        boolean success = userService.login(request.getUserId(), request.getPassword());
        
        Map<String, Object> response = new HashMap<>();
        if (success) {
            response.put("success", true);
            response.put("message", "로그인 성공");
            // JWT 토큰 생성 및 반환 (필요한 경우)
             String token = jwtProvider.generateToken(request.getUserId());
             response.put("token", token);
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "로그인 실패: 아이디 또는 비밀번호가 올바르지 않습니다.");
            return ResponseEntity.status(401).body(response);
        }
    }

    // 유저 조회
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponseDTO>> getUserInfo(@PathVariable String userId) {
        try {
            UserEntity userEntity = userService.findByUserId(userId)
                    .orElseThrow(() -> new UserNotFoundException("User not found"));
            UserResponseDTO dto = UserResponseDTO.of(userEntity);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(ApiResponse.success(dto));
        } catch (UserNotFoundException e) {
            log.warn("사용자 조회 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("사용자를 찾을 수 없습니다."));
        } catch (Exception e) {
            log.error("사용자 조회 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("서버 오류가 발생했습니다."));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> register(
            @RequestBody @Valid UserRegisterDTO registerDTO) {
        log.info(registerDTO.toString());

        try {
            UserEntity user = userService.registerUser(
                    registerDTO.getUserId(),
                    registerDTO.getPassword(),
                    registerDTO.isDirectSignup()
            );

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("User registered successfully"));
        } catch (DuplicateUserException e) {
            log.warn("회원가입 실패 - 중복 유저: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error("이미 존재하는 사용자입니다."));
        } catch (Exception e) {
            // ✅ 이 아래 log.error(...) 줄을 추가해 주세요
            log.error("회원가입 중 서버 오류 발생", e);  // ★ 이 줄 꼭 추가 ★
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("회원가입 중 오류가 발생했습니다."));
        }
    }

    /**
     * 사용자 탈퇴 API
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<String>> withdrawUser(
//            @AuthenticationPrincipal CustomOAuth2User principal,
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestParam(required = false) String password) {
//
//        // 인증 정보가 없는 경우
//        if (principal == null) {
//            log.warn("사용자 탈퇴 실패 - 인증 정보 없음");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(ApiResponse.error("인증 정보가 없습니다. 로그인이 필요합니다."));
//        }

        try {
            // 1. Authorization 헤더 검증
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                log.warn("사용자 탈퇴 실패 - 유효하지 않은 인증 헤더");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증 헤더가 유효하지 않습니다."));
            }

            // 2. 토큰 추출 및 검증
            String token = authHeader.substring(7);
            if (!jwtProvider.validateToken(token)) {
                log.warn("사용자 탈퇴 실패 - 유효하지 않은 토큰");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("유효하지 않은 토큰입니다. 다시 로그인해 주세요."));
            }

            // 3. 토큰에서 사용자 ID 추출
            Authentication authentication = jwtProvider.getAuthentication(token);
            String userId = authentication.getName();
//            String userId = principal.getUserId(); // 시큐리티 세션에서 userId 추출
            log.info("사용자 아이디: {}", userId);


            if (userId == null || userId.isEmpty()) {
                log.warn("사용자 탈퇴 실패 - 토큰에서 사용자 ID를 추출할 수 없음");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("인증 정보를 확인할 수 없습니다."));
            }

            // 4. 회원 탈퇴 처리
            userService.withdrawUser(userId, password);
            log.info("사용자 탈퇴 성공 - 사용자 ID: {}", userId);
            return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 완료되었습니다."));
        } catch (UserNotFoundException e) {
            log.warn("사용자 탈퇴 실패 - 사용자 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error("사용자를 찾을 수 없습니다."));
        } catch (UnAuthorizedException e) {
            log.warn("사용자 탈퇴 실패 - 인증 실패: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("인증에 실패했습니다. 비밀번호를 확인해주세요."));
        } catch (Exception e) {
            log.error("사용자 탈퇴 중 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error("회원 탈퇴 중 오류가 발생했습니다."));
        }
    }

//    @PostMapping("/reset-password")
//    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDTO request) {
//        try {
//            Optional<UserEntity> optionalUser = userService.findByUserId(request.getId());
//            if (optionalUser.isEmpty()) {
//                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                        .body(ApiResponse.error("존재하지 않는 사용자입니다."));
//            }
//            userService.updatePassword(request.getId(), request.getPassword());
//            return ResponseEntity.status(HttpStatus.OK)
//                    .body(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
//        } catch (Exception e) {
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                    .body(ApiResponse.error("서버 오류가 발생했습니다."));
//        }
//    }

}
