package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.view.*;


/**
 * ReservationDetailController는 ReservationDetailView의 로직을 담당합니다.
 */
public class ReservationDetailController {
	private ReservationManager RM;
	private Place place;
	private MainController MC;
	private ReservationDetailView RDview;
	
	public ReservationDetailController(ReservationManager RM, Place place, MainController maincontroller, ReservationDetailView RDview) {
		this.RM = RM;
		this.place = place;
		this.MC = maincontroller;
		this.RDview = RDview;
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
			MC.onRoomClicked((Classroom) place, RDview);
		}
		else {
			MC.onFacilityClicked((Facility) place, RDview);
		}
	}
}
