package org.javateam11.ClassroomReservation.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Building 클래스는 하나의 건물 정보를 표현합니다.
 * 각 건물은 여러 층, 강의실, 시설물을 가질 수 있습니다.
 */
public class Building {

    // 건물 이름
    private String name;

    // 건물 내 존재하는 층 리스트
    private List<Integer> floors;

    // 강의실 리스트
    private List<Classroom> classrooms;

    // 시설물 리스트
    private List<Facility> facilities;

    /**
     * Building 생성자
     * @param name 건물 이름
     * @param floors 존재하는 층 리스트
     */
    public Building(String name, List<Integer> floors) {
        this.name = name;
        this.floors = floors;
        this.classrooms = new ArrayList<>();
        this.facilities = new ArrayList<>();
    }

    // 각 필드의 getter 및 강의실/시설물 추가 메서드
    public String getName() { return name; }
    public List<Integer> getFloors() { return floors; }
    public List<Classroom> getClassrooms() { return classrooms; }
    public List<Facility> getFacilities() { return facilities; }
    public void addClassroom(Classroom classroom) { this.classrooms.add(classroom); }
    public void addFacility(Facility facility) { this.facilities.add(facility); }
} 