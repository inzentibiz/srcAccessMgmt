@echo off
REM ===== 서버실 출입관리 - 내장 톰캣(Maven spring-boot:run) 재시작 =====
REM 사용법: 이 파일을 더블클릭하거나 터미널에서 restart.bat 실행
REM 중지: 실행된 콘솔 창에서 Ctrl + C

setlocal
set "JAVA_HOME=%USERPROFILE%\.jdks\corretto-11.0.19"
set "MVN=C:\Program Files\JetBrains\IntelliJ IDEA 2024.2.6\plugins\maven\lib\maven3\bin\mvn.cmd"

echo === 기존 9090 포트 프로세스 정리 ===
for /f "tokens=5" %%p in ('netstat -ano ^| findstr :9090 ^| findstr LISTENING') do taskkill /F /PID %%p >nul 2>&1

echo === 내장 톰캣 시작 (중지하려면 Ctrl + C) ===
call "%MVN%" -f "%~dp0pom.xml" spring-boot:run

endlocal
