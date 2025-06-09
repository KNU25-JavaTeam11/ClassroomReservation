package org.javateam11.ClassroomReservation.controller;

/**
 * ReservationDetail 기능을 담당하는 컨트롤러의 인터페이스
 * MVC 패턴에서 View와 Controller 간의 결합도를 낮추기 위해 사용됩니다.
 */
public interface IReservationDetailController {
    /**
     * 시설/시설물의 이름을 반환합니다.
     */
    String getName();

    /**
     * 예약하기 버튼 클릭 시 호출되는 메서드
     */
    void onDetailReserveClicked();
}