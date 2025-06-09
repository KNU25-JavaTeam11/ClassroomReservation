package org.javateam11.ClassroomReservation.dto;

/**
 * 로그인/회원가입 요청을 위한 DTO 클래스
 */
public class LoginRequest {
    private String studentId;
    private String password;

    public LoginRequest(String studentId, String password) {
        this.studentId = studentId;
        this.password = password;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "LoginRequest{" +
                "studentId='" + studentId + '\'' +
                '}';
    }
}