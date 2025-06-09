package org.javateam11.ClassroomReservation.dto;

/**
 * 백엔드 API 에러 응답을 파싱하기 위한 DTO
 */
public class ErrorResponse {
    private String error;

    public ErrorResponse() {
    }

    public ErrorResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}