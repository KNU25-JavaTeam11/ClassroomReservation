package org.javateam11.ClassroomReservation.service;

import org.javateam11.ClassroomReservation.model.Reservation;
import javax.swing.SwingUtilities;
import java.util.List;
import java.util.function.Consumer;

/**
 * 예약 관련 Spring 백엔드 API 호출을 담당하는 서비스 클래스
 * Swing의 EDT(Event Dispatch Thread)를 고려한 멀티스레드 설계
 */
public class ReservationService {
    private final ApiService apiService;

    public ReservationService() {
        this.apiService = new ApiService();
    }

    /**
     * 모든 예약 조회 (비동기)
     * UI 업데이트는 EDT에서 실행됨
     */
    public void getAllReservations(Consumer<List<Reservation>> onSuccess, Consumer<String> onError) {
        apiService.getAsync("/api/reservations", List.class)
                .thenAccept(reservations -> {
                    // UI 업데이트는 반드시 EDT에서 실행
                    SwingUtilities.invokeLater(() -> onSuccess.accept((List<Reservation>) reservations));
                })
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> onError.accept("예약 목록 조회 실패: " + throwable.getMessage()));
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

        apiService.getAsync(endpoint, List.class)
                .thenAccept(reservations -> {
                    SwingUtilities.invokeLater(() -> onSuccess.accept((List<Reservation>) reservations));
                })
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> onError.accept("강의실 예약 조회 실패: " + throwable.getMessage()));
                    return null;
                });
    }

    /**
     * 새 예약 생성 (비동기)
     */
    public void createReservation(Reservation reservation,
            Consumer<Reservation> onSuccess, Consumer<String> onError) {
        apiService.postAsync("/api/reservations", reservation, Reservation.class)
                .thenAccept(createdReservation -> {
                    SwingUtilities.invokeLater(() -> onSuccess.accept(createdReservation));
                })
                .exceptionally(throwable -> {
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

        apiService.putAsync(endpoint, reservation, Reservation.class)
                .thenAccept(updatedReservation -> {
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

        apiService.getAsync(endpoint, Boolean.class)
                .thenAccept(isAvailable -> {
                    SwingUtilities.invokeLater(() -> onSuccess.accept(isAvailable));
                })
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> onError.accept("예약 가능 여부 확인 실패: " + throwable.getMessage()));
                    return null;
                });
    }
}