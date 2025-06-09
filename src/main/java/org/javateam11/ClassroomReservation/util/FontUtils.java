package org.javateam11.ClassroomReservation.util;

import java.awt.*;

/**
 * 크로스 플랫폼 폰트 호환성을 위한 유틸리티 클래스
 * 윈도우에서는 맑은 고딕, 맥에서는 Apple SD Gothic Neo 등 적절한 폰트를 사용
 */
public class FontUtils {

    private static final String[] KOREAN_FONTS = {
            "맑은 고딕", // Windows
            "Malgun Gothic", // Windows (영문명)
            "Apple SD Gothic Neo", // macOS
            "Apple Gothic", // macOS (old)
            "Noto Sans CJK KR", // Linux
            "NanumGothic", // Alternative
            "Dialog" // System default
    };

    private static String selectedFontName = null;

    /**
     * 시스템에서 사용 가능한 한국어 폰트를 찾아 반환
     */
    public static Font getKoreanFont(int style, int size) {
        if (selectedFontName == null) {
            selectedFontName = findBestKoreanFont();
        }

        return new Font(selectedFontName, style, size);
    }

    /**
     * 시스템에서 사용 가능한 최적의 한국어 폰트를 찾아 반환
     */
    private static String findBestKoreanFont() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] availableFonts = ge.getAvailableFontFamilyNames();

        // 사용 가능한 폰트 중에서 우선순위에 따라 선택
        for (String fontName : KOREAN_FONTS) {
            for (String availableFont : availableFonts) {
                if (availableFont.equals(fontName)) {
                    return fontName;
                }
            }
        }

        // 모든 폰트가 실패한 경우 시스템 기본 폰트 사용
        return Font.DIALOG;
    }

    /**
     * 일반 텍스트용 폰트 (14pt, PLAIN)
     */
    public static Font getPlainFont() {
        return getKoreanFont(Font.PLAIN, 14);
    }

    /**
     * 라벨용 폰트 (14pt, PLAIN)
     */
    public static Font getLabelFont() {
        return getKoreanFont(Font.PLAIN, 14);
    }

    /**
     * 버튼용 폰트 (14pt, BOLD)
     */
    public static Font getButtonFont() {
        return getKoreanFont(Font.BOLD, 14);
    }

    /**
     * 제목용 폰트 (20pt, BOLD)
     */
    public static Font getTitleFont() {
        return getKoreanFont(Font.BOLD, 20);
    }

    /**
     * 작은 텍스트용 폰트 (12pt, PLAIN)
     */
    public static Font getSmallFont() {
        return getKoreanFont(Font.PLAIN, 12);
    }

    /**
     * 사용자 정의 크기의 폰트
     */
    public static Font getCustomFont(int style, int size) {
        return getKoreanFont(style, size);
    }

    /**
     * 현재 사용중인 폰트 이름 반환 (디버깅용)
     */
    public static String getCurrentFontName() {
        Font font = getKoreanFont(Font.PLAIN, 14);
        return font.getFontName();
    }
}