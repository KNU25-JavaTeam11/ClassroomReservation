package org.javateam11.ClassroomReservation.dto;

/**
 * 회원가입 응답을 위한 DTO 클래스
 */
public class RegisterResponse {
    private String studentId;
    private String name;
    private String message;

    public RegisterResponse() {
    }

    public RegisterResponse(String studentId, String name, String message) {
        this.studentId = studentId;
        this.name = name;
        this.message = message;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "RegisterResponse{" +
                "studentId='" + studentId + '\'' +
                ", name='" + name + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}