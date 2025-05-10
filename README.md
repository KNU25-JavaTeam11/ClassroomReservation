# 컴퓨터학부 강의실/시설물 예약 시스템

## 소개
이 프로젝트는 대학교 컴퓨터학부 건물(예: IT4, IT5 등) 내 강의실과 시설물을 예약할 수 있는 자바 기반의 GUI 프로그램입니다. 자바 Swing과 MVC 패턴을 적용하여, 실제 2D 도면 느낌의 UI와 직관적인 예약 기능을 제공합니다.

## 주요 기능
- **2D 도면 기반 UI**: 건물/층별 강의실 및 시설물 배치를 시각적으로 확인
- **가용 상태 표시**: 각 강의실/시설물의 사용 가능 여부를 색상과 텍스트로 표시
- **예약 기능**: 버튼 클릭 시 날짜/시간대/예약자 입력 후 예약 가능 여부 확인 및 처리
- **MVC 패턴 적용**: Model-View-Controller 구조로 유지보수성과 확장성 강화

## MVC 패턴이란?
**MVC(Model-View-Controller) 패턴**은 소프트웨어 설계 원칙 중 하나로, 프로그램을 세 가지 역할로 분리하여 개발합니다.
- **Model(모델)**: 데이터와 비즈니스 로직을 담당합니다. (예: 강의실, 시설물, 예약 정보 등)
- **View(뷰)**: 사용자에게 보여지는 UI를 담당합니다. (예: Swing 화면, 버튼 등)
- **Controller(컨트롤러)**: 사용자의 입력을 받아 Model과 View를 연결하고, 이벤트를 처리합니다.
이렇게 분리하면 코드의 재사용성과 유지보수성이 높아지고, 역할별로 협업이 쉬워집니다.

## 폴더 구조
```
ClassroomReservation/
├── src/
│   └── main/
│       ├── java/org/javateam11/
│       │   ├── controller/      # 컨트롤러 (이벤트 처리)
│       │   ├── model/           # 도메인 모델 및 비즈니스 로직
│       │   └── view/            # 사용자 인터페이스 (Swing)
│       └── resources/
├── build.gradle
├── README.md
└── ...
```

## 실행 방법
1. **Eclipse Temurin JDK 21**이 설치되어 있어야 합니다.
2. IntelliJ IDEA를 사용하여 실행하는 것을 권장합니다:
   - IntelliJ IDEA에서 프로젝트를 열고 `src/main/java/org/javateam11/Main.java`를 실행하세요.
   
   또는 터미널에서 프로젝트 루트 디렉토리로 이동 후 아래 명령어를 실행할 수 있습니다:
   ```bash
   ./gradlew build
   ./gradlew run
   ```

## 개발 환경
- Eclipse Temurin JDK 21
- Gradle 빌드 시스템
- (권장) IntelliJ IDEA 또는 Eclipse

## 기여 방법
1. 팀원들은 각자마다 자신이 개발할/수정할 기능의 브랜치를 **반드시 dev 브랜치를 베이스로** 만들어서 거기서 작업합니다.
2. 브랜치 이름은 자신이 개발할/수정할 기능을 잘 나타내는 이름으로 작성합니다. (예: `feat/reservation-view`, `fix/button-color`)
> ⚠️ **주의사항**
> - main 브랜치에 직접 커밋하지 마세요!
> - 모든 변경사항은 반드시 dev 브랜치에서 분기한 새로운 브랜치에서 작업해주세요.
> - 작업이 완료되면 Pull Request를 통해 코드 리뷰를 받은 후에만 main 브랜치에 병합됩니다.

2. 기능 추가/버그 수정 후 Pull Request(PR)를 올려주세요.
3. 코드 스타일과 주석 규칙을 지켜주세요.