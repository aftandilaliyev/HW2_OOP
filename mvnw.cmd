@REM Maven Wrapper Script - Windows batch version
@echo off
setlocal enabledelayedexpansion

set "MAVEN_PROJECTBASEDIR=%~dp0"
set "WRAPPER_JAR=%MAVEN_PROJECTBASEDIR%.mvn\wrapper\maven-wrapper.jar"
set "DOWNLOAD_URL=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar"

if not exist "%WRAPPER_JAR%" (
    echo Downloading Maven wrapper...
    certutil -urlcache -split -f "%DOWNLOAD_URL%" "%WRAPPER_JAR%"
    if errorlevel 1 (
        echo.
        echo ERROR: Failed to download Maven wrapper jar.
        echo Please download manually from: %DOWNLOAD_URL%
        echo And save to: %WRAPPER_JAR%
        exit /b 1
    )
)

@REM Strip trailing backslash to avoid quoting issues with -D property
set "PROJ=%MAVEN_PROJECTBASEDIR:~0,-1%"

java -Dmaven.multiModuleProjectDirectory="%PROJ%" -cp "%WRAPPER_JAR%" org.apache.maven.wrapper.MavenWrapperMain %*
endlocal
