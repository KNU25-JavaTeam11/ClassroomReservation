package org.javateam11.ClassroomReservation.view.components;

import org.javateam11.ClassroomReservation.model.Building;
import org.javateam11.ClassroomReservation.model.User;
import org.javateam11.ClassroomReservation.service.TokenManager;
import org.javateam11.ClassroomReservation.util.FontUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * 메인 화면의 상단 패널(건물/층 선택, 사용자 메뉴)을 구성하는 클래스
 */
public class TopPanelBuilder {

    public static JPanel createTopPanel(List<Building> buildings,
            JComboBox<String> buildingCombo,
            JComboBox<Integer> floorCombo,
            ActionListener buildingChangeListener,
            ActionListener floorChangeListener,
            ActionListener myReservationListener,
            ActionListener myInfoListener,
            ActionListener logoutListener) {

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(UIConstants.TOPBAR_COLOR);
        topPanel.setBorder(new EmptyBorder(15, 20, 15, 20));

        // 왼쪽: 건물/층 선택
        JPanel leftPanel = createSelectionPanel(buildings, buildingCombo, floorCombo,
                buildingChangeListener, floorChangeListener);

        // 오른쪽: 사용자 메뉴
        JPanel rightPanel = createUserPanel(myReservationListener, myInfoListener, logoutListener);

        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(rightPanel, BorderLayout.EAST);

        return topPanel;
    }

    private static JPanel createSelectionPanel(List<Building> buildings,
            JComboBox<String> buildingCombo,
            JComboBox<Integer> floorCombo,
            ActionListener buildingChangeListener,
            ActionListener floorChangeListener) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        panel.setBackground(UIConstants.TOPBAR_COLOR);

        // 건물 선택
        JLabel buildingLabel = StyleManager.createStyledLabel("건물:");

        // 기존 콤보박스 스타일 적용
        buildingCombo.setFont(FontUtils.getPlainFont());
        buildingCombo.setBackground(UIConstants.COMBO_BACKGROUND);
        buildingCombo.setBorder(BorderFactory.createLineBorder(UIConstants.COMBO_BORDER));
        buildingCombo.setFocusable(false);
        buildingCombo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 건물 목록 추가
        for (Building building : buildings) {
            buildingCombo.addItem(building.getName());
        }
        buildingCombo.addActionListener(buildingChangeListener);

        // 층 선택
        JLabel floorLabel = StyleManager.createStyledLabel("층:");

        floorCombo.setFont(FontUtils.getPlainFont());
        floorCombo.setBackground(UIConstants.COMBO_BACKGROUND);
        floorCombo.setBorder(BorderFactory.createLineBorder(UIConstants.COMBO_BORDER));
        floorCombo.setFocusable(false);
        floorCombo.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        floorCombo.addActionListener(floorChangeListener);

        panel.add(buildingLabel);
        panel.add(buildingCombo);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(floorLabel);
        panel.add(floorCombo);

        return panel;
    }

    private static JPanel createUserPanel(ActionListener myReservationListener,
            ActionListener myInfoListener,
            ActionListener logoutListener) {

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(UIConstants.TOPBAR_COLOR);

        // 현재 사용자 정보 가져오기
        TokenManager tokenManager = TokenManager.getInstance();
        String currentUserName = tokenManager.getCurrentName();
        if (currentUserName == null) {
            currentUserName = "사용자";
        }

        // 사용자 드롭다운 버튼
        JButton userButton = createUserDropdownButton(currentUserName,
                myReservationListener,
                myInfoListener,
                logoutListener);
        panel.add(userButton);

        return panel;
    }

    private static JButton createUserDropdownButton(String userName,
            ActionListener myReservationListener,
            ActionListener myInfoListener,
            ActionListener logoutListener) {

        JButton userButton = new JButton(userName + " ▼");
        userButton.setFont(FontUtils.getPlainFont());
        userButton.setBackground(UIConstants.PRIMARY_COLOR);
        userButton.setForeground(Color.WHITE);
        userButton.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        userButton.setFocusPainted(false);
        userButton.setOpaque(true);
        userButton.setBorderPainted(false);
        userButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 드롭다운 메뉴 생성
        JPopupMenu userMenu = new JPopupMenu();
        userMenu.setBorder(BorderFactory.createLineBorder(UIConstants.COMBO_BORDER));

        JMenuItem myReservationItem = StyleManager.createStyledMenuItem("내 예약");
        myReservationItem.addActionListener(myReservationListener);

        JMenuItem myInfoItem = StyleManager.createStyledMenuItem("내 정보");
        myInfoItem.addActionListener(myInfoListener);

        JMenuItem logoutItem = StyleManager.createStyledMenuItem("로그아웃");
        logoutItem.addActionListener(logoutListener);

        userMenu.add(myReservationItem);
        userMenu.addSeparator();
        userMenu.add(myInfoItem);
        userMenu.addSeparator();
        userMenu.add(logoutItem);

        // 버튼 클릭 시 메뉴 표시
        userButton.addActionListener(e -> {
            userMenu.show(userButton, 0, userButton.getHeight());
        });

        // 호버 효과
        userButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                userButton.setBackground(UIConstants.HOVER_COLOR);
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                userButton.setBackground(UIConstants.PRIMARY_COLOR);
            }
        });

        return userButton;
    }
}