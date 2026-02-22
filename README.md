# BookMarker

여러 브라우저의 북마크를 한 곳에서 모아 관리할 수 있는 Android 앱입니다.

## 개요

BookMarker는 디바이스에 설치된 브라우저를 감지하고, 브라우저에서 내보낸 HTML 북마크 파일을 가져와 앱에서 탐색할 수 있도록 제공하는 앱입니다.

## 주요 기능

- **브라우저 자동 감지**: 디바이스에 설치된 브라우저를 자동으로 감지
- **HTML 북마크 가져오기**: 파일 선택기로 북마크 HTML을 가져와 파싱
- **트리 탐색 UI**: 폴더/북마크 구조를 펼쳐서 탐색
- **북마크 열기**: 선택한 기본 브라우저로 링크 열기

## 지원 브라우저

설치된 브라우저를 패키지 매니저로 감지해 표시합니다.

예시:
- Chrome
- Samsung Internet
- Firefox
- Edge
- Brave

## 기술 스택

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **Architecture**: Multi-module + Orbit MVI(Home) + Mavericks(Settings)
- **DI**: Hilt
- **Storage**: AndroidX DataStore(Preferences + Proto-style snapshot serialization)
- **Navigation**: Navigation Compose (Nav3 Router 기반 라우팅)
- **Minimum SDK**: 28 (Android 9.0)
- **Target SDK**: 36

## 모듈 구조

- `app`: Application, Activity, 초기화
- `feature:home`: 홈 화면, 북마크 가져오기/탐색
- `feature:settings`: 설정, 기본 브라우저 선택
- `core:model`: 도메인 모델
- `core:data`: repository 구현
- `core:domain`: usecase
- `core:datastore`: DataStore 구현
- `core:navigation`: 라우팅/외부 앱 이동
- `core:ui`, `core:designsystem`, `core:common`: 공통 UI/테마/유틸
