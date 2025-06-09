package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.service.AuthService;
import org.javateam11.ClassroomReservation.controller.ControllerFactory;
import org.javateam11.ClassroomReservation.Main;
import org.javateam11.ClassroomReservation.util.FontUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * LoginView는 로그인 창 UI를 담당합니다.
 * 학번, 비밀번호를 입력받아 로그인하는 기능을 합니다.
 */
public class LoginView extends JFrame {
	private final AuthService authService;
	private JTextField idField;
	private JPasswordField passwordField;
	private JButton loginButton;
	private JButton signUpButton;
	private JLabel statusLabel;

	public LoginView() {
		this.authService = new AuthService();
		initializeUI();
		setupEventListeners();
	}

	private void initializeUI() {
		setTitle("강의실 예약 시스템 - 로그인");
		setSize(400, 350);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setLayout(new BorderLayout());

		// 메인 패널
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
		mainPanel.setBackground(Color.WHITE);

		// 타이틀
		JLabel titleLabel = new JLabel("컴퓨터학부 강의실/시설물 예약 시스템");
		titleLabel.setFont(FontUtils.getTitleFont());
		titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		titleLabel.setForeground(new Color(51, 51, 51));

		// 입력 패널
		JPanel inputPanel = new JPanel(new GridBagLayout());
		inputPanel.setBackground(Color.WHITE);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 5, 10, 5);

		// 학번 라벨과 필드
		JLabel stuNumLabel = new JLabel("학번:");
		stuNumLabel.setFont(FontUtils.getLabelFont());
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.anchor = GridBagConstraints.WEST;
		inputPanel.add(stuNumLabel, gbc);

		idField = new JTextField(15);
		idField.setFont(FontUtils.getPlainFont());
		idField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		inputPanel.add(idField, gbc);

		// 비밀번호 라벨과 필드
		JLabel passwordLabel = new JLabel("비밀번호:");
		passwordLabel.setFont(FontUtils.getLabelFont());
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.NONE;
		inputPanel.add(passwordLabel, gbc);

		passwordField = new JPasswordField(15);
		passwordField.setFont(FontUtils.getPlainFont());
		passwordField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(new Color(200, 200, 200)),
				BorderFactory.createEmptyBorder(8, 10, 8, 10)));
		gbc.gridx = 1;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		inputPanel.add(passwordField, gbc);

		// 버튼 패널
		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
		buttonPanel.setBackground(Color.WHITE);

		loginButton = new JButton("로그인");
		loginButton.setFont(FontUtils.getButtonFont());
		loginButton.setPreferredSize(new Dimension(100, 35));
		loginButton.setBackground(new Color(25, 118, 210));
		loginButton.setForeground(Color.WHITE);
		loginButton.setBorder(BorderFactory.createEmptyBorder());
		loginButton.setFocusPainted(false);
		loginButton.setOpaque(true);
		loginButton.setBorderPainted(false);

		loginButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				if (loginButton.isEnabled()) {
					loginButton.setBackground(new Color(21, 101, 192));
				}
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				if (loginButton.isEnabled()) {
					loginButton.setBackground(new Color(25, 118, 210));
				}
			}
		});

		signUpButton = new JButton("회원가입");
		signUpButton.setFont(FontUtils.getPlainFont());
		signUpButton.setPreferredSize(new Dimension(100, 35));
		signUpButton.setBackground(new Color(245, 245, 245));
		signUpButton.setForeground(new Color(60, 60, 60));
		signUpButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
		signUpButton.setFocusPainted(false);
		signUpButton.setOpaque(true);

		signUpButton.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				signUpButton.setBackground(new Color(235, 235, 235));
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				signUpButton.setBackground(new Color(245, 245, 245));
			}
		});

		buttonPanel.add(loginButton);
		buttonPanel.add(signUpButton);

		// 상태 라벨
		statusLabel = new JLabel(" ");
		statusLabel.setFont(FontUtils.getSmallFont());
		statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		statusLabel.setForeground(Color.RED);

		// 컴포넌트들을 메인 패널에 추가
		mainPanel.add(titleLabel);
		mainPanel.add(Box.createVerticalStrut(30));
		mainPanel.add(inputPanel);
		mainPanel.add(Box.createVerticalStrut(20));
		mainPanel.add(buttonPanel);
		mainPanel.add(Box.createVerticalStrut(10));
		mainPanel.add(statusLabel);

		add(mainPanel, BorderLayout.CENTER);
	}

	private void setupEventListeners() {
		// 로그인 버튼 클릭 이벤트
		loginButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				performLogin();
			}
		});

		// 회원가입 버튼 클릭 이벤트
		signUpButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				openSignUpView();
			}
		});

		// Enter 키로 로그인
		KeyListener enterKeyListener = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					performLogin();
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
			}
		};

		idField.addKeyListener(enterKeyListener);
		passwordField.addKeyListener(enterKeyListener);
	}

	private void performLogin() {
		String username = idField.getText().trim();
		String password = new String(passwordField.getPassword());

		if (username.isEmpty() || password.isEmpty()) {
			showStatus("학번과 비밀번호를 모두 입력해주세요.", Color.RED);
			return;
		}

		// 로그인 버튼 비활성화
		loginButton.setEnabled(false);
		signUpButton.setEnabled(false);
		showStatus("로그인 중...", Color.BLUE);

		authService.login(username, password)
				.thenAccept(response -> {
					SwingUtilities.invokeLater(() -> {
						showStatus("로그인 성공!", Color.GREEN);
						dispose();
						Main.startMainApplication();
					});
				})
				.exceptionally(throwable -> {
					SwingUtilities.invokeLater(() -> {
						loginButton.setEnabled(true);
						signUpButton.setEnabled(true);

						// 예외에서 깨끗한 에러 메시지 추출
						String errorMessage = extractCleanErrorMessage(throwable);

						if (errorMessage.contains("401") || errorMessage.contains("Unauthorized")) {
							showStatus("학번 또는 비밀번호가 잘못되었습니다.", Color.RED);
						} else if (errorMessage.contains("Connection") || errorMessage.contains("timeout")) {
							showStatus("서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.", Color.RED);
						} else {
							// 서버에서 온 에러 메시지를 그대로 표시 (접두어 없이)
							showStatus(errorMessage, Color.RED);
						}
					});
					return null;
				});
	}

	private void openSignUpView() {
		SwingUtilities.invokeLater(() -> {
			SignUpView signUpView = ControllerFactory.getInstance().createSignUpView();
			signUpView.setVisible(true);
		});
	}

	private void showStatus(String message, Color color) {
		statusLabel.setText(message);
		statusLabel.setForeground(color);
	}

	/**
	 * 예외에서 깨끗한 에러 메시지를 추출하는 헬퍼 메서드
	 */
	private String extractCleanErrorMessage(Throwable throwable) {
		Throwable current = throwable;

		// 중첩된 예외를 풀어서 원본 메시지를 찾음
		while (current.getCause() != null && current.getCause() != current) {
			current = current.getCause();
		}

		String message = current.getMessage();
		if (message != null && !message.trim().isEmpty()) {
			// "java.lang.RuntimeException: " 같은 불필요한 접두어 제거
			if (message.startsWith("java.lang.RuntimeException: ")) {
				message = message.substring("java.lang.RuntimeException: ".length());
			}
			return message;
		}

		// 메시지가 없으면 기본 메시지 반환
		return "로그인 중 오류가 발생했습니다";
	}
}
