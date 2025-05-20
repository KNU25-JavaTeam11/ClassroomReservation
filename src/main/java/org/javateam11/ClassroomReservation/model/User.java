package org.javateam11.ClassroomReservation.model;

import java.util.Objects;

public class User {
    private String username; //학생의 이름
    private String studentNumber; //학번

    public User(String username, String studentNumber) {
        this.username = username;
        this.studentNumber = studentNumber;
    }

    //두 유저가 같은지 학번으로 판단
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;  // 같은 객체면 true
        if (obj == null || getClass() != obj.getClass()) return false;  // null 또는 다른 타입이면 false
        User user = (User) obj;
        return Objects.equals(studentNumber, user.studentNumber);  // 학번 기준 비교
    }
    @Override
    public int hashCode() {
        return Objects.hash(studentNumber);
    }

    //게터
    public String getUsername() { return username; }
    public String getStudentNumber() { return studentNumber; }

    //셋터
    public void setUsername(String username) { this.username = username; }
    public void setStudentNumber(String studentNumber) { this.studentNumber = studentNumber; }

}
