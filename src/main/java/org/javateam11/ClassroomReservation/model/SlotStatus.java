package org.javateam11.ClassroomReservation.model;

import java.awt.Color;

//슬롯 상태 정의
public enum SlotStatus {
    EMPTY(new Color(189, 195, 199)), // 연한 회색 (비어있음)
    AVAILABLE(new Color(52, 152, 219)), // 하늘색 (예약 가능)
    RESERVED(new Color(231, 76, 60)), // 빨간색 (예약됨)
    MY_RESERVED(new Color(46, 204, 113)), // 초록색 (내 예약)
    IN_USE(new Color(241, 196, 15)), // 황색 (사용 중)
    PAST(new Color(149, 165, 166)); // 진한 회색 (지나간 시간)

    private final Color color;

    SlotStatus(Color c) {
        this.color = c;
    }

    public Color getColor() {
        return color;
    }

    public SlotStatus next() {
        return values()[(ordinal() + 1) % values().length];
    }
}
