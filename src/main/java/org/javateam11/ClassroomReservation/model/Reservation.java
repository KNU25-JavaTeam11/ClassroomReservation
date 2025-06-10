package org.javateam11.ClassroomReservation.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Reservation 클래스는 강의실/시설물의 예약 정보를 표현합니다.
 * ReservationDto와 필드명을 맞춘 로컬 모델 클래스입니다.
 */
public class Reservation {

    // 예약 ID (백엔드에서 생성)
    private Long id;

    // 강의실 ID
    private Long roomId;

    // 학번 (예약자)
    private String studentId;

    // 예약 날짜
    private LocalDate date;

    // 예약 시작 시간
    private LocalTime startTime;

    // 예약 종료 시간
    private LocalTime endTime;

    // 강의실 이름 (UI 표시용, DTO에는 없지만 필요)
    private String roomName;

    // 예약자 User 객체 (로그인 정보)
    private User reservedBy;

    /**
     * 기본 생성자
     */
    public Reservation() {
    }

    /**
     * 전체 필드 생성자
     * 
     * @param id        예약 ID
     * @param roomId    강의실 ID
     * @param studentId 학번
     * @param date      예약 날짜
     * @param startTime 시작 시간
     * @param endTime   종료 시간
     * @param roomName  강의실 이름
     */
    public Reservation(Long id, Long roomId, String studentId, LocalDate date, LocalTime startTime, LocalTime endTime,
            String roomName) {
        this.id = id;
        this.roomId = roomId;
        this.studentId = studentId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.reservedBy = null;
    }

    /**
     * ID 없는 생성자 (새 예약 생성 시)
     */
    public Reservation(Long roomId, String studentId, LocalDate date, LocalTime startTime, LocalTime endTime,
            String roomName) {
        this.roomId = roomId;
        this.studentId = studentId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.reservedBy = null;
    }

    /**
     * User 정보를 포함한 생성자
     */
    public Reservation(Long roomId, String studentId, LocalDate date, LocalTime startTime, LocalTime endTime,
            String roomName, User reservedBy) {
        this.roomId = roomId;
        this.studentId = studentId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.reservedBy = reservedBy;
    }

    /**
     * MainController에서 사용하는 생성자 (roomId 없이)
     * (String studentId, LocalDate date, LocalTime startTime, LocalTime endTime,
     * String roomName) 순서
     */
    public Reservation(String studentId, LocalDate date, LocalTime startTime, LocalTime endTime, String roomName) {
        this.roomId = null; // 추후 설정
        this.studentId = studentId;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.roomName = roomName;
        this.reservedBy = null;
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

    public String getRoomName() {
        return roomName;
    }

    public User getReservedBy() {
        return reservedBy;
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

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setReservedBy(User reservedBy) {
        this.reservedBy = reservedBy;
    }

    // 기존 호환성을 위한 메서드들
    public String getReserver() {
        return studentId;
    }

    public String getClassroomName() {
        return roomName;
    }

    public String getPlaceName() {
        return roomName;
    }

    /**
     * ReservationDto를 Reservation으로 변환하는 정적 메서드
     * 백엔드 API 응답을 로컬 모델로 변환할 때 사용
     */
    public static Reservation fromDto(org.javateam11.ClassroomReservation.dto.ReservationDto dto, String roomName) {
        return new Reservation(
                dto.getId(),
                dto.getRoomId(),
                dto.getStudentId(),
                dto.getDate(),
                dto.getStartTime(),
                dto.getEndTime(),
                roomName);
    }

    /**
     * Reservation을 ReservationDto로 변환하는 메서드
     * 백엔드 API 호출 시 사용
     */
    public org.javateam11.ClassroomReservation.dto.ReservationDto toDto() {
        org.javateam11.ClassroomReservation.dto.ReservationDto dto = new org.javateam11.ClassroomReservation.dto.ReservationDto();
        dto.setId(this.id);
        dto.setRoomId(this.roomId);
        dto.setStudentId(this.studentId);
        dto.setDate(this.date);
        dto.setStartTime(this.startTime);
        dto.setEndTime(this.endTime);
        return dto;
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", roomId=" + roomId +
                ", studentId='" + studentId + '\'' +
                ", date=" + date +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", roomName='" + roomName + '\'' +
                '}';
    }
}