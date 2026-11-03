Set shell = CreateObject("WScript.Shell")
shell.CurrentDirectory = "D:\javaproject\ClassScoreManager"
shell.Run "cmd /c " & Chr(34) & "D:\javaproject\ClassScoreManager\run-tomcat.bat" & Chr(34), 0, False
