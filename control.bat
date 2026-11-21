@echo off
setlocal EnableDelayedExpansion
chcp 65001 >nul
title AquaRuta VSA - Panel de Control
cd /d "%~dp0"

REM Localizar Docker Desktop y agregarlo al PATH
set "DOCKER_APP="
if exist "%LOCALAPPDATA%\Programs\DockerDesktop\resources\bin\docker.exe" (
    set "PATH=%LOCALAPPDATA%\Programs\DockerDesktop\resources\bin;%PATH%"
    set "DOCKER_APP=%LOCALAPPDATA%\Programs\DockerDesktop\Docker Desktop.exe"
)
if exist "%ProgramFiles%\Docker\Docker\resources\bin\docker.exe" (
    set "PATH=%ProgramFiles%\Docker\Docker\resources\bin;%PATH%"
    set "DOCKER_APP=%ProgramFiles%\Docker\Docker\Docker Desktop.exe"
)

REM Manejo de parametros directos (CLI)
if /i "%~1"=="status" goto check_status_cli
if /i "%~1"=="start" goto start_all_cli
if /i "%~1"=="stop" goto stop_all_cli
if /i "%~1"=="start-wsl" goto start_wsl_cli
if /i "%~1"=="stop-wsl" goto stop_wsl_cli
if /i "%~1"=="start-db" goto start_db_cli
if /i "%~1"=="stop-db" goto stop_db_cli
if /i "%~1"=="start-backend" goto start_backend_cli
if /i "%~1"=="stop-backend" goto stop_backend_cli

:menu
cls
echo ======================================================================
echo                 AquaRuta VSA - Panel de Control
echo      Sistema Geoespacial y de Grafos [Villahermosa, Tabasco]
echo ======================================================================
echo.

REM Deteccion de Estado de WSL (vmmem)
set "WSL_STATUS=DETENIDO"
tasklist /fi "imagename eq vmmem*" 2>nul | findstr /i "vmmem" >nul 2>&1
if not errorlevel 1 set "WSL_STATUS=ACTIVO [vmmem en ejecucion]"

REM Deteccion de Estado de Docker Desktop
set "DOCKER_STATUS=DETENIDO"
docker info >nul 2>&1
if not errorlevel 1 set "DOCKER_STATUS=ACTIVO [Motor corriendo]"

REM Deteccion de Estado de la Base de Datos PostGIS en puerto 5432
set "DB_STATUS=DETENIDO"
netstat -ano 2>nul | findstr ":5432" | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 (
    set "DB_STATUS=EN EJECUCION [Puerto 5432 Activo]"
) else (
    if "!DOCKER_STATUS!"=="DETENIDO" (
        set "DB_STATUS=DETENIDO (Requiere iniciar Docker/WSL)"
    ) else (
        set "DB_STATUS=DETENIDO (Listo para docker compose up)"
    )
)

REM Deteccion de Estado del Backend en puerto 8080
set "BACKEND_STATUS=DETENIDO"
set "BACKEND_PID="
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":8080" ^| findstr "LISTENING"') do (
    set "BACKEND_PID=%%a"
    set "BACKEND_STATUS=EN EJECUCION [PID: %%a - Puerto 8080]"
)

