package org.javateam11.ClassroomReservation.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Reservation 클래스는 강의실의 예약 정보를 표현합니다.
 * Builder 패턴을 사용하여 깔끔한 객체 생성을 지원합니다.
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
     * Builder를 통해서만 생성 가능하도록 private 생성자
     */
    private Reservation() {
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

    // Setter 메서드들 (Builder 내부에서만 사용)
    private void setId(Long id) {
        this.id = id;
    }

    private void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    private void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    private void setDate(LocalDate date) {
        this.date = date;
    }

    private void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    private void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    private void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    private void setReservedBy(User reservedBy) {
        this.reservedBy = reservedBy;
    }

    /**
     * Builder 패턴을 위한 정적 팩토리 메서드
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * 새 예약 생성을 위한 팩토리 메서드 (MainController에서 사용)
     * roomId는 나중에 설정됨
     */
    public static Reservation forNewReservation(String studentId, LocalDate date,
            LocalTime startTime, LocalTime endTime, String roomName) {
        return builder()
                .studentId(studentId)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .roomName(roomName)
                .build();
    }

    /**
     * ReservationDto를 Reservation으로 변환하는 팩토리 메서드
     * 백엔드 API 응답을 로컬 모델로 변환할 때 사용
     */
    public static Reservation fromDto(org.javateam11.ClassroomReservation.dto.ReservationDto dto, String roomName) {
        return builder()
                .id(dto.getId())
                .roomId(dto.getRoomId())
                .studentId(dto.getStudentId())
                .date(dto.getDate())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .roomName(roomName)
                .build();
    }

    /**
     * 완전한 예약 정보를 가진 객체 생성을 위한 팩토리 메서드
     */
    public static Reservation withCompleteInfo(Long id, Long roomId, String studentId,
            LocalDate date, LocalTime startTime, LocalTime endTime,
            String roomName, User reservedBy) {
        return builder()
                .id(id)
                .roomId(roomId)
                .studentId(studentId)
                .date(date)
                .startTime(startTime)
                .endTime(endTime)
                .roomName(roomName)
                .reservedBy(reservedBy)
                .build();
    }

    /**
     * 기존 예약을 복사하면서 일부 정보만 변경하는 팩토리 메서드
     */
    public static Reservation copyFrom(Reservation source) {
        return builder()
                .id(source.id)
                .roomId(source.roomId)
                .studentId(source.studentId)
                .date(source.date)
                .startTime(source.startTime)
                .endTime(source.endTime)
                .roomName(source.roomName)
                .reservedBy(source.reservedBy)
                .build();
    }

    /**
     * roomId를 업데이트한 새 인스턴스를 반환
     */
    public Reservation withRoomId(Long roomId) {
        return builder()
                .from(this)
                .roomId(roomId)
                .build();
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

    /**
     * Reservation Builder 클래스
     * 필수 필드와 선택적 필드를 명확하게 구분하여 안전한 객체 생성을 지원
     */
    public static class Builder {
        private final Reservation reservation;

        private Builder() {
            this.reservation = new Reservation();
        }

        /**
         * 기존 예약 정보로부터 빌더를 초기화
         */
        public Builder from(Reservation source) {
            reservation.setId(source.id);
            reservation.setRoomId(source.roomId);
            reservation.setStudentId(source.studentId);
            reservation.setDate(source.date);
            reservation.setStartTime(source.startTime);
            reservation.setEndTime(source.endTime);
            reservation.setRoomName(source.roomName);
            reservation.setReservedBy(source.reservedBy);
            return this;
        }

        public Builder id(Long id) {
            reservation.setId(id);
            return this;
        }

        public Builder roomId(Long roomId) {
            reservation.setRoomId(roomId);
            return this;
        }

        public Builder studentId(String studentId) {
            reservation.setStudentId(studentId);
            return this;
        }

        public Builder date(LocalDate date) {
            reservation.setDate(date);
            return this;
        }

        public Builder startTime(LocalTime startTime) {
            reservation.setStartTime(startTime);
            return this;
        }

        public Builder endTime(LocalTime endTime) {
            reservation.setEndTime(endTime);
            return this;
        }

        public Builder roomName(String roomName) {
            reservation.setRoomName(roomName);
            return this;
        }

        public Builder reservedBy(User reservedBy) {
            reservation.setReservedBy(reservedBy);
            return this;
        }

        /**
         * 예약 시간을 한번에 설정
         */
        public Builder timeSlot(LocalTime startTime, LocalTime endTime) {
            reservation.setStartTime(startTime);
            reservation.setEndTime(endTime);
            return this;
        }

        /**
         * 기본적인 예약 정보를 한번에 설정 (새 예약 생성용)
         */
        public Builder basicReservationInfo(String studentId, LocalDate date,
                LocalTime startTime, LocalTime endTime, String roomName) {
            reservation.setStudentId(studentId);
            reservation.setDate(date);
            reservation.setStartTime(startTime);
            reservation.setEndTime(endTime);
            reservation.setRoomName(roomName);
            return this;
        }

        /**
         * 빌드 전 유효성 검사를 수행하고 Reservation 객체를 생성
         */
        public Reservation build() {
            validateRequiredFields();
            return reservation;
        }

        /**
         * 필수 필드가 설정되었는지 확인
         */
        private void validateRequiredFields() {
            if (reservation.studentId == null || reservation.studentId.trim().isEmpty()) {
                throw new IllegalStateException("학번(studentId)은 필수입니다.");
            }
            if (reservation.date == null) {
                throw new IllegalStateException("예약 날짜(date)는 필수입니다.");
            }
            if (reservation.startTime == null) {
                throw new IllegalStateException("시작 시간(startTime)은 필수입니다.");
            }
            if (reservation.endTime == null) {
                throw new IllegalStateException("종료 시간(endTime)은 필수입니다.");
            }
            if (reservation.roomName == null || reservation.roomName.trim().isEmpty()) {
                throw new IllegalStateException("강의실 이름(roomName)은 필수입니다.");
            }
            if (reservation.startTime.isAfter(reservation.endTime) ||
                    reservation.startTime.equals(reservation.endTime)) {
                throw new IllegalStateException("시작 시간은 종료 시간보다 이전이어야 합니다.");
            }
        }
    }
}