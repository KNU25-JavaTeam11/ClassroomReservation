package org.javateam11.ClassroomReservation.dto;

/**
 * 로그인/회원가입 요청을 위한 DTO 클래스
 */
public class RegisterRequest {
    private String studentId;
    private String password;
    private String name;

    public RegisterRequest(String studentId, String name, String password) {
        this.studentId = studentId;
        this.name = name;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "RegisterRequest{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}