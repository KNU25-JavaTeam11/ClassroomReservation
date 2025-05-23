package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.controller.MainController;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * MainView는 Swing 기반의 메인 GUI 화면을 담당합니다.
 * - 건물/층 선택, 2D 도면 스타일의 강의실/시설물 배치, 예약 다이얼로그 등 UI를 구현합니다.
 * - MVC 패턴에서 View 역할을 하며, Controller와의 상호작용을 위해 MainController를 참조합니다.
 */
public class MainView extends JFrame {

    // 건물 선택 콤보박스 (사용자가 건물을 선택할 수 있음)
    private JComboBox<String> buildingCombo;

    // 층 선택 콤보박스 (사용자가 층을 선택할 수 있음)
    private JComboBox<Integer> floorCombo;

    // 강의실/시설물 2D 배치 패널 (실제 버튼들이 배치되는 공간)
    private JPanel mapPanel;

    // 컨트롤러 (이벤트 콜백 연결, 예약 처리 등)
    private MainController controller;

    /**
     * MainView 생성자
     * @param controller 이벤트 처리를 위한 컨트롤러 (MVC의 Controller)
     * @param buildings 건물 리스트 (Model에서 전달받음)
     *
     * - UI 컴포넌트 초기화 및 레이아웃 설정
     * - 콤보박스 선택 이벤트 연결
     * - 최초 실행 시 첫 건물/층을 자동 선택
     */
    public MainView(MainController controller, List<Building> buildings) {
        this.controller = controller;

        setTitle("강의실/시설물 예약 시스템"); // 윈도우 타이틀
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 창 닫기 시 프로그램 종료
        setSize(900, 700); // 창 크기
        setLocationRelativeTo(null); // 화면 중앙에 배치
        setLayout(new BorderLayout()); // BorderLayout 사용

        // 상단: 건물, 층 선택 UI
        JPanel topPanel = new JPanel();
        buildingCombo = new JComboBox<>(); // 건물 선택 콤보박스
        for (Building b : buildings) buildingCombo.addItem(b.getName()); // 건물명 추가
        floorCombo = new JComboBox<>(); // 층 선택 콤보박스
        topPanel.add(new JLabel("건물: ")); // 라벨
        topPanel.add(buildingCombo);
        topPanel.add(new JLabel("층: "));
        topPanel.add(floorCombo);
        add(topPanel, BorderLayout.NORTH); // 상단에 배치

        // 중앙: 2D 도면 패널 (null 레이아웃으로 버튼 위치 직접 지정)
        mapPanel = new JPanel(null);
        mapPanel.setBackground(Color.WHITE);
        add(mapPanel, BorderLayout.CENTER);
        
        //회원가입창 테스트 버튼
        JButton signUp = new JButton("회원가입 테스트");
        topPanel.add(signUp);
        signUp.addActionListener(e -> controller.onSignUpClicked());

        // 콤보박스 선택 이벤트 연결
        // 건물 선택 시 해당 건물의 층 목록으로 갱신
        buildingCombo.addActionListener(e -> updateFloors(buildings));
        // 층 선택 시 해당 층의 강의실/시설물 배치 갱신
        floorCombo.addActionListener(e -> updateMap(buildings));

        // 초기화: 첫 건물/층 선택 (프로그램 시작 시 자동으로 첫 건물/층 표시)
        if (!buildings.isEmpty()) {
            buildingCombo.setSelectedIndex(0);
            updateFloors(buildings);
        }
    }

    /**
     * 선택된 건물에 따라 층 콤보박스를 갱신합니다.
     * @param buildings 건물 리스트
     *
     * - 사용자가 건물을 바꿀 때마다 해당 건물의 층 목록으로 콤보박스를 갱신
     * - 층이 바뀌면 자동으로 2D 도면도 갱신
     */
    private void updateFloors(List<Building> buildings) {
        String selectedBuilding = (String) buildingCombo.getSelectedItem();
        floorCombo.removeAllItems(); // 기존 층 목록 삭제

        for (Building b : buildings) {
            if (b.getName().equals(selectedBuilding)) {
                // 해당 건물의 모든 층을 콤보박스에 추가
                for (Integer f : b.getFloors()) floorCombo.addItem(f);
                if (!b.getFloors().isEmpty()) floorCombo.setSelectedIndex(0); // 첫 층 자동 선택
                updateMap(buildings); // 도면 갱신
                break;
            }
        }
    }

    /**
     * 선택된 건물/층에 따라 2D 도면에 강의실/시설물 버튼을 배치합니다.
     * @param buildings 건물 리스트
     *
     * - 각 강의실/시설물의 좌표(x, y)에 버튼을 배치
     * - 버튼 클릭 시 컨트롤러의 onRoomClicked/onFacilityClicked 호출
     * - 가용 상태에 따라 색상/텍스트 다르게 표시
     */
    private void updateMap(List<Building> buildings) {
        mapPanel.removeAll(); // 기존 버튼 제거
        String selectedBuilding = (String) buildingCombo.getSelectedItem();
        Integer selectedFloor = (Integer) floorCombo.getSelectedItem();
        if (selectedFloor == null) return; // 층이 선택되지 않은 경우 종료

        for (Building b : buildings) {
            if (b.getName().equals(selectedBuilding)) {
                // 강의실 버튼 배치
                for (Classroom c : b.getClassrooms()) {
                    if (c.getFloor() == selectedFloor) {
                        JButton btn = createRoomButton(c.getName(), c.isAvailable());
                        btn.setBounds(c.getX(), c.getY(), 100, 50); // 위치/크기 지정
                        btn.addActionListener(e -> controller.onRoomClicked(c)); // 클릭 이벤트 연결
                        mapPanel.add(btn);
                    }
                }

                // 시설물 버튼 배치
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
     * @param available 가용 여부 (true: 비어있음, false: 사용중)
     * @return JButton 객체
     *
     * - 비어있음: 초록색 배경 + 검정 글씨
     * - 사용중: 빨간색 배경 + 흰색 글씨
     * - macOS 등 일부 환경에서 색상 적용이 잘 안될 경우 setOpaque(true), setBorderPainted(false)로 강제 적용
     *
     * TODO: [실습] 아래 버튼 색상/글자색 지정 부분을 직접 구현해보세요.
     */
    private JButton createRoomButton(String name, boolean available) {
        JButton btn = new JButton(name + (available ? " (비어있음)" : " (사용중)"));
        // TODO: [실습] 아래 두 줄을 구현하세요:
        // 1. available이 true면 초록색 배경+검정 글씨, false면 빨간색 배경+흰색 글씨로 설정
        if (available) {
			btn.setBackground(Color.GREEN);
			btn.setForeground(Color.BLACK);
		} else {
			btn.setBackground(Color.RED);
			btn.setForeground(Color.WHITE);
		}
        // 2. macOS 등에서 색상 적용이 잘 안될 경우 setOpaque(true), setBorderPainted(false)도 적용
        btn.setOpaque(true);
		btn.setBorderPainted(false);
        return btn;
    }

    /**
     * 예약 다이얼로그를 띄워 사용자 입력을 받고, ReservationHandler로 결과를 전달합니다.
     * @param name 강의실/시설물 이름
     * @param handler 예약 처리 콜백 (예약 입력값을 컨트롤러로 전달)
     *
     * - 사용자에게 예약자, 날짜, 시작/종료 시간 입력을 받음
     * - 입력값이 올바르지 않으면 경고 메시지 출력
     */
    public void showReservationDialog(String name, ReservationHandler handler) {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        JTextField reserverField = new JTextField(); // 예약자 입력
        JTextField dateField = new JTextField("2024-06-01"); // 날짜 입력
        JTextField startField = new JTextField("09:00"); // 시작 시간 입력
        JTextField endField = new JTextField("10:00"); // 종료 시간 입력

        panel.add(new JLabel("예약자 이름:")); panel.add(reserverField);
        panel.add(new JLabel("날짜(yyyy-MM-dd):")); panel.add(dateField);
        panel.add(new JLabel("시작 시간(HH:mm):")); panel.add(startField);
        panel.add(new JLabel("종료 시간(HH:mm):")); panel.add(endField);

        // 다이얼로그 표시 (OK/Cancel)
        int result = JOptionPane.showConfirmDialog(this, panel, name + " 예약", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try {
                String reserver = reserverField.getText();
                LocalDate date = LocalDate.parse(dateField.getText());
                LocalTime start = LocalTime.parse(startField.getText());
                LocalTime end = LocalTime.parse(endField.getText());
                handler.onReserve(reserver, date, start, end); // 입력값을 콜백으로 전달
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "입력값이 올바르지 않습니다.");
            }
        }
    }

    /**
     * 예약 입력값을 전달받아 처리하는 콜백 인터페이스
     * - 컨트롤러에서 구현하여 예약 처리 로직을 담당
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