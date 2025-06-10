package org.javateam11.ClassroomReservation;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.controller.ControllerFactory;
import org.javateam11.ClassroomReservation.view.LoginView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.swing.SwingUtilities;
import java.util.*;

/**
 * 프로그램 진입점(Main 클래스)
 * 로그인 화면을 띄우고, 성공 후 메인 애플리케이션을 실행합니다.
 */
public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    /**
     * main 메서드: 프로그램 실행 시작점
     */
    public static void main(String[] args) {
        logger.info("=== 강의실 예약 시스템 시작 ===");
        logger.info("Java 버전: {}", System.getProperty("java.version"));
        logger.info("운영체제: {} {}", System.getProperty("os.name"), System.getProperty("os.version"));

        try {
            // 샘플 건물, 강의실 데이터 생성
            logger.debug("샘플 데이터 생성 시작");
            List<Building> buildings = createSampleData();

            logger.info("샘플 데이터 로드 완료: {}개 건물, 총 {}개 강의실",
                    buildings.size(),
                    buildings.stream().mapToInt(b -> b.getClassrooms().size()).sum());

            // GUI를 Event Dispatch Thread에서 실행
            SwingUtilities.invokeLater(() -> {
                logger.debug("로그인 화면 표시");
                LoginView loginView = ControllerFactory.getInstance().createLoginView();
                loginView.setVisible(true);
            });

            logger.info("로그인 화면 표시 완료");

        } catch (Exception e) {
            logger.error("애플리케이션 시작 중 오류 발생", e);
            System.err.println("애플리케이션을 시작할 수 없습니다. 로그를 확인해주세요.");
            System.exit(1);
        }
    }

    /**
     * 샘플 데이터 생성
     */
    private static List<Building> createSampleData() {
        List<Building> buildings = new ArrayList<>();

        // IT4 건물 (1층)
        Building it4 = new Building("IT4", Arrays.asList(1));
        it4.addClassroom(new Classroom("104", "IT4", 1, 360, 125)); // 1층 104호
        it4.addClassroom(new Classroom("106", "IT4", 1, 600, 330)); // 1층 106호
        it4.addClassroom(new Classroom("108", "IT4", 1, 115, 330)); // 1층 108호
        it4.addClassroom(new Classroom("DIY실", "IT4", 1, 760, 430)); // 1층 DIY실
        buildings.add(it4);

        // IT5 건물 (2, 3층)
        Building it5 = new Building("IT5", Arrays.asList(2, 3));
        it5.addClassroom(new Classroom("224", "IT5", 2, 625, 415)); // 2층 224호
        it5.addClassroom(new Classroom("225", "IT5", 2, 690, 530)); // 2층 225호
        it5.addClassroom(new Classroom("245", "IT5", 2, 355, 135)); // 2층 245호
        it5.addClassroom(new Classroom("248", "IT5", 2, 200, 120)); // 2층 248호
        it5.addClassroom(new Classroom("342", "IT5", 3, 500, 155)); // 3층 342호
        it5.addClassroom(new Classroom("345", "IT5", 3, 355, 135)); // 3층 345호
        it5.addClassroom(new Classroom("348", "IT5", 3, 200, 120)); // 3층 348호
        buildings.add(it5);

        return buildings;
    }

    /**
     * 로그인 성공 후 메인 애플리케이션을 시작합니다.
     */
    public static void startMainApplication() {
        SwingUtilities.invokeLater(() -> {
            try {
                logger.debug("MainController 초기화 및 실행");
                List<Building> buildings = createSampleData();
                ControllerFactory.getInstance().createMainController(buildings).show();
                logger.info("메인 애플리케이션 시작 완료");
            } catch (Exception e) {
                logger.error("메인 애플리케이션 시작 중 오류 발생", e);
                System.err.println("메인 애플리케이션을 시작할 수 없습니다.");
            }
        });
    }
}