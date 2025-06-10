package org.javateam11.ClassroomReservation.view;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

/**
 * MapPanel은 png파일을 사용하여 구조도를 변경하기 위해 만든 클래스입니다.
 * 고품질 렌더링과 부드러운 시각적 효과를 제공합니다.
 */
public class MapPanel extends JPanel {
    private BufferedImage backgroundImage;

    public MapPanel() {
        setOpaque(true);
        setBackground(new Color(236, 240, 241)); // 연한 회색 배경
    }

    /**
     * updateMap 메소드에서 받아온 이미지를 현재 배경으로 설정한 뒤 repaint하는 메소드입니다.
     * 
     * @param img 현재 건물 구조도
     */
    public void setBackgroundImage(BufferedImage img) {
        this.backgroundImage = img;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 고품질 렌더링 설정
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        if (backgroundImage != null) {
            // 이미지를 패널 크기에 맞게 조정하여 그리기
            g2d.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            // 배경 이미지 위에 그리드 패턴 그리기 (투명도 낮춤)
            drawGridPattern(g2d, true);
        } else {
            // 배경 이미지가 없을 때 기본 패턴과 메시지 그리기
            drawGridPattern(g2d, false);
            drawDefaultMessage(g2d);
        }

        g2d.dispose();
    }

    /**
     * 그리드 패턴을 그립니다.
     * 
     * @param g2d            Graphics2D 객체
     * @param withBackground 배경 이미지가 있는지 여부
     */
    private void drawGridPattern(Graphics2D g2d, boolean withBackground) {
        int width = getWidth();
        int height = getHeight();

        // 배경 이미지가 있을 때는 더 투명하게, 없을 때는 덜 투명하게
        int alpha = withBackground ? 30 : 50;
        g2d.setColor(new Color(200, 200, 200, alpha));
        g2d.setStroke(new BasicStroke(1));

        int gridSize = 50;
        for (int x = 0; x < width; x += gridSize) {
            g2d.drawLine(x, 0, x, height);
        }
        for (int y = 0; y < height; y += gridSize) {
            g2d.drawLine(0, y, width, y);
        }
    }

    /**
     * 배경 이미지가 없을 때 표시할 기본 메시지를 그립니다.
     */
    private void drawDefaultMessage(Graphics2D g2d) {
        int width = getWidth();
        int height = getHeight();

        // 중앙에 안내 텍스트
        g2d.setColor(new Color(149, 165, 166));
        g2d.setFont(new Font("맑은 고딕", Font.BOLD, 16));
        String message = "건물과 층을 선택하세요";
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(message);
        int textHeight = fm.getHeight();
        g2d.drawString(message,
                (width - textWidth) / 2,
                (height + textHeight) / 2);
    }
}
