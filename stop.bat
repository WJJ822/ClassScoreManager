@echo off
chcp 65001 >nul
setlocal
set "JAVA_HOME=D:\java"
set "CATALINA_HOME=D:\apache-tomcat-9.0.122-windows-x64\apache-tomcat-9.0.122"
set "CATALINA_BASE=%CATALINA_HOME%"
echo 正在停止 Tomcat 9...
call "%CATALINA_HOME%\bin\shutdown.bat"
echo 已发送停止指令。
pause
