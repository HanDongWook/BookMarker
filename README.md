# BookMarker

여러 브라우저의 북마크를 한 곳에서 모아 관리할 수 있는 Android 앱입니다.

## 개요

BookMarker는 디바이스에 설치된 다양한 브라우저(Chrome, Samsung Internet, Firefox 등)의 북마크를 통합하여 보여주는 앱입니다. 여러 브라우저를 사용하는 사용자가 북마크를 효율적으로 관리할 수 있도록 도와줍니다.

## 주요 기능

- **브라우저 자동 감지**: 디바이스에 설치된 브라우저를 자동으로 감지
- **북마크 통합**: 여러 브라우저의 북마크를 한 곳에서 확인
- **통합 검색**: 모든 브라우저의 북마크를 한 번에 검색
- **브라우저별 필터링**: 특정 브라우저의 북마크만 필터링하여 보기
- **빠른 열기**: 원하는 브라우저로 북마크 바로 열기

## 지원 브라우저

- Google Chrome
- Samsung Internet
- Mozilla Firefox
- Microsoft Edge
- Opera
- Brave
- 기타 표준 북마크 API를 지원하는 브라우저

## 기술 스택

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: MVVM
- **Minimum SDK**: 28 (Android 9.0)
- **Target SDK**: 36

## 권한

앱에서 사용하는 권한:

| 권한 | 용도 |
|------|------|
| `QUERY_ALL_PACKAGES` | 설치된 브라우저 목록 조회 |