echo  ESTADO ACTUAL DEL ENTORNO:
echo  --------------------------------------------------------------------
echo   [1] WSL (Subsistema Linux) : !WSL_STATUS!
echo   [2] Docker Desktop         : !DOCKER_STATUS!
echo   [3] Base de Datos PostGIS  : !DB_STATUS!
echo   [4] Backend Spring Boot    : !BACKEND_STATUS!
echo  --------------------------------------------------------------------
echo.
echo  CONTROL GLOBAL:
echo   [1] Ver Estado detallado
echo   [2] Encender TODO [WSL + Docker + BD PostGIS + Backend]
echo   [3] Apagar TODO [Backend + BD + Docker + WSL Shutdown]
echo.
echo  CONTROL POR COMPONENTES:
echo   [4] Encender Base de Datos [Inicia WSL/Docker si es necesario]
echo   [5] Apagar Base de Datos [Detiene contenedor PostGIS]
echo   [6] Encender Backend [Spring Boot en puerto 8080]
echo   [7] Apagar Backend [Spring Boot]
echo   [8] Encender solo WSL [wsl -e true]
echo   [9] Apagar solo WSL [wsl --shutdown (Libera memoria RAM)]
echo.
echo  ACCESOS DIRECTOS:
echo   [W] Abrir Interfaz Web [http://localhost:8080]
echo   [S] Abrir Swagger UI [http://localhost:8080/swagger-ui.html]
echo   [D] Abrir aplicacion Docker Desktop
echo   [0] Salir
echo ======================================================================
set /p "OPC=Selecciona una opcion: "

if "%OPC%"=="1" goto ver_estado
if "%OPC%"=="2" goto encender_todo
if "%OPC%"=="3" goto apagar_todo
if "%OPC%"=="4" goto encender_db
if "%OPC%"=="5" goto apagar_db
if "%OPC%"=="6" goto encender_backend
if "%OPC%"=="7" goto apagar_backend
if "%OPC%"=="8" goto encender_wsl
if "%OPC%"=="9" goto apagar_wsl
if /i "%OPC%"=="W" goto abrir_web
if /i "%OPC%"=="S" goto abrir_swagger
if /i "%OPC%"=="D" goto abrir_docker_app
if "%OPC%"=="0" goto salir

echo Opcion no valida.
timeout /t 2 >nul
goto menu

:ver_estado
cls
echo ======================================================================
echo                       ESTADO DETALLADO
echo ======================================================================
echo.
echo [1] Distribuciones WSL:
wsl -l -v
echo.
echo [2] Proceso de Memoria WSL (vmmem):
tasklist /fi "imagename eq vmmem*"
echo.
echo [3] Contenedores Docker:
docker compose ps 2>nul
if errorlevel 1 echo Docker Desktop no esta respondiendo.
echo.
echo [4] Puerto 5432 (PostgreSQL / PostGIS):
netstat -ano | findstr ":5432" | findstr "LISTENING"
if errorlevel 1 echo Puerto 5432 libre (Base de Datos no esta escuchando).
echo.
echo [5] Puerto 8080 (Backend Spring Boot):
netstat -ano | findstr ":8080" | findstr "LISTENING"
if errorlevel 1 echo Puerto 8080 libre (Backend no esta escuchando).
echo.
pause
goto menu

:encender_wsl
cls
echo Iniciando Windows Subsystem for Linux (WSL)...
wsl -e true >nul 2>&1
echo WSL iniciado correctamente.
timeout /t 2 >nul
goto menu

:apagar_wsl
cls
echo ======================================================================
echo                 APAGANDO WSL (wsl --shutdown)
echo ======================================================================
echo.
echo Deteniendo maquinas virtuales y liberando memoria RAM de vmmem...
wsl --shutdown
echo WSL apagado exitosamente. Memoria liberada.
timeout /t 2 >nul
goto menu

:encender_todo
cls
echo ======================================================================
echo                   ENCENDIENDO TODOS LOS SERVICIOS
echo ======================================================================
echo.
echo [1/4] Asegurando que WSL este iniciado...
wsl -e true >nul 2>&1

echo.
echo [2/4] Verificando e iniciando Docker Desktop...
docker info >nul 2>&1
if errorlevel 1 (
    echo Docker Desktop no esta activo. Iniciando aplicacion...
    if exist "!DOCKER_APP!" (
        start "" "!DOCKER_APP!"
    ) else (
        echo [ERROR] No se encontro el ejecutable de Docker Desktop.
        pause
        goto menu
    )
    echo Esperando a que el motor de Docker responda...
    set /a intentos=0
    :loop_wait_docker_all
    timeout /t 3 >nul
    docker info >nul 2>&1
    if errorlevel 1 (
        set /a intentos+=1
        echo   ...esperando a Docker Desktop [intento !intentos!/25]
        if !intentos! lss 25 goto loop_wait_docker_all
        echo [ADVERTENCIA] Docker tardo demasiado. Continuando...
    )
    echo Motor Docker Desktop listo.
) else (
    echo Motor Docker Desktop ya esta activo.
)

echo.
echo [3/4] Levantando contenedor PostGIS con Docker Compose...
docker compose up -d
echo Esperando a que PostgreSQL acepte conexiones en puerto 5432...
set /a intentos_pg=0
:loop_wait_pg_all
timeout /t 2 >nul
netstat -ano 2>nul | findstr ":5432" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    set /a intentos_pg+=1
    if !intentos_pg! lss 10 goto loop_wait_pg_all
)
echo Base de datos PostGIS en linea en puerto 5432.

echo.
echo [4/4] Iniciando Backend Spring Boot en ventana separada...
start "AquaRuta VSA - Backend" cmd /k "mvnw.cmd spring-boot:run"
echo Backend lanzado exitosamente.
echo Espera unos segundos a que cargue el grafo vial a la memoria RAM.
timeout /t 3 >nul
goto menu

:apagar_todo
cls
echo ======================================================================
echo                   APAGANDO TODOS LOS SERVICIOS
echo ======================================================================
echo.
echo [1/4] Deteniendo Backend en puerto 8080...
set "KILLED_APP=0"
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":8080" ^| findstr "LISTENING"') do (
    echo Finalizando proceso Java con PID %%a...
    taskkill /f /pid %%a >nul 2>&1
    set "KILLED_APP=1"
)
if "!KILLED_APP!"=="0" echo Backend ya estaba detenido.

