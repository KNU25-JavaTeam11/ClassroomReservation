package org.javateam11.ClassroomReservation.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 백엔드 API 응답에서 받아오는 예약 정보 DTO
 * API 응답 예시:
 * {
 * "id": 1,
 * "roomId": 4,
 * "studentId": "2024003159",
 * "date": "2025-06-10",
 * "startTime": "14:00",
 * "endTime": "17:00"
 * }
 */
public class ReservationDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("roomId")
    private Long roomId;

    @JsonProperty("studentId")
    private String studentId;

    @JsonProperty("date")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @JsonProperty("startTime")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonProperty("endTime")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    // 기본 생성자
    public ReservationDto() {
    }

    // 전체 필드를 받는 생성자
    public ReservationDto(Long id, Long roomId, String studentId, LocalDate date, LocalTime startTime,
            LocalTime endTime) {
        this.id = id;
        this.roomId = roomId;
        this.studentId = studentId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getter 메서드들
    public Long getId() {
        return id;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getStudentId() {
        return studentId;
    }

    public LocalDate getDate() {
        return date;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    // Setter 메서드들
    public void setId(Long id) {
        this.id = id;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "ReservationDto{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", studentId='" + studentId + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}