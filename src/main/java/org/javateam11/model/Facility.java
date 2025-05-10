package org.javateam11.model;

/**
 * Facility 클래스는 프린터, 빔프로젝터 등 강의실 외의 시설물 정보를 표현합니다.
 * Place(공통 필드) 상속.
 */
public class Facility extends Place {

    /**
     * Facility 생성자
     * @param name 시설물 이름
     * @param building 소속 건물명
     * @param floor 층 정보
     * @param x 2D 도면상의 x좌표
     * @param y 2D 도면상의 y좌표
     */
    public Facility(String name, String building, int floor, int x, int y) {
        super(name, building, floor, x, y);
    }

    // Facility만의 추가 필드/메서드가 있다면 여기에 작성
} 