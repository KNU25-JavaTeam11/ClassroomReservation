package org.javateam11.ClassroomReservation.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JWT 토큰을 관리하는 싱글톤 클래스
 * 애플리케이션 전반에서 사용자 인증 상태를 관리
 */
public class TokenManager {
    private static final Logger logger = LoggerFactory.getLogger(TokenManager.class);
    private static TokenManager instance;

    private String currentToken;
    private String currentStudentId;
    private String currentName;

    private TokenManager() {
    }

    public static synchronized TokenManager getInstance() {
        if (instance == null) {
            instance = new TokenManager();
        }
        return instance;
    }

    /**
     * 토큰과 사용자 전체 정보를 저장
     */
    public void setAuthentication(String studentId, String name, String token) {
        this.currentStudentId = studentId;
        this.currentName = name;
        this.currentToken = token;
        logger.info("사용자 '{}({})'의 인증 정보가 저장되었습니다.", name, studentId);
    }

    /**
     * 현재 JWT 토큰 반환
     */
    public String getToken() {
        return currentToken;
    }

    /**
     * 현재 로그인된 사용자명 반환
     */
    public String getCurrentName() {
        return currentName;
    }

    /**
     * 현재 로그인된 사용자의 학번 반환
     */
    public String getCurrentStudentId() {
        return currentStudentId;
    }

    /**
     * 사용자가 로그인되어 있는지 확인
     */
    public boolean isAuthenticated() {
        return currentToken != null && !currentToken.trim().isEmpty();
    }

    /**
     * 인증 정보 초기화 (로그아웃)
     */
    public void clearAuthentication() {
        String prevName = currentName;
        this.currentToken = null;
        this.currentStudentId = null;
        this.currentName = null;
        logger.info("사용자 '{}' 인증 정보가 초기화되었습니다.", prevName);
    }

    /**
     * Authorization 헤더 값 생성
     */
    public String getAuthorizationHeader() {
        if (!isAuthenticated()) {
            throw new IllegalStateException("인증되지 않은 상태입니다.");
        }
        return "Bearer " + currentToken;
    }
}