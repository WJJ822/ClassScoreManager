@echo off
chcp 65001 >nul
setlocal
set "JAVA_HOME=D:\java"
set "CATALINA_HOME=D:\apache-tomcat-9.0.122-windows-x64\apache-tomcat-9.0.122"
set "CATALINA_BASE=%CATALINA_HOME%"
set "CATALINA_OPTS=-Dfile.encoding=UTF-8"
set "CLASSSCORE_HOME=%~dp0"
cd /d "%CATALINA_HOME%\bin"
call "%CATALINA_HOME%\bin\catalina.bat" run
