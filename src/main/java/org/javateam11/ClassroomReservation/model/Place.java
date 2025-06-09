package org.javateam11.ClassroomReservation.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Place는 강의실(Classroom)과 시설물(Facility)의 공통 필드를 가진 추상 클래스입니다.
 */
public abstract class Place {

    // 이름
    protected String name;

    // 소속 건물명
    protected String building;

    // 층 정보
    protected int floor;

    // 2D 도면상의 위치 좌표
    protected int x, y;

    // 현재 가용 상태 (true: 비어있음, false: 사용중)
    protected boolean available;

    // 예약 정보 리스트
    protected List<Reservation> reservations;

    /**
     * Place 생성자
     */
    public Place(String name, String building, int floor, int x, int y) {
        this.name = name;
        this.building = building;
        this.floor = floor;
        this.x = x;
        this.y = y;
        this.available = true;
        this.reservations = new ArrayList<>();
    }

    // 공통 getter/setter 및 예약 관련 메서드
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