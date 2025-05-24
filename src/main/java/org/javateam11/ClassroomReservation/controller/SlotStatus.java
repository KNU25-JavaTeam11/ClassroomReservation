package org.javateam11.ClassroomReservation.controller;
import java.awt.Color;

//슬롯 상태 정의
public enum SlotStatus {
EMPTY(Color.LIGHT_GRAY),
AVAILABLE(new Color(0x66CCFF)),
RESERVED(Color.GRAY),
IN_USE(Color.DARK_GRAY);

private final Color color;
SlotStatus(Color c) { this.color = c; }
public Color getColor() { return color; }
public SlotStatus next() {
   return values()[(ordinal() + 1) % values().length];
}
}
