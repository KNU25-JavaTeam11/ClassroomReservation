package org.javateam11.ClassroomReservation.dto;

/**
 * 로그인 응답을 위한 DTO 클래스
 */
public class LoginResponse {
    private String studentId;
    private String name;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(String studentId, String name, String token) {
        this.studentId = studentId;
        this.name = name;
        this.token = token;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "studentId='" + studentId + '\'' +
                ", token='" + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null") + '\''
                +
                '}';
    }
}