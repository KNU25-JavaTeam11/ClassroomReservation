package org.javateam11.ClassroomReservation.service;

import org.javateam11.ClassroomReservation.dto.AuthRequest;
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
     * 회원가입
     */
    public CompletableFuture<AuthResponse> register(String username, String password) {
        logger.info("회원가입 시도: 사용자명 '{}'", username);

        AuthRequest request = new AuthRequest(username, password);

        return apiService.postAsync("/api/auth/register", request, AuthResponse.class)
                .thenApply(response -> {
                    logger.info("회원가입 성공: 사용자명 '{}'", response.getUsername());
                    // 회원가입 성공 시 자동으로 토큰 저장
                    tokenManager.setAuthentication(response.getUsername(), response.getToken());
                    return response;
                })
                .exceptionally(throwable -> {
                    logger.error("회원가입 실패: 사용자명 '{}'", username, throwable);
                    throw new RuntimeException("회원가입에 실패했습니다: " + throwable.getMessage(), throwable);
                });
    }

    /**
     * 로그인
     */
    public CompletableFuture<AuthResponse> login(String username, String password) {
        logger.info("로그인 시도: 사용자명 '{}'", username);

        AuthRequest request = new AuthRequest(username, password);

        return apiService.postAsync("/api/auth/login", request, AuthResponse.class)
                .thenApply(response -> {
                    logger.info("로그인 성공: 사용자명 '{}'", response.getUsername());
                    // 로그인 성공 시 토큰 저장
                    tokenManager.setAuthentication(response.getUsername(), response.getToken());
                    return response;
                })
                .exceptionally(throwable -> {
                    logger.error("로그인 실패: 사용자명 '{}'", username, throwable);
                    throw new RuntimeException("로그인에 실패했습니다: " + throwable.getMessage(), throwable);
                });
    }

    /**
     * 로그아웃
     */
    public void logout() {
        String currentUser = tokenManager.getCurrentUsername();
        tokenManager.clearAuthentication();
        logger.info("로그아웃 완료: 사용자명 '{}'", currentUser);
    }

    /**
     * 현재 사용자가 로그인되어 있는지 확인
     */
    public boolean isLoggedIn() {
        return tokenManager.isAuthenticated();
    }

    /**
     * 현재 로그인된 사용자명 반환
     */
    public String getCurrentUsername() {
        return tokenManager.getCurrentUsername();
    }
}