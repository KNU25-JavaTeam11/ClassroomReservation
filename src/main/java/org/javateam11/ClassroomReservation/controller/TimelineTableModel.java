package org.javateam11.ClassroomReservation.controller;

import javax.swing.table.AbstractTableModel;

//TableModel: 한 행, N 열
public class TimelineTableModel extends AbstractTableModel {
	 private final int slotCount;
	 private final SlotStatus[] statuses;
	 private final String[] columnNames;

	 public TimelineTableModel(int startHour, int endHour, int intervalMinutes) {
	     this.slotCount = (endHour - startHour) * 60 / intervalMinutes;
	     this.statuses = new SlotStatus[slotCount];
	     this.columnNames = new String[slotCount];

	     // 초기 상태, 컬럼 이름 설정
	     for (int i = 0; i < slotCount; i++) {
	         statuses[i] = SlotStatus.EMPTY;
	         int totalMinutes = startHour * 60 + i * intervalMinutes;
	         int h = totalMinutes / 60;
	         int m = totalMinutes % 60;
	         columnNames[i] = String.format("%02d:%02d", h, m);
	     }
	 }

	 @Override
	 public int getRowCount() { return 1; }

	 @Override
	 public int getColumnCount() { return slotCount; }

	 @Override
	 public Object getValueAt(int row, int col) {
	     return statuses[col];
	 }

	 @Override
	 public String getColumnName(int col) {
	     return columnNames[col];
	 }

	 @Override
	 public Class<?> getColumnClass(int col) {
	     return SlotStatus.class;
	 }

	 public void toggleStatus(int column) {
	     statuses[column] = statuses[column].next();
	     fireTableCellUpdated(0, column);
	 }
}