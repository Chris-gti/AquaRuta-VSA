@ECHO OFF
SETLOCAL

IF NOT EXIST "%JAVA_HOME%\bin\java.exe" (
    IF EXIST "C:\Program Files\Java\jdk-26.0.1\bin\java.exe" (
        SET "JAVA_HOME=C:\Program Files\Java\jdk-26.0.1"
    ) ELSE IF EXIST "C:\Program Files\Java\latest\bin\java.exe" (
        SET "JAVA_HOME=C:\Program Files\Java\latest"
    )
)

SET "DIR=%~dp0"
CALL "%DIR%.mvn\apache-maven-3.9.9\bin\mvn.cmd" %*
EXIT /B %ERRORLEVEL%
