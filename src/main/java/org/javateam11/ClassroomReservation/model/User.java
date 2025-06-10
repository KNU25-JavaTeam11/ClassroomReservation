package org.javateam11.ClassroomReservation.model;

import java.util.Objects;

public class User {
    private String studentId;
    private String name;

    public User(String studentId, String name) {
        this.studentId = studentId;
        this.name = name;
    }

    // 두 유저가 같은지 학번으로 판단
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true; // 같은 객체면 true
        if (obj == null || getClass() != obj.getClass())
            return false; // null 또는 다른 타입이면 false
        User user = (User) obj;
        return Objects.equals(studentId, user.studentId); // 학번 기준 비교
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId);
    }

    // 게터
    public String getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    // 셋터
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setName(String name) {
        this.name = name;
    }

}
