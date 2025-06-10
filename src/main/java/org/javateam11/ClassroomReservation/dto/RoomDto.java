package org.javateam11.ClassroomReservation.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 백엔드 API에서 받아오는 강의실 정보 DTO
 * 백엔드 Room 엔티티에 대응
 */
public class RoomDto {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("building")
    private String building;

    @JsonProperty("name")
    private String name;

    @JsonProperty("floor")
    private Integer floor;

    // 기본 생성자
    public RoomDto() {
    }

    // 전체 필드를 받는 생성자
    public RoomDto(Long id, String building, String name, Integer floor) {
        this.id = id;
        this.building = building;
        this.name = name;
        this.floor = floor;
    }

    // Getter 메서드들
    public Long getId() {
        return id;
    }

    public String getBuilding() {
        return building;
    }

    public String getName() {
        return name;
    }

    public Integer getFloor() {
        return floor;
    }

    // Setter 메서드들
    public void setId(Long id) {
        this.id = id;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFloor(Integer floor) {
        this.floor = floor;
    }

    /**
     * 로컬 강의실 데이터와 매칭하기 위한 키 생성
     * "건물명_강의실명" 형태로 반환
     */
    public String getMatchingKey() {
        return building + "_" + name;
    }

    @Override
    public String toString() {
        return "RoomDto{" +
                "id=" + id +
                ", building='" + building + '\'' +
                ", name='" + name + '\'' +
                ", floor=" + floor +
                '}';
    }
}