package org.javateam11.ClassroomReservation.service;

import org.javateam11.ClassroomReservation.model.Reservation;
import org.javateam11.ClassroomReservation.dto.ReservationDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import javax.swing.SwingUtilities;
import java.time.LocalDate;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 예약 관련 Spring 백엔드 API 호출을 담당하는 서비스 클래스
 * Swing의 EDT(Event Dispatch Thread)를 고려한 멀티스레드 설계
 */
public class ReservationService {
    private static final Logger logger = LoggerFactory.getLogger(ReservationService.class);
    private final ApiService apiService;

    public ReservationService() {
        this.apiService = new ApiService();
        logger.debug("ReservationService 초기화 완료");
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

                    // ReservationDto를 Reservation으로 변환
                    List<Reservation> reservations = reservationDtos.stream()
                            .map(dto -> Reservation.fromDto(dto, "강의실-" + dto.getRoomId())) // 임시로 강의실 이름 생성
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
                    String classroomName = buildingName + " " + floor + "층 " + roomNumber + "호";
                    List<Reservation> reservations = reservationDtos.stream()
                            .map(dto -> Reservation.fromDto(dto, classroomName))
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
        apiService.postAsync("/api/reservations", dto, ReservationDto.class)
                .thenAccept(createdReservationDto -> {
                    logger.info("예약 생성 성공: 예약자={}, 장소={}",
                            reservation.getStudentId(), reservation.getRoomName());
                    // 응답받은 DTO를 다시 Reservation으로 변환
                    Reservation createdReservation = Reservation.fromDto(createdReservationDto,
                            reservation.getRoomName());
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
     * 예약 수정 (비동기)
     */
    public void updateReservation(Long reservationId, Reservation reservation,
            Consumer<Reservation> onSuccess, Consumer<String> onError) {
        String endpoint = "/api/reservations/" + reservationId;

        // Reservation을 ReservationDto로 변환하여 전송
        ReservationDto dto = reservation.toDto();
        apiService.putAsync(endpoint, dto, ReservationDto.class)
                .thenAccept(updatedReservationDto -> {
                    // 응답받은 DTO를 다시 Reservation으로 변환
                    Reservation updatedReservation = Reservation.fromDto(updatedReservationDto,
                            reservation.getRoomName());
                    SwingUtilities.invokeLater(() -> onSuccess.accept(updatedReservation));
                })
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> onError.accept("예약 수정 실패: " + throwable.getMessage()));
                    return null;
                });
    }

    /**
     * 예약 삭제 (비동기)
     */
    public void deleteReservation(Long reservationId,
            Runnable onSuccess, Consumer<String> onError) {
        String endpoint = "/api/reservations/" + reservationId;

        apiService.deleteAsync(endpoint)
                .thenRun(() -> {
                    SwingUtilities.invokeLater(onSuccess);
                })
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> onError.accept("예약 삭제 실패: " + throwable.getMessage()));
                    return null;
                });
    }

    /**
     * 예약 가능 여부 확인 (비동기)
     */
    public void checkAvailability(String buildingName, int floor, String roomNumber,
            String date, String timeSlot,
            Consumer<Boolean> onSuccess, Consumer<String> onError) {
        String endpoint = String.format(
                "/api/reservations/availability?building=%s&floor=%d&room=%s&date=%s&timeSlot=%s",
                buildingName, floor, roomNumber, date, timeSlot);

        logger.debug("예약 가능 여부 확인 요청: {} {}층 {}호, 날짜={}, 시간={}",
                buildingName, floor, roomNumber, date, timeSlot);

        apiService.getAsync(endpoint, Boolean.class)
                .thenAccept(isAvailable -> {
                    logger.info("예약 가능 여부 확인 완료: {} {}층 {}호 - {}",
                            buildingName, floor, roomNumber, isAvailable ? "예약 가능" : "예약 불가");
                    SwingUtilities.invokeLater(() -> onSuccess.accept(isAvailable));
                })
                .exceptionally(throwable -> {
                    logger.error("예약 가능 여부 확인 실패: {} {}층 {}호, 날짜={}, 시간={}",
                            buildingName, floor, roomNumber, date, timeSlot, throwable);
                    SwingUtilities.invokeLater(() -> onError.accept("예약 가능 여부 확인 실패: " + throwable.getMessage()));
                    return null;
                });
    }
}