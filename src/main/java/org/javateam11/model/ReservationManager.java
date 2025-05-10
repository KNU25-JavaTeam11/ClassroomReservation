package org.javateam11.model;

import java.util.ArrayList;
import java.util.List;

/**
 * ReservationManager 클래스는 예약 관리 기능을 담당합니다.
 * 예약 가능 여부 확인, 예약 추가, 전체 예약 리스트 반환 기능을 제공합니다.
 */
public class ReservationManager {

    // 전체 예약 리스트
    private List<Reservation> reservations;

    /**
     * ReservationManager 생성자
     * 예약 리스트를 초기화합니다.
     */
    public ReservationManager() {
        this.reservations = new ArrayList<>();
    }

    /**
     * 해당 강의실이 특정 예약 시간에 예약 가능한지 확인합니다.
     * @param classroom 강의실 객체
     * @param newReservation 새로 시도하는 예약 정보
     * @return 예약 가능 여부 (true: 가능, false: 불가)
     *
     * TODO:[실습] 아래 함수 내용을 직접 구현해보세요.
     * - 같은 날짜에 시간이 겹치는 예약이 있으면 false, 아니면 true를 반환
     */
    public boolean isAvailable(Classroom classroom, Reservation newReservation) {
        // TODO: [실습] 구현: classroom.getReservations()를 순회하며 날짜/시간 겹침 여부 확인
        return false; // 임시 반환값 (실습 후 수정)
    }

    /**
     * 강의실에 예약을 추가하고 전체 예약 리스트에도 추가합니다.
     * @param classroom 강의실 객체
     * @param reservation 예약 정보
     *
     * TODO: [실습] 아래 함수 내용을 직접 구현해보세요.
     * - classroom에 예약 추가, 전체 리스트에도 추가
     */
    public void addReservation(Classroom classroom, Reservation reservation) {
        // TODO:[실습] 구현: classroom.addReservation, reservations.add 등
    }

    /**
     * 전체 예약 리스트를 반환합니다.
     * @return 예약 리스트
     */
    public List<Reservation> getReservations() {
        return reservations;
    }
} 