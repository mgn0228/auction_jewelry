### 구성
- Spring Boot : 3.1.2
- Java : 17.x.x
- Build : Maven
- IDE : IntelliJ
- Dababase
  - MariaDB
- DB 쿼리 작성
  - Mybatis
---
### 구조
- 테이블 스키마와 요청값을 기반으로 동적 SQL을 생성하여 처리
  - INFO_MENU : 메뉴 항목의 코드값 그룹
  - INFO_DATABASE : 페이지(PID)별로 어떤 프로시저 또는 쿼를 수행할지에 대한 메타 정보
  - INFO_TABLE_FIELDS : INFO_DATABASE에서 정의한 쿼리에 포함될 필드들에 대한 상세 설정
  - CONTENT_PAGES : 화면(PID) 단위로 어떤 페이지가 있고, 그 페이지가 어떤 속성을 가졌는지를 정의
  - CONTENT_PAGES_PROPS : CONTENT_PAGES와 연결된 속성 정보

<pre>
  사용자 요청 (PID)
  ↓
  CONTENT_PAGES + CONTENT_PAGES_PROPS 조회
  ↓
  필요한 Sub PID 확인
  ↓
  INFO_DATABASE 조회 (쿼리/테이블 메타)
  ↓
  INFO_TABLE_FIELDS 조회 (필드 상세 메타)
  ↓
  동적 화면/폼/목록/메뉴 생성
</pre>
