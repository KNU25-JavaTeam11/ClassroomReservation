package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.view.MainView;
import org.javateam11.ClassroomReservation.view.ReservationDetailView;
import org.javateam11.ClassroomReservation.view.SignUpView;
import org.javateam11.ClassroomReservation.view.LoginView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * MainController는 MVC 패턴에서 Controller 역할을 담당하며,
 * 사용자 인터페이스(View)와 데이터(Model) 사이의 상호작용을 중재합니다.
 * 강의실 클릭 이벤트 처리 및 예약 로직을 관리합니다.
 * Spring 백엔드와의 비동기 통신을 지원합니다.
 */
public class MainController implements IMainController {
    private static final Logger logger = LoggerFactory.getLogger(MainController.class);

    // 메인 뷰 (UI)
    private MainView view;

    /**
     * MainController 생성자
     *
     * @param buildings 건물 리스트 (샘플 데이터 등)
     */
    public MainController(List<Building> buildings) {
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
     * Spring 백엔드를 통해 비동기로 예약을 처리합니다.
     *
     * @param classroom 클릭된 강의실 객체
     */
    public void onReserveClicked(Classroom classroom, ReservationDetailView detailView) {
        logger.info("강의실 예약 시도: {} ({}층 {}호)", classroom.getName(), classroom.getFloor(), classroom.getBuildingName());

        // 강의실 예약 뷰 띄우기
        view.showReservationView(classroom, detailView, view);
    }

    /**
     * 강의실 클릭 시 호출되는 메서드
     * 예약 상세정보 창을 띄웁니다.
     *
     * @param classroom 클릭된 강의실 객체
     */
    @Override
    public void onReservationClicked(Classroom classroom) {
        // MainView에서 roomIdMap을 가져와서 전달
        Map<String, Long> roomIdMap = view.getRoomIdMap();
        ReservationDetailView reservationDetailView = ControllerFactory.getInstance()
                .createReservationDetailView(classroom, this, roomIdMap);
        reservationDetailView.setVisible(true);
    }

    /**
     * 회원가입 버튼 클릭시 호출되는 메서드
     * 회원가입 창을 띄웁니다.
     */
    @Override
    public void onSignUpClicked() {
        SignUpView signUpView = ControllerFactory.getInstance().createSignUpView();
        signUpView.setVisible(true);
    }

    // 테스트용 login객체 생성 메서드
    @Override
    public void onLoginButtonClicked() {
        LoginView loginView = ControllerFactory.getInstance().createLoginView();
        loginView.setVisible(true);
    }
}