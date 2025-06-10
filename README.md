# 컴퓨터학부 강의실 예약 시스템

## 소개

이 프로젝트는 경북대학교 컴퓨터학부 건물(예: IT4, IT5 등) 내 강의실을 예약할 수 있는 자바 기반의 GUI 프로그램입니다. 자바 Swing과 MVC 패턴을 적용하여, 실제 2D 도면 느낌의 UI와 직관적인 예약 기능을 제공합니다.

**🔗 Spring 백엔드 연동**을 통해 실시간 예약 데이터 동기화와 멀티스레드 처리를 제공합니다.

## 주요 기능

- **2D 도면 기반 UI**: 건물/층별 강의실 배치를 시각적으로 확인
- **가용 상태 표시**: 각 강의실의 사용 가능 여부를 색상과 텍스트로 표시
- **예약 기능**: 버튼 클릭 시 날짜/시간대/예약자 입력 후 예약 가능 여부 확인 및 처리
- **Spring 백엔드 연동**: HTTP API를 통한 실시간 데이터 동기화
- **비동기 처리**: UI 블로킹 없는 백엔드 통신 (멀티스레드)
- **MVC 패턴 적용**: Model-View-Controller 구조로 유지보수성과 확장성 강화

## 🔧 기술 스택

### 백엔드 연동

- **HTTP 클라이언트**: OkHttp 4.12.0
- **JSON 처리**: Jackson 2.16.1 (Spring Boot와 호환)
- **로깅**: SLF4J + Logback
- **비동기 처리**: CompletableFuture + SwingUtilities.invokeLater

### 멀티스레드 설계

- **UI 스레드 보호**: 모든 네트워크 요청은 백그라운드 스레드에서 처리
- **EDT 안전성**: UI 업데이트는 반드시 Event Dispatch Thread에서 실행
- **로딩 표시**: 사용자에게 처리 중임을 알리는 모달 다이얼로그

### 멀티스레드 처리 흐름

1. **사용자 액션** (예: 예약 버튼 클릭) → EDT
2. **로딩 다이얼로그 표시** → EDT
3. **백엔드 API 호출** → 백그라운드 스레드 (CompletableFuture)
4. **응답 처리 및 UI 업데이트** → EDT (SwingUtilities.invokeLater)
5. **로딩 다이얼로그 닫기** → EDT

## Gradle이란?

**Gradle**은 자바 프로젝트에서 자주 사용하는 "빌드 도구"입니다.

- 여러 개의 자바 파일을 한 번에 컴파일하고, 실행에 필요한 라이브러리(외부 jar)도 자동으로 관리해줍니다.
- `build.gradle` 파일에 필요한 설정을 적어두면, 복잡한 명령 없이 `./gradlew build` 또는 `./gradlew run`만으로 프로젝트를 빌드/실행할 수 있습니다.
- 이 프로젝트에서는 소스코드 컴파일, 실행, 라이브러리 관리(예: OkHttp, Jackson) 등을 Gradle이 자동으로 처리해줍니다.
- **장점:**
  - IDE 없이도 터미널에서 쉽게 빌드/실행 가능
  - 팀원 모두가 동일한 환경에서 개발 가능
  - 라이브러리 추가/관리가 매우 쉬움

## MVC 패턴이란?

**MVC(Model-View-Controller) 패턴**은 소프트웨어 설계 원칙 중 하나로, 프로그램을 세 가지 역할로 분리하여 개발합니다.

- **Model(모델)**: 데이터와 비즈니스 로직을 담당합니다. (예: 강의실, 예약 정보 등)
- **View(뷰)**: 사용자에게 보여지는 UI를 담당합니다. (예: Swing 화면, 버튼 등)
- **Controller(컨트롤러)**: 사용자의 입력을 받아 Model과 View를 연결하고, 이벤트를 처리합니다.
- **Service(서비스)**: 외부 시스템(Spring 백엔드)과의 통신을 담당합니다.
  이렇게 분리하면 코드의 재사용성과 유지보수성이 높아지고, 역할별로 협업이 쉬워집니다.

## 폴더 구조

```
ClassroomReservation/
├── src/
│   └── main/
│       ├── java/org/javateam11/ClassroomReservation/
│       │   ├── controller/      # 컨트롤러 (이벤트 처리)
│       │   ├── model/           # 도메인 모델 및 비즈니스 로직
│       │   ├── service/         # Spring 백엔드 연동 서비스
│       │   └── view/            # 사용자 인터페이스 (Swing)
│       └── resources/
│           └── logback.xml      # 로깅 설정
├── build.gradle                 # 의존성 및 빌드 설정
├── README.md
└── ...
```

## 🚀 실행 방법

### 1. 사전 요구사항

- **JDK 17 이상**이 설치되어 있어야 합니다.
- **Spring 백엔드 서버**가 http://localhost:8080 에서 실행 중이어야 합니다.
  - API 문서: http://localhost:8080/swagger-ui/index.html

### 2. 실행 방법

IntelliJ IDEA를 사용하여 실행하는 것을 권장합니다:

- IntelliJ IDEA에서 프로젝트를 열고 `src/main/java/org/javateam11/ClassroomReservation/Main.java`를 실행하세요.

또는 터미널에서 프로젝트 루트 디렉토리로 이동 후 아래 명령어를 실행할 수 있습니다:

```bash
# 의존성 다운로드 및 빌드
./gradlew build

# 프로그램 실행
./gradlew run
```

### 3. 백엔드 연결 필수

⚠️ **중요**: 이 애플리케이션은 Spring 백엔드 서버와의 연동이 **필수**입니다.

- 백엔드 서버가 실행되지 않은 경우, 예약 기능이 동작하지 않습니다.
- 백엔드 서버 주소는 `config/ApplicationConfig.java`에서 수정할 수 있습니다.

## 개발 환경

- Eclipse Temurin JDK 21
- Gradle 빌드 시스템
- (권장) IntelliJ IDEA 또는 Eclipse

## 기여 방법

> ⚠️ **주의사항**
>
> - main 또는 dev 브랜치에 직접 커밋하지 마세요!
> - dev 브랜치에서 새 브랜치를 만들기 전에 항상 pull 해서 최신 상태를 유지해주세요.
> - 모든 변경사항은 반드시 dev 브랜치에서 분기한 새로운 브랜치에서 작업해주세요.
> - 작업이 완료되면 Pull Request를 통해 코드 리뷰를 받은 후에만 main 브랜치에 병합됩니다.

1. 팀원들은 각자마다 자신이 개발할/수정할 기능의 브랜치를 **반드시 dev 브랜치를 베이스로** 만들어서 거기서 작업합니다.
2. 브랜치 이름은 자신이 개발할/수정할 기능을 잘 나타내는 이름으로 작성합니다. (예: `feat/reservation-view`, `fix/button-color`)
3. 기능 추가/버그 수정 후 Pull Request(PR)를 올려주세요.
4. 코드 스타일을 잘 지키고 주석을 잘 달아주세요.