echo.
echo [2/4] Deteniendo contenedor de Base de Datos PostGIS...
docker compose stop 2>nul
echo Contenedores detenidos.

echo.
echo [3/4] Cerrando aplicacion Docker Desktop...
taskkill /f /im "Docker Desktop.exe" >nul 2>&1
taskkill /f /im "com.docker.backend.exe" >nul 2>&1
taskkill /f /im "com.docker.proxy.exe" >nul 2>&1
taskkill /f /im "com.docker.build.exe" >nul 2>&1
taskkill /f /im "docker.exe" >nul 2>&1
echo Procesos de Docker finalizados.

echo.
echo [4/4] Ejecutando wsl --shutdown para liberar toda la memoria RAM...
wsl --shutdown
echo WSL apagado por completo. Memoria vmmem liberada.

echo.
echo Todos los servicios, contenedores, Docker y WSL han sido apagados.
timeout /t 3 >nul
goto menu

:encender_db
cls
echo ======================================================================
echo                   INICIANDO BASE DE DATOS Y DOCKER
echo ======================================================================
echo.
echo Iniciando WSL...
wsl -e true >nul 2>&1
echo.
docker info >nul 2>&1
if errorlevel 1 (
    echo Iniciando Docker Desktop...
    if exist "!DOCKER_APP!" (
        start "" "!DOCKER_APP!"
    ) else (
        echo [ERROR] No se encontro el ejecutable de Docker Desktop.
        pause
        goto menu
    )
    echo Esperando a que el motor de Docker este listo...
    set /a intentos=0
    :loop_wait_docker_db
    timeout /t 3 >nul
    docker info >nul 2>&1
    if errorlevel 1 (
        set /a intentos+=1
        echo   ...esperando motor de Docker [intento !intentos!/25]
        if !intentos! lss 25 goto loop_wait_docker_db
        echo [ADVERTENCIA] Docker tardo demasiado.
        pause
        goto menu
    )
)

echo Levantando contenedor PostGIS...
docker compose up -d
timeout /t 2 >nul
goto menu

:apagar_db
cls
echo Deteniendo contenedor PostGIS...
docker compose stop 2>nul
timeout /t 2 >nul
goto menu

