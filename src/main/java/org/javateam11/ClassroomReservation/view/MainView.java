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
import org.javateam11.ClassroomReservation.view.components.*;

import org.javateam11.ClassroomReservation.model.Building;
import org.javateam11.ClassroomReservation.model.User;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
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
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // 연한 회색

    // 상단 패널 (건물/층 선택, 사용자 메뉴)
    private TopPanel topPanel;

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
            topPanel.getBuildingCombo().setSelectedIndex(0);
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
            String selectedBuilding = (String) topPanel.getBuildingCombo().getSelectedItem();
            Integer selectedFloor = (Integer) topPanel.getFloorCombo().getSelectedItem();

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

        if (topPanel != null) {
            topPanel.cleanup();
        }
    }

    /**
     * 메인 윈도우 설정
     */
    private void setupMainWindow() {
        setTitle("🏫 강의실 예약 시스템");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 800);
        setResizable(false);
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
        topPanel = new TopPanel(buildings, currentUser);

        // 콜백 설정
        topPanel.setMyReservationCallback(() -> {
            if (myResView == null) {
                myResView = ControllerFactory.getInstance().createMyReservationView(currentUser);
            }
            myResView.setVisible(true);
        });

        topPanel.setMyInfoCallback(() -> {
            if (myInfoView == null) {
                myInfoView = ControllerFactory.getInstance().createMyInformationView(currentUser);
            }
            myInfoView.setVisible(true);
        });

        topPanel.setLogoutCallback(() -> {
            // TokenManager에서 인증 정보 삭제
            TokenManager.getInstance().clearAuthentication();
            // 프로그램 종료 (추후 로그인 화면으로 돌아가도록 개선 가능)
            System.exit(0);
        });

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
        LegendPanel legendPanel = new LegendPanel();
        centerPanel.add(legendPanel, BorderLayout.SOUTH);

        add(centerPanel, BorderLayout.CENTER);
    }

    /**
     * 이벤트 리스너 설정
     */
    private void setupEventListeners(List<Building> buildings) {
        topPanel.setBuildingChangeListener(e -> updateFloors(buildings));
        topPanel.setFloorChangeListener(e -> updateMap(buildings));
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
        String selectedBuilding = (String) topPanel.getBuildingCombo().getSelectedItem();
        topPanel.getFloorCombo().removeAllItems(); // 기존 층 목록 삭제

        for (Building b : buildings) {
            if (b.getName().equals(selectedBuilding)) {
                // 해당 건물의 모든 층을 콤보박스에 추가
                for (Integer f : b.getFloors())
                    topPanel.getFloorCombo().addItem(f);
                if (!b.getFloors().isEmpty())
                    topPanel.getFloorCombo().setSelectedIndex(0); // 첫 층 자동 선택
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
        String selectedBuilding = (String) topPanel.getBuildingCombo().getSelectedItem();
        Integer selectedFloor = (Integer) topPanel.getFloorCombo().getSelectedItem();
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
     */
    public void showReservationDialog(String name, ReservationDialog.ReservationHandler handler) {
        ReservationDialog.showReservationDialog(this, name, handler);
    }

    /**
     * roomIdMap을 반환합니다.
     * 
     * @return 강의실 이름과 roomId 매핑 정보
     */
    public Map<String, Long> getRoomIdMap() {
        return roomIdMap;
    }

}