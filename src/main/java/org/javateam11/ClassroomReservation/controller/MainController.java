package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.service.ReservationService;
import org.javateam11.ClassroomReservation.view.MainView;
import org.javateam11.ClassroomReservation.view.ReservationDetailView;
import org.javateam11.ClassroomReservation.view.SignUpView;
import org.javateam11.ClassroomReservation.view.LoginView;

import org.javateam11.ClassroomReservation.view.MainView.ReservationHandler;

import javax.swing.*;
import java.util.List;

/**
 * MainController는 MVC 패턴에서 Controller 역할을 담당하며,
 * 사용자 인터페이스(View)와 데이터(Model) 사이의 상호작용을 중재합니다.
 * 강의실/시설물 클릭 이벤트 처리 및 예약 로직을 관리합니다.
 * Spring 백엔드와의 비동기 통신을 지원합니다.
 */
public class MainController {

    // 전체 건물 리스트
    private List<Building> buildings;

    // Spring 백엔드 연동 서비스
    private ReservationService reservationService;

    // 메인 뷰 (UI)
    private MainView view;

    /**
     * MainController 생성자
     *
     * @param buildings 건물 리스트 (샘플 데이터 등)
     */
    public MainController(List<Building> buildings) {
        this.buildings = buildings;
        this.reservationService = new ReservationService(); // Spring 백엔드 연동
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
    public void onRoomClicked(Classroom classroom, ReservationDetailView detailView) {
        // 강의실 예약 다이얼로그 띄우기, 예약 정보 받아오기
        view.showReservationDialog(classroom.getName(), (reserver, date, start, end) -> {
            Reservation reservation = new Reservation(reserver, date, start, end, classroom.getName());

            // 로딩 표시
            JDialog loadingDialog = showLoadingDialog("예약 처리 중...");

            // 먼저 예약 가능 여부 확인 (Spring 백엔드)
            reservationService.checkAvailability(
                    classroom.getBuildingName(),
                    classroom.getFloor(),
                    classroom.getName(),
                    date.toString(),
                    start + "-" + end,
                    // 성공 시 콜백
                    isAvailable -> {
                        loadingDialog.dispose();
                        if (isAvailable) {
                            // 예약 가능하면 예약 생성
                            createReservationOnServer(reservation, detailView);
                        } else {
                            JOptionPane.showMessageDialog(view, "이미 예약된 시간이므로 예약할 수 없습니다.");
                        }
                    },
                    // 오류 시 콜백
                    errorMessage -> {
                        loadingDialog.dispose();
                        JOptionPane.showMessageDialog(view,
                                "백엔드 서버 연결에 실패했습니다.\n" +
                                        "서버가 실행 중인지 확인해주세요.\n\n" +
                                        "오류: " + errorMessage,
                                "연결 오류",
                                JOptionPane.ERROR_MESSAGE);
                    });
        });
    }

    /**
     * 예약 상세정보 창에서 예약하기 버튼 클릭 시 호출되는 메서드
     * Spring 백엔드를 통해 비동기로 예약을 처리합니다.
     *
     * @param facility 클릭된 시설물 객체
     */
    public void onFacilityClicked(Facility facility, ReservationDetailView detailView) {
        // 시설물 예약 다이얼로그 띄우기, 예약 정보 받아오기
        view.showReservationDialog(facility.getName(), (reserver, date, start, end) -> {
            Reservation reservation = new Reservation(reserver, date, start, end, facility.getName());

            // 로딩 표시
            JDialog loadingDialog = showLoadingDialog("예약 처리 중...");

            // 먼저 예약 가능 여부 확인 (Spring 백엔드)
            reservationService.checkAvailability(
                    facility.getBuildingName(),
                    facility.getFloor(),
                    facility.getName(),
                    date.toString(),
                    start + "-" + end,
                    // 성공 시 콜백
                    isAvailable -> {
                        loadingDialog.dispose();
                        if (isAvailable) {
                            // 예약 가능하면 예약 생성
                            createReservationOnServer(reservation, detailView);
                        } else {
                            JOptionPane.showMessageDialog(view, "이미 예약된 시간이므로 예약할 수 없습니다.");
                        }
                    },
                    // 오류 시 콜백
                    errorMessage -> {
                        loadingDialog.dispose();
                        JOptionPane.showMessageDialog(view,
                                "백엔드 서버 연결에 실패했습니다.\n" +
                                        "서버가 실행 중인지 확인해주세요.\n\n" +
                                        "오류: " + errorMessage,
                                "연결 오류",
                                JOptionPane.ERROR_MESSAGE);
                    });
        });
    }

    /**
     * Spring 백엔드에 예약을 생성합니다.
     */
    private void createReservationOnServer(Reservation reservation, ReservationDetailView detailView) {
        JDialog loadingDialog = showLoadingDialog("예약 생성 중...");

        reservationService.createReservation(
                reservation,
                // 성공 시 콜백
                createdReservation -> {
                    loadingDialog.dispose();
                    JOptionPane.showMessageDialog(view, "예약이 완료되었습니다.");
                    detailView.dispose();
                },
                // 오류 시 콜백
                errorMessage -> {
                    loadingDialog.dispose();
                    JOptionPane.showMessageDialog(view,
                            "예약 생성에 실패했습니다.\n\n" +
                                    "오류: " + errorMessage,
                            "예약 실패",
                            JOptionPane.ERROR_MESSAGE);
                });
    }

    /**
     * 로딩 다이얼로그를 표시합니다.
     */
    private JDialog showLoadingDialog(String message) {
        JDialog dialog = new JDialog(view, "처리 중", true);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setLayout(new java.awt.BorderLayout());

        JLabel label = new JLabel(message, SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        dialog.add(label, java.awt.BorderLayout.CENTER);

        dialog.setSize(200, 100);
        dialog.setLocationRelativeTo(view);
        dialog.setVisible(true);

        return dialog;
    }

    /**
     * 강의실, 시설물 클릭 시 호출되는 메서드
     * 예약 상세정보 창을 띄웁니다.
     *
     * @param place 클릭된 시설물, 강의실 객체
     */
    public void onReservationClicked(Place place) {
        ReservationDetailView reservationDetailView = new ReservationDetailView(); // 먼저 생성
        ReservationDetailController reservationDetailController = new ReservationDetailController(
                place, this, reservationDetailView);
        reservationDetailView.setController(reservationDetailController); // setter로 controller 주입
        reservationDetailView.setVisible(true);
    }

    /**
     * 회원가입 버튼 클릭시 호출되는 메서드
     * 회원가입 창을 띄웁니다.
     */
    public void onSignUpClicked() {
        SignUpController signup_con = new SignUpController();
        SignUpView signup_view = new SignUpView(signup_con);
        signup_view.setVisible(true);
    }

    // 테스트용 login객체 생성 메서드
    public void onLoginButtonClicked() {
        LoginController logincontroller = new LoginController();
        LoginView loginView = new LoginView(logincontroller);
        loginView.setVisible(true);
    }
}