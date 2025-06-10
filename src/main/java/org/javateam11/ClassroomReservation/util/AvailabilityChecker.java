package org.javateam11.ClassroomReservation.util;

import org.javateam11.ClassroomReservation.dto.ReservationDto;
import org.javateam11.ClassroomReservation.model.Classroom;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 예약 정보를 바탕으로 강의실의 현재 사용 가능 여부를 판단하는 유틸리티 클래스
 */
public class AvailabilityChecker {

    /**
     * 현재 시간 기준으로 특정 강의실이 사용 가능한지 판단
     * 
     * @param classroom    확인할 강의실
     * @param reservations 현재 날짜의 모든 예약 정보
     * @param roomIdMap    강의실 이름과 roomId 매핑 정보
     * @return true: 사용 가능, false: 사용 중
     */
    public static boolean isCurrentlyAvailable(Classroom classroom, List<ReservationDto> reservations,
            Map<String, Long> roomIdMap) {
        if (reservations == null || reservations.isEmpty()) {
            return true; // 예약이 없으면 사용 가능
        }

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();

        // 강의실 이름으로 roomId 찾기
        Long roomId = roomIdMap.get(classroom.getName());
        if (roomId == null) {
            // roomId를 찾을 수 없으면 기본적으로 사용 가능으로 판단
            return true;
        }

        // 오늘 날짜에 해당하는 예약 중에서 현재 시간과 겹치는 예약이 있는지 확인
        boolean isOccupied = reservations.stream()
                .filter(reservation -> reservation.getRoomId().equals(roomId))
                .filter(reservation -> reservation.getDate().equals(today))
                .anyMatch(reservation -> {
                    LocalTime startTime = reservation.getStartTime();
                    LocalTime endTime = reservation.getEndTime();

                    // 현재 시간이 예약 시간 범위에 포함되는지 확인
                    // 시작 시간 이상이고 종료 시간 미만인 경우 사용 중으로 판단
                    return currentTime.isAfter(startTime) && currentTime.isBefore(endTime) ||
                            currentTime.equals(startTime);
                });

        return !isOccupied; // 점유되지 않았으면 사용 가능
    }

    /**
     * 예약 정보 리스트를 roomId별로 그룹화하여 반환
     * 
     * @param reservations 예약 정보 리스트
     * @return roomId별로 그룹화된 예약 정보
     */
    public static Map<Long, List<ReservationDto>> groupReservationsByRoomId(List<ReservationDto> reservations) {
        return reservations.stream()
                .collect(Collectors.groupingBy(ReservationDto::getRoomId));
    }

    /**
     * 특정 시간대에 강의실이 사용 가능한지 판단 (예약 시 사용)
     * 
     * @param roomId       강의실 ID
     * @param date         예약 날짜
     * @param startTime    예약 시작 시간
     * @param endTime      예약 종료 시간
     * @param reservations 해당 날짜의 모든 예약 정보
     * @return true: 예약 가능, false: 예약 불가
     */
    public static boolean isAvailableForReservation(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime,
            List<ReservationDto> reservations) {
        if (reservations == null || reservations.isEmpty()) {
            return true;
        }

        // 같은 강의실, 같은 날짜의 예약 중에서 시간이 겹치는 예약이 있는지 확인
        return reservations.stream()
                .filter(reservation -> reservation.getRoomId().equals(roomId))
                .filter(reservation -> reservation.getDate().equals(date))
                .noneMatch(reservation -> {
                    LocalTime existingStart = reservation.getStartTime();
                    LocalTime existingEnd = reservation.getEndTime();

                    // 시간 겹침 검사
                    // 새로운 예약의 시작 시간이 기존 예약의 종료 시간 이전이고,
                    // 새로운 예약의 종료 시간이 기존 예약의 시작 시간 이후인 경우 겹침
                    return startTime.isBefore(existingEnd) && endTime.isAfter(existingStart);
                });
    }

    /**
     * 현재 시간 기준으로 강의실 상태를 문자열로 반환
     * 
     * @param classroom    확인할 강의실
     * @param reservations 현재 날짜의 모든 예약 정보
     * @param roomIdMap    강의실 이름과 roomId 매핑 정보
     * @return 상태 문자열 ("사용 가능" 또는 "사용 중")
     */
    public static String getAvailabilityStatus(Classroom classroom, List<ReservationDto> reservations,
            Map<String, Long> roomIdMap) {
        return isCurrentlyAvailable(classroom, reservations, roomIdMap) ? "사용 가능" : "사용 중";
    }

    /**
     * 다음 예약까지 남은 시간을 계산하여 반환
     * 
     * @param classroom    확인할 강의실
     * @param reservations 현재 날짜의 모든 예약 정보
     * @param roomIdMap    강의실 이름과 roomId 매핑 정보
     * @return 다음 예약까지의 시간 정보 (분 단위) 또는 null (다음 예약이 없는 경우)
     */
    public static Integer getMinutesToNextReservation(Classroom classroom, List<ReservationDto> reservations,
            Map<String, Long> roomIdMap) {
        if (reservations == null || reservations.isEmpty()) {
            return null;
        }

        LocalDate today = LocalDate.now();
        LocalTime currentTime = LocalTime.now();
        Long roomId = roomIdMap.get(classroom.getName());

        if (roomId == null) {
            return null;
        }

        // 현재 시간 이후의 가장 가까운 예약 찾기
        return reservations.stream()
                .filter(reservation -> reservation.getRoomId().equals(roomId))
                .filter(reservation -> reservation.getDate().equals(today))
                .filter(reservation -> reservation.getStartTime().isAfter(currentTime))
                .map(reservation -> {
                    int currentMinutes = currentTime.getHour() * 60 + currentTime.getMinute();
                    int reservationMinutes = reservation.getStartTime().getHour() * 60
                            + reservation.getStartTime().getMinute();
                    return reservationMinutes - currentMinutes;
                })
                .min(Integer::compareTo)
                .orElse(null);
    }
}