package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.view.*;


/**
 * ReservationDetailController는 ReservationDetailView의 로직을 담당합니다.
 */
public class ReservationDetailController {
	private ReservationManager reservationManager;
	private Place place;
	private MainController mainController;
	private ReservationDetailView reservationDetailView;
	
	public ReservationDetailController(ReservationManager reservationManager, Place place, MainController maincontroller, ReservationDetailView reservationDetailView) {
		this.reservationManager = reservationManager;
		this.place = place;
		this.mainController = maincontroller;
		this.reservationDetailView = reservationDetailView;
	}
	
	/**
	 * 상세보기 창 title에서 각 시설/시설물들의 이름을 나타내기 위해 getter를 만듬.
	 * @return place객체의 name반환.
	 */
	public String getName() {
		return place.getName();
	}
	
	/**
	 * 시설/시설물 버튼 클릭 시 작동되는 메서드
	 * 클릭 시 그 객체가 시설/시설물인지 판단하여 시설이면 onRoomClicked, 시설물이면 onFacilityClicked 호출.
	 */
	public void onDetailReserveClicked() {
		if (place instanceof Classroom) {
			mainController.onRoomClicked((Classroom) place, reservationDetailView);
		}
		else {
			mainController.onFacilityClicked((Facility) place, reservationDetailView);
		}
	}
}
