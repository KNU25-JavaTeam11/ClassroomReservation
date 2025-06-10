package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.model.Reservation;
import org.javateam11.ClassroomReservation.service.ReservationService;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class MyReservationView extends JFrame {
	private User currentUser;
	private List<Reservation> reservations;
	private DefaultTableModel tableModel;
	private ReservationService reservationService;

	// 내가 예약한 내역을 볼 수 있음
	public MyReservationView(User user) {
		// 현 사용자
		currentUser = user;
		reservations = new ArrayList<Reservation>();
		reservationService = new ReservationService();

		setTitle("내 예약");
		setSize(450, 500);
		setLocationRelativeTo(null);

		initializeUI();
		loadReservations();
	}

	private void initializeUI() {
		// JTable
		String[] header = { "이름", "강의실", "예약날짜", "예약시간" };
		tableModel = new DefaultTableModel(header, 0);
		JTable reservationTable = new JTable(tableModel);

		// 스크롤
		JScrollPane scrollpane = new JScrollPane(reservationTable);
		add(scrollpane);
	}

	private void loadReservations() {
		// 로딩 표시
		JLabel loadingLabel = new JLabel("예약 정보를 불러오는 중...", SwingConstants.CENTER);
		add(loadingLabel, BorderLayout.NORTH);

		// 모든 예약을 가져와서 현재 사용자의 예약만 필터링
		reservationService.getAllReservations(
				// 성공 시 콜백
				allReservations -> {
					remove(loadingLabel); // 로딩 라벨 제거

					// 현재 사용자의 예약만 필터링
					reservations.clear();
					for (Reservation reservation : allReservations) {
						if (reservation.getStudentId().equals(currentUser.getName())) {
							reservations.add(reservation);
						}
					}

					// 테이블 데이터 업데이트
					updateTable();
					revalidate();
					repaint();
				},
				// 오류 시 콜백
				errorMessage -> {
					remove(loadingLabel); // 로딩 라벨 제거
					JOptionPane.showMessageDialog(this,
							"예약 정보를 불러오는데 실패했습니다.\n" +
									"서버가 실행 중인지 확인해주세요.\n\n" +
									"오류: " + errorMessage,
							"연결 오류",
							JOptionPane.ERROR_MESSAGE);
					revalidate();
					repaint();
				});
	}

	private void updateTable() {
		// 기존 데이터 삭제
		tableModel.setRowCount(0);

		// 새 데이터 추가
		for (Reservation reservation : reservations) {
			String[] row = {
					reservation.getStudentId(),
					reservation.getRoomName(),
					reservation.getDate().toString(),
					reservation.getStartTime().toString() + " - " + reservation.getEndTime().toString()
			};
			tableModel.addRow(row);
		}
	}
}