package org.javateam11.ClassroomReservation.view.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * 예약 입력 다이얼로그를 담당하는 컴포넌트
 */
public class ReservationDialog {

    /**
     * 예약 입력값을 전달받아 처리하는 콜백 인터페이스
     */
    public interface ReservationHandler {
        /**
         * 예약 입력값을 전달받아 처리합니다.
         * 
         * @param reserver 예약자 이름
         * @param date     예약 날짜
         * @param start    시작 시간
         * @param end      종료 시간
         */
        void onReserve(String reserver, LocalDate date, LocalTime start, LocalTime end);
    }

    /**
     * 예약 다이얼로그를 표시하고 사용자 입력을 받습니다.
     * 
     * @param parent   부모 컴포넌트
     * @param roomName 강의실 이름
     * @param handler  예약 처리 콜백
     */
    public static void showReservationDialog(Component parent, String roomName, ReservationHandler handler) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JTextField reserverField = StyleManager.createStyledTextField();
        JTextField dateField = StyleManager.createStyledTextField();
        dateField.setText("2024-06-01");
        JTextField startField = StyleManager.createStyledTextField();
        startField.setText("09:00");
        JTextField endField = StyleManager.createStyledTextField();
        endField.setText("10:00");

        panel.add(StyleManager.createStyledLabel("👤 예약자 이름:"));
        panel.add(reserverField);
        panel.add(StyleManager.createStyledLabel("📅 날짜 (yyyy-MM-dd):"));
        panel.add(dateField);
        panel.add(StyleManager.createStyledLabel("⏰ 시작 시간 (HH:mm):"));
        panel.add(startField);
        panel.add(StyleManager.createStyledLabel("⏰ 종료 시간 (HH:mm):"));
        panel.add(endField);

        // 다이얼로그 표시 (OK/Cancel)
        int result = JOptionPane.showConfirmDialog(
                parent,
                panel,
                "🏫 " + roomName + " 예약",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String reserver = reserverField.getText().trim();
                if (reserver.isEmpty()) {
                    throw new IllegalArgumentException("예약자 이름을 입력해주세요.");
                }

                LocalDate date = LocalDate.parse(dateField.getText().trim());
                LocalTime start = LocalTime.parse(startField.getText().trim());
                LocalTime end = LocalTime.parse(endField.getText().trim());

                if (start.isAfter(end)) {
                    throw new IllegalArgumentException("시작 시간이 종료 시간보다 늦을 수 없습니다.");
                }

                handler.onReserve(reserver, date, start, end);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(
                        parent,
                        "❌ 입력값 오류: " + e.getMessage(),
                        "입력 오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}