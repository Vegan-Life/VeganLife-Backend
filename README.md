<p align="center">
  <img src="https://github.com/Vegan-Life/VeganLife-Backend/assets/47537803/f3ef4db1-6709-4f32-ae51-f491aad3f9ef" />
</p>

### 목차
- [🌿 비건 라이프: VeganLife](#비건-라이프-veganlife) <br/>
    - [⏲️ 개발 기간](#개발-기간) <br/>
    - [👪 팀원](#팀원) <br/>
    - [📃 산출물](#산출물) <br/>
    - [🗄️ 서비스 아키텍처](#서비스-아키텍처) <br/>
    - [⚒️ 주요 기능](#주요-기능) <br/>
    - [💡 문제 해결](#문제-해결) <br/>
    - [📱 유저 인터페이스](#유저-인터페이스) <br/>
    - [🛠️ 기술 스택](#기술-스택) <br/>
    

<br/>

# 🌿비건 라이프 VeganLife
> 채식 주의자를 위한 식단 및 영양 관리 애플리케이션입니다.

<br/>

## ⏲️개발 기간
> 2023-12-04 ~ 진행 중

<br/>

## 👪팀원
| 이름      | 역할      | GitHub               |
|---------|---------|----------------------|
| 김승완     | 안드로이드   | [@kimsw215](https://github.com/kimsw215) |
| 공혜연     | 안드로이드   | [@hxeyexn](https://github.com/hxeyexn) |
| 고 준     | 안드로이드   | [@june0103](https://github.com/june0103) |
| 박소윤     | 백엔드/인프라 | [@soun997](https://github.com/soun997) |
| 오승연     | 백엔드     | [@O-Wensu](https://github.com/O-Wensu) |

<br/>

## 📃산출물
### [🔗 Figma 와이어프레임](https://www.figma.com/file/Rt7pheH75dRirLrzvG4i7A/%EB%B9%84%EA%B1%B4%EB%9D%BC%EC%9D%B4%ED%94%84-(team%3A-%EC%BD%A9%EA%B3%A0%EA%B8%B0)?type=design&node-id=229-414&mode=design&t=EHYavtaWTamjfuL4-0)
### [🔗 ERD 설계](https://www.erdcloud.com/d/W5PJdZsqQCy3zoaRG)
### [🔗 API 명세서](https://drive.google.com/file/d/1CYhHMkKGLQQP3vYzfIlluHBzVwXEoRUa/view?usp=sharing)

<br/>

## 🗄️서비스 아키텍처
<p align="center">
  <img src="https://github.com/Vegan-Life/VeganLife-Backend/assets/47537803/06bd6bbe-c585-4b34-8b2e-624a68c07742" width="80%" />
</p>

<br/>

## ⚒️주요 기능
### **식단 기록**
- 식사 종류별 식단 기록 등록
  - 식품 데이터셋 기반의 식품 정보 검색
- 나만의 식품 정보 등록 
### **섭취 정보**
-  식단 기록의 음식 영양소를 기반으로 일일/주간/월간/연간 섭취량 조회
### **채식 레시피**
- 사용자의 채식 타입에 맞는 레시피 추천
- 레시피 등록/조회/수정/삭제
- 레시피 스크랩
### **커뮤니티**
- 게시글 등록/조회/수정/삭제
- 댓글/답글 작성
- 댓글/게시글 좋아요
### **알림**
- 커뮤니티 활동 관련 알림
- 권장 섭취량 초과 알림

<br/>

## 💡문제 해결
#### [🔗 QueryDsl+복합 인덱스를 사용한 섭취량 조회 API 성능 2배 개선](https://yeon-dev.tistory.com/246)
#### [🔗 테스트 데이터 삽입 이후 발생하는 슬로우 쿼리 해결](https://publish.obsidian.md/ssoscode/Programming/Spring+JPA/issues/%EC%9D%B8%EB%8D%B1%EC%8A%A4%EB%A5%BC+%EC%83%9D%EC%84%B1%ED%96%88%EC%A7%80%EB%A7%8C%2C+%EC%9D%B4%EB%A5%BC+%ED%99%9C%EC%9A%A9%ED%95%98%EC%A7%80+%EB%AA%BB%ED%95%98%EB%8A%94+%EB%AC%B8%EC%A0%9C)
#### [🔗 EC2 요금 절감을 위한 노력](https://publish.obsidian.md/ssoscode/Programming/Infra/AWS/issues/EC2+%EB%8B%A4%EC%9A%B4%EA%B7%B8%EB%A0%88%EC%9D%B4%EB%93%9C)
#### [ㄴ🔗 Jenkins ➡️ Github Actions 마이그레이션](https://publish.obsidian.md/ssoscode/Programming/Infra/AWS/issues/Jenkins+%E2%9E%A1%EF%B8%8F+Github+Actions)
#### [🔗 커스텀 검증 애노테이션을 통해 리스트 내 문자열 길이 검증](https://yeon-dev.tistory.com/247)

<br/>

## 📱유저 인터페이스
<details>
<summary>상세 화면 확인</summary>
<p align="center">
  <img src="https://github.com/Vegan-Life/VeganLife-Backend/assets/47537803/6125fee2-c33b-4a3b-837f-69eea4204c63"/>
</p>
</details>

<br/>

## 🛠️기술 스택
**개발** <br/>
![Java](https://img.shields.io/badge/java17-%23007396.svg?style=for-the-badge&logo=openjdk&logoColor=white)
![SpringBoot](https://img.shields.io/badge/Spring_Boot-6DB33F.svg?&style=for-the-badge&logo=Spring%20Boot&logoColor=white)
![SpringSecurity](https://img.shields.io/badge/spring_security-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
![SpringJPA](https://img.shields.io/badge/spring_data_jpa-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)

**데이터베이스** <br/>
![MariaDB](https://img.shields.io/badge/MariaDB-003545.svg?&style=for-the-badge&logo=MariaDB&logoColor=white              )

**인프라** <br/>
![AWS](https://img.shields.io/badge/AWS-232F3E.svg?&style=for-the-badge&logo=Amazon%20AWS&logoColor=white)
![Nginx](https://img.shields.io/badge/nginx-009639.svg?style=for-the-badge&logo=nginx&logoColor=white)

**데브옵스** <br/>
![Docker](https://img.shields.io/badge/Docker-2496ED.svg?&style=for-the-badge&logo=Docker&logoColor=white)
![GitHubActions](https://img.shields.io/badge/GitHub_Actions-2088FF.svg?&style=for-the-badge&logo=GitHub%20Actions&logoColor=white)

**관리 및 협업** <br/>
![GitHub](https://img.shields.io/badge/GitHub-181717.svg?&style=for-the-badge&logo=GitHub&logoColor=white)
![Discord](https://img.shields.io/badge/Discord-5865F2.svg?style=for-the-badge&logo=notion&logoColor=white)
![Notion](https://img.shields.io/badge/Notion-000000.svg?style=for-the-badge&logo=notion&logoColor=white)

<br/>
