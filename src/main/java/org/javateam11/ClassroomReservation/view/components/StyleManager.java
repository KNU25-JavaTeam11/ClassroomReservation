package org.javateam11.ClassroomReservation.view.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UI 컴포넌트 스타일링을 위한 유틸리티 클래스
 */
public class StyleManager {

    // UI 색상 상수들
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private static final Color SUCCESS_COLOR = new Color(39, 174, 96);
    private static final Color DANGER_COLOR = new Color(231, 76, 60);
    private static final Color WARNING_COLOR = new Color(241, 196, 15);
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241);
    private static final Color TEXT_COLOR = new Color(44, 62, 80);
    private static final Color HOVER_COLOR = new Color(52, 152, 219);
    private static final Color TOPBAR_COLOR = new Color(248, 249, 250);
    private static final Color COMBO_BACKGROUND = new Color(255, 255, 255);
    private static final Color COMBO_BORDER = new Color(189, 195, 199);
    private static final Color COMBO_HOVER = new Color(231, 236, 239);

    /**
     * 스타일이 적용된 버튼 생성
     */
    public static JButton createStyledButton(String text, Color backgroundColor) {
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
    public static <T> JComboBox<T> createStyledComboBox() {
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
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("맑은 고딕", Font.BOLD, 13));
        label.setForeground(TEXT_COLOR);
        return label;
    }

    /**
     * 스타일이 적용된 메뉴 아이템 생성
     */
    public static JMenuItem createStyledMenuItem(String text) {
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
     * 스타일이 적용된 텍스트필드 생성
     */
    public static JTextField createStyledTextField() {
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
     * 강의실 버튼을 생성하고 상태에 따라 색상/글자색을 지정합니다.
     */
    public static JButton createRoomButton(String name, boolean available) {
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

        return btn;
    }

    // Getter 메서드들 (상수 접근용)
    public static Color getPrimaryColor() {
        return PRIMARY_COLOR;
    }

    public static Color getSuccessColor() {
        return SUCCESS_COLOR;
    }

    public static Color getDangerColor() {
        return DANGER_COLOR;
    }

    public static Color getWarningColor() {
        return WARNING_COLOR;
    }

    public static Color getBackgroundColor() {
        return BACKGROUND_COLOR;
    }

    public static Color getTextColor() {
        return TEXT_COLOR;
    }

    public static Color getHoverColor() {
        return HOVER_COLOR;
    }

    public static Color getTopbarColor() {
        return TOPBAR_COLOR;
    }

    public static Color getComboBackground() {
        return COMBO_BACKGROUND;
    }

    public static Color getComboBorder() {
        return COMBO_BORDER;
    }

    public static Color getComboHover() {
        return COMBO_HOVER;
    }
}