package org.javateam11.ClassroomReservation.controller;

import java.util.concurrent.CompletableFuture;

/**
 * SignUp 기능을 담당하는 컨트롤러의 인터페이스
 * MVC 패턴에서 View와 Controller 간의 결합도를 낮추기 위해 사용됩니다.
 */
public interface ISignUpController {
    /**
     * 회원가입 처리
     */
    CompletableFuture<Boolean> register(String name, String studentNumber, String password, String confirmPassword);
}