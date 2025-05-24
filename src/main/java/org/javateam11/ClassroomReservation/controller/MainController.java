package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.view.MainView;
import org.javateam11.ClassroomReservation.view.ReservationDetailView;

import org.javateam11.ClassroomReservation.view.MainView.ReservationHandler;

import javax.swing.*;
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
     * 예약 상세정보 창에서 예약하기 버튼 클릭 시 호출되는 메서드
     * 예약 다이얼로그를 띄우고, 예약 가능 여부를 확인 후 처리합니다.
     * @param classroom 클릭된 강의실 객체
     */
    public void onRoomClicked(Classroom classroom, ReservationDetailView detailView) {
    	//강의실 예약 다이얼로그 띄우기, 예약 정보 받아오기
    	view.showReservationDialog(classroom.getName(), (reserver, date, start, end) -> {
    		Reservation reservation = new Reservation(reserver, date, start, end, classroom.getName());
    		//예약 가능여부 확인
    		if (reservationManager.isAvailable(classroom, reservation)) { //예약가능
    			//예약하기
    			reservationManager.addReservation(classroom, reservation);
    			JOptionPane.showMessageDialog(view,  "예약이 완료되었습니다.");
    			detailView.dispose();
    		}
    		else { //예약불가
    			JOptionPane.showMessageDialog(view,  "이미 예약된 시간이므로 예약할 수 없습니다.");
    		}
    	});
    }

    /**
     * 예약 상세정보 창에서 예약하기 버튼 클릭 시 호출되는 메서드
     * 예약 다이얼로그를 띄우고, 예약 가능 여부를 확인 후 처리합니다.
     * @param facility 클릭된 시설물 객체
     */
    public void onFacilityClicked(Facility facility, ReservationDetailView detailView) {
    	//강의실 예약 다이얼로그 띄우기, 예약 정보 받아오기
    	view.showReservationDialog(facility.getName(), (reserver, date, start, end) -> {
    		Reservation reservation = new Reservation(reserver, date, start, end, facility.getName());
    		//예약 가능여부 확인
    		if (reservationManager.isAvailable(facility, reservation)) { //예약가능
    			//예약하기
    			reservationManager.addReservation(facility, reservation);
    			JOptionPane.showMessageDialog(view,  "예약이 완료되었습니다.");
    			detailView.dispose();
    		}
    		else { //예약불가
    			JOptionPane.showMessageDialog(view,  "이미 예약된 시간이므로 예약할 수 없습니다.");
    		}
    	});
    }
    
    /**
     * 강의실, 시설물 클릭 시 호출되는 메서드
     * 예약 상세정보 창을 띄웁니다.
     * @param place 클릭된 시설물, 강의실 객체
     */
    public void onReservationClicked(Place place) {
        ReservationDetailView RDview = new ReservationDetailView(); // 먼저 생성
        ReservationDetailController RDcontroller = new ReservationDetailController(reservationManager, place, this, RDview);
        RDview.setController(RDcontroller); // setter로 controller 주입
        RDview.setVisible(true);
    }

} 