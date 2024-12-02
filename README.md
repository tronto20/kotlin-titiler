# kotlin-titiler

[titiler](https://github.com/developmentseed/titiler) 의 기능을 kotlin (JVM) 으로 구현하는 것을 목표로 합니다.

GDAL/Spring 기반으로 동작하는 동적 타일 렌더링 서버입니다.

## Features

---

- 이미지 동적 렌더링
- Spring 기반 웹서비스
- COG (Cloud Optimized GeoTIFF) 지원

## Modules

---

- core : 정해진 파라미터들로 원하는 타일을 생성합니다.
- spring-application : Spring 을 기반으로 웹 서비스를 구축합니다.

## Project Configuration

---

이 프로젝트는 IntelliJ 로 개발되고 있으며, 일관된 개발 경험을 위해 .idea/* 의 일부 파일을 vcs 로 관리합니다.

kotlinter 와 .editorconfig 를 사용하여 코드 스타일을 관리합니다.

dependencies 모듈을 통해 라이브러리 의존성을 관리합니다.
다른 모듈들은 dependencies 모듈을 사용하며 버전을 명시하지 않습니다.

```kotlin
...
dependencies {
    implementation(platform(projects.dependencies))
    ...
}
...
```

## 기능 흐름

---

1. 소스 영상에서 이미지 데이터 추출
    1. 소스 영상 열기
    2. 좌표계에 맞게 영상 변환 (VRT)
    3. 원하는 영역에 맞게 bounds 생성하여 데이터 추출
    4. 원하는 영역에 맞게 자르고 회전
2. 이미지 처리
    1. 밴드 수 맞추기
    2. 데이터 rescale 하기
4. 타겟 영상 생성
    1. Create 를 지원하면 바로 생성. CreateCopy 만 지원하면 Buffered 로 생성.
    2. 처리된 이미지 결과물을 쓰기
5. 타겟 데이터 출력
    1. 타겟 영상의 raw data 를 응답
    2. 타겟 영상 제거

