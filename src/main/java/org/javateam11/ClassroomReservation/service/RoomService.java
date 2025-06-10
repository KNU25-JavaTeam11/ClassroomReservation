package org.javateam11.ClassroomReservation.service;

import org.javateam11.ClassroomReservation.dto.RoomDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.function.Consumer;

/**
 * 강의실 관련 Spring 백엔드 API 호출을 담당하는 서비스 클래스
 */
public class RoomService {
    private static final Logger logger = LoggerFactory.getLogger(RoomService.class);
    private final ApiService apiService;

    public RoomService() {
        this.apiService = new ApiService();
        logger.debug("RoomService 초기화 완료");
    }

    /**
     * 모든 강의실 목록 조회 (비동기)
     * 로컬 데이터와 백엔드 데이터를 매핑하기 위해 사용
     */
    public void getAllRooms(Consumer<List<RoomDto>> onSuccess, Consumer<String> onError) {
        logger.debug("모든 강의실 조회 요청 시작");

        apiService.getAsync("/api/rooms", new TypeReference<List<RoomDto>>() {
        })
                .thenAccept(rooms -> {
                    logger.info("모든 강의실 조회 성공: {}개 강의실", rooms.size());
                    SwingUtilities.invokeLater(() -> onSuccess.accept(rooms));
                })
                .exceptionally(throwable -> {
                    logger.error("모든 강의실 조회 실패", throwable);
                    SwingUtilities.invokeLater(() -> onError.accept("강의실 목록 조회 실패: " + throwable.getMessage()));
                    return null;
                });
    }
}