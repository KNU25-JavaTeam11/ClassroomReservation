package org.javateam11.ClassroomReservation.view.components;

import org.javateam11.ClassroomReservation.model.Building;
import org.javateam11.ClassroomReservation.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 메인 화면의 상단 패널을 담당하는 컴포넌트
 * - 건물/층 선택 콤보박스
 * - 현재 시간 표시
 * - 사용자 드롭다운 메뉴
 */
public class TopPanel extends JPanel {

    // UI 색상 상수들
    private static final Color TOPBAR_COLOR = new Color(248, 249, 250);
    private static final Color COMBO_BORDER = new Color(189, 195, 199);

    private JComboBox<String> buildingCombo;
    private JComboBox<Integer> floorCombo;
    private JLabel timeLabel;
    private Timer timeUpdateTimer;
    private User currentUser;

    // 사용자 드롭다운 버튼과 우측 패널 참조
    private JButton userDropdownBtn;
    private JPanel rightPanel;

    // 콜백 인터페이스들
    private Runnable myReservationCallback;
    private Runnable myInfoCallback;
    private Runnable logoutCallback;

    /**
     * TopPanel 생성자
     */
    public TopPanel(List<Building> buildings, User currentUser) {
        this.currentUser = currentUser;

        setLayout(new BorderLayout());
        setBackground(TOPBAR_COLOR);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, COMBO_BORDER),
                new EmptyBorder(15, 20, 15, 20)));

        initializeComponents(buildings);
        setupLayout();
        startTimeUpdate();
    }

    /**
     * 컴포넌트들을 초기화합니다.
     */
    private void initializeComponents(List<Building> buildings) {
        // 건물 콤보박스 초기화
        buildingCombo = StyleManager.createStyledComboBox();
        for (Building building : buildings) {
            buildingCombo.addItem(building.getName());
        }

        // 층 콤보박스 초기화
        floorCombo = StyleManager.createStyledComboBox();

        // 시간 라벨 초기화
        updateTimeLabel();
    }

    /**
     * 레이아웃을 설정합니다.
     */
    private void setupLayout() {
        // 상단 좌측 - 빈 공간 (필요시 추가 메뉴 배치 가능)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(TOPBAR_COLOR);

        // 상단 중앙 - 건물/층 선택 콤보박스
        JPanel centerPanel = createSelectionPanel();

        // 상단 우측 - 시간 표시 및 사용자 드롭다운
        rightPanel = createRightPanel();

        add(leftPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);
    }

    /**
     * 건물/층 선택 패널을 생성합니다.
     */
    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
        panel.setBackground(TOPBAR_COLOR);

        JLabel buildingLabel = StyleManager.createStyledLabel("건물:");
        JLabel floorLabel = StyleManager.createStyledLabel("층:");

        panel.add(buildingLabel);
        panel.add(buildingCombo);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(floorLabel);
        panel.add(floorCombo);

        return panel;
    }

    /**
     * 우측 패널(시간 표시 + 사용자 메뉴)을 생성합니다.
     */
    private JPanel createRightPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(TOPBAR_COLOR);

        timeLabel = StyleManager.createStyledLabel("");

        // 초기에는 기본 사용자 버튼만 생성 (callback 없이)
        String userDisplayText = "👤 " + currentUser.getName() + "(" + currentUser.getStudentId() + ") ▼";
        userDropdownBtn = StyleManager.createStyledButton(userDisplayText, StyleManager.getPrimaryColor());

        panel.add(timeLabel);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(userDropdownBtn);

        return panel;
    }

    /**
     * 사용자 드롭다운 버튼을 업데이트합니다.
     */
    private void updateUserDropdownButton() {
        if (rightPanel != null && userDropdownBtn != null) {
            // 기존 버튼 제거
            rightPanel.remove(userDropdownBtn);

            // 새로운 사용자 드롭다운 버튼 생성
            userDropdownBtn = UserDropdownPanel.createUserDropdownButton(
                    currentUser,
                    myReservationCallback,
                    myInfoCallback,
                    logoutCallback);

            // 새 버튼 추가
            rightPanel.add(userDropdownBtn);

            // UI 업데이트
            rightPanel.revalidate();
            rightPanel.repaint();
        }
    }

    /**
     * 시간 표시를 업데이트합니다.
     */
    private void updateTimeLabel() {
        String currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        if (timeLabel != null) {
            timeLabel.setText(currentTime);
        }
    }

    /**
     * 시간 업데이트 타이머를 시작합니다.
     */
    private void startTimeUpdate() {
        timeUpdateTimer = new Timer(1000, e -> updateTimeLabel());
        timeUpdateTimer.start();
    }

    /**
     * 리소스를 정리합니다.
     */
    public void cleanup() {
        if (timeUpdateTimer != null) {
            timeUpdateTimer.stop();
            timeUpdateTimer = null;
        }
    }

    // Getter 메서드들
    public JComboBox<String> getBuildingCombo() {
        return buildingCombo;
    }

    public JComboBox<Integer> getFloorCombo() {
        return floorCombo;
    }

    // 이벤트 리스너 설정 메서드들
    public void setBuildingChangeListener(ActionListener listener) {
        if (buildingCombo != null) {
            buildingCombo.addActionListener(listener);
        }
    }

    public void setFloorChangeListener(ActionListener listener) {
        if (floorCombo != null) {
            floorCombo.addActionListener(listener);
        }
    }

    public void setMyReservationCallback(Runnable callback) {
        this.myReservationCallback = callback;
        updateUserDropdownButton();
    }

    public void setMyInfoCallback(Runnable callback) {
        this.myInfoCallback = callback;
        updateUserDropdownButton();
    }

    public void setLogoutCallback(Runnable callback) {
        this.logoutCallback = callback;
        updateUserDropdownButton();
    }
}