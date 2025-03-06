# BLE 기반 스마트 디바이스 관리 시스템

[![License: MIT](https://img.shields.io/badge/License-MIT-brightgreen.svg)](LICENSE)
[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](#)

---

## 개요

이 프로젝트는 **저전력 BLE 통신**을 활용한 전기차 배터리 관리 시스템입니다.  
회원가입, 아이디/비밀번호 찾기, 배터리 정보 등록, QR 코드 전송, BLE 연결, 배터리 진단, 사용자 설정 및 문의 게시판 등 다양한 기능을 포함하고 있습니다.

---

## 주요 기능

- **사용자 인증 시스템:** JWT 토큰 기반 로그인/회원가입, 아이디/비밀번호 찾기
- **역할 기반 접근 제어:** 구매자와 판매자에 따라 분리된 기능 및 화면 제공
- **배터리 정보 관리:** 제조사, 모델, 제품 번호 등록 및 관리
- **QR 코드 스캔:** 카메라를 이용한 배터리 QR 코드 등록
- **BLE 장치 연결:** 블루투스 저전력 장치 검색 및 페어링
- **데이터 수집 및 전송:** 센서 데이터 실시간 모니터링 후 서버 전송
- **진단 기능:** 수집된 데이터를 기반으로 배터리 상태 진단
- **사용자 설정:** 프로필 관리, 비밀번호 변경, 로그아웃 등
- **문의 게시판:** 사용자 문의 등록 및 답변 확인

---

## 상세 기술 스택

| 분야            | 기술 및 도구                                                                          |
| --------------- | ------------------------------------------------------------------------------------- |
| **프론트엔드**  | Android (Kotlin, Jetpack Compose)                                                     |
| **백엔드**      | RESTful API (Swagger 기반) – 웹 백엔드 연동 작업 진행                                    |
| **통신**        | 저전력 BLE 통신                                                                        |
| **기타**        | Daum API (우편번호 조회)                                                                 |

- **언어:** Kotlin, Java
- **UI 프레임워크:** Jetpack Compose
- **네트워크:** Retrofit2, OkHttp3
- **비동기 처리:** Coroutines, Flow
- **데이터 저장:** SharedPreferences
- **아키텍처:** MVVM, Repository 패턴
- **하드웨어 통신:** Bluetooth Low Energy (BLE)

---

## 구현 세부사항

- **안드로이드 버전별 BLE 권한 처리:** Android 6.0부터 13.0까지 각 버전에 맞춘 권한 요청 로직 적용
- **JWT 토큰 관리:** 토큰 자동 갱신 및 안전한 저장 처리
- **바이너리 프로토콜 구현:** 센서 데이터 수집을 위한 커스텀 프로토콜 적용
- **포그라운드 서비스:** 앱이 백그라운드 상태에서도 BLE 연결 유지
- **Daum 주소 API 연동:** 사업자 및 개인 주소 검색 기능 제공
- **MVVM 아키텍처 적용:** ViewModel, Repository, Service 계층 분리
- **반응형 UI:** Jetpack Compose와 Flow를 활용한 상태 관리
- **다중 사용자 타입 지원:** 구매자와 판매자에 따른 별도 워크플로우 제공
- **타임아웃 처리:** 블루투스 통신 안정성을 위한 타임아웃 메커니즘 적용
- **배터리 데이터 가공:** 센서 데이터 정규화 후 서버 전송 포맷 변환

---

## 외부 라이브러리

- **Retrofit2:** RESTful API 통신
- **OkHttp3:** HTTP 클라이언트
- **Coil:** 이미지 로딩
- **MLKit:** QR 코드 스캔
- **EventBus:** 컴포넌트 간 이벤트 전달

---

## 목차

- [메인 화면](#메인-화면)
- [회원가입 파트](#회원가입-파트)
- [아이디/비밀번호 찾기](#아이디비밀번호-찾기)
- [메인화면 진입](#메인화면-진입)
- [배터리 정보 입력 칸](#배터리-정보-입력-칸)
- [QR 코드 전송 파트](#qr-코드-전송-파트)
- [블루투스 연결 파트 (BLE)](#블루투스-연결-파트-ble)
- [진단 파트](#진단-파트)
- [설정 및 정보 수정](#설정-및-정보-수정)
- [우편번호 API (Daum API)](#우편번호-api-daum-api)
- [비밀번호 변경 및 회원 탈퇴](#비밀번호-변경-및-회원-탈퇴)
- [문의 게시판](#문의-게시판)
- [작업 환경 및 Swagger API](#작업-환경-및-swagger-api)
- [기술 스택](#상세-기술-스택)

---

## 메인 화면

<div align="center">
  <img src="https://github.com/user-attachments/assets/922c79d8-e2fd-4cc0-9356-43a87f7486c4" width="300px" alt="메인 화면">
</div>

---

## 회원가입 파트

<div align="center">
  <img src="https://github.com/user-attachments/assets/74901565-0681-4662-8516-31e1ca755aaa" width="300px" alt="회원가입 화면 1">
  <img src="https://github.com/user-attachments/assets/614da738-906a-4814-adb4-a2d3425223b2" width="300px" alt="회원가입 화면 2">
</div>

---

## 아이디/비밀번호 찾기

<div align="center">
  <img src="https://github.com/user-attachments/assets/b59b1021-fda6-4e50-8a5f-c22c90e59340" width="300px" alt="아이디/비밀번호 찾기 1">
  <img src="https://github.com/user-attachments/assets/0846d9b2-c39e-4f15-a8aa-5ee11111553e" width="300px" alt="아이디/비밀번호 찾기 2">
  <img src="https://github.com/user-attachments/assets/52b5d13c-c600-4bf8-9cbb-7cd2a3f3f8d7" width="300px" alt="아이디/비밀번호 찾기 3">
</div>

---

## 메인화면 진입

<div align="center">
  <img src="https://github.com/user-attachments/assets/f6ee6f0e-6a5e-4dea-9738-9263bd7f414b" width="300px" alt="메인화면 진입">
</div>

---

## 배터리 정보 입력 칸

<div align="center">
  <img src="https://github.com/user-attachments/assets/00076124-6797-4e67-b5b0-e386f5218b3c" width="300px" alt="배터리 정보 1">
  <img src="https://github.com/user-attachments/assets/1c501576-398d-49e9-925e-bf165f12c5d3" width="300px" alt="배터리 정보 2">
</div>

---

## QR 코드 전송 파트

- **QR 코드 전송 (사진 선택 후 업로드):**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/44cf19a9-47ea-461b-8545-627f312ff95d" width="300px" alt="QR 코드 전송">
  </div>
- **전송 중 상태 (원 표시):**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/ef824da0-e366-4b31-b4b7-53ffdc2bc5c2" width="300px" alt="전송 중">
  </div>
- **서버 업로드 후 상태:**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/e6f7466d-7890-427a-9aa1-733ec1b78dda" width="300px" alt="서버 업로드 완료">
    <img src="https://github.com/user-attachments/assets/ed821008-93c2-4ebd-be07-cc6acb5bd6d9" width="300px" alt="업로드 이미지">
  </div>
- **웹 서버 저장 모습:**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/34a24100-233f-4517-a577-d342e830c317" width="300px" alt="웹 서버 이미지 1">
    <img src="https://github.com/user-attachments/assets/55ef57d0-5fe1-4041-921c-a49b50d025f9" width="300px" alt="웹 서버 이미지 2">
  </div>

---

## 블루투스 연결 파트 (BLE)

- **BLE 연결:**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/d406a368-159e-4494-ad92-e89312d8097f" width="300px" alt="블루투스 연결">
  </div>
- **연결 해제 (클릭 시):**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/36dd5d71-6a3b-4167-9bae-052a23682d8b" width="300px" alt="블루투스 해제">
  </div>

---

## 진단 파트

- **진단 데이터 로그 (유효 데이터만 저장 후 서버 전송):**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/7b45166e-edf6-491e-8a77-a7040d1864b4" width="300px" alt="진단 로그">
  </div>
- **서버 오류 (응답코드 500):**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/67ab9ddc-1a4a-4274-a66d-012227a8d195" width="300px" alt="서버 오류">
  </div>
- **서버 전송 성공:**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/f181f46f-cae6-4aee-bd88-6e17df9997df" width="300px" alt="전송 성공">
  </div>
- **BLE 장치 문제 발생:**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/0ac9e7a6-f68b-4527-9359-2ce715d0d463" width="300px" alt="BLE 문제 1">
    <img src="https://github.com/user-attachments/assets/5ed093a8-867d-42eb-aaa9-7652520ec931" width="300px" alt="BLE 문제 2">
  </div>

---

## 설정 및 정보 수정

- **설정 (개인정보 및 탈퇴):**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/b487e42a-87cb-4cea-9041-732605ce9bb5" width="300px" alt="설정 화면 1">
    <img src="https://github.com/user-attachments/assets/bb708319-a00a-4ea2-8466-63e067178b8c" width="300px" alt="설정 화면 2">
  </div>
- **정보 수정:**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/f2886c03-e61a-4e52-a695-d4d0aef7b6fc" width="300px" alt="정보 수정 1">
    <img src="https://github.com/user-attachments/assets/693f3c6a-2358-4658-abcd-0fbdc912e70b" width="300px" alt="정보 수정 2">
  </div>

---

## 우편번호 API (Daum API)

<div align="center">
  <img src="https://github.com/user-attachments/assets/b75edade-4330-4fb1-8b99-95009cb14f90" width="300px" alt="우편번호 API 1">
  <img src="https://github.com/user-attachments/assets/0111c810-27e8-4ac2-b885-a751313976d7" width="300px" alt="우편번호 API 2">
</div>

---

## 비밀번호 변경 및 회원 탈퇴

<div align="center">
  <img src="https://github.com/user-attachments/assets/7d666388-e26a-4b52-92ce-eab73cf20281" width="300px" alt="비밀번호 변경">
  <img src="https://github.com/user-attachments/assets/1180412e-3452-449f-8883-a1245e5c5f03" width="300px" alt="회원 탈퇴">
</div>

---

## 문의 게시판

<div align="center">
  <img src="https://github.com/user-attachments/assets/224a1fec-5305-4477-83a8-298969977ea4" width="300px" alt="문의 게시판 1">
  <img src="https://github.com/user-attachments/assets/ae394e86-f2bb-4d49-ab41-82dd8db00ad1" width="300px" alt="문의 게시판 2">
  <img src="https://github.com/user-attachments/assets/33e6ac2e-b177-47f2-9c20-fd3223dee666" width="300px" alt="문의 게시판 3">
</div>

---
## 작업 환경 및 Swagger API

- **작업 환경:**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/ec8162d5-3430-4e6c-865c-d309c8667175" width="300px" alt="작업 환경">
  </div>
- **Swagger 등록 API:**  
  <div align="center">
    <img src="https://github.com/user-attachments/assets/5e22e314-495c-452d-90a8-f3f424ea389e" width="300px" alt="Swagger API">
  </div>

---
