package com.example.finalproject.exception;
/**
 * API 응답을 표준화하기 위한 제네릭 래퍼 클래스입니다.
 *
 * <p>API 성공/실패 여부, 응답 데이터, 오류 메시지를 일관된 형식으로 반환합니다.</p>
 *
 * <p>사용 예시:
 * <pre>
 *     // 성공 응답
 * {
 *   "success": true,
 *   "data": {
 *     "id": 1,
 *     "userId": "gildong"
 *   },
 *   "error": null
 * }
 *
 *     // 실패 응답
 * {
 *   "success": false,
 *   "data": null,
 *   "error": "사용자를 찾을 수 없습니다."
 * }
 * </pre>
 * </p>
 *
 * @param <T> 응답 데이터 타입
 */
public class ApiResponse<T> {
  private boolean success;
  private T data;
  private String error;

  private ApiResponse(boolean success, T data, String error) {
    this.success = success;
    this.data = data;
    this.error = error;
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, data, null);
  }

  public static <T> ApiResponse<T> error(String errorMessage) {
    return new ApiResponse<>(false, null, errorMessage);
  }

}