package org.javateam11.ClassroomReservation.model;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Reservation 클래스는 강의실/시설물의 예약 정보를 표현합니다.
 * 예약자, 날짜, 시작/종료 시간, 강의실/시설물 이름을 포함합니다.
 */
public class Reservation {

    // 예약자 이름
    private String reserver;

    // 예약 날짜
    private LocalDate date;

    // 예약 시작 시간
    private LocalTime startTime;

    // 예약 종료 시간
    private LocalTime endTime;

    // 예약 대상 강의실/시설물 이름
    private String classroomName;

    /**
     * Reservation 생성자
     * @param reserver 예약자 이름
     * @param date 예약 날짜
     * @param startTime 시작 시간
     * @param endTime 종료 시간
     * @param classroomName 예약 대상 강의실/시설물 이름
     */
    public Reservation(String reserver, LocalDate date, LocalTime startTime, LocalTime endTime, String classroomName) {
        this.reserver = reserver;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.classroomName = classroomName;
    }

    // 각 필드의 getter
    public String getReserver() { return reserver; }
    public LocalDate getDate() { return date; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public String getClassroomName() { return classroomName; }
} 