@echo off
chcp 65001 >nul
setlocal
set "JAVA_HOME=D:\java"
set "CATALINA_HOME=D:\apache-tomcat-9.0.122-windows-x64\apache-tomcat-9.0.122"
set "CATALINA_BASE=%CATALINA_HOME%"
set "CATALINA_OPTS=-Dfile.encoding=UTF-8"
set "CLASSSCORE_HOME=%~dp0"
echo.
echo   GradeFlow 班级学生成绩管理系统
echo   ----------------------------------------
echo   正在启动 Tomcat 9...
call "%CATALINA_HOME%\bin\startup.bat"
timeout /t 5 /nobreak >nul
start "" "http://localhost:8080/classscore/"
echo.
echo   访问地址: http://localhost:8080/classscore/
echo   教师账号: teacher / 123456
echo   学生账号: 20240101 / 123456
echo.
pause
