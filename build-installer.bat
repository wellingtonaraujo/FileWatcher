@echo off
cls
echo =======================================================
echo   🚀 Gerando instalador do FileWatcher...
echo =======================================================
echo.

REM Caminhos principais
set "JAVA_HOME=C:\Program Files\Apache NetBeans\jdk"
set "PROJECT_DIR=C:\JavaProjects\FileWatcher"
set "OUTPUT_DIR=%PROJECT_DIR%\installer"
set "RUNTIME_DIR=%PROJECT_DIR%\runtime"
set "JPACKAGE=%JAVA_HOME%\bin\jpackage.exe"
set "JLINK=%JAVA_HOME%\bin\jlink.exe"
set "JMODO_PATH=%JAVA_HOME%\jmods"

REM =======================================================
echo 🧩 Etapa 1: Criando runtime Java customizado...
REM =======================================================

if exist "%RUNTIME_DIR%" (
    echo 🔁 Limpando runtime anterior...
    rmdir /s /q "%RUNTIME_DIR%"
)

"%JLINK%" ^
  --module-path "%JMODO_PATH%" ^
  --add-modules java.base,java.desktop,java.sql,java.naming,java.management,java.logging ^
  --output "%RUNTIME_DIR%"

if %errorlevel% neq 0 (
    echo ❌ Erro ao gerar runtime com jlink!
    pause
    exit /b 1
)

echo ✅ Runtime gerado com sucesso em: %RUNTIME_DIR%
echo.

REM =======================================================
echo 🏗️  Etapa 2: Criando instalador com jpackage...
REM =======================================================

if not exist "%OUTPUT_DIR%" mkdir "%OUTPUT_DIR%"

"%JPACKAGE%" ^
--name "FileWatcher" ^
--app-version 1.0 ^
--input "%PROJECT_DIR%\target" ^
--main-jar FileWatcher-1.0-SNAPSHOT.jar ^
--main-class com.wellington.filewatcher.FileWatcher ^
--icon "%PROJECT_DIR%\src\main\resources\images\icon.ico" ^
--type exe ^
--runtime-image "%RUNTIME_DIR%" ^
--win-dir-chooser ^
--win-menu ^
--win-shortcut ^
--description "Monitoramento automático de arquivos de exames oftalmológicos." ^
--vendor "Wellington Araujo" ^
--dest "%OUTPUT_DIR%"

if %errorlevel% neq 0 (
    echo ❌ Erro ao gerar instalador com jpackage!
    pause
    exit /b 1
)

echo.
echo ✅ Instalador gerado com sucesso!
echo 📦 Local: %OUTPUT_DIR%
pause
