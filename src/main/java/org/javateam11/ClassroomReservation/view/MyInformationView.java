package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.model.*;
//import org.javateam11.ClassroomReservation.controller.MainController;
import javax.swing.*;
import java.awt.*;

public class MyInformationView extends JFrame {

	private User currentUser;
	// 내 정보(학번, 이름 등)를 표시하고 로그아웃, 비번 변경할 수 있는 창 UI 개발

	public MyInformationView(User user) {
		// 현 사용자
		currentUser = user;

		setTitle("내 정보");
		setSize(350, 200);
		setLocationRelativeTo(null);
		setLayout(new GridLayout(4, 3));

		// 여백 배치
		JPanel[] gridBlank = new JPanel[12];
		for (int i = 0; i < 12; i++)
			gridBlank[i] = new JPanel(); // 중앙: 1, 4, 7, 10

		// 기본 정보 표시
		JLabel name = new JLabel("이름: " + currentUser.getName());
		gridBlank[1].add(name);
		JLabel number = new JLabel("학번: " + currentUser.getStudentId());
		gridBlank[4].add(number);

		// 로그아웃 버튼
		JButton logout = new JButton("로그아웃");
		gridBlank[7].add(logout);

		// 비밀번호 변경 버튼
		JButton changePW = new JButton("비밀번호 변경");
		// changePW.addActionListener(e -> /*비밀번호 변경 창 개발*/);
		gridBlank[10].add(changePW);

		for (int i = 0; i < 12; i++)
			add(gridBlank[i]);
	}
}