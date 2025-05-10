package org.javateam11.view;

import org.javateam11.model.*;
import org.javateam11.controller.MainController;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * MainView는 Swing 기반의 메인 GUI 화면을 담당합니다.
 * 건물/층 선택, 2D 도면 스타일의 강의실/시설물 배치, 예약 다이얼로그 등 UI를 구현합니다.
 */
public class MainView extends JFrame {
    
    // 건물 선택 콤보박스
    private JComboBox<String> buildingCombo;

    // 층 선택 콤보박스
    private JComboBox<Integer> floorCombo;

    // 강의실/시설물 2D 배치 패널
    private JPanel mapPanel;

    // 컨트롤러 (이벤트 콜백 연결)
    private MainController controller;

    /**
     * MainView 생성자
     * @param controller 이벤트 처리를 위한 컨트롤러
     * @param buildings 건물 리스트
     */
    public MainView(MainController controller, List<Building> buildings) {
        this.controller = controller;

        setTitle("강의실/시설물 예약 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // 상단: 건물, 층 선택 UI
        JPanel topPanel = new JPanel();
        buildingCombo = new JComboBox<>();
        for (Building b : buildings) buildingCombo.addItem(b.getName());
        floorCombo = new JComboBox<>();
        topPanel.add(new JLabel("건물: "));
        topPanel.add(buildingCombo);
        topPanel.add(new JLabel("층: "));
        topPanel.add(floorCombo);
        add(topPanel, BorderLayout.NORTH);

        // 중앙: 2D 도면 패널
        mapPanel = new JPanel(null);
        mapPanel.setBackground(Color.WHITE);
        add(mapPanel, BorderLayout.CENTER);

        // 콤보박스 선택 이벤트 연결
        buildingCombo.addActionListener(e -> updateFloors(buildings));
        floorCombo.addActionListener(e -> updateMap(buildings));

        // 초기화: 첫 건물/층 선택
        if (!buildings.isEmpty()) {
            buildingCombo.setSelectedIndex(0);
            updateFloors(buildings);
        }
    }

    /**
     * 선택된 건물에 따라 층 콤보박스를 갱신합니다.
     * @param buildings 건물 리스트
     */
    private void updateFloors(List<Building> buildings) {
        String selectedBuilding = (String) buildingCombo.getSelectedItem();
        floorCombo.removeAllItems();

        for (Building b : buildings) {
            if (b.getName().equals(selectedBuilding)) {
                for (Integer f : b.getFloors()) floorCombo.addItem(f);
                if (!b.getFloors().isEmpty()) floorCombo.setSelectedIndex(0);
                updateMap(buildings);
                break;
            }
        }
    }

    /**
     * 선택된 건물/층에 따라 2D 도면에 강의실/시설물 버튼을 배치합니다.
     * @param buildings 건물 리스트
     */
    private void updateMap(List<Building> buildings) {
        mapPanel.removeAll();
        String selectedBuilding = (String) buildingCombo.getSelectedItem();
        Integer selectedFloor = (Integer) floorCombo.getSelectedItem();
        if (selectedFloor == null) return;

        for (Building b : buildings) {
            if (b.getName().equals(selectedBuilding)) {
                for (Classroom c : b.getClassrooms()) {
                    if (c.getFloor() == selectedFloor) {
                        JButton btn = createRoomButton(c.getName(), c.isAvailable());
                        btn.setBounds(c.getX(), c.getY(), 100, 50);
                        btn.addActionListener(e -> controller.onRoomClicked(c));
                        mapPanel.add(btn);
                    }
                }

                for (Facility f : b.getFacilities()) {
                    if (f.getFloor() == selectedFloor) {
                        JButton btn = createRoomButton(f.getName(), f.isAvailable());
                        btn.setBounds(f.getX(), f.getY(), 100, 50);
                        btn.addActionListener(e -> controller.onFacilityClicked(f));
                        mapPanel.add(btn);
                    }
                }
            }
        }

        mapPanel.repaint();
        mapPanel.revalidate();
    }

    /**
     * 강의실/시설물 버튼을 생성하고 상태에 따라 색상/글자색을 지정합니다.
     * @param name 강의실/시설물 이름
     * @param available 가용 여부
     * @return JButton 객체
     */
    private JButton createRoomButton(String name, boolean available) {
        JButton btn = new JButton(name + (available ? " (비어있음)" : " (사용중)"));
        btn.setBackground(available ? Color.GREEN : Color.RED);
        btn.setForeground(available ? Color.BLACK : Color.WHITE);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        return btn;
    }

    /**
     * 예약 다이얼로그를 띄워 사용자 입력을 받고, ReservationHandler로 결과를 전달합니다.
     * @param name 강의실/시설물 이름
     * @param handler 예약 처리 콜백
     */
    public void showReservationDialog(String name, ReservationHandler handler) {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        JTextField reserverField = new JTextField();
        JTextField dateField = new JTextField("2024-06-01");
        JTextField startField = new JTextField("09:00");
        JTextField endField = new JTextField("10:00");

        panel.add(new JLabel("예약자 이름:")); panel.add(reserverField);
        panel.add(new JLabel("날짜(yyyy-MM-dd):")); panel.add(dateField);
        panel.add(new JLabel("시작 시간(HH:mm):")); panel.add(startField);
        panel.add(new JLabel("종료 시간(HH:mm):")); panel.add(endField);

        int result = JOptionPane.showConfirmDialog(this, panel, name + " 예약", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String reserver = reserverField.getText();
                LocalDate date = LocalDate.parse(dateField.getText());
                LocalTime start = LocalTime.parse(startField.getText());
                LocalTime end = LocalTime.parse(endField.getText());
                handler.onReserve(reserver, date, start, end);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "입력값이 올바르지 않습니다.");
            }
        }
    }

    /**
     * 예약 입력값을 전달받아 처리하는 콜백 인터페이스
     */
    public interface ReservationHandler {
        /**
         * 예약 입력값을 전달받아 처리합니다.
         * @param reserver 예약자 이름
         * @param date 예약 날짜
         * @param start 시작 시간
         * @param end 종료 시간
         */
        void onReserve(String reserver, LocalDate date, LocalTime start, LocalTime end);
    }
} 