package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.model.Reservation;
import org.javateam11.ClassroomReservation.service.ReservationService;
import org.javateam11.ClassroomReservation.view.components.StyleManager;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

public class MyReservationView extends JFrame {
	private User currentUser;
	private List<Reservation> reservations;
	private DefaultTableModel tableModel;
	private ReservationService reservationService;
	private JTable reservationTable;
	private JButton refreshButton;
	private JButton cancelButton;
	private JLabel statusLabel;

	// 내가 예약한 내역을 볼 수 있음
	public MyReservationView(User user) {
		// 현 사용자
		currentUser = user;
		reservations = new ArrayList<Reservation>();
		reservationService = new ReservationService();

		setTitle("내 예약 관리");
		setSize(700, 600);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		initializeUI();
		loadReservations();
	}

	private void initializeUI() {
		setLayout(new BorderLayout());
		getContentPane().setBackground(StyleManager.getBackgroundColor());

		// 상단 패널 (제목 및 새로고침 버튼)
		JPanel topPanel = createTopPanel();
		add(topPanel, BorderLayout.NORTH);

		// 중앙 패널 (테이블)
		JPanel centerPanel = createCenterPanel();
		add(centerPanel, BorderLayout.CENTER);

		// 하단 패널 (버튼들과 상태 표시)
		JPanel bottomPanel = createBottomPanel();
		add(bottomPanel, BorderLayout.SOUTH);
	}

	private JPanel createTopPanel() {
		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.setBackground(StyleManager.getTopbarColor());
		topPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

		// 제목 레이블
		JLabel titleLabel = new JLabel("내 예약 관리");
		titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		titleLabel.setForeground(StyleManager.getTextColor());
		topPanel.add(titleLabel, BorderLayout.WEST);

		// 새로고침 버튼
		refreshButton = StyleManager.createStyledButton("새로고침", StyleManager.getPrimaryColor());
		refreshButton.addActionListener(e -> {
			refreshButton.setEnabled(false);
			loadReservations();
		});
		topPanel.add(refreshButton, BorderLayout.EAST);

		return topPanel;
	}

	private JPanel createCenterPanel() {
		JPanel centerPanel = new JPanel(new BorderLayout());
		centerPanel.setBackground(StyleManager.getBackgroundColor());
		centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

		// 테이블 설정
		String[] header = { "강의실", "예약날짜", "예약시간", "상태" };
		tableModel = new DefaultTableModel(header, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // 모든 셀을 편집 불가능하게 설정
			}
		};

		reservationTable = new JTable(tableModel);
		setupTable();

		// 스크롤 패널에 테이블 추가
		JScrollPane scrollPane = new JScrollPane(reservationTable);
		scrollPane.setBackground(Color.WHITE);
		scrollPane.getViewport().setBackground(Color.WHITE);
		scrollPane.setBorder(BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(StyleManager.getComboBorder(), 1),
				"예약 목록",
				0, 0,
				new Font("맑은 고딕", Font.BOLD, 12),
				StyleManager.getTextColor()));

		centerPanel.add(scrollPane, BorderLayout.CENTER);
		return centerPanel;
	}

	private void setupTable() {
		// 테이블 기본 설정
		reservationTable.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		reservationTable.setRowHeight(35);
		reservationTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		reservationTable.getTableHeader().setReorderingAllowed(false);

		// 테이블 선택 색상 설정 (더 연한 색상)
		reservationTable.setSelectionBackground(new Color(240, 248, 255)); // 연한 파란색
		reservationTable.setSelectionForeground(StyleManager.getTextColor());

		// 헤더 스타일링 (더 부드러운 회색 계열)
		JTableHeader header = reservationTable.getTableHeader();
		header.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		header.setBackground(new Color(248, 249, 250)); // 연한 회색
		header.setForeground(new Color(73, 80, 87)); // 진한 회색 텍스트
		header.setPreferredSize(new Dimension(header.getPreferredSize().width, 40));
		header.setBorder(BorderFactory.createEmptyBorder()); // 헤더 보더 제거

		// 헤더 렌더러로 개별 컬럼 보더도 제거
		header.setDefaultRenderer(new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setHorizontalAlignment(JLabel.CENTER);
				setFont(new Font("맑은 고딕", Font.BOLD, 12));
				setBackground(new Color(248, 249, 250));
				setForeground(new Color(73, 80, 87));
				setBorder(BorderFactory.createEmptyBorder(10, 8, 10, 8)); // 패딩만 남기고 보더 제거
				setOpaque(true);
				return c;
			}
		});

		// 셀 렌더러 설정
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);

		DefaultTableCellRenderer statusRenderer = new DefaultTableCellRenderer() {
			@Override
			public Component getTableCellRendererComponent(JTable table, Object value,
					boolean isSelected, boolean hasFocus, int row, int column) {
				Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
				setHorizontalAlignment(JLabel.CENTER);

				if (!isSelected) {
					if ("예약됨".equals(value)) {
						setBackground(new Color(232, 245, 253)); // 연한 파란색
						setForeground(new Color(13, 110, 253)); // 파란색 텍스트
					} else if ("사용중".equals(value)) {
						setBackground(new Color(230, 247, 237)); // 연한 초록색 (기존 색상)
						setForeground(StyleManager.getSuccessColor());
					} else if ("사용완료".equals(value)) {
						setBackground(new Color(248, 249, 250)); // 연한 회색
						setForeground(new Color(108, 117, 125)); // 회색 텍스트
					} else {
						setBackground(Color.WHITE);
						setForeground(StyleManager.getTextColor());
					}
				}
				return c;
			}
		};

		// 열별 렌더러 적용
		TableColumnModel columnModel = reservationTable.getColumnModel();
		columnModel.getColumn(0).setCellRenderer(centerRenderer); // 강의실
		columnModel.getColumn(1).setCellRenderer(centerRenderer); // 날짜
		columnModel.getColumn(2).setCellRenderer(centerRenderer); // 시간
		columnModel.getColumn(3).setCellRenderer(statusRenderer); // 상태

		// 열 너비 설정
		columnModel.getColumn(0).setPreferredWidth(120); // 강의실
		columnModel.getColumn(1).setPreferredWidth(120); // 날짜
		columnModel.getColumn(2).setPreferredWidth(180); // 시간
		columnModel.getColumn(3).setPreferredWidth(80); // 상태

		// 선택 이벤트 리스너
		reservationTable.getSelectionModel().addListSelectionListener(e -> {
			if (!e.getValueIsAdjusting()) {
				updateButtonStates();
			}
		});
	}

	private JPanel createBottomPanel() {
		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.setBackground(StyleManager.getBackgroundColor());
		bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

		// 왼쪽: 상태 라벨
		statusLabel = new JLabel("예약 정보를 불러오는 중...");
		statusLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
		statusLabel.setForeground(StyleManager.getTextColor());
		bottomPanel.add(statusLabel, BorderLayout.WEST);

		// 오른쪽: 버튼 패널
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
		buttonPanel.setBackground(StyleManager.getBackgroundColor());

		// 예약 취소 버튼
		cancelButton = StyleManager.createStyledButton("예약 취소", StyleManager.getDangerColor());
		cancelButton.setEnabled(false);
		cancelButton.addActionListener(new CancelReservationListener());
		buttonPanel.add(cancelButton);

		bottomPanel.add(buttonPanel, BorderLayout.EAST);
		return bottomPanel;
	}

	private void loadReservations() {
		statusLabel.setText("예약 정보를 불러오는 중...");
		statusLabel.setForeground(StyleManager.getTextColor());

		reservationService.getAllReservations(
				// 성공 시 콜백
				allReservations -> {
					// 현재 사용자의 예약만 필터링
					reservations.clear();
					for (Reservation reservation : allReservations) {
						if (reservation.getStudentId().equals(currentUser.getName())) {
							reservations.add(reservation);
						}
					}

					// 테이블 데이터 업데이트
					updateTable();
					refreshButton.setEnabled(true);

					if (reservations.isEmpty()) {
						statusLabel.setText("예약 내역이 없습니다.");
					} else {
						statusLabel.setText("총 " + reservations.size() + "개의 예약이 있습니다.");
					}
					updateButtonStates();
				},
				// 오류 시 콜백
				errorMessage -> {
					JOptionPane.showMessageDialog(this,
							"예약 정보를 불러오는데 실패했습니다.\n" +
									"서버가 실행 중인지 확인해주세요.\n\n" +
									"오류: " + errorMessage,
							"연결 오류",
							JOptionPane.ERROR_MESSAGE);

					statusLabel.setText("예약 정보 로드 실패");
					statusLabel.setForeground(StyleManager.getDangerColor());
					refreshButton.setEnabled(true);
				});
	}

	private void updateTable() {
		// 기존 데이터 삭제
		tableModel.setRowCount(0);

		// 새 데이터 추가
		for (Reservation reservation : reservations) {
			String status = getReservationStatus(reservation);
			String[] row = {
					reservation.getRoomName(),
					reservation.getDate().toString(),
					reservation.getStartTime().toString() + " - " + reservation.getEndTime().toString(),
					status
			};
			tableModel.addRow(row);
		}
	}

	/**
	 * 예약의 현재 상태를 계산하여 반환
	 */
	private String getReservationStatus(Reservation reservation) {
		LocalDateTime now = LocalDateTime.now();
		LocalDate reservationDate = reservation.getDate();
		LocalTime startTime = reservation.getStartTime();
		LocalTime endTime = reservation.getEndTime();

		LocalDateTime reservationStart = LocalDateTime.of(reservationDate, startTime);
		LocalDateTime reservationEnd = LocalDateTime.of(reservationDate, endTime);

		if (now.isBefore(reservationStart)) {
			return "예약됨";
		} else if (now.isAfter(reservationEnd)) {
			return "사용완료";
		} else {
			return "사용중";
		}
	}

	private void updateButtonStates() {
		int selectedRow = reservationTable.getSelectedRow();
		if (selectedRow >= 0) {
			// 선택된 예약의 상태 확인
			Reservation selectedReservation = reservations.get(selectedRow);
			String status = getReservationStatus(selectedReservation);
			// "예약됨" 상태일 때만 취소 버튼 활성화
			cancelButton.setEnabled("예약됨".equals(status));
		} else {
			cancelButton.setEnabled(false);
		}
	}

	// 예약 취소 이벤트 리스너
	private class CancelReservationListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent e) {
			int selectedRow = reservationTable.getSelectedRow();
			if (selectedRow < 0) {
				JOptionPane.showMessageDialog(MyReservationView.this,
						"취소할 예약을 선택해주세요.",
						"알림",
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}

			Reservation selectedReservation = reservations.get(selectedRow);
			String status = getReservationStatus(selectedReservation);

			// 예약됨 상태가 아닌 경우 취소 불가
			if (!"예약됨".equals(status)) {
				String message;
				if ("사용중".equals(status)) {
					message = "현재 사용중인 예약은 취소할 수 없습니다.";
				} else if ("사용완료".equals(status)) {
					message = "이미 완료된 예약은 취소할 수 없습니다.";
				} else {
					message = "해당 예약은 취소할 수 없습니다.";
				}

				JOptionPane.showMessageDialog(MyReservationView.this,
						message,
						"취소 불가",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			// 확인 대화상자
			int result = JOptionPane.showConfirmDialog(MyReservationView.this,
					"다음 예약을 취소하시겠습니까?\n\n" +
							"강의실: " + selectedReservation.getRoomName() + "\n" +
							"날짜: " + selectedReservation.getDate() + "\n" +
							"시간: " + selectedReservation.getStartTime() + " - " + selectedReservation.getEndTime(),
					"예약 취소 확인",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);

			if (result == JOptionPane.YES_OPTION) {
				cancelReservation(selectedReservation, selectedRow);
			}
		}
	}

	private void cancelReservation(Reservation reservation, int rowIndex) {
		cancelButton.setEnabled(false);
		statusLabel.setText("예약을 취소하는 중...");
		statusLabel.setForeground(StyleManager.getWarningColor());

		reservationService.deleteReservation(
				reservation.getId(),
				// 성공 시 콜백
				() -> {
					// UI에서 해당 행 제거
					reservations.remove(rowIndex);
					tableModel.removeRow(rowIndex);

					statusLabel.setText("예약이 성공적으로 취소되었습니다.");
					statusLabel.setForeground(StyleManager.getSuccessColor());

					JOptionPane.showMessageDialog(MyReservationView.this,
							"예약이 성공적으로 취소되었습니다.",
							"취소 완료",
							JOptionPane.INFORMATION_MESSAGE);

					updateButtonStates();

					// 3초 후 상태 텍스트 업데이트
					Timer timer = new Timer(3000, ev -> {
						if (reservations.isEmpty()) {
							statusLabel.setText("예약 내역이 없습니다.");
						} else {
							statusLabel.setText("총 " + reservations.size() + "개의 예약이 있습니다.");
						}
						statusLabel.setForeground(StyleManager.getTextColor());
					});
					timer.setRepeats(false);
					timer.start();
				},
				// 오류 시 콜백
				errorMessage -> {
					statusLabel.setText("예약 취소 실패");
					statusLabel.setForeground(StyleManager.getDangerColor());
					cancelButton.setEnabled(true);

					JOptionPane.showMessageDialog(MyReservationView.this,
							"예약 취소에 실패했습니다.\n\n" +
									"오류: " + errorMessage,
							"취소 실패",
							JOptionPane.ERROR_MESSAGE);
				});
	}
}