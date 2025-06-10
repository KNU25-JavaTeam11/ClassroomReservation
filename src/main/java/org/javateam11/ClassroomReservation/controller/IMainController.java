package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.model.*;

/**
 * Main 기능을 담당하는 컨트롤러의 인터페이스
 * MVC 패턴에서 View와 Controller 간의 결합도를 낮추기 위해 사용됩니다.
 */
public interface IMainController {
    /**
     * 강의실 클릭 시 호출되는 메서드
     */
    void onReservationClicked(Classroom classroom);

    /**
     * 회원가입 버튼 클릭시 호출되는 메서드
     */
    void onSignUpClicked();

    /**
     * 로그인 버튼 클릭시 호출되는 메서드
     */
    void onLoginButtonClicked();
}