package org.javateam11.ClassroomReservation.service;

import org.javateam11.ClassroomReservation.dto.LoginRequest;
import org.javateam11.ClassroomReservation.dto.RegisterRequest;
import org.javateam11.ClassroomReservation.dto.LoginResponse;
import org.javateam11.ClassroomReservation.dto.RegisterResponse;
import org.javateam11.ClassroomReservation.util.ErrorMessageUtils;
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
     * 회원가입
     */
    public CompletableFuture<RegisterResponse> register(String studentId, String name, String password) {
        logger.info("회원가입 시도: 학번 '{}'", studentId);

        RegisterRequest request = new RegisterRequest(studentId, name, password);

        return apiService.postAsync("/api/auth/register", request, RegisterResponse.class)
                .thenApply(response -> {
                    logger.info("회원가입 성공: 학번 '{}', 메시지: {}", response.getStudentId(), response.getMessage());
                    // 회원가입 성공 시는 토큰이 없으므로 토큰 저장을 하지 않음
                    return response;
                })
                .handle((response, throwable) -> {
                    if (throwable != null) {
                        logger.error("회원가입 실패: 학번 '{}'", studentId, throwable);
                        // 원본 예외에서 메시지 추출
                        String errorMessage = ErrorMessageUtils.extractCleanErrorMessage(throwable);
                        throw new RuntimeException(errorMessage, throwable);
                    }
                    return response;
                });
    }

    /**
     * 로그인
     */
    public CompletableFuture<LoginResponse> login(String username, String password) {
        logger.info("로그인 시도: 사용자명 '{}'", username);

        LoginRequest request = new LoginRequest(username, password);

        return apiService.postAsync("/api/auth/login", request, LoginResponse.class)
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
                        String errorMessage = ErrorMessageUtils.extractCleanErrorMessage(throwable);
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