package org.javateam11.ClassroomReservation.service;

import org.javateam11.ClassroomReservation.dto.AuthResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AuthService 사용 예제를 보여주는 클래스
 * 실제 사용 시에는 GUI 컴포넌트에서 호출하게 됩니다.
 */
public class AuthServiceExample {
    private static final Logger logger = LoggerFactory.getLogger(AuthServiceExample.class);

    public static void main(String[] args) {
        AuthService authService = new AuthService();

        // 사용 예제
        demonstrateAuthFlow(authService);
    }

    private static void demonstrateAuthFlow(AuthService authService) {
        try {
            // 1. 회원가입 예제
            logger.info("=== 회원가입 시도 ===");
            authService.register("testuser", "testpass")
                    .thenAccept(response -> {
                        logger.info("회원가입 성공! 사용자: {}", response.getUsername());
                        logger.info("현재 로그인 상태: {}", authService.isLoggedIn());
                    })
                    .exceptionally(throwable -> {
                        logger.error("회원가입 실패: {}", throwable.getMessage());
                        return null;
                    })
                    .join(); // 동기적으로 대기

            Thread.sleep(1000);

            // 2. 로그아웃
            logger.info("=== 로그아웃 ===");
            authService.logout();
            logger.info("로그아웃 후 로그인 상태: {}", authService.isLoggedIn());

            Thread.sleep(1000);

            // 3. 로그인 예제
            logger.info("=== 로그인 시도 ===");
            authService.login("testuser", "testpass")
                    .thenAccept(response -> {
                        logger.info("로그인 성공! 사용자: {}", response.getUsername());
                        logger.info("현재 사용자: {}", authService.getCurrentUsername());
                    })
                    .exceptionally(throwable -> {
                        logger.error("로그인 실패: {}", throwable.getMessage());
                        return null;
                    })
                    .join(); // 동기적으로 대기

            // 4. 인증된 API 호출 예제 (실제 사용할 때)
            demonstrateAuthenticatedApiCall();

        } catch (Exception e) {
            logger.error("예제 실행 중 오류 발생", e);
        }
    }

    private static void demonstrateAuthenticatedApiCall() {
        logger.info("=== 인증된 API 호출 예제 ===");

        ApiService apiService = new ApiService();

        // 인증이 필요한 API 호출 예제
        // 예: 사용자의 예약 목록 조회
        /*
         * apiService.getAuthenticatedAsync("/api/reservations/my",
         * ReservationListResponse.class)
         * .thenAccept(reservations -> {
         * logger.info("내 예약 목록 조회 성공: {} 개", reservations.size());
         * })
         * .exceptionally(throwable -> {
         * logger.error("예약 목록 조회 실패: {}", throwable.getMessage());
         * return null;
         * });
         */

        logger.info("실제 API 엔드포인트가 준비되면 위의 주석을 해제하여 사용하세요.");
    }
}