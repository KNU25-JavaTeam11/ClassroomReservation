package org.javateam11.ClassroomReservation.view.components;

import org.javateam11.ClassroomReservation.model.User;

import javax.swing.*;
import java.awt.*;

/**
 * 사용자 드롭다운 메뉴를 담당하는 컴포넌트
 */
public class UserDropdownPanel {

    /**
     * 사용자 드롭다운 버튼을 생성합니다.
     */
    public static JButton createUserDropdownButton(User currentUser,
            Runnable myReservationCallback, Runnable logoutCallback) {

        String userDisplayText = "👤 " + currentUser.getName() + "(" + currentUser.getStudentId() + ")";
        JButton userBtn = StyleManager.createStyledButton(userDisplayText + " ▼", StyleManager.getPrimaryColor());

        // 팝업 메뉴 생성
        JPopupMenu popupMenu = new JPopupMenu();
        popupMenu.setBorder(BorderFactory.createLineBorder(StyleManager.getComboBorder(), 1));
        popupMenu.setBackground(Color.WHITE);

        // 내 예약 메뉴 아이템
        JMenuItem myReservationItem = StyleManager.createStyledMenuItem("내 예약");
        if (myReservationCallback != null) {
            myReservationItem.addActionListener(e -> myReservationCallback.run());
        }

        // 구분선
        JSeparator separator = new JSeparator();

        // 로그아웃 메뉴 아이템
        JMenuItem logoutItem = StyleManager.createStyledMenuItem("로그아웃");
        if (logoutCallback != null) {
            logoutItem.addActionListener(e -> {
                // 메인 프레임을 찾아서 중앙에 표시하도록 설정
                Window parentWindow = SwingUtilities.getWindowAncestor(userBtn);
                int result = JOptionPane.showConfirmDialog(
                        parentWindow,
                        "정말 로그아웃하시겠습니까?",
                        "로그아웃 확인",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (result == JOptionPane.YES_OPTION) {
                    logoutCallback.run();
                }
            });
        }

        popupMenu.add(myReservationItem);
        popupMenu.add(separator);
        popupMenu.add(logoutItem);

        // 버튼 클릭 시 팝업 메뉴 표시
        userBtn.addActionListener(e -> popupMenu.show(userBtn, 0, userBtn.getHeight()));

        return userBtn;
    }
}