package org.javateam11.ClassroomReservation.view.components;

import org.javateam11.ClassroomReservation.util.FontUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * UI 컴포넌트의 스타일링을 담당하는 클래스
 */
public class StyleManager {

    /**
     * 스타일이 적용된 버튼을 생성합니다.
     */
    public static JButton createStyledButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(FontUtils.getButtonFont());
        button.setBackground(backgroundColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 호버 효과 추가
        button.addMouseListener(new MouseAdapter() {
            private Color originalColor = backgroundColor;

            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(originalColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalColor);
            }
        });

        return button;
    }

    /**
     * 스타일이 적용된 콤보박스를 생성합니다.
     */
    @SuppressWarnings("unchecked")
    public static <T> JComboBox<T> createStyledComboBox() {
        JComboBox<T> comboBox = new JComboBox<>();
        comboBox.setFont(FontUtils.getPlainFont());
        comboBox.setBackground(UIConstants.COMBO_BACKGROUND);
        comboBox.setBorder(BorderFactory.createLineBorder(UIConstants.COMBO_BORDER));
        comboBox.setFocusable(false);
        comboBox.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        // 커스텀 UI 적용
        comboBox.setUI(new BasicComboBoxUI() {
            @Override
            protected JButton createArrowButton() {
                JButton button = super.createArrowButton();
                button.setBackground(UIConstants.COMBO_BACKGROUND);
                button.setBorder(BorderFactory.createEmptyBorder());
                return button;
            }

            @Override
            public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
                g.setColor(UIConstants.COMBO_BACKGROUND);
                g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
            }
        });

        // 호버 효과 추가
        comboBox.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (comboBox.isEnabled()) {
                    comboBox.setBackground(UIConstants.COMBO_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (comboBox.isEnabled()) {
                    comboBox.setBackground(UIConstants.COMBO_BACKGROUND);
                }
            }
        });

        return comboBox;
    }

    /**
     * 스타일이 적용된 라벨을 생성합니다.
     */
    public static JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FontUtils.getLabelFont());
        label.setForeground(UIConstants.TEXT_COLOR);
        return label;
    }

    /**
     * 스타일이 적용된 메뉴 아이템을 생성합니다.
     */
    public static JMenuItem createStyledMenuItem(String text) {
        JMenuItem menuItem = new JMenuItem(text);
        menuItem.setFont(FontUtils.getPlainFont());
        menuItem.setBackground(Color.WHITE);
        menuItem.setBorder(new EmptyBorder(8, 16, 8, 16));

        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                menuItem.setBackground(UIConstants.COMBO_HOVER);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                menuItem.setBackground(Color.WHITE);
            }
        });

        return menuItem;
    }

    /**
     * 스타일이 적용된 텍스트 필드를 생성합니다.
     */
    public static JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(FontUtils.getPlainFont());
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.COMBO_BORDER),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
        return textField;
    }
}