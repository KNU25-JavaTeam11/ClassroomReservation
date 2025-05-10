package org.javateam11.controller;

import org.javateam11.model.*;
import org.javateam11.view.MainView;
import javax.swing.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * MainController는 MVC 패턴에서 Controller 역할을 담당하며,
 * 사용자 인터페이스(View)와 데이터(Model) 사이의 상호작용을 중재합니다.
 * 강의실/시설물 클릭 이벤트 처리 및 예약 로직을 관리합니다.
 */
public class MainController {

    // 전체 건물 리스트
    private List<Building> buildings;

    // 예약 관리 객체 (예약 가능 여부, 예약 추가 등)
    private ReservationManager reservationManager;

    // 메인 뷰 (UI)
    private MainView view;

    /**
     * MainController 생성자
     * @param buildings 건물 리스트 (샘플 데이터 등)
     */
    public MainController(List<Building> buildings) {
        this.buildings = buildings;
        this.reservationManager = new ReservationManager();
        // MainView에 자신(this)을 넘겨 이벤트 콜백을 연결
        this.view = new MainView(this, buildings);
    }

    /**
     * 메인 뷰를 화면에 표시합니다.
     */
    public void show() {
        view.setVisible(true);
    }

    /**
     * 강의실 버튼 클릭 시 호출되는 메서드
     * 예약 다이얼로그를 띄우고, 예약 가능 여부를 확인 후 처리합니다.
     * @param classroom 클릭된 강의실 객체
     */
    public void onRoomClicked(Classroom classroom) {
        view.showReservationDialog(classroom.getName(), (reserver, date, start, end) -> {
            Reservation reservation = new Reservation(reserver, date, start, end, classroom.getName());
            if (reservationManager.isAvailable(classroom, reservation)) {
                reservationManager.addReservation(classroom, reservation);
                classroom.setAvailable(false);
                JOptionPane.showMessageDialog(view, "예약이 완료되었습니다.");
                view.repaint();
            } else {
                JOptionPane.showMessageDialog(view, "해당 시간에 이미 예약이 있습니다.");
            }
        });
    }

    /**
     * 시설물 버튼 클릭 시 호출되는 메서드
     * 예약 다이얼로그를 띄우고, 예약 가능 여부를 확인 후 처리합니다.
     * @param facility 클릭된 시설물 객체
     */
    public void onFacilityClicked(Facility facility) {
        view.showReservationDialog(facility.getName(), (reserver, date, start, end) -> {
            Reservation reservation = new Reservation(reserver, date, start, end, facility.getName());
            // 시설물 예약 로직은 강의실과 동일하게 처리
            if (facility.isAvailable()) {
                facility.addReservation(reservation);
                facility.setAvailable(false);
                JOptionPane.showMessageDialog(view, "예약이 완료되었습니다.");
                view.repaint();
            } else {
                JOptionPane.showMessageDialog(view, "해당 시간에 이미 예약이 있습니다.");
            }
        });
    }
} 