package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.controller.IReservationController;
import org.javateam11.ClassroomReservation.util.FontUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 예약 생성을 위한 뷰 클래스
 * 다른 view 패턴과 동일하게 JFrame을 상속받아 구현
 */
public class ReservationView extends JFrame {

    private IReservationController reservationController;

    private JTextField dateField;
    private JTextField startField;
    private JTextField endField;
    private JButton reserveButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    public ReservationView(IReservationController controller) {
        this.reservationController = controller;
        initializeUI();
        setupEventListeners();
    }

    public void setController(IReservationController controller) {
        this.reservationController = controller;
    }

    private void initializeUI() {
        String roomName = reservationController != null ? reservationController.getRoomName() : "강의실";
        setTitle(roomName + " 예약");
        setSize(450, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        // 타이틀
        JLabel titleLabel = new JLabel("강의실 예약");
        titleLabel.setFont(FontUtils.getTitleFont());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(51, 51, 51));

        // 강의실 이름 표시
        JLabel roomLabel = new JLabel(roomName);
        roomLabel.setFont(FontUtils.getLabelFont());
        roomLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        roomLabel.setForeground(new Color(25, 118, 210));

        // 입력 패널
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 10, 8, 10);

        // 날짜 라벨과 필드
        JLabel dateLabel = new JLabel("날짜 (yyyy-MM-dd):");
        dateLabel.setFont(FontUtils.getLabelFont());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 0.0;
        inputPanel.add(dateLabel, gbc);

        dateField = new JTextField(20);
        dateField.setFont(FontUtils.getPlainFont());
        dateField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        dateField.setText(LocalDate.now().toString());
        dateField.setPreferredSize(new Dimension(200, 35));
        dateField.setMinimumSize(new Dimension(200, 35));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        inputPanel.add(dateField, gbc);

        // 시작 시간 라벨과 필드
        JLabel startLabel = new JLabel("시작 시간 (HH:mm):");
        startLabel.setFont(FontUtils.getLabelFont());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        inputPanel.add(startLabel, gbc);

        startField = new JTextField(20);
        startField.setFont(FontUtils.getPlainFont());
        startField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        startField.setText("09:00");
        startField.setPreferredSize(new Dimension(200, 35));
        startField.setMinimumSize(new Dimension(200, 35));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        inputPanel.add(startField, gbc);

        // 종료 시간 라벨과 필드
        JLabel endLabel = new JLabel("종료 시간 (HH:mm):");
        endLabel.setFont(FontUtils.getLabelFont());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        inputPanel.add(endLabel, gbc);

        endField = new JTextField(20);
        endField.setFont(FontUtils.getPlainFont());
        endField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        endField.setText("10:00");
        endField.setPreferredSize(new Dimension(200, 35));
        endField.setMinimumSize(new Dimension(200, 35));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        inputPanel.add(endField, gbc);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        reserveButton = new JButton("예약하기");
        reserveButton.setFont(FontUtils.getButtonFont());
        reserveButton.setPreferredSize(new Dimension(100, 35));
        reserveButton.setBackground(new Color(25, 118, 210));
        reserveButton.setForeground(Color.WHITE);
        reserveButton.setBorder(BorderFactory.createEmptyBorder());
        reserveButton.setFocusPainted(false);
        reserveButton.setOpaque(true);
        reserveButton.setBorderPainted(false);

        reserveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (reserveButton.isEnabled()) {
                    reserveButton.setBackground(new Color(21, 101, 192));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (reserveButton.isEnabled()) {
                    reserveButton.setBackground(new Color(25, 118, 210));
                }
            }
        });

        cancelButton = new JButton("취소");
        cancelButton.setFont(FontUtils.getPlainFont());
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setBackground(new Color(245, 245, 245));
        cancelButton.setForeground(new Color(60, 60, 60));
        cancelButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        cancelButton.setFocusPainted(false);
        cancelButton.setOpaque(true);

        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(235, 235, 235));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(245, 245, 245));
            }
        });

        buttonPanel.add(reserveButton);
        buttonPanel.add(cancelButton);

        // 상태 라벨
        statusLabel = new JLabel(" ");
        statusLabel.setFont(FontUtils.getSmallFont());
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        statusLabel.setForeground(Color.RED);

        // 컴포넌트들을 메인 패널에 추가
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(roomLabel);
        mainPanel.add(Box.createVerticalStrut(30));
        mainPanel.add(inputPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(statusLabel);

        add(mainPanel, BorderLayout.CENTER);
    }

    private void setupEventListeners() {
        // 예약 버튼 클릭 이벤트
        reserveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performReservation();
            }
        });

        // 취소 버튼 클릭 이벤트
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
    }

    private void performReservation() {
        try {
            String dateText = dateField.getText().trim();
            String startText = startField.getText().trim();
            String endText = endField.getText().trim();

            if (dateText.isEmpty() || startText.isEmpty() || endText.isEmpty()) {
                showStatus("모든 필드를 입력해주세요.", Color.RED);
                return;
            }

            LocalDate date = LocalDate.parse(dateText);
            LocalTime start = LocalTime.parse(startText);
            LocalTime end = LocalTime.parse(endText);

            if (start.isAfter(end)) {
                showStatus("시작 시간이 종료 시간보다 늦을 수 없습니다.", Color.RED);
                return;
            }

            if (date.isBefore(LocalDate.now())) {
                showStatus("과거 날짜는 예약할 수 없습니다.", Color.RED);
                return;
            }

            // 예약 처리 컨트롤러 호출
            if (reservationController != null) {
                reservationController.createReservation(date, start, end);
            }

            // 성공 시 창 닫기
            dispose();

        } catch (Exception e) {
            showStatus("입력값 오류: " + e.getMessage(), Color.RED);
        }
    }

    private void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}