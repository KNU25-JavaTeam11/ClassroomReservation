package org.javateam11.ClassroomReservation.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Room 클래스는 강의실 정보를 표현합니다.
 */
public class Room {

    // 이름
    private String name;

    // 소속 건물명
    private String building;

    // 층 정보
    private int floor;

    // 2D 도면상의 위치 좌표
    private int x, y;

    /**
     * Room 생성자
     * 
     * @param name     강의실 이름
     * @param building 소속 건물명
     * @param floor    층 정보
     * @param x        2D 도면상의 x좌표
     * @param y        2D 도면상의 y좌표
     */
    public Room(String name, String building, int floor, int x, int y) {
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.x = x;
        this.y = y;
    }

    // getter/setter 및 예약 관련 메서드
    public String getName() {
        return name;
    }

    public String getBuildingName() {
        return building;
    }

    public int getFloor() {
        return floor;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}