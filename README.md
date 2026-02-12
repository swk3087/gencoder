# Gencoder (Android Vibe Coding Tool)

GPT CLI / Gemini CLI를 한 앱에서 선택해 사용하고, 모델별 대화를 기억하며, 토큰/세션을 안전하게 관리하는 안드로이드 앱입니다.

## 핵심 기능
- **모델 선택**: GPT CLI, Gemini CLI 전환
- **대화 메모리**: 모델별 대화 기록(Room)
- **토큰 관리**: EncryptedSharedPreferences 기반 보관
- **로그인/세션 관리**: 사용자명, 선택 모델, 작업 폴더 유지
- **작업 폴더 지정**: CLI 실행 기준 폴더 지정
- **Git Clone 지원**: 앱 내에서 `git clone` 실행

## 구조
- `ui/`: Compose 화면 + 상태 표시
- `ui/viewmodel/`: 앱 상태 관리 및 비즈니스 흐름 제어
- `data/local/`: Room DB/DAO
- `data/repo/`: CLI 실행, git clone, 대화 저장 저장소
- `util/`: 보안 토큰 저장소, 세션 저장소

## 동작 방식
1. 사용자명/작업 폴더 입력
2. 모델 선택 후 토큰 입력
3. 프롬프트 실행 시 선택 모델 CLI 호출
4. 사용자/모델 응답을 DB에 저장
5. 필요 시 Git URL 입력 후 clone

## 주의사항
- 장치 또는 환경에 `gpt`, `gemini`, `git` 실행 파일이 존재해야 합니다.
- 모델 CLI별 인증 파라미터가 다르면 `CliExecutor`에서 인자 구성을 맞춰야 합니다.
- 현재는 HTTP(S) clone URL만 허용합니다.
