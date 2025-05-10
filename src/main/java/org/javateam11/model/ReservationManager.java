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
     */
    public boolean isAvailable(Classroom classroom, Reservation newReservation) {
        // 강의실의 모든 기존 예약을 순회하면서
        for (Reservation r : classroom.getReservations()) {
            // 같은 날짜의 예약이 있는지 확인
            if (r.getDate().equals(newReservation.getDate())) {
                // 시간 겹침 여부 확인
                // 새로운 예약의 종료 시간이 기존 예약의 시작 시간보다 이전이거나
                // 새로운 예약의 시작 시간이 기존 예약의 종료 시간보다 이후인 경우가 아닌 경우
                // (즉, 시간이 겹치는 경우) 예약 불가능
                if (!(newReservation.getEndTime().isBefore(r.getStartTime()) || 
                     newReservation.getStartTime().isAfter(r.getEndTime()))) {
                    return false;
                }
            }
        }
        // 모든 기존 예약과 겹치지 않으면 예약 가능
        return true;
    }

    /**
     * 강의실에 예약을 추가하고 전체 예약 리스트에도 추가합니다.
     * @param classroom 강의실 객체
     * @param reservation 예약 정보
     */
    public void addReservation(Classroom classroom, Reservation reservation) {
        classroom.addReservation(reservation);
        reservations.add(reservation);
    }

    /**
     * 전체 예약 리스트를 반환합니다.
     * @return 예약 리스트
     */
    public List<Reservation> getReservations() {
        return reservations;
    }
} 