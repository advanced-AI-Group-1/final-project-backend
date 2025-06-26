package com.example.finalproject.domain.user.controller;

import com.example.finalproject.domain.user.dto.UserRegisterDTO;
import com.example.finalproject.domain.user.dto.UserResponseDTO;
import com.example.finalproject.domain.user.dto.LoginRequestDTO;
import com.example.finalproject.domain.user.entity.UserEntity;
import com.example.finalproject.domain.user.service.UserService;
import com.example.finalproject.exception.ApiResponse;
import com.example.finalproject.exception.error.DuplicateUserException;
import com.example.finalproject.exception.error.UserNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

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
            UserEntity userEntity = userService.findByUserId(userId).orElseThrow(() -> new UserNotFoundException("User not found"));
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
    public ResponseEntity<ApiResponse<String>> register(@RequestBody @Valid UserRegisterDTO registerDTO) {

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
            log.error("회원가입 중 서버 오류 발생", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("회원가입 중 오류가 발생했습니다."));
        }
    }
}
