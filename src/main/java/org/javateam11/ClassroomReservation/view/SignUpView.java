package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.controller.*;
import javax.swing.*;
import java.awt.*;


/**
 * SignUpView는 회원가입창 UI를 담당합니다. 
 * 학번, 이름, 비밀번호, 비밀번호 확인을 입력란으로 입력받습니다.
 * SignUpController에서 회원가입 창의 로직을 담당합니다.
 */
public class SignUpView extends JFrame {
	public SignUpView(SignUpController signupcontroller){
		setTitle("회원가입");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        
        JPanel inputInfo = new JPanel();
        inputInfo.setLayout(null);
        inputInfo.setBounds(0, 0, 400, 210);
        JLabel n = new JLabel("            이름");
        n.setBounds(50, 30, 100, 25);
        JTextField name = new JTextField();
        name.setBounds(150, 30, 180, 25);

        JLabel sn = new JLabel("            학번");
        sn.setBounds(50, 70, 100, 25);
        JTextField stuNum = new JTextField();
        stuNum.setBounds(150, 70, 180, 25);

        JLabel p = new JLabel("     비밀번호");
        p.setBounds(50, 110, 100, 25);
        JTextField password = new JTextField();
        password.setBounds(150, 110, 180, 25);

        JLabel pr = new JLabel("비밀번호 재확인");
        pr.setBounds(30, 150, 120, 25);
        JTextField pw_recheck = new JTextField();
        pw_recheck.setBounds(150, 150, 180, 25);

        inputInfo.add(n);
        inputInfo.add(name);
        inputInfo.add(sn);
        inputInfo.add(stuNum);
        inputInfo.add(p);
        inputInfo.add(password);
        inputInfo.add(pr);
        inputInfo.add(pw_recheck);

        add(inputInfo);
        
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(null);
        bottomPanel.setBounds(0, 210, 400, 60); 

        JButton submitBtn = new JButton("회원가입");
        submitBtn.setBounds(140, 0, 120, 30);  
        bottomPanel.add(submitBtn);
        add(bottomPanel);
	}
}
