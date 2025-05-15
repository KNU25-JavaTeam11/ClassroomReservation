package org.javateam11.ClassroomReservation.model;

/**
 * Classroom 클래스는 강의실 정보를 표현합니다.
 * Place(공통 필드) 상속.
 */
public class Classroom extends Place {

    /**
     * Classroom 생성자
     * @param name 강의실 이름
     * @param building 소속 건물명
     * @param floor 층 정보
     * @param x 2D 도면상의 x좌표
     * @param y 2D 도면상의 y좌표
     */
    public Classroom(String name, String building, int floor, int x, int y) {
        super(name, building, floor, x, y);
    }

    // Classroom만의 추가 필드/메서드가 있다면 여기에 작성
} 