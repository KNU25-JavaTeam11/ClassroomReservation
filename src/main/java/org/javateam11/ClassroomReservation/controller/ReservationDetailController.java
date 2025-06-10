package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.view.*;

/**
 * ReservationDetailView의 로직을 담당하는 컨트롤러
 */
public class ReservationDetailController {
	private Classroom classroom;
	private MainController mainController;
	private ReservationDetailView reservationDetailView;

	public ReservationDetailController(Classroom classroom, MainController maincontroller,
			ReservationDetailView reservationDetailView) {
		this.classroom = classroom;
		this.mainController = maincontroller;
		this.reservationDetailView = reservationDetailView;
	}

	/**
	 * 상세보기 창 title에서 강의실 이름을 나타내기 위해 getter를 만듬.
	 * 
	 * @return classroom 객체의 name 반환.
	 */
	public String getName() {
		return classroom.getName();
	}

	/**
	 * 건물 이름을 반환합니다.
	 * 
	 * @return classroom 객체의 building 반환.
	 */
	public String getBuildingName() {
		return classroom.getBuildingName();
	}

	/**
	 * 강의실 버튼 클릭 시 작동되는 메서드
	 * 클릭 시 강의실 예약 처리를 위해 onRoomClicked 호출.
	 */
	public void onDetailReserveClicked() {
		mainController.onReserveClicked(classroom, reservationDetailView);
	}
}
