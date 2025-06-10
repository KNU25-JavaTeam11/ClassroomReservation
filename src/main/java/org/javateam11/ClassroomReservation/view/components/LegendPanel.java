package org.javateam11.ClassroomReservation.view.components;

import javax.swing.*;
import java.awt.*;

/**
 * 상태 범례를 표시하는 패널 컴포넌트
 */
public class LegendPanel extends JPanel {

    public LegendPanel() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(189, 195, 199)),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));

        initializeComponents();
    }

    /**
     * 범례 컴포넌트들을 초기화합니다.
     */
    private void initializeComponents() {
        // 예약 가능 표시
        JPanel availablePanel = createStatusPanel("✅", "예약 가능");

        // 사용 중 표시
        JPanel occupiedPanel = createStatusPanel("❌", "사용 중");

        // 설명 텍스트
        JLabel instructionText = new JLabel("💡 강의실을 클릭하여 예약하세요");
        instructionText.setFont(new Font("맑은 고딕", Font.PLAIN, 12));
        instructionText.setForeground(new Color(127, 140, 141));

        add(availablePanel);
        add(occupiedPanel);
        add(Box.createHorizontalStrut(30));
        add(instructionText);
    }

    /**
     * 상태 표시 패널을 생성합니다.
     */
    private JPanel createStatusPanel(String icon, String text) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        panel.setBackground(Color.WHITE);

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("맑은 고딕", Font.BOLD, 16));

        JLabel textLabel = StyleManager.createStyledLabel(text);

        panel.add(iconLabel);
        panel.add(textLabel);

        return panel;
    }
}