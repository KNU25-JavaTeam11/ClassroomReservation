package org.javateam11.ClassroomReservation;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.controller.MainController;
import java.util.*;

/**
 * 프로그램 진입점(Main 클래스)
 * 샘플 건물/강의실/시설물 데이터를 생성하고, 컨트롤러를 실행합니다.
 */
public class Main {
    
    /**
     * main 메서드: 프로그램 실행 시작점
     */
    public static void main(String[] args) {
        // 샘플 건물, 강의실, 시설물 데이터 생성
        List<Building> buildings = new ArrayList<>();

        // IT4 건물 (1, 2층)
        Building it4 = new Building("IT4", Arrays.asList(1,2));
        it4.addClassroom(new Classroom("401", "IT4", 1, 100, 100)); // 1층 401호
        it4.addClassroom(new Classroom("402", "IT4", 1, 250, 100)); // 1층 402호
        it4.addFacility(new Facility("프린터", "IT4", 1, 400, 100)); // 1층 프린터
        it4.addClassroom(new Classroom("501", "IT4", 2, 100, 200)); // 2층 501호
        it4.addClassroom(new Classroom("502", "IT4", 2, 250, 200)); // 2층 502호
        it4.addFacility(new Facility("빔프로젝터", "IT4", 2, 400, 200)); // 2층 빔프로젝터
        buildings.add(it4);

        // IT5 건물 (1층)
        Building it5 = new Building("IT5", Arrays.asList(1));
        it5.addClassroom(new Classroom("101", "IT5", 1, 100, 100)); // 1층 101호
        it5.addFacility(new Facility("프린터", "IT5", 1, 400, 100)); // 1층 프린터
        buildings.add(it5);

        // 컨트롤러 실행 (MVC 구조의 시작점)
        new MainController(buildings).show();
    }
}