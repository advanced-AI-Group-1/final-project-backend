package com.example.finalproject.domain.user.controller;

import com.example.finalproject.domain.user.dto.LoginRequestDTO;
import com.example.finalproject.domain.user.dto.ResetPasswordRequest;
import com.example.finalproject.domain.user.dto.UserRegisterDTO;
import com.example.finalproject.domain.user.dto.UserResponseDTO;
import com.example.finalproject.domain.user.entity.EmailVerificationTokenEntity;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.repository.EmailVerificationTokenRepository;
import com.example.finalproject.domain.user.repository.UserRepository;
import com.example.finalproject.domain.user.security.CustomOAuth2User;
import com.example.finalproject.domain.user.service.EmailSenderService;
import com.example.finalproject.domain.user.service.EmailVerificationService;
import com.example.finalproject.domain.user.service.UserService;
import com.example.finalproject.exception.ApiResponse;
import com.example.finalproject.exception.error.DuplicateUserException;
import com.example.finalproject.exception.error.UnAuthorizedException;
import com.example.finalproject.exception.error.UserNotFoundException;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

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

    private final UserService userService;
    private final UserRepository userRepository;
    private final EmailVerificationService emailVerificationService;
    private final EmailSenderService emailSenderService;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationTokenRepository tokenRepository;


    @RestControllerAdvice
    public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<String>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
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
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO request) {
        System.out.println("📩 POST /login 요청 도착: " + request.getUserId()); // (테스트를 위해 추가됨)
        boolean success = userService.login(request.getUserId(), request.getPassword());
        if (success) {
            return ResponseEntity.ok("로그인 성공");
        } else {
            return ResponseEntity.status(401).body("로그인 실패");
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

    //    @PostMapping("/signup")
//    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid UserRegisterDTO registerDTO) {
//
//        log.info(registerDTO.toString());
//
//        try {
//            userService.registerUser(
//                registerDTO.getUserId(),
//                registerDTO.getPassword(),
//                registerDTO.isDirectSignup()
//            );
//
//            return ResponseEntity.status(HttpStatus.CREATED)
//                .body(ApiResponse.success("User registered successfully"));
//        } catch (DuplicateUserException e) {
//            log.warn("회원가입 실패 - 중복 유저: {}", e.getMessage());
//            return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(ApiResponse.error("이미 존재하는 사용자입니다."));
//        } catch (Exception e) {
//            log.error("회원가입 중 서버 오류 발생", e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(ApiResponse.error("회원가입 중 오류가 발생했습니다."));
//        }
//    }
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<String>> register(
        @RequestBody @Valid UserRegisterDTO registerDTO) {
        log.info(registerDTO.toString());

        try {
            userService.registerUser(
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
        @AuthenticationPrincipal CustomOAuth2User principal,
        @RequestParam(required = false) String password) {

        try {
            String userId = principal.getUserId(); // 시큐리티 세션에서 userId 추출
            userService.withdrawUser(userId, password);
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

    @PostMapping("/send-verification-email")
    public ResponseEntity<String> sendVerificationEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isBlank()) {
            return ResponseEntity.badRequest().body("이메일이 필요합니다.");
        }

        Optional<UserEntity> optionalUser = userRepository.findByUserId(email);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.badRequest().body("사용자를 찾을 수 없습니다.");
        }
        UserEntity user = optionalUser.get();

        String token = emailVerificationService.createEmailVerificationToken(user);

        try {
            // 메일 발송
            emailSenderService.sendVerificationEmail(email, token);
        } catch (Exception e) {
            // 메일 발송 실패 로그 및 안내
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("인증 메일 전송에 실패했습니다.");
        }

        return ResponseEntity.ok("인증 이메일을 발송했습니다.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        String token = request.getToken();
        String newPassword = request.getPassword();

        EmailVerificationTokenEntity tokenEntity = tokenRepository.findByToken(token);
        if (tokenEntity == null || tokenEntity.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("잘못된 또는 만료된 토큰입니다.");
        }

        UserEntity user = tokenEntity.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // 토큰 삭제
        tokenRepository.delete(tokenEntity);

        return ResponseEntity.ok("비밀번호가 재설정되었습니다.");
    }

}
