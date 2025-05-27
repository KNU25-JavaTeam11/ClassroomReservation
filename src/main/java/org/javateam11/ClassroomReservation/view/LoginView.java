package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.controller.*;
import org.javateam11.ClassroomReservation.model.*;
import javax.swing.*;
import java.awt.*;


/**
 * LoginView는 로그인 창 UI를 담당합니다.
 * 학번, 비밀번호를 입력받아 로그인하는 기능을 합니다.
 */

public class LoginView extends JFrame {
	
	private LoginController logincontroller;
	
	public LoginView(LoginController logincontroller) {
		this.logincontroller = logincontroller;
		setTitle("로그인 창");
		setSize(300, 200);
		setLayout(null);
		setResizable(false); // 로그인 창 크기 고정
		
		JLabel stuNum = new JLabel("학번");
		stuNum.setBounds(20, 20, 80 ,30);
		JTextField id = new JTextField();
		id.setBounds(100, 20, 100, 30);
		JLabel password = new JLabel("비밀번호");
		password.setBounds(20, 75, 80, 30);
		JPasswordField pw = new JPasswordField();
		pw.setBounds(100, 75, 100, 30);
		JButton login = new JButton("로그인");
		login.setBounds(100, 120, 80 ,30);
		
		add(stuNum); add(id);
		add(password); add(pw);
		add(login);
	}
	
}
