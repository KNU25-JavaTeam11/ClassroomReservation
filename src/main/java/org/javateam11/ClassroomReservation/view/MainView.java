package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.controller.IMainController;
import org.javateam11.ClassroomReservation.controller.ControllerFactory;
import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.service.TokenManager;
import org.javateam11.ClassroomReservation.service.ReservationService;
import org.javateam11.ClassroomReservation.service.RoomService;
import org.javateam11.ClassroomReservation.dto.ReservationDto;
import org.javateam11.ClassroomReservation.dto.RoomDto;
import org.javateam11.ClassroomReservation.util.AvailabilityChecker;

import org.javateam11.ClassroomReservation.model.Building;
import org.javateam11.ClassroomReservation.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.util.Map;
import java.util.HashMap;

/**
 * MainView는 Swing 기반의 메인 GUI 화면을 담당합니다.
 * - 건물/층 선택, 2D 도면 스타일의 강의실/시설물 배치, 예약 다이얼로그 등 UI를 구현합니다.
 * - MVC 패턴에서 View 역할을 하며, Controller와의 상호작용을 위해 MainController를 참조합니다.
 */
public class MainView extends JFrame {

    private static final Logger logger = LoggerFactory.getLogger(MainView.class);

    // UI 색상 상수들
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185); // 블루
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96); // 녹색
    private static final Color DANGER_COLOR = new Color(231, 76, 60); // 빨강
    private static final Color WARNING_COLOR = new Color(241, 196, 15); // 노랑
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // 연한 회색
    private static final Color TEXT_COLOR = new Color(44, 62, 80); // 다크 그레이
    private static final Color HOVER_COLOR = new Color(52, 152, 219); // 밝은 블루
    private static final Color TOPBAR_COLOR = new Color(248, 249, 250); // 상단바 색상 (매우 연한 회색)
    private static final Color COMBO_BACKGROUND = new Color(255, 255, 255); // 콤보박스 배경
    private static final Color COMBO_BORDER = new Color(189, 195, 199); // 콤보박스 테두리
    private static final Color COMBO_HOVER = new Color(231, 236, 239); // 콤보박스 호버

    // 건물 선택 콤보박스 (사용자가 건물을 선택할 수 있음)
    private JComboBox<String> buildingCombo;

    // 층 선택 콤보박스 (사용자가 층을 선택할 수 있음)
    private JComboBox<Integer> floorCombo;

    // 강의실/시설물 2D 배치 패널 (실제 버튼들이 배치되는 공간)
    private MapPanel mapPanel;

    // 컨트롤러 (이벤트 콜백 연결, 예약 처리 등)
    private IMainController controller;

    // 현재 사용자
    private User currentUser;

    // 내 예약창과 내 정보창 (지연 초기화)
    private MyReservationView myResView;
    private MyInformationView myInfoView;

    // 예약 서비스 (백엔드 API 호출용)
    private ReservationService reservationService;

    // 강의실 서비스 (백엔드 강의실 정보 API 호출용)
    private RoomService roomService;

    // 강의실/시설물 이름과 roomId 매핑 (백엔드에서 받아와서 로컬 데이터와 매핑)
    private Map<String, Long> roomIdMap;

    // 로컬 건물 데이터 (x, y 좌표 포함)
    private List<Building> localBuildings;

    // 자동 새로고침을 위한 타이머
    private Timer refreshTimer;

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
        this.localBuildings = buildings;

        // TokenManager에서 현재 로그인된 사용자 정보 가져오기
        initializeCurrentUser();

        // 서비스들 초기화
        this.reservationService = new ReservationService();
        this.roomService = new RoomService();

        setupMainWindow();
        setupTopPanel(buildings);
        setupMapPanel();
        setupEventListeners(buildings);

        // 백엔드에서 강의실 목록을 가져와서 로컬 데이터와 매핑
        initializeRoomIdMappingFromBackend();

        // 초기화: 첫 건물/층 선택 (프로그램 시작 시 자동으로 첫 건물/층 표시)
        if (!buildings.isEmpty()) {
            buildingCombo.setSelectedIndex(0);
            updateFloors(buildings);
        }

        // 자동 새로고침 타이머 시작 (30초마다 예약 정보 갱신)
        startAutoRefresh(buildings);
    }

    /**
     * TokenManager에서 현재 사용자 정보를 가져와서 User 객체 생성
     */
    private void initializeCurrentUser() {
        TokenManager tokenManager = TokenManager.getInstance();

        if (tokenManager.isAuthenticated()) {
            String studentId = tokenManager.getCurrentStudentId();
            String name = tokenManager.getCurrentName();

            if (studentId != null && name != null) {
                this.currentUser = new User(name, studentId);
            } else {
                // 정보가 부족한 경우 기본값 사용 (이론적으로는 발생하지 않아야 함)
                this.currentUser = new User("Unknown", tokenManager.getCurrentStudentId());
            }
        } else {
            // 로그인되지 않은 상태 (이론적으로는 MainView에 도달하기 전에 처리되어야 함)
            throw new IllegalStateException("사용자가 로그인되지 않았습니다.");
        }
    }

    /**
     * 백엔드에서 강의실 목록을 가져와서 로컬 데이터와 매핑하여 roomIdMap 초기화
     */
    private void initializeRoomIdMappingFromBackend() {
        this.roomIdMap = new HashMap<>();

        // 백엔드에서 모든 강의실 목록 조회
        roomService.getAllRooms(
                // 성공 시 콜백
                backendRooms -> {
                    logger.info("백엔드에서 강의실 목록 조회 성공: {}개 강의실", backendRooms.size());

                    // 백엔드 데이터와 로컬 데이터 매핑
                    mapBackendRoomsToLocal(backendRooms);

                    logger.info("roomIdMap 매핑 완료: {}개 항목", roomIdMap.size());

                    // 매핑 완료 후 로그 출력
                    roomIdMap.forEach((name, id) -> logger.debug("매핑: {} -> roomId {}", name, id));
                },
                // 오류 시 콜백
                errorMessage -> {
                    logger.error("백엔드 강의실 목록 조회 실패: {}", errorMessage);
                    // 폴백: 임시 매핑 데이터 사용
                    initializeFallbackMapping();
                    logger.warn("폴백 매핑 사용: {}개 항목", roomIdMap.size());
                });
    }

    /**
     * 백엔드 강의실 데이터를 로컬 강의실 데이터와 매핑
     */
    private void mapBackendRoomsToLocal(List<RoomDto> backendRooms) {
        // 백엔드 룸을 매핑키로 인덱싱
        Map<String, RoomDto> backendRoomMap = new HashMap<>();
        for (RoomDto room : backendRooms) {
            String key = room.getBuilding() + "_" + room.getName();
            backendRoomMap.put(key, room);
        }

        // 로컬 강의실 데이터를 순회하면서 백엔드 데이터와 매칭
        for (Building building : localBuildings) {
            // 강의실 매핑
            for (Classroom classroom : building.getClassrooms()) {
                String localKey = building.getName() + "_" + classroom.getName();
                RoomDto backendRoom = backendRoomMap.get(localKey);

                if (backendRoom != null) {
                    roomIdMap.put(classroom.getName(), backendRoom.getId());
                    logger.debug("강의실 매핑: {} ({}) -> roomId {}",
                            classroom.getName(), localKey, backendRoom.getId());
                } else {
                    logger.warn("백엔드에서 매칭되지 않은 로컬 강의실: {} ({})",
                            classroom.getName(), localKey);
                }
            }

        }
    }

    /**
     * 백엔드 연결 실패 시 사용할 폴백 매핑
     */
    private void initializeFallbackMapping() {
        // 로컬 데이터 기반으로 임시 ID 할당
        Long currentId = 1L;

        for (Building building : localBuildings) {
            // 강의실에 임시 ID 할당
            for (Classroom classroom : building.getClassrooms()) {
                roomIdMap.put(classroom.getName(), currentId++);
            }

        }

        logger.info("폴백 매핑 완료: 강의실 {}개",
                localBuildings.stream().mapToInt(b -> b.getClassrooms().size()).sum());
    }

    /**
     * 자동 새로고침 타이머를 시작합니다.
     * 30초마다 백엔드에서 최신 예약 정보를 가져와서 버튼 색상을 업데이트합니다.
     */
    private void startAutoRefresh(List<Building> buildings) {
        // 기존 타이머가 있으면 중지
        if (refreshTimer != null) {
            refreshTimer.stop();
        }

        // 30초마다 실행되는 타이머 생성
        refreshTimer = new Timer(30000, e -> {
            // 현재 선택된 건물과 층이 있을 때만 새로고침
            String selectedBuilding = (String) buildingCombo.getSelectedItem();
            Integer selectedFloor = (Integer) floorCombo.getSelectedItem();

            if (selectedBuilding != null && selectedFloor != null) {
                System.out.println("자동 새로고침 실행: " + selectedBuilding + " " + selectedFloor + "층");

                // 현재 날짜의 예약 정보를 다시 가져와서 업데이트
                LocalDate today = LocalDate.now();
                reservationService.getReservationsByDate(today,
                        reservations -> {
                            javax.swing.SwingUtilities.invokeLater(() -> {
                                updateMapWithReservations(buildings, selectedBuilding, selectedFloor, reservations);
                            });
                        },
                        errorMessage -> {
                            // 오류 시에는 조용히 실패 (사용자에게 알리지 않음)
                            System.err.println("자동 새로고침 실패: " + errorMessage);
                        });
            }
        });

        // 타이머 시작
        refreshTimer.start();
        System.out.println("자동 새로고침 타이머 시작됨 (30초 간격)");
    }

    /**
     * 윈도우가 닫힐 때 타이머를 정리합니다.
     */
    private void cleanup() {
        if (refreshTimer != null) {
            refreshTimer.stop();
            refreshTimer = null;
            System.out.println("자동 새로고침 타이머 중지됨");
        }
    }

    /**
     * 메인 윈도우 설정
     */
    private void setupMainWindow() {
        setTitle("🏫 강의실 예약 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 750);
        setResizable(true); // 크기 조정 가능하도록 변경
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // 윈도우 종료 시 타이머 정리
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                cleanup();
                System.exit(0);
            }
        });

        // 윈도우 아이콘 설정 (있다면)
        try {
            URL iconUrl = getClass().getResource("/images/icon.jpg");
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
        topPanel.setBackground(TOPBAR_COLOR);
        topPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COMBO_BORDER),
                new EmptyBorder(15, 20, 15, 20)));

        // 상단 좌측 - 빈 공간 (필요시 추가 메뉴 배치 가능)
        JPanel topMenu = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topMenu.setBackground(TOPBAR_COLOR);
        topPanel.add(topMenu, BorderLayout.WEST);

        // 상단 중앙 콤보박스
        JPanel topRoom = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        topRoom.setBackground(TOPBAR_COLOR);

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

        // 상단 우측 - 현재 시간 표시 및 사용자 드롭다운
        JPanel topButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        topButtons.setBackground(TOPBAR_COLOR);

        // 현재 시간 표시 라벨
        JLabel timeLabel = createStyledLabel(
                "🕒 " + java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));

        // 시간 표시를 업데이트하는 타이머 (1초마다)
        Timer timeUpdateTimer = new Timer(1000, e -> {
            String currentTime = java.time.LocalTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss"));
            timeLabel.setText("🕒 " + currentTime);
        });
        timeUpdateTimer.start();

        JButton userDropdownBtn = createUserDropdownButton();

        topButtons.add(timeLabel);
        topButtons.add(Box.createHorizontalStrut(20));
        topButtons.add(userDropdownBtn);
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

        // 기본 스타일
        comboBox.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        comboBox.setBackground(COMBO_BACKGROUND);
        comboBox.setForeground(TEXT_COLOR);
        comboBox.setOpaque(true);
        comboBox.setFocusable(true);
        comboBox.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 크기 설정
        comboBox.setPreferredSize(new Dimension(140, 38));

        // 커스텀 UI 설정
        comboBox.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = new JButton();
                button.setBackground(COMBO_BACKGROUND);
                button.setBorder(BorderFactory.createEmptyBorder());
                button.setFocusable(false);
                button.setContentAreaFilled(false);

                // 커스텀 화살표 아이콘
                button.setText("▼");
                button.setFont(new Font("맑은 고딕", Font.BOLD, 10));
                button.setForeground(TEXT_COLOR);

                return button;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (hasFocus || comboBox.isPopupVisible()) {
                    g2d.setColor(COMBO_HOVER);
                } else {
                    g2d.setColor(COMBO_BACKGROUND);
                }
                g2d.fillRoundRect(bounds.x, bounds.y, bounds.width, bounds.height, 6, 6);
                g2d.dispose();
            }
        });

        // 테두리 설정
        comboBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COMBO_BORDER, 1),
                BorderFactory.createEmptyBorder(6, 12, 6, 8)));

        // 마우스 이벤트 추가
        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (comboBox.isEnabled()) {
                    comboBox.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(PRIMARY_COLOR, 2),
                            BorderFactory.createEmptyBorder(5, 11, 5, 7)));
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (comboBox.isEnabled()) {
                    comboBox.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(COMBO_BORDER, 1),
                            BorderFactory.createEmptyBorder(6, 12, 6, 8)));
                }
            }
        });

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
     * 사용자 드롭다운 버튼 생성
     */
    private JButton createUserDropdownButton() {
        String userDisplayText = "👤 " + currentUser.getName() + "(" + currentUser.getStudentId() + ")";
        JButton userBtn = createStyledButton(userDisplayText + " ▼", PRIMARY_COLOR);

        // 팝업 메뉴 생성
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(COMBO_BORDER, 1));
        popupMenu.setBackground(Color.WHITE);

        // 내 예약 메뉴 아이템
        JMenuItem myReservationItem = createStyledMenuItem("📅 내 예약");
        myReservationItem.addActionListener(e -> {
            if (myResView == null) {
                myResView = ControllerFactory.getInstance().createMyReservationView(currentUser);
            }
            myResView.setVisible(true);
        });

        // 내 정보 메뉴 아이템
        JMenuItem myInfoItem = createStyledMenuItem("⚙️ 내 정보");
        myInfoItem.addActionListener(e -> {
            if (myInfoView == null) {
                myInfoView = ControllerFactory.getInstance().createMyInformationView(currentUser);
            }
            myInfoView.setVisible(true);
        });

        // 구분선
        JSeparator separator = new JSeparator();

        // 로그아웃 메뉴 아이템
        JMenuItem logoutItem = createStyledMenuItem("🚪 로그아웃");
        logoutItem.addActionListener(e -> {
            int result = JOptionPane.showConfirmDialog(
                    this,
                    "정말 로그아웃하시겠습니까?",
                    "로그아웃 확인",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);
            if (result == JOptionPane.YES_OPTION) {
                // TokenManager에서 인증 정보 삭제
                TokenManager.getInstance().clearAuthentication();
                // 프로그램 종료 (추후 로그인 화면으로 돌아가도록 개선 가능)
                System.exit(0);
            }
        });

        popupMenu.add(myReservationItem);
        popupMenu.add(myInfoItem);
        popupMenu.add(separator);
        popupMenu.add(logoutItem);

        // 버튼 클릭 시 팝업 메뉴 표시
        userBtn.addActionListener(e -> {
            popupMenu.show(userBtn, 0, userBtn.getHeight());
        });

        return userBtn;
    }

    /**
     * 스타일이 적용된 메뉴 아이템 생성
     */
    private JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        menuItem.setForeground(TEXT_COLOR);
        menuItem.setBackground(Color.WHITE);
        menuItem.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // 호버 효과
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(new Color(236, 240, 241));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE);
            }
        });

        return menuItem;
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
     * 백엔드 API에서 실시간 예약 정보를 가져와서 현재 시간 기준으로 사용 가능 여부를 판단합니다.
     * 
     * @param buildings 건물 리스트
     *
     *                  - 각 강의실/시설물의 좌표(x, y)에 버튼을 배치
     *                  - 버튼 클릭 시 컨트롤러의 onReservationClicked 호출
     *                  - 백엔드 API 예약 정보를 바탕으로 실시간 사용 가능 여부 판단
     *                  - 콤보박스 변경에 따라 건물/층 구조도 png 변경
     */
    private void updateMap(List<Building> buildings) {
        mapPanel.removeAll(); // 기존 버튼 제거
        String selectedBuilding = (String) buildingCombo.getSelectedItem();
        Integer selectedFloor = (Integer) floorCombo.getSelectedItem();
        if (selectedFloor == null)
            return; // 층이 선택되지 않은 경우 종료

        // 현재 날짜의 예약 정보를 백엔드에서 가져와서 버튼 색상 업데이트
        LocalDate today = LocalDate.now();
        reservationService.getReservationsByDate(today,
                // 성공 시 콜백
                reservations -> {
                    // UI 업데이트는 EDT에서 실행
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        updateMapWithReservations(buildings, selectedBuilding, selectedFloor, reservations);
                    });
                },
                // 오류 시 콜백
                errorMessage -> {
                    // API 호출 실패 시 기본 로직으로 폴백
                    System.err.println("예약 정보 조회 실패, 기본 로직 사용: " + errorMessage);
                    javax.swing.SwingUtilities.invokeLater(() -> {
                        updateMapWithReservations(buildings, selectedBuilding, selectedFloor, null);
                    });
                });
    }

    /**
     * 예약 정보를 바탕으로 실제 맵을 업데이트하는 헬퍼 메서드
     */
    private void updateMapWithReservations(List<Building> buildings, String selectedBuilding,
            Integer selectedFloor, List<ReservationDto> reservations) {
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

                            // 백엔드 예약 정보를 바탕으로 현재 사용 가능 여부 판단
                            boolean isAvailable = reservations != null
                                    ? AvailabilityChecker.isCurrentlyAvailable(c, reservations, roomIdMap)
                                    : c.isAvailable(); // API 실패 시 기본값 사용

                            JButton btn = createRoomButton(c.getName(), isAvailable, reservations);
                            btn.setBounds(c.getX(), c.getY(), 110, 60); // 크기를 약간 키움
                            btn.addActionListener(e -> controller.onReservationClicked(c));
                            mapPanel.add(btn);
                        } catch (IOException e) {
                            System.err.println("이미지 로드 실패: " + e.getMessage());
                        }
                    }
                }

            }
        }

        mapPanel.repaint();
        mapPanel.revalidate();
    }

    /**
     * 강의실 버튼을 생성하고 상태에 따라 색상/글자색을 지정합니다.
     * 
     * @param name      강의실 이름
     * @param available 가용 여부 (true: 비어있음, false: 사용중)
     * @return JButton 객체
     */
    private JButton createRoomButton(String name, boolean available) {
        return createRoomButton(name, available, null);
    }

    /**
     * 강의실 버튼을 생성하고 예약 정보를 바탕으로 상태에 따라 색상/글자색을 지정합니다.
     * 
     * @param name         강의실 이름
     * @param available    가용 여부 (true: 비어있음, false: 사용중)
     * @param reservations 현재 날짜의 예약 정보 (추가 정보 표시용)
     * @return JButton 객체
     */
    private JButton createRoomButton(String name, boolean available, List<ReservationDto> reservations) {
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

        // 향상된 툴팁 추가 (예약 정보 포함)
        String statusText = available ? "예약 가능" : "사용 중";
        String toolTipText = name + " - " + statusText;

        // 예약 정보가 있고 roomIdMap에 해당 강의실이 있으면 추가 정보 표시
        if (reservations != null && roomIdMap.containsKey(name)) {
            Long roomId = roomIdMap.get(name);
            LocalDate today = LocalDate.now();

            // 오늘 해당 강의실의 예약 정보 필터링
            List<ReservationDto> todaysReservations = reservations.stream()
                    .filter(r -> r.getRoomId().equals(roomId) && r.getDate().equals(today))
                    .collect(java.util.stream.Collectors.toList());

            if (!todaysReservations.isEmpty()) {
                toolTipText += "\n\n📅 오늘의 예약:";
                for (ReservationDto reservation : todaysReservations) {
                    toolTipText += "\n• " + reservation.getStartTime() + " - " + reservation.getEndTime() +
                            " (학번: " + reservation.getStudentId() + ")";
                }
            }

            // 다음 예약까지 남은 시간 정보 추가
            if (available) {
                Integer minutesToNext = AvailabilityChecker.getMinutesToNextReservation(
                        new org.javateam11.ClassroomReservation.model.Classroom(name, "", 0, 0, 0),
                        reservations, roomIdMap);
                if (minutesToNext != null) {
                    int hours = minutesToNext / 60;
                    int minutes = minutesToNext % 60;
                    if (hours > 0) {
                        toolTipText += "\n⏰ 다음 예약까지: " + hours + "시간 " + minutes + "분";
                    } else {
                        toolTipText += "\n⏰ 다음 예약까지: " + minutes + "분";
                    }
                }
            }
        }

        toolTipText += "\n\n클릭하여 예약";
        btn.setToolTipText("<html>" + toolTipText.replace("\n", "<br>") + "</html>");

        return btn;
    }

    /**
     * 예약 다이얼로그를 띄워 사용자 입력을 받고, ReservationHandler로 결과를 전달합니다.
     * 
     * @param name    강의실 이름
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