package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.model.*;
import org.javateam11.ClassroomReservation.model.Reservation;
import org.javateam11.ClassroomReservation.controller.MainController;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.ArrayList;

public class MyReservationView extends JFrame {
	private User currentUser;
	private List<Reservation> reservations;
	//내가 예약한 내역을 볼 수 있음
	public MyReservationView(User user) {
		//현 사용자
		currentUser = user;
		reservations = new ArrayList<Reservation>(); //임시 생성
		
		setTitle("내 예약");
		setSize(450, 500);
		setLocationRelativeTo(null);
		
		//JTable
		String[] header = {"이름", "강의실/시설물", "예약날짜", "예약시간"};
		String[][] contents = new String[reservations.size()][4];
		for (int i = 0; i < reservations.size(); i++) {
			contents[i][0] = reservations.get(i).getReserver();
			contents[i][1] = reservations.get(i).getClassroomName();
			contents[i][2] = reservations.get(i).getDate().toString();
			contents[i][3] = reservations.get(i).getStartTime().toString() + " - " + reservations.get(i).getEndTime().toString();
		}
		JTable reservationTable = new JTable(contents, header);
		//스크롤
		JScrollPane scrollpane = new JScrollPane(reservationTable);
		add(scrollpane);
	}
}