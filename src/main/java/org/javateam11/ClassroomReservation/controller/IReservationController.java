package org.javateam11.ClassroomReservation.controller;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * ReservationController 인터페이스
 * ReservationView와 Controller 간의 상호작용을 정의합니다.
 */
public interface IReservationController {

    /**
     * 예약 생성을 처리합니다.
     * 
     * @param date  예약 날짜
     * @param start 시작 시간
     * @param end   종료 시간
     */
    void createReservation(LocalDate date, LocalTime start, LocalTime end);

    /**
     * 강의실 이름을 반환합니다.
     * 
     * @return 강의실 이름
     */
    String getRoomName();
}