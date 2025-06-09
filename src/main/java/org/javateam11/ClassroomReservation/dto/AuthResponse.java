package org.javateam11.ClassroomReservation.dto;

/**
 * 로그인/회원가입 응답을 위한 DTO 클래스
 */
public class AuthResponse {
    private String username;
    private String token;

    public AuthResponse() {
    }

    public AuthResponse(String username, String token) {
        this.username = username;
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "username='" + username + '\'' +
                ", token='" + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null") + '\''
                +
                '}';
    }
}