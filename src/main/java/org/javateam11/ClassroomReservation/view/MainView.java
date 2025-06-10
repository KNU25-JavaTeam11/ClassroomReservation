package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.controller.IMainController;
import org.javateam11.ClassroomReservation.controller.ControllerFactory;
import org.javateam11.ClassroomReservation.model.*;

import org.javateam11.ClassroomReservation.model.Building;
import org.javateam11.ClassroomReservation.model.User;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * MainView는 Swing 기반의 메인 GUI 화면을 담당합니다.
 * - 건물/층 선택, 2D 도면 스타일의 강의실/시설물 배치, 예약 다이얼로그 등 UI를 구현합니다.
 * - MVC 패턴에서 View 역할을 하며, Controller와의 상호작용을 위해 MainController를 참조합니다.
 */
public class MainView extends JFrame {

    // UI 색상 상수들
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185); // 블루
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96); // 녹색
    private static final Color DANGER_COLOR = new Color(231, 76, 60); // 빨강
    private static final Color WARNING_COLOR = new Color(241, 196, 15); // 노랑
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // 연한 회색
    private static final Color TEXT_COLOR = new Color(44, 62, 80); // 다크 그레이
    private static final Color HOVER_COLOR = new Color(52, 152, 219); // 밝은 블루

    // 건물 선택 콤보박스 (사용자가 건물을 선택할 수 있음)
    private JComboBox<String> buildingCombo;

    // 층 선택 콤보박스 (사용자가 층을 선택할 수 있음)
    private JComboBox<Integer> floorCombo;

    // 강의실/시설물 2D 배치 패널 (실제 버튼들이 배치되는 공간)
    private MapPanel mapPanel;

    // 컨트롤러 (이벤트 콜백 연결, 예약 처리 등)
    private IMainController controller;

    // 현재 사용자 (임시로 새 User 생성 대입해둠)
    private User currentUser = new User("심채연", "2024009663");

    // 내 예약창과 내 정보창 (지연 초기화)
    private MyReservationView myResView;
    private MyInformationView myInfoView;

    /**
     * MainView 생성자
     * 
     * @param controller 이벤트 처리를 위한 컨트롤러 (MVC의 Controller)
     * @param buildings  건물 리스트 (Model에서 전달받음)
     *
     *                   - UI 컴포넌트 초기화 및 레이아웃 설정
     *                   - 콤보박스 선택 이벤트 연결
     *                   - 최초 실행 시 첫 건물/층을 자동 선택
     */
    public MainView(IMainController controller, List<Building> buildings) {
        this.controller = controller;

        setupMainWindow();
        setupTopPanel(buildings);
        setupMapPanel();
        setupEventListeners(buildings);

        // 초기화: 첫 건물/층 선택 (프로그램 시작 시 자동으로 첫 건물/층 표시)
        if (!buildings.isEmpty()) {
            buildingCombo.setSelectedIndex(0);
            updateFloors(buildings);
        }
    }

    /**
     * 메인 윈도우 설정
     */
    private void setupMainWindow() {
        setTitle("🏫 강의실/시설물 예약 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setResizable(true); // 크기 조정 가능하도록 변경
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // 윈도우 아이콘 설정 (있다면)
        try {
            URL iconUrl = getClass().getResource("/images/icon.png");
            if (iconUrl != null) {
                setIconImage(ImageIO.read(iconUrl));
            }
        } catch (Exception e) {
            // 아이콘이 없어도 계속 진행
        }
    }

    /**
     * 상단 패널 설정
     */
    private void setupTopPanel(List<Building> buildings) {
        // 상단
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(Color.WHITE);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 상단 좌측 메뉴바
        JPanel topMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topMenu.setBackground(Color.WHITE);

        JButton myResBtn = createStyledButton("📅 내 예약", PRIMARY_COLOR);
        myResBtn.addActionListener(e -> {
            if (myResView == null) {
                myResView = ControllerFactory.getInstance().createMyReservationView(currentUser);
            }
            myResView.setVisible(true);
        });

        JButton myInfoBtn = createStyledButton("👤 내 정보", PRIMARY_COLOR);
        myInfoBtn.addActionListener(e -> {
            if (myInfoView == null) {
                myInfoView = ControllerFactory.getInstance().createMyInformationView(currentUser);
            }
            myInfoView.setVisible(true);
        });

        topMenu.add(myResBtn);
        topMenu.add(myInfoBtn);
        topPanel.add(topMenu, BorderLayout.WEST);

        // 상단 중앙 콤보박스
        JPanel topRoom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        topRoom.setBackground(Color.WHITE);

        JLabel buildingLabel = createStyledLabel("🏢 건물:");
        buildingCombo = createStyledComboBox();
        for (Building b : buildings)
            buildingCombo.addItem(b.getName());

        JLabel floorLabel = createStyledLabel("📍 층:");
        floorCombo = createStyledComboBox();

        topRoom.add(buildingLabel);
        topRoom.add(buildingCombo);
        topRoom.add(Box.createHorizontalStrut(20));
        topRoom.add(floorLabel);
        topRoom.add(floorCombo);
        topPanel.add(topRoom, BorderLayout.CENTER);

        // 상단 우측 버튼들
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topButtons.setBackground(Color.WHITE);

        JButton loginBtn = createStyledButton("🔐 로그인", SUCCESS_COLOR);
        loginBtn.addActionListener(e -> controller.onLoginButtonClicked());

        JButton signUpBtn = createStyledButton("✨ 회원가입", WARNING_COLOR);
        signUpBtn.addActionListener(e -> controller.onSignUpClicked());

        topButtons.add(loginBtn);
        topButtons.add(signUpBtn);
        topPanel.add(topButtons, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    /**
     * 맵 패널 설정
     */
    private void setupMapPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(BACKGROUND_COLOR);

        mapPanel = new MapPanel();
        mapPanel.setLayout(null);
        mapPanel.setBackground(BACKGROUND_COLOR);
        centerPanel.add(mapPanel, BorderLayout.CENTER);

        // 하단에 범례 추가
        JPanel legendPanel = createLegendPanel();
        centerPanel.add(legendPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * 상태 범례 패널 생성
     */
    private JPanel createLegendPanel() {
        JPanel legendPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        legendPanel.setBackground(Color.WHITE);
        legendPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        // 예약 가능 표시
        JPanel availablePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        availablePanel.setBackground(Color.WHITE);
        JLabel availableIcon = new JLabel("✅");
        availableIcon.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        JLabel availableText = createStyledLabel("예약 가능");
        availablePanel.add(availableIcon);
        availablePanel.add(availableText);

        // 사용 중 표시
        JPanel occupiedPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        occupiedPanel.setBackground(Color.WHITE);
        JLabel occupiedIcon = new JLabel("❌");
        occupiedIcon.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        JLabel occupiedText = createStyledLabel("사용 중");
        occupiedPanel.add(occupiedIcon);
        occupiedPanel.add(occupiedText);

        // 설명 텍스트
        JLabel instructionText = new JLabel("💡 강의실을 클릭하여 예약하세요");
        instructionText.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        instructionText.setForeground(new Color(127, 140, 141));

        legendPanel.add(availablePanel);
        legendPanel.add(occupiedPanel);
        legendPanel.add(Box.createHorizontalStrut(30));
        legendPanel.add(instructionText);

        return legendPanel;
    }

    /**
     * 이벤트 리스너 설정
     */
    private void setupEventListeners(List<Building> buildings) {
        buildingCombo.addActionListener(e -> updateFloors(buildings));
        floorCombo.addActionListener(e -> updateMap(buildings));
    }

    /**
     * 스타일이 적용된 버튼 생성
     */
    private JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("맑은 고딕", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // 호버 효과
        button.addMouseListener(new MouseAdapter() {
            private Color originalColor = backgroundColor;

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.brighter());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    /**
     * 스타일이 적용된 콤보박스 생성
     */
    private <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        comboBox.setBackground(Color.WHITE);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        comboBox.setPreferredSize(new Dimension(120, 35));
        return comboBox;
    }

    /**
     * 스타일이 적용된 라벨 생성
     */
    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    /**
     * 선택된 건물에 따라 층 콤보박스를 갱신합니다.
     * 
     * @param buildings 건물 리스트
     *
     *                  - 사용자가 건물을 바꿀 때마다 해당 건물의 층 목록으로 콤보박스를 갱신
     *                  - 층이 바뀌면 자동으로 2D 도면도 갱신
     */
    private void updateFloors(List<Building> buildings) {
        String selectedBuilding = (String) buildingCombo.getSelectedItem();
        floorCombo.removeAllItems(); // 기존 층 목록 삭제

        for (Building b : buildings) {
            if (b.getName().equals(selectedBuilding)) {
                // 해당 건물의 모든 층을 콤보박스에 추가
                for (Integer f : b.getFloors())
                    floorCombo.addItem(f);
                if (!b.getFloors().isEmpty())
                    floorCombo.setSelectedIndex(0); // 첫 층 자동 선택
                updateMap(buildings); // 도면 갱신
                break;
            }
        }
    }

    /**
     * 선택된 건물/층에 따라 2D 도면에 강의실/시설물 버튼을 배치합니다.
     * 
     * @param buildings 건물 리스트
     *
     *                  - 각 강의실/시설물의 좌표(x, y)에 버튼을 배치
     *                  - 버튼 클릭 시 컨트롤러의 onReservationClicked 호출
     *                  - 가용 상태에 따라 색상/텍스트 다르게 표시
     *                  - 콤보박스 변경에 따라 건물/층 구조도 png 변경
     */
    private void updateMap(List<Building> buildings) {
        mapPanel.removeAll(); // 기존 버튼 제거
        String selectedBuilding = (String) buildingCombo.getSelectedItem();
        Integer selectedFloor = (Integer) floorCombo.getSelectedItem();
        if (selectedFloor == null)
            return; // 층이 선택되지 않은 경우 종료

        for (Building b : buildings) {
            if (b.getName().equals(selectedBuilding)) {
                // 강의실 버튼 배치
                for (Classroom c : b.getClassrooms()) {
                    if (c.getFloor() == selectedFloor) {
                        try {
                            String imageFileName = selectedBuilding + "_" + selectedFloor + "F.png";
                            URL imageUrl = getClass().getResource("/images/" + imageFileName);
                            if (imageUrl != null) {
                                BufferedImage img = ImageIO.read(imageUrl);
                                mapPanel.setBackgroundImage(img);
                            }
                            JButton btn = createRoomButton(c.getName(), c.isAvailable());
                            btn.setBounds(c.getX(), c.getY(), 110, 60); // 크기를 약간 키움
                            btn.addActionListener(e -> controller.onReservationClicked(c));
                            mapPanel.add(btn);
                        } catch (IOException e) {
                            System.err.println("이미지 로드 실패: " + e.getMessage());
                        }
                    }
                }

                // 시설물 버튼 배치
                for (Facility f : b.getFacilities()) {
                    if (f.getFloor() == selectedFloor) {
                        JButton btn = createRoomButton(f.getName(), f.isAvailable());
                        btn.setBounds(f.getX(), f.getY(), 110, 60);
                        btn.addActionListener(e -> controller.onReservationClicked(f));
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
     * 
     * @param name      강의실/시설물 이름
     * @param available 가용 여부 (true: 비어있음, false: 사용중)
     * @return JButton 객체
     */
    private JButton createRoomButton(String name, boolean available) {
        // 텍스트에서 상태 정보 제거하고 아이콘으로 표현
        String displayText = name;
        String statusIcon = available ? "✅" : "❌";

        JButton btn = new JButton("<html><center>" + statusIcon + "<br/>" + displayText + "</center></html>");

        // 색상 설정 - 더 세련된 팔레트 사용
        Color backgroundColor = available ? new Color(46, 204, 113) : // 세련된 녹색
                new Color(231, 76, 60); // 세련된 빨강색

        Color textColor = Color.WHITE;
        Color hoverColor = available ? new Color(39, 174, 96) : new Color(192, 57, 43);

        btn.setBackground(backgroundColor);
        btn.setForeground(textColor);
        btn.setFont(new Font("맑은 고딕", Font.BOLD, 10));
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 둥근 모서리 효과를 위한 커스텀 버튼
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)));

        // 호버 효과 추가
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btn.setBackground(hoverColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(hoverColor.darker(), 2),
                        BorderFactory.createEmptyBorder(4, 7, 4, 7)));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                btn.setBackground(backgroundColor);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(backgroundColor.darker(), 1),
                        BorderFactory.createEmptyBorder(5, 8, 5, 8)));
            }
        });

        // 툴팁 추가
        String statusText = available ? "예약 가능" : "사용 중";
        btn.setToolTipText(name + " - " + statusText + " (클릭하여 예약)");

        return btn;
    }

    /**
     * 예약 다이얼로그를 띄워 사용자 입력을 받고, ReservationHandler로 결과를 전달합니다.
     * 
     * @param name    강의실/시설물 이름
     * @param handler 예약 처리 콜백 (예약 입력값을 컨트롤러로 전달)
     *
     *                - 사용자에게 예약자, 날짜, 시작/종료 시간 입력을 받음
     *                - 입력값이 올바르지 않으면 경고 메시지 출력
     */
    public void showReservationDialog(String name, ReservationHandler handler) {
        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        panel.setBackground(Color.WHITE);

        JTextField reserverField = createStyledTextField();
        JTextField dateField = createStyledTextField();
        dateField.setText("2024-06-01");
        JTextField startField = createStyledTextField();
        startField.setText("09:00");
        JTextField endField = createStyledTextField();
        endField.setText("10:00");

        panel.add(createStyledLabel("👤 예약자 이름:"));
        panel.add(reserverField);
        panel.add(createStyledLabel("📅 날짜 (yyyy-MM-dd):"));
        panel.add(dateField);
        panel.add(createStyledLabel("⏰ 시작 시간 (HH:mm):"));
        panel.add(startField);
        panel.add(createStyledLabel("⏰ 종료 시간 (HH:mm):"));
        panel.add(endField);

        // 다이얼로그 표시 (OK/Cancel)
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "🏫 " + name + " 예약",
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
                        this,
                        "❌ 입력값 오류: " + e.getMessage(),
                        "입력 오류",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * 스타일이 적용된 텍스트필드 생성
     */
    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        textField.setBackground(Color.WHITE);
        textField.setForeground(TEXT_COLOR);
        return textField;
    }

    /**
     * 예약 입력값을 전달받아 처리하는 콜백 인터페이스
     * - 컨트롤러에서 구현하여 예약 처리 로직을 담당
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
}