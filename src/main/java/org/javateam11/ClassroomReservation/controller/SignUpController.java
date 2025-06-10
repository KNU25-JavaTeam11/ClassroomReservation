package org.javateam11.ClassroomReservation.controller;

import org.javateam11.ClassroomReservation.service.AuthService;
import org.javateam11.ClassroomReservation.util.ErrorMessageUtils;
import org.javateam11.ClassroomReservation.view.SignUpView;

import javax.swing.*;
import java.util.concurrent.CompletableFuture;

/**
 * 회원가입 창의 로직을 담당하는 컨트롤러
 */
public class SignUpController implements ISignUpController {
    private final AuthService authService;
    private SignUpView signUpView;

    public SignUpController() {
        this.authService = new AuthService();
    }

    public void setSignUpView(SignUpView signUpView) {
        this.signUpView = signUpView;
    }

    /**
     * 회원가입 처리
     */
    @Override
    public CompletableFuture<Boolean> register(String name, String studentNumber, String password,
            String confirmPassword) {
        // 입력 유효성 검사
        if (name == null || name.trim().isEmpty()) {
            showError("이름을 입력해주세요.");
            return CompletableFuture.completedFuture(false);
        }

        if (studentNumber == null || studentNumber.trim().isEmpty()) {
            showError("학번을 입력해주세요.");
            return CompletableFuture.completedFuture(false);
        }

        if (password == null || password.isEmpty()) {
            showError("비밀번호를 입력해주세요.");
            return CompletableFuture.completedFuture(false);
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            showError("비밀번호 확인을 입력해주세요.");
            return CompletableFuture.completedFuture(false);
        }

        if (!password.equals(confirmPassword)) {
            showError("비밀번호가 일치하지 않습니다.");
            return CompletableFuture.completedFuture(false);
        }

        if (password.length() < 4) {
            showError("비밀번호는 최소 4자리 이상이어야 합니다.");
            return CompletableFuture.completedFuture(false);
        }

        // 회원가입 API 호출
        return authService.register(studentNumber.trim(), name, password)
                .thenApply(response -> {
                    SwingUtilities.invokeLater(() -> {
                        showSuccess("회원가입이 완료되었습니다!");
                        if (signUpView != null) {
                            signUpView.dispose();
                        }
                    });
                    return true;
                })
                .exceptionally(throwable -> {
                    SwingUtilities.invokeLater(() -> {
                        // 예외에서 깨끗한 에러 메시지 추출
                        String errorMessage = ErrorMessageUtils.extractCleanErrorMessage(throwable);

                        if (errorMessage.contains("409") || errorMessage.contains("Conflict")) {
                            showError("이미 등록된 학번입니다.");
                        } else if (errorMessage.contains("Connection") || errorMessage.contains("timeout")) {
                            showError("서버에 연결할 수 없습니다. 잠시 후 다시 시도해주세요.");
                        } else {
                            // 서버에서 온 에러 메시지를 그대로 표시 (접두어 없이)
                            showError(errorMessage);
                        }
                    });
                    return false;
                });
    }

    private void showError(String message) {
        if (signUpView != null) {
            signUpView.showStatus(message, java.awt.Color.RED);
        } else {
            JOptionPane.showMessageDialog(null, message, "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSuccess(String message) {
        if (signUpView != null) {
            signUpView.showStatus(message, java.awt.Color.GREEN);
        } else {
            JOptionPane.showMessageDialog(null, message, "성공", JOptionPane.INFORMATION_MESSAGE);
        }
    }

}
