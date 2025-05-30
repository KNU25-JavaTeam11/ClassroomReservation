package org.javateam11.ClassroomReservation.view;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;


/**
 * MapPanel은 png파일을 사용하여 구조도를 변경하기 위해 만든 클래스입니다.
 */
public class MapPanel extends JPanel {
    private BufferedImage backgroundImage;
    
    /**
     * updateMap 메소드에서 받아온 이미지를 현재 배경으로 설정한 뒤 repaint하는 메소드입니다.
     * @param img 현재 건물 구조도
     */
    public void setBackgroundImage(BufferedImage img) {
        this.backgroundImage = img;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
