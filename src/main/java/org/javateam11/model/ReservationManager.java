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


    public boolean isAvailable(Place place, Reservation newReservation) {

        for (Reservation existing : place.getReservations()) {
            //날짜 같은지 판단
            if (existing.getDate().isEqual(newReservation.getDate())) {
                //시간이 겹치는지 판단
                if (!(newReservation.getEndTime().isBefore(existing.getStartTime()) ||
                        newReservation.getStartTime().isAfter(existing.getEndTime())))
                {return false; }
            }
        }
        return true;
    }

    //강의실 업 캐스팅
    public boolean isAvailable(Classroom classroom, Reservation newReservation) {
        return isAvailable((Place) classroom, newReservation);
    }

    //시설물 업 캐스팅
    public boolean isAvailable(Facility facility, Reservation newReservation) {
        return isAvailable((Place) facility, newReservation);
    }

    /**
     * 강의실에 예약을 추가하고 전체 예약 리스트에도 추가합니다.
     * @param classroom 강의실 객체
     * @param reservation 예약 정보
     *
     * TODO: [실습] 아래 함수 내용을 직접 구현해보세요.
     * - classroom에 예약 추가, 전체 리스트에도 추가
     */

    //예약추가
    public void addReservation(Place place, Reservation reservation) {
        place.addReservation(reservation);
        reservations.add(reservation);
    }

    //강의실
    public void addReservation(Classroom classroom, Reservation reservation) {
        addReservation((Place) classroom, reservation);
    }

    //시설물
    public void addReservation(Facility facility, Reservation reservation) {
        addReservation((Place) facility, reservation);
    }

    /**
     * 전체 예약 리스트를 반환합니다.
     * @return 예약 리스트
     */
    public List<Reservation> getReservations() {
        return reservations;
    }
} 