package org.javateam11.ClassroomReservation.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Classroom 클래스는 강의실 정보를 표현합니다.
 */
public class Classroom {

    // 이름
    private String name;

    // 소속 건물명
    private String building;

    // 층 정보
    private int floor;

    // 2D 도면상의 위치 좌표
    private int x, y;

    // 현재 가용 상태 (true: 비어있음, false: 사용중)
    private boolean available;

    // 예약 정보 리스트
    private List<Reservation> reservations;

    /**
     * Classroom 생성자
     * 
     * @param name     강의실 이름
     * @param building 소속 건물명
     * @param floor    층 정보
     * @param x        2D 도면상의 x좌표
     * @param y        2D 도면상의 y좌표
     */
    public Classroom(String name, String building, int floor, int x, int y) {
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.x = x;
        this.y = y;
        this.available = true;
        this.reservations = new ArrayList<>();
    }

    // getter/setter 및 예약 관련 메서드
    public String getName() {
        return name;
    }

    public String getBuilding() {
        return building;
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public List<Reservation> getReservations() {
        return reservations;
    }

    public void addReservation(Reservation reservation) {
        this.reservations.add(reservation);
    }
}