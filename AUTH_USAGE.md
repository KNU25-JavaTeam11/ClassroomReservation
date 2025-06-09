# JWT 인증 시스템 사용법

이 문서는 ClassroomReservation 프로젝트에 추가된 JWT 토큰 기반 인증 시스템의 사용법을 설명합니다.

## 구성 요소

### 1. DTO 클래스

- `AuthRequest`: 로그인/회원가입 요청 데이터
- `AuthResponse`: 로그인/회원가입 응답 데이터 (username, token 포함)

### 2. 핵심 서비스 클래스

- `TokenManager`: JWT 토큰을 관리하는 싱글톤 클래스
- `AuthService`: 인증 관련 API 호출을 담당하는 서비스
- `ApiService`: HTTP 요청 처리 (인증된 요청과 일반 요청 모두 지원)

## 기본 사용법

### 1. 회원가입

```java
AuthService authService = new AuthService();

authService.register("사용자명", "비밀번호")
    .thenAccept(response -> {
        System.out.println("회원가입 성공: " + response.getUsername());
        // 회원가입 성공 시 자동으로 로그인 상태가 됩니다
    })
    .exceptionally(throwable -> {
        System.err.println("회원가입 실패: " + throwable.getMessage());
        return null;
    });
```

### 2. 로그인

```java
authService.login("사용자명", "비밀번호")
    .thenAccept(response -> {
        System.out.println("로그인 성공: " + response.getUsername());
        System.out.println("토큰: " + response.getToken());
    })
    .exceptionally(throwable -> {
        System.err.println("로그인 실패: " + throwable.getMessage());
        return null;
    });
```

### 3. 로그아웃

```java
authService.logout();
System.out.println("로그아웃 완료");
```

### 4. 인증 상태 확인

```java
boolean isLoggedIn = authService.isLoggedIn();
String currentUser = authService.getCurrentUsername();

System.out.println("로그인 상태: " + isLoggedIn);
System.out.println("현재 사용자: " + currentUser);
```

## 인증된 API 호출

### 1. GET 요청 (인증 필요)

```java
ApiService apiService = new ApiService();

// 내 예약 목록 조회 예제
apiService.getAuthenticatedAsync("/api/reservations/my", ReservationListResponse.class)
    .thenAccept(reservations -> {
        System.out.println("예약 목록 조회 성공");
    })
    .exceptionally(throwable -> {
        System.err.println("API 호출 실패: " + throwable.getMessage());
        return null;
    });
```

### 2. POST 요청 (인증 필요)

```java
// 새 예약 생성 예제
ReservationRequest request = new ReservationRequest(/* ... */);

apiService.postAuthenticatedAsync("/api/reservations", request, ReservationResponse.class)
    .thenAccept(response -> {
        System.out.println("예약 생성 성공");
    })
    .exceptionally(throwable -> {
        System.err.println("예약 생성 실패: " + throwable.getMessage());
        return null;
    });
```

### 3. PUT 요청 (인증 필요)

```java
// 예약 수정 예제
UpdateReservationRequest request = new UpdateReservationRequest(/* ... */);

apiService.putAuthenticatedAsync("/api/reservations/1", request, ReservationResponse.class)
    .thenAccept(response -> {
        System.out.println("예약 수정 성공");
    })
    .exceptionally(throwable -> {
        System.err.println("예약 수정 실패: " + throwable.getMessage());
        return null;
    });
```

### 4. DELETE 요청 (인증 필요)

```java
// 예약 삭제 예제
apiService.deleteAuthenticatedAsync("/api/reservations/1")
    .thenRun(() -> {
        System.out.println("예약 삭제 성공");
    })
    .exceptionally(throwable -> {
        System.err.println("예약 삭제 실패: " + throwable.getMessage());
        return null;
    });
```

## 오류 처리

### 1. 인증 오류 (401)

```java
// 토큰이 만료되거나 유효하지 않은 경우
apiService.getAuthenticatedAsync("/api/some-endpoint", SomeResponse.class)
    .exceptionally(throwable -> {
        if (throwable.getMessage().contains("인증이 만료되었습니다")) {
            // 다시 로그인 화면으로 이동
            showLoginDialog();
        }
        return null;
    });
```

### 2. 네트워크 오류

```java
authService.login("username", "password")
    .exceptionally(throwable -> {
        if (throwable.getCause() instanceof IOException) {
            // 네트워크 연결 오류
            showErrorMessage("서버에 연결할 수 없습니다.");
        } else {
            // 기타 오류
            showErrorMessage("로그인 중 오류가 발생했습니다: " + throwable.getMessage());
        }
        return null;
    });
```

## GUI 통합 예제

### Swing에서 사용하는 경우

```java
public class LoginDialog extends JDialog {
    private AuthService authService = new AuthService();

    private void performLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        authService.login(username, password)
            .thenAccept(response -> {
                SwingUtilities.invokeLater(() -> {
                    // UI 업데이트는 EDT에서 수행
                    dispose(); // 로그인 대화상자 닫기
                    showMainWindow(); // 메인 창 표시
                });
            })
            .exceptionally(throwable -> {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "로그인 실패: " + throwable.getMessage(),
                        "오류",
                        JOptionPane.ERROR_MESSAGE);
                });
                return null;
            });
    }
}
```

## 주의사항

1. **스레드 안전성**: 모든 API 호출은 비동기로 처리되므로, UI 업데이트 시 적절한 스레드에서 수행해야 합니다.

2. **토큰 관리**: TokenManager는 싱글톤이므로 애플리케이션 전체에서 공유됩니다.

3. **오류 처리**: 네트워크 오류, 인증 오류 등을 적절히 처리해야 합니다.

4. **보안**: 토큰은 메모리에만 저장되며, 애플리케이션 종료 시 사라집니다.

## 백엔드 API 명세

- **회원가입**: `POST /api/auth/register`
- **로그인**: `POST /api/auth/login`
- **요청 형식**: `{"username": "사용자명", "password": "비밀번호"}`
- **응답 형식**: `{"username": "사용자명", "token": "JWT토큰"}`
- **인증 헤더**: `Authorization: Bearer {JWT토큰}`
