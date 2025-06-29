package org.javateam11.ClassroomReservation.service;

import org.javateam11.ClassroomReservation.model.Reservation;
import org.javateam11.ClassroomReservation.dto.ReservationDto;
import org.javateam11.ClassroomReservation.dto.RoomDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 예약 관련 Spring 백엔드 API 호출을 담당하는 서비스 클래스
 * Swing의 EDT(Event Dispatch Thread)를 고려한 멀티스레드 설계
 */
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ApiService apiService;
    private final RoomService roomService;

    // 강의실 정보 캐싱을 위한 Map (roomId -> 강의실 전체 이름)
    private final Map<Long, String> roomNameCache = new ConcurrentHashMap<>();
    private volatile boolean roomCacheInitialized = false;

    public ReservationService() {
        this.apiService = new ApiService();
        this.roomService = new RoomService();
        logger.debug("ReservationService 초기화 완료");
        initializeRoomCache();
    }

    /**
     * 강의실 캐시 초기화
     * 애플리케이션 시작 시 모든 강의실 정보를 로드하여 캐싱
     */
    private void initializeRoomCache() {
        logger.debug("강의실 캐시 초기화 시작");
        roomService.getAllRooms(
                rooms -> {
                    for (RoomDto room : rooms) {
                        String fullRoomName = room.getBuilding() + "-" + room.getName();
                        roomNameCache.put(room.getId(), fullRoomName);
                    }
                    roomCacheInitialized = true;
                    logger.info("강의실 캐시 초기화 완료: {}개 강의실", rooms.size());
                },
                error -> {
                    logger.error("강의실 캐시 초기화 실패: {}", error);
                    roomCacheInitialized = false;
                });
    }

    /**
     * roomId로 실제 강의실 이름 조회
     */
    private String getRoomName(Long roomId) {
        if (!roomCacheInitialized || !roomNameCache.containsKey(roomId)) {
            logger.warn("강의실 캐시 미초기화 또는 roomId {} 없음, 임시 이름 사용", roomId);
            return "강의실-" + roomId; // 캐시가 없으면 임시 이름 사용
        }
        return roomNameCache.get(roomId);
    }

    /**
     * 강의실 캐시 새로고침
     * 강의실 정보가 변경되었을 때 호출하여 캐시를 업데이트
     */
    public void refreshRoomCache() {
        logger.debug("강의실 캐시 새로고침 요청");
        roomNameCache.clear();
        roomCacheInitialized = false;
        initializeRoomCache();
    }

    /**
     * 모든 예약 조회 (비동기)
     * UI 업데이트는 EDT에서 실행됨
     * 백엔드에서 ReservationDto를 받아서 Reservation으로 변환
     */
    public void getAllReservations(Consumer<List<Reservation>> onSuccess, Consumer<String> onError) {
        logger.debug("모든 예약 조회 요청 시작");
        apiService.getAsync("/api/reservations", new TypeReference<List<ReservationDto>>() {
        })
                .thenAccept(reservationDtos -> {
                    logger.info("모든 예약 조회 성공: {}개 예약", reservationDtos.size());

                    // ReservationDto를 Reservation으로 변환 (실제 강의실 이름 사용)
                    List<Reservation> reservations = reservationDtos.stream()
                            .map(dto -> Reservation.fromDto(dto, getRoomName(dto.getRoomId())))
                            .collect(Collectors.toList());

                    // UI 업데이트는 반드시 EDT에서 실행
                    SwingUtilities.invokeLater(() -> onSuccess.accept(reservations));
                })
                .exceptionally(throwable -> {
                    logger.error("모든 예약 조회 실패", throwable);
                    SwingUtilities.invokeLater(() -> onError.accept("예약 목록 조회 실패: " + throwable.getMessage()));
                    return null;
                });
    }

    /**
     * 특정 날짜의 모든 예약 조회 (비동기)
     * 현재 시간 기준으로 강의실 사용 가능 여부 판단을 위해 사용
     */
    public void getReservationsByDate(LocalDate date, Consumer<List<ReservationDto>> onSuccess,
            Consumer<String> onError) {
        String endpoint = "/api/reservations?date=" + date.toString();

        logger.debug("특정 날짜 예약 조회 요청: {}", date);

        apiService.getAsync(endpoint, new TypeReference<List<ReservationDto>>() {
        })
                .thenAccept(reservations -> {
                    logger.info("특정 날짜 예약 조회 성공: {} - {}개 예약", date, reservations.size());
                    SwingUtilities.invokeLater(() -> onSuccess.accept(reservations));
                })
                .exceptionally(throwable -> {
                    logger.error("특정 날짜 예약 조회 실패: {}", date, throwable);
                    SwingUtilities.invokeLater(() -> onError.accept("특정 날짜 예약 조회 실패: " + throwable.getMessage()));
                    return null;
                });
    }

    /**
     * 특정 강의실의 예약 조회 (비동기)
     */
    public void getReservationsByClassroom(String buildingName, int floor, String roomNumber,
            Consumer<List<Reservation>> onSuccess, Consumer<String> onError) {
        String endpoint = String.format("/api/reservations/classroom?building=%s&floor=%d&room=%s",
                buildingName, floor, roomNumber);

        logger.debug("강의실 예약 조회 요청: {} {}층 {}호", buildingName, floor, roomNumber);

        apiService.getAsync(endpoint, new TypeReference<List<ReservationDto>>() {
        })
                .thenAccept(reservationDtos -> {
                    logger.info("강의실 예약 조회 성공: {} {}층 {}호 - {}개 예약",
                            buildingName, floor, roomNumber, reservationDtos.size());

                    // ReservationDto를 Reservation으로 변환
                    List<Reservation> reservations = reservationDtos.stream()
                            .map(dto -> Reservation.fromDto(dto, getRoomName(dto.getRoomId())))
                            .collect(Collectors.toList());

                    SwingUtilities.invokeLater(() -> onSuccess.accept(reservations));
                })
                .exceptionally(throwable -> {
                    logger.error("강의실 예약 조회 실패: {} {}층 {}호", buildingName, floor, roomNumber, throwable);
                    SwingUtilities.invokeLater(() -> onError.accept("강의실 예약 조회 실패: " + throwable.getMessage()));
                    return null;
                });
    }

    /**
     * 새 예약 생성 (비동기)
     */
    public void createReservation(Reservation reservation,
            Consumer<Reservation> onSuccess, Consumer<String> onError) {
        logger.debug("예약 생성 요청: 예약자={}, 장소={}, 날짜={}, 시간={}-{}",
                reservation.getStudentId(), reservation.getRoomName(),
                reservation.getDate(), reservation.getStartTime(), reservation.getEndTime());

        // Reservation을 ReservationDto로 변환하여 전송
        ReservationDto dto = reservation.toDto();
        apiService.postAuthenticatedAsync("/api/reservations", dto, ReservationDto.class)
                .thenAccept(createdReservationDto -> {
                    logger.info("예약 생성 성공: 예약자={}, 장소={}",
                            reservation.getStudentId(), reservation.getRoomName());

                    // 응답받은 DTO를 다시 Reservation으로 변환
                    Reservation createdReservation = Reservation.fromDto(createdReservationDto,
                            getRoomName(createdReservationDto.getRoomId()));
                    SwingUtilities.invokeLater(() -> onSuccess.accept(createdReservation));
                })
                .exceptionally(throwable -> {
                    logger.error("예약 생성 실패: 예약자={}, 장소={}",
                            reservation.getStudentId(), reservation.getRoomName(), throwable);
                    SwingUtilities.invokeLater(() -> onError.accept("예약 생성 실패: " + throwable.getMessage()));
                    return null;
                });
    }

    /**
     * 예약 삭제 (비동기)
     */
    public void deleteReservation(Long reservationId,
            Runnable onSuccess, Consumer<String> onError) {
        String endpoint = "/api/reservations/" + reservationId;

        logger.debug("예약 삭제 요청: reservationId={}", reservationId);

        apiService.deleteAuthenticatedAsync(endpoint)
                .thenRun(() -> {
                    logger.info("예약 삭제 성공: reservationId={}", reservationId);
                    SwingUtilities.invokeLater(onSuccess);
                })
                .exceptionally(throwable -> {
                    logger.error("예약 삭제 실패: reservationId={}", reservationId, throwable);
                    SwingUtilities.invokeLater(() -> onError.accept("예약 삭제 실패: " + throwable.getMessage()));
                    return null;
                });
    }
}