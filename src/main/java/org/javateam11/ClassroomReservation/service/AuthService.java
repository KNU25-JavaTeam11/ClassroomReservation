package org.javateam11.ClassroomReservation.service;

import org.javateam11.ClassroomReservation.dto.LoginRequest;
import org.javateam11.ClassroomReservation.dto.RegisterRequest;
import org.javateam11.ClassroomReservation.dto.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;

/**
 * 인증 관련 API 호출을 담당하는 서비스 클래스
 */
public class AuthService {
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final ApiService apiService;
    private final TokenManager tokenManager;

    public AuthService() {
        this.apiService = new ApiService();
        this.tokenManager = TokenManager.getInstance();
    }

    /**
     * 예외에서 의미있는 에러 메시지를 추출하는 헬퍼 메서드
     */
    private String extractErrorMessage(Throwable throwable) {
        Throwable rootCause = throwable;

        // CompletionException이나 다른 래퍼 예외를 풀어서 원본 예외를 찾음
        while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
            rootCause = rootCause.getCause();
        }

        String message = rootCause.getMessage();
        if (message != null && !message.trim().isEmpty()) {
            return message;
        }

        // 메시지가 없으면 기본 메시지 반환
        return "요청 처리 중 오류가 발생했습니다";
    }

    /**
     * 회원가입
     */
    public CompletableFuture<AuthResponse> register(String studentId, String name, String password) {
        logger.info("회원가입 시도: 학번 '{}'", studentId);

        RegisterRequest request = new RegisterRequest(studentId, name, password);

        return apiService.postAsync("/api/auth/register", request, AuthResponse.class)
                .thenApply(response -> {
                    logger.info("회원가입 성공: 학번 '{}'", response.getStudentId());
                    // 회원가입 성공 시 자동으로 토큰 저장
                    tokenManager.setAuthentication(response.getStudentId(), response.getName(), response.getToken());
                    return response;
                })
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        logger.error("회원가입 실패: 학번 '{}'", studentId, throwable);
                        // 원본 예외에서 메시지 추출
                        String errorMessage = extractErrorMessage(throwable);
                        throw new RuntimeException(errorMessage, throwable);
                    }
                    return response;
                });
    }

    /**
     * 로그인
     */
    public CompletableFuture<AuthResponse> login(String username, String password) {
        logger.info("로그인 시도: 사용자명 '{}'", username);

        LoginRequest request = new LoginRequest(username, password);

        return apiService.postAsync("/api/auth/login", request, AuthResponse.class)
                .thenApply(response -> {
                    logger.info("로그인 성공: 학번 '{}'", response.getStudentId());
                    // 로그인 성공 시 토큰 저장
                    tokenManager.setAuthentication(response.getStudentId(), response.getName(), response.getToken());
                    return response;
                })
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        logger.error("로그인 실패: 사용자명 '{}'", username, throwable);
                        // 원본 예외에서 메시지 추출
                        String errorMessage = extractErrorMessage(throwable);
                        throw new RuntimeException(errorMessage, throwable);
                    }
                    return response;
                });
    }

    /**
     * 로그아웃
     */
    public void logout() {
        String currentUser = tokenManager.getCurrentStudentId();
        tokenManager.clearAuthentication();
        logger.info("로그아웃 완료: 사용자명 '{}'", currentUser);
    }

    /**
     * 현재 사용자가 로그인되어 있는지 확인
     */
    public boolean isLoggedIn() {
        return tokenManager.isAuthenticated();
    }
}