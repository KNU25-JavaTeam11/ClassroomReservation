package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.model.*;
//import org.javateam11.ClassroomReservation.controller.MainController;
import javax.swing.*;
//import java.awt.*;
//import java.time.LocalDate;
//import java.time.LocalTime;
//import java.util.List;

public class MyReservationView extends JFrame {
	private User currentUser;
	//내가 예약한 내역을 볼 수 있음
	public MyReservationView(User user) {
		//현 사용자
		currentUser = user;
		
		setTitle("내 예약");
		setSize(450, 500);
		setLocationRelativeTo(null);
	}
}