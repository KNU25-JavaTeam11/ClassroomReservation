package org.javateam11.ClassroomReservation.config;

/**
 * 애플리케이션 전역 설정을 관리하는 클래스
 */
public class ApplicationConfig {

    // API 관련 설정
    private static final String DEFAULT_API_BASE_URL = "http://localhost:8080";
    private static final int DEFAULT_CONNECT_TIMEOUT_SECONDS = 10;
    private static final int DEFAULT_READ_TIMEOUT_SECONDS = 30;
    private static final int DEFAULT_WRITE_TIMEOUT_SECONDS = 30;

    // UI 관련 설정
    private static final int DEFAULT_AUTO_REFRESH_INTERVAL_SECONDS = 30;
    private static final int DEFAULT_MAIN_WINDOW_WIDTH = 1200;
    private static final int DEFAULT_MAIN_WINDOW_HEIGHT = 800;

    /**
     * API 베이스 URL 반환
     */
    public static String getApiBaseUrl() {
        return System.getProperty("api.base.url", DEFAULT_API_BASE_URL);
    }

    /**
     * 연결 타임아웃 시간(초) 반환
     */
    public static int getConnectTimeoutSeconds() {
        String timeoutStr = System.getProperty("api.connect.timeout");
        if (timeoutStr != null) {
            try {
                return Integer.parseInt(timeoutStr);
            } catch (NumberFormatException e) {
                return DEFAULT_CONNECT_TIMEOUT_SECONDS;
            }
        }
        return DEFAULT_CONNECT_TIMEOUT_SECONDS;
    }

    /**
     * 읽기 타임아웃 시간(초) 반환
     */
    public static int getReadTimeoutSeconds() {
        return DEFAULT_READ_TIMEOUT_SECONDS;
    }

    /**
     * 쓰기 타임아웃 시간(초) 반환
     */
    public static int getWriteTimeoutSeconds() {
        return DEFAULT_WRITE_TIMEOUT_SECONDS;
    }

    /**
     * 자동 새로고침 간격(초) 반환
     */
    public static int getAutoRefreshIntervalSeconds() {
        return DEFAULT_AUTO_REFRESH_INTERVAL_SECONDS;
    }

    /**
     * 메인 윈도우 기본 너비 반환
     */
    public static int getMainWindowWidth() {
        return DEFAULT_MAIN_WINDOW_WIDTH;
    }

    /**
     * 메인 윈도우 기본 높이 반환
     */
    public static int getMainWindowHeight() {
        return DEFAULT_MAIN_WINDOW_HEIGHT;
    }

    private ApplicationConfig() {
        // 유틸리티 클래스이므로 인스턴스 생성 방지
    }
}