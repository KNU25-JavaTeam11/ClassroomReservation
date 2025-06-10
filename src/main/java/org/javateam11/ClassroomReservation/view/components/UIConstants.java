package org.javateam11.ClassroomReservation.view.components;

import java.awt.Color;

/**
 * UI에서 사용하는 색상 상수들을 관리하는 클래스
 */
public class UIConstants {

    // 기본 색상들
    public static final Color PRIMARY_COLOR = new Color(41, 128, 185); // 블루
    public static final Color SUCCESS_COLOR = new Color(39, 174, 96); // 녹색
    public static final Color DANGER_COLOR = new Color(231, 76, 60); // 빨강
    public static final Color WARNING_COLOR = new Color(241, 196, 15); // 노랑
    public static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // 연한 회색
    public static final Color TEXT_COLOR = new Color(44, 62, 80); // 다크 그레이
    public static final Color HOVER_COLOR = new Color(52, 152, 219); // 밝은 블루

    // 특정 컴포넌트 색상들
    public static final Color TOPBAR_COLOR = new Color(248, 249, 250); // 상단바 색상 (매우 연한 회색)
    public static final Color COMBO_BACKGROUND = new Color(255, 255, 255); // 콤보박스 배경
    public static final Color COMBO_BORDER = new Color(189, 195, 199); // 콤보박스 테두리
    public static final Color COMBO_HOVER = new Color(231, 236, 239); // 콤보박스 호버

    // 상태 표시 색상들
    public static final Color AVAILABLE_COLOR = SUCCESS_COLOR;
    public static final Color OCCUPIED_COLOR = DANGER_COLOR;
    public static final Color SELECTED_COLOR = PRIMARY_COLOR;

    private UIConstants() {
        // 유틸리티 클래스이므로 인스턴스 생성 방지
    }
}