:encender_backend
cls
echo ======================================================================
echo                   INICIANDO BACKEND SPRING BOOT
echo ======================================================================
echo.
netstat -ano 2>nul | findstr ":5432" | findstr "LISTENING" >nul 2>&1
if errorlevel 1 (
    echo [ADVERTENCIA CRITICA]
    echo No se detecto PostgreSQL escuchando en el puerto 5432.
    echo Debes encender la base de datos con la Opcion [2] o [4].
    echo.
    set /p "CONTINUAR=Deseas intentar arrancar de todos modos? (S/N): "
    if /i not "!CONTINUAR!"=="S" goto menu
)

echo Lanzando Spring Boot en ventana separada...
start "AquaRuta VSA - Backend" cmd /k "mvnw.cmd spring-boot:run"
timeout /t 3 >nul
goto menu

:apagar_backend
cls
echo Deteniendo Backend Spring Boot (Puerto 8080)...
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":8080" ^| findstr "LISTENING"') do (
    taskkill /f /pid %%a >nul 2>&1
)
timeout /t 2 >nul
goto menu

:abrir_docker_app
if exist "!DOCKER_APP!" start "" "!DOCKER_APP!"
goto menu

:abrir_web
start http://localhost:8080
goto menu

:abrir_swagger
start http://localhost:8080/swagger-ui.html
goto menu

REM --- Comandos CLI directos ---
:check_status_cli
set "W_ST=DETENIDO"
tasklist /fi "imagename eq vmmem*" 2>nul | findstr /i "vmmem" >nul 2>&1
if not errorlevel 1 set "W_ST=ACTIVO"
set "D_ST=DETENIDO"
docker info >nul 2>&1
if not errorlevel 1 set "D_ST=ACTIVO"
set "DB_ST=DETENIDO"
netstat -ano 2>nul | findstr ":5432" | findstr "LISTENING" >nul 2>&1
if not errorlevel 1 set "DB_ST=EN EJECUCION [Puerto 5432]"
set "BK_ST=DETENIDO"
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":8080" ^| findstr "LISTENING"') do set "BK_ST=RUNNING [PID: %%a]"
echo WSL Subsistema : %W_ST%
echo Docker Desktop : %D_ST%
echo Base de Datos  : %DB_ST%
echo Backend App    : %BK_ST%
exit /b 0

:start_all_cli
wsl -e true >nul 2>&1
docker info >nul 2>&1
if errorlevel 1 (
    if exist "!DOCKER_APP!" start "" "!DOCKER_APP!"
    :wait_cli_docker
    timeout /t 3 >nul
    docker info >nul 2>&1
    if errorlevel 1 goto wait_cli_docker
)
docker compose up -d
start "AquaRuta VSA - Backend" cmd /k "mvnw.cmd spring-boot:run"
exit /b 0

:stop_all_cli
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":8080" ^| findstr "LISTENING"') do taskkill /f /pid %%a >nul 2>&1
docker compose stop 2>nul
taskkill /f /im "Docker Desktop.exe" >nul 2>&1
taskkill /f /im "com.docker.backend.exe" >nul 2>&1
taskkill /f /im "com.docker.proxy.exe" >nul 2>&1
taskkill /f /im "com.docker.build.exe" >nul 2>&1
wsl --shutdown
exit /b 0

:start_wsl_cli
wsl -e true >nul 2>&1
echo WSL iniciado.
exit /b 0

:stop_wsl_cli
wsl --shutdown
echo WSL apagado.
exit /b 0

:start_db_cli
wsl -e true >nul 2>&1
docker info >nul 2>&1
if errorlevel 1 (
    if exist "!DOCKER_APP!" start "" "!DOCKER_APP!"
    :wait_cli_db
    timeout /t 3 >nul
    docker info >nul 2>&1
    if errorlevel 1 goto wait_cli_db
)
docker compose up -d
exit /b 0

:stop_db_cli
docker compose stop 2>nul
exit /b 0

:start_backend_cli
start "AquaRuta VSA - Backend" cmd /k "mvnw.cmd spring-boot:run"
exit /b 0

:stop_backend_cli
for /f "tokens=5" %%a in ('netstat -aon 2^>nul ^| findstr ":8080" ^| findstr "LISTENING"') do taskkill /f /pid %%a >nul 2>&1
exit /b 0

:salir
exit /b 0
