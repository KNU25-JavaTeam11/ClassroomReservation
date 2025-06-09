# 폰트 호환성 가이드

## 개요

이 프로젝트는 윈도우와 맥OS에서 모두 적절한 한국어 폰트를 사용할 수 있도록 크로스 플랫폼 폰트 호환성을 지원합니다.

## FontUtils 클래스

`org.javateam11.ClassroomReservation.util.FontUtils` 클래스는 운영체제에 따라 적절한 한국어 폰트를 자동으로 선택합니다.

### 폰트 우선순위

1. **맑은 고딕** (Windows)
2. **Malgun Gothic** (Windows 영문명)
3. **Apple SD Gothic Neo** (macOS)
4. **Apple Gothic** (macOS 구버전)
5. **Noto Sans CJK KR** (Linux)
6. **NanumGothic** (대안)
7. **Dialog** (시스템 기본)

### 사용 방법

```java
import org.javateam11.ClassroomReservation.util.FontUtils;

// 기본 폰트 사용
Font titleFont = FontUtils.getTitleFont();      // 20pt, BOLD
Font labelFont = FontUtils.getLabelFont();      // 14pt, PLAIN
Font buttonFont = FontUtils.getButtonFont();    // 14pt, BOLD
Font plainFont = FontUtils.getPlainFont();      // 14pt, PLAIN
Font smallFont = FontUtils.getSmallFont();      // 12pt, PLAIN

// 사용자 정의 폰트
Font customFont = FontUtils.getCustomFont(Font.ITALIC, 16);
```

### 기존 코드 마이그레이션

**변경 전:**

```java
titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
```

**변경 후:**

```java
titleLabel.setFont(FontUtils.getTitleFont());
```

## 지원 운영체제

- **Windows**: 맑은 고딕 사용
- **macOS**: Apple SD Gothic Neo 사용
- **Linux**: Noto Sans CJK KR 또는 NanumGothic 사용

## 장점

1. **크로스 플랫폼 호환성**: 운영체제별로 최적의 폰트 자동 선택
2. **성능 최적화**: 폰트 검색 결과 캐싱으로 성능 향상
3. **유지보수성**: 중앙화된 폰트 관리로 일관성 유지
4. **확장성**: 새로운 폰트나 운영체제 지원 쉽게 추가 가능

## 디버깅

현재 사용중인 폰트를 확인하려면:

```java
String currentFont = FontUtils.getCurrentFontName();
System.out.println("현재 폰트: " + currentFont);
```

## 주의사항

- 폰트 변경 시 애플리케이션 재시작 필요
- 시스템에 한국어 폰트가 없는 경우 기본 Dialog 폰트 사용
- 폰트 이름은 대소문자를 구분하므로 정확히 입력해야 함
