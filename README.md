# kotlin-titiler

[titiler](https://github.com/developmentseed/titiler) 의 기능을 kotlin (JVM) 으로 구현하는 것을 목표로 합니다.

GDAL/Spring 기반으로 동작하는 동적 타일 렌더링 서버입니다.

## Features

---

- 이미지 동적 렌더링 (예정)
- Spring 기반 웹서비스 (예정)
- Cloud Optimized GeoTIFF 지원 (예정)

## Modules

---

이 프로젝트는 여러 모듈로 나누어서 개발합니다.

- core : 정해진 파라미터들로 원하는 타일을 생성합니다.
- application : Spring 을 기반으로 웹 서비스를 구축합니다. (예정)
- 추가로 여러 모듈을 개발하여 기능을 확장합니다.

## Project Configuration

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
