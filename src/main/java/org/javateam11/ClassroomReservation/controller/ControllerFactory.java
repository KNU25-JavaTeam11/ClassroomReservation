package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.view.*;

import java.util.List;
import java.util.Map;

/**
 * ControllerFactory는 Controller 객체들의 생성과 의존성 주입을 담당합니다.
 * MVC 패턴을 개선하여 View가 Controller를 직접 생성하지 않도록 합니다.
 */
public class ControllerFactory {
    private static ControllerFactory instance;

    private ControllerFactory() {
    }

    public static ControllerFactory getInstance() {
        if (instance == null) {
            instance = new ControllerFactory();
        }
        return instance;
    }

    /**
     * MainController를 생성합니다.
     */
    public MainController createMainController(List<Building> buildings) {
        return new MainController(buildings);
    }

    /**
     * SignUpController와 SignUpView를 생성하고 연결합니다.
     */
    public SignUpView createSignUpView() {
        SignUpController controller = new SignUpController();
        SignUpView view = new SignUpView(controller);
        controller.setSignUpView(view);
        return view;
    }

    /**
     * ReservationDetailController와 ReservationDetailView를 생성하고 연결합니다.
     */
    public ReservationDetailView createReservationDetailView(Classroom classroom, MainController mainController,
            Map<String, Long> roomIdMap) {
        ReservationDetailView view = new ReservationDetailView();
        ReservationDetailController controller = new ReservationDetailController(classroom, mainController, view);
        view.setController(controller);

        // roomId 설정 (API 호출 시 필요)
        if (roomIdMap != null && roomIdMap.containsKey(classroom.getName())) {
            Long roomId = roomIdMap.get(classroom.getName());
            view.setRoomId(roomId);
        }

        return view;
    }

    /**
     * LoginView를 생성합니다.
     */
    public LoginView createLoginView() {
        return new LoginView();
    }

    /**
     * MyReservationView를 생성합니다.
     */
    public MyReservationView createMyReservationView(User user) {
        return new MyReservationView(user);
    }

    /**
     * MyInformationView를 생성합니다.
     */
    public MyInformationView createMyInformationView(User user) {
        return new MyInformationView(user);
    }
}