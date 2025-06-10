package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.model.Classroom;
import org.javateam11.ClassroomReservation.model.Reservation;
import org.javateam11.ClassroomReservation.service.ReservationService;
import org.javateam11.ClassroomReservation.service.TokenManager;
import org.javateam11.ClassroomReservation.view.ReservationDetailView;
import org.javateam11.ClassroomReservation.view.ReservationView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

/**
 * ReservationController 구현체
 * 예약 생성 로직을 처리합니다.
 */
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    private final Classroom classroom;
    private final ReservationDetailView detailView;
    private final Component parentComponent;
    private ReservationView reservationView;
    private final ReservationService reservationService;
    private final Map<String, Long> roomIdMap;

    public ReservationController(Classroom classroom, ReservationDetailView detailView, Component parentComponent,
            Map<String, Long> roomIdMap) {
        this.classroom = classroom;
        this.detailView = detailView;
        this.parentComponent = parentComponent;
        this.roomIdMap = roomIdMap;
        this.reservationService = new ReservationService();
    }

    public void setReservationView(ReservationView reservationView) {
        this.reservationView = reservationView;
    }

    public void createReservation(LocalDate date, LocalTime start, LocalTime end) {
        logger.info("강의실 예약 시도: {} ({}층 {}호)", classroom.getName(), classroom.getFloor(), classroom.getBuildingName());

        // 현재 로그인한 사용자의 학번을 예약자로 사용
        String currentStudentId = TokenManager.getInstance().getCurrentStudentId();
        if (currentStudentId == null) {
            JOptionPane.showMessageDialog(parentComponent, "로그인이 필요합니다.", "인증 오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // roomId 조회
        Long roomId = null;
        if (roomIdMap != null) {
            roomId = roomIdMap.get(classroom.getName());
        }

        if (roomId == null) {
            logger.error("강의실 {}의 roomId를 찾을 수 없습니다", classroom.getName());
            JOptionPane.showMessageDialog(parentComponent,
                    "강의실 정보를 찾을 수 없습니다.\n새로고침 후 다시 시도해주세요.",
                    "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Reservation reservation = Reservation.forNewReservation(currentStudentId, date, start, end,
                classroom.getName()).withRoomId(roomId);
        logger.debug("서버에 예약 생성 요청: {} (roomId: {})", reservation.getRoomName(), reservation.getRoomId());

        reservationService.createReservation(
                reservation,
                // 성공 시 콜백
                createdReservation -> {
                    logger.info("예약 생성 성공: 예약자={}, 장소={}, 날짜={}, 시간={}-{}",
                            reservation.getStudentId(), reservation.getRoomName(),
                            reservation.getDate(), reservation.getStartTime(), reservation.getEndTime());
                    JOptionPane.showMessageDialog(parentComponent, "예약이 완료되었습니다.");

                    // 예약 뷰와 상세 뷰 닫기
                    if (reservationView != null) {
                        reservationView.dispose();
                    }
                    if (detailView != null) {
                        detailView.dispose();
                    }
                },
                // 오류 시 콜백
                errorMessage -> {
                    logger.error("예약 생성 실패: 장소={}, 예약자={}, 오류={}",
                            reservation.getRoomName(), reservation.getStudentId(), errorMessage);
                    JOptionPane.showMessageDialog(parentComponent,
                            "예약 생성에 실패했습니다.\n\n" +
                                    "오류: " + errorMessage,
                            "예약 실패",
                            JOptionPane.ERROR_MESSAGE);
                });
    }

    public String getRoomName() {
        return classroom.getName();
    }
}