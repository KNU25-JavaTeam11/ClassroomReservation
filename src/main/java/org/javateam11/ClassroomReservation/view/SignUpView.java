package org.javateam11.ClassroomReservation.view;

import org.javateam11.ClassroomReservation.controller.ISignUpController;
import org.javateam11.ClassroomReservation.util.FontUtils;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * SignUpView는 회원가입창 UI를 담당합니다.
 * 학번, 이름, 비밀번호, 비밀번호 확인을 입력란으로 입력받습니다.
 * ISignUpController 인터페이스를 통해 회원가입 로직을 처리합니다.
 */
public class SignUpView extends JFrame {
    private final ISignUpController signUpController;
    private JTextField nameField;
    private JTextField stuNumField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JButton submitButton;
    private JButton cancelButton;
    private JLabel statusLabel;

    public SignUpView(ISignUpController signUpController) {
        this.signUpController = signUpController;
        initializeUI();
        setupEventListeners();
    }

    private void initializeUI() {
        setTitle("강의실 예약 시스템 - 회원가입");
        setSize(450, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        // 메인 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        mainPanel.setBackground(Color.WHITE);

        // 타이틀
        JLabel titleLabel = new JLabel("회원가입");
        titleLabel.setFont(FontUtils.getTitleFont());
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setForeground(new Color(51, 51, 51));

        // 입력 패널
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);

        // 이름 라벨과 필드
        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setFont(FontUtils.getLabelFont());
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(nameLabel, gbc);

        nameField = new JTextField(15);
        nameField.setFont(FontUtils.getPlainFont());
        nameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(nameField, gbc);

        // 학번 라벨과 필드
        JLabel stuNumLabel = new JLabel("학번:");
        stuNumLabel.setFont(FontUtils.getLabelFont());
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(stuNumLabel, gbc);

        stuNumField = new JTextField(15);
        stuNumField.setFont(FontUtils.getPlainFont());
        stuNumField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(stuNumField, gbc);

        // 비밀번호 라벨과 필드
        JLabel passwordLabel = new JLabel("비밀번호:");
        passwordLabel.setFont(FontUtils.getLabelFont());
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(FontUtils.getPlainFont());
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(passwordField, gbc);

        // 비밀번호 확인 라벨과 필드
        JLabel confirmPasswordLabel = new JLabel("비밀번호 확인:");
        confirmPasswordLabel.setFont(FontUtils.getLabelFont());
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.NONE;
        inputPanel.add(confirmPasswordLabel, gbc);

        confirmPasswordField = new JPasswordField(15);
        confirmPasswordField.setFont(FontUtils.getPlainFont());
        confirmPasswordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)));
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        inputPanel.add(confirmPasswordField, gbc);

        // 버튼 패널
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(Color.WHITE);

        submitButton = new JButton("회원가입");
        submitButton.setFont(FontUtils.getButtonFont());
        submitButton.setPreferredSize(new Dimension(100, 35));
        submitButton.setBackground(new Color(25, 118, 210));
        submitButton.setForeground(Color.WHITE);
        submitButton.setBorder(BorderFactory.createEmptyBorder());
        submitButton.setFocusPainted(false);
        submitButton.setOpaque(true);
        submitButton.setBorderPainted(false);

        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (submitButton.isEnabled()) {
                    submitButton.setBackground(new Color(21, 101, 192));
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (submitButton.isEnabled()) {
                    submitButton.setBackground(new Color(25, 118, 210));
                }
            }
        });

        cancelButton = new JButton("취소");
        cancelButton.setFont(FontUtils.getPlainFont());
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setBackground(new Color(245, 245, 245));
        cancelButton.setForeground(new Color(60, 60, 60));
        cancelButton.setBorder(BorderFactory.createLineBorder(new Color(180, 180, 180)));
        cancelButton.setFocusPainted(false);
        cancelButton.setOpaque(true);

        cancelButton.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(235, 235, 235));
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                cancelButton.setBackground(new Color(245, 245, 245));
            }
        });

        buttonPanel.add(submitButton);
        buttonPanel.add(cancelButton);

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
        // 회원가입 버튼 클릭 이벤트
        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSignUp();
            }
        });

        // 취소 버튼 클릭 이벤트
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        // Enter 키로 회원가입
        KeyListener enterKeyListener = new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performSignUp();
                }
            }

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        };

        nameField.addKeyListener(enterKeyListener);
        stuNumField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);
        confirmPasswordField.addKeyListener(enterKeyListener);
    }

    private void performSignUp() {
        String name = nameField.getText().trim();
        String studentNumber = stuNumField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        // 버튼 비활성화
        submitButton.setEnabled(false);
        cancelButton.setEnabled(false);
        showStatus("회원가입 중...", Color.BLUE);

        signUpController.register(name, studentNumber, password, confirmPassword)
                .thenAccept(success -> {
                    SwingUtilities.invokeLater(() -> {
                        if (!success) {
                            // 실패 시 버튼 다시 활성화
                            submitButton.setEnabled(true);
                            cancelButton.setEnabled(true);
                        }
                    });
                })
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> {
                        submitButton.setEnabled(true);
                        cancelButton.setEnabled(true);
                        showStatus("회원가입 처리 중 오류가 발생했습니다.", Color.RED);
                    });
                    return null;
                });
    }

    /**
     * 상태 메시지를 표시합니다.
     */
    public void showStatus(String message, Color color) {
        statusLabel.setText(message);
        statusLabel.setForeground(color);
    }
}
