# AquaRuta VSA — Plataforma Geoespacial y de Grafos Resiliente a Inundaciones

> **Proyecto Universitario de Alto Impacto Social**  
> **Problema de Atención Nacional #10:** *Crisis del Agua y Vulnerabilidad Hidrometeorológica Urbana*  
> **Área Geográfica de Aplicación:** Mancha Urbana Céntrica de Villahermosa, Tabasco, México

---

## 1. Resumen Ejecutivo del Proyecto
Villahermosa, Tabasco, es una ciudad fluvial delimitada por los ríos Carrizal y Grijalva. La conjunción de precipitaciones extraordinarias y su topografía plana genera dos problemas simultáneos y críticos:
1. **Anegamiento de la Red Vial:** Calles e intersecciones bajas se vuelven intransitables en minutos, aislando colonias como Gaviotas, Tamulté y el Centro Histórico.
2. **Crisis en el Suministro de Agua Potable:** Las plantas potabilizadoras suelen detener operaciones por exceso de turbiedad durante las crecidas de los ríos, dejando sin servicio a miles de ciudadanos.

**AquaRuta VSA** es un sistema web de ingeniería de transporte y monitoreo hídrico que combina teoría de grafos, algoritmos de caminos mínimos (A*), modelos topográficos e ingesta de datos abiertos (OpenStreetMap y Open-Meteo) para ofrecer:
* **Enrutamiento Vial Resiliente a Inundaciones:** Calcula rutas seguras penalizando o esquivando dinámicamente calles con cotas bajas o reportes de anegamiento.
* **Mapa Colaborativo Validado de Cortes de Agua:** Filtra falsas alarmas ciudadanas exigiendo consenso espacio-temporal (quórum de al menos 3 reportes en un radio Haversine de 200 metros y ventana de 2 horas) antes de confirmar una alerta oficial.
* **Panel de Simulación Hidrológica (Modo Demo):** Herramienta interactiva para inyectar escenarios de lluvia pluvial en tiempo real y observar la reacción de los algoritmos de enrutamiento.

---

## 2. Stack Tecnológico

| Capa / Componente | Tecnología | Propósito |
| :--- | :--- | :--- |
| **Backend** | Java 21 LTS + Spring Boot 3.3.5 | API REST, Casos de Uso y Servicios |
| **Motor de Grafos** | JGraphT 1.5.2 | Modelado de red vial dirigida en memoria y algoritmo A* |
| **Base de Datos** | PostgreSQL 16 + PostGIS 3.4 | Persistencia espacial con índices `GIST` (`EPSG:4326`) |
| **Frontend** | HTML5, CSS3, Vanilla JS (ES6+) | Interfaz tipo sala de control de ingeniería cartográfica |
| **Motor de Mapas** | Leaflet.js + OpenStreetMap | Visualización de teselas oficiales sin marcas de agua |
| **Datos Abiertos** | Overpass API / Open-Meteo | Ingesta de vialidades y precipitación pluvial en tiempo real |
| **Documentación** | SpringDoc OpenAPI 2.6.0 | Especificación Swagger UI interactiva |

---

## 3. Guía de Instalación y Despliegue en Windows

### Requisitos Previos
* **Java Development Kit (JDK 21 o superior)** instalado en el sistema.
* **Docker Desktop para Windows** (para levantar la base de datos PostGIS).

### Control Rápido con `control.bat`
Para facilitar la operación en Windows, el proyecto incluye un script interactivo en la raíz que controla **WSL (Subsistema Linux), Docker Desktop, la Base de Datos PostGIS y el Backend Spring Boot**:
* **Doble clic en `control.bat`** (o ejecuta `.\control.bat` en la terminal) para abrir el **Panel de Control interactivo**.
  * **Opción [2] Encender TODO:** Asegura que WSL esté activo, abre Docker Desktop (si está cerrado), levanta el contenedor PostGIS y finalmente arranca Spring Boot.
  * **Opción [3] Apagar TODO:** Detiene el Backend en el puerto 8080, detiene el contenedor PostGIS, cierra Docker Desktop y **apaga WSL por completo (`wsl --shutdown`)** para liberar toda la memoria RAM (`vmmem`).
  * **Opción [8] Encender solo WSL:** Inicia el subsistema Linux (`wsl -e true`).
  * **Opción [9] Apagar solo WSL:** Ejecuta `wsl --shutdown` para liberar la memoria RAM de inmediato.
* También puedes usarlo mediante comandos directos desde la consola:
  ```powershell
  .\control.bat status        # Muestra el estado de WSL, Docker, BD y Backend
  .\control.bat start         # Enciende WSL, Docker, la BD y el Backend juntos
  .\control.bat stop          # Apaga el Backend, la BD, Docker y hace shutdown de WSL
  .\control.bat start-wsl     # Enciende solo el subsistema WSL
  .\control.bat stop-wsl      # Apaga WSL y libera la memoria vmmem (wsl --shutdown)
  .\control.bat start-db      # Inicia WSL/Docker y levanta PostGIS
  .\control.bat stop-db       # Detiene PostGIS
  .\control.bat start-backend # Enciende solo Spring Boot
  .\control.bat stop-backend  # Apaga solo Spring Boot
  ```

---

### Paso 1: Levantar la Base de Datos Geoespacial
Abre una terminal PowerShell o CMD en la raíz del proyecto y ejecuta:

```powershell
docker compose up -d
```

*Esto iniciará el contenedor `aquaruta-postgis` en el puerto `5432` con la base de datos `aquaruta_db` e inicializará automáticamente las tablas espaciales y cuadrantes meteorológicos.*

### Paso 2: Compilar y Ejecutar la Aplicación Spring Boot
El proyecto incluye un Maven Wrapper portable autoconfigurado. Ejecuta en PowerShell:

```powershell
.\mvnw.ps1 spring-boot:run
```

*(O en CMD clásico de Windows: `mvnw.cmd spring-boot:run`)*

Al arrancar por primera vez, el servicio `MapIngestionService` cargará la red arterial de Villahermosa Centro en memoria RAM en menos de **2 segundos**.

### Paso 3: Acceder a la Plataforma
* **Interfaz Web Principal:** [http://localhost:8080](http://localhost:8080)
* **Documentación Interactiva Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
* **Especificación OpenAPI (JSON):** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 4. Tabla de Endpoints REST Principales

| Método | Endpoint | Descripción |
| :--- | :--- | :--- |
| `GET` | `/api/v1/routes` | Calcula la ruta óptima A* entre dos coordenadas con evasión de anegamientos. |
| `POST` | `/api/v1/reports` | Registra un nuevo reporte ciudadano y evalúa quórum espacio-temporal. |
| `GET` | `/api/v1/reports` | Lista todos los reportes ciudadanos registrados. |
| `GET` | `/api/v1/reports/alerts` | Consulta alertas confirmadas activas por consenso (&ge; 3 reportes en 200m). |
| `GET` | `/api/v1/weather/cells` | Obtiene el estado pluvial actual de los 4 cuadrantes de Villahermosa. |
| `POST` | `/api/v1/simulation/rain` | Inyecta lluvia artificial (`mm/h`) en un cuadrante y recalcula impedancias en vivo. |
| `POST` | `/api/v1/simulation/reports/burst` | Dispara ráfaga de reportes para forzar validación de quórum en vivo. |
| `POST` | `/api/v1/simulation/reset` | Restablece el escenario a condiciones secas basales. |
| `POST` | `/api/v1/ingestion/reload` | Recarga la red vial desde PostGIS hacia la memoria RAM. |

---

## 5. Guion Paso a Paso para la Grabación del Video de Demostración

Sigue esta secuencia para demostrar con impacto visual el funcionamiento de los algoritmos en el video:

### Escena 1: Presentación y Escenario Seco Basal (0:00 - 1:00)
1. Abre el navegador en `http://localhost:8080`.
2. Muestra el mapa interactivo de Villahermosa a pantalla completa sin marcas de agua.
3. En el panel izquierdo (**Navegación A &rarr; B**), haz clic en el preset **Tabasco 2000** como Origen y **Gaviotas Norte** como Destino.
4. Muestra la ruta trazada en azul: en condiciones secas, el algoritmo A* elige el camino más directo atravesando el Centro y el Puente Grijalva 1. Señala las métricas en pantalla (Distancia: ~2.4 km, Tiempo: ~4 min, Tramos evitados: 0).

### Escena 2: Inyección de Tormenta Severa y Desvío Dinámico A* (1:00 - 2:15)
1. Abre el panel lateral derecho de **"Modo Simulación (Demo)"**.
2. Selecciona el cuadrante **"Este - Gaviotas / Margen Grijalva"** (zona baja con cota &lt; 4.0 msnm).
3. Mueve el deslizador a **90 mm/h** (o haz clic en el botón *"90 mm/h - Tormenta Severa"*).
4. **Efecto en vivo:** Muestra cómo el cuadrante se colorea de rojo carmesí.
5. Haz clic en **"Recalcular Ruta Óptima (A*)"** en el panel izquierdo (con el modo *"Ruta Segura Anti-Inundación"* activo).
6. **Resultado Algorítmico:** Explica cómo el algoritmo detecta la saturación hidráulica de la cota baja de Gaviotas y automáticamente desvía la ruta hacia arterias de mayor cota (mostrando el contador: *"Tramos inundados evitados: 1 o más"* y notificación toast en pantalla).

### Escena 3: Crowdsourcing y Consenso Espacio-Temporal (2:15 - 3:30)
1. En el panel de simulación, pulsa el botón **"Simular Ráfaga de Cortes (Quórum)"**.
2. **Efecto en vivo:**
   * Se disparan 3 reportes ciudadanos en un radio de 75 metros en la Zona Luz.
   * El algoritmo de consenso evalúa la distancia Haversine (&lt; 200m) y la ventana temporal (&lt; 2h).
   * Aparece inmediatamente en pantalla el círculo rojo discontinuo que representa la **"Alerta Confirmada por Quórum"**.
   * Se muestra la notificación toast: *"¡Consenso alcanzado! 3 reportes ciudadanos confirmaron corte de agua en radio de 200m"*.
3. Cambia a la pestaña **"Reporte Ciudadano"** en el panel izquierdo y muestra cómo un ciudadano puede reportar haciendo clic en cualquier calle.
4. Finalmente, pulsa **"Restaurar Escenario Base (Seco)"** para demostrar la reactividad del sistema.

---

## 6. Documentación Adicional
Para la memoria técnica completa, diagramas formales Mermaid (Arquitectura Hexagonal, Clases UML y Modelo ER), formulaciones matemáticas y documentación exhaustiva del código fuente, consulta:
* [docs/DOCUMENTACION_TECNICA.md](docs/DOCUMENTACION_TECNICA.md): Delimitación geográfica, diagramas de arquitectura hexagonal, modelo entidad-relación PostGIS y funciones de impedancia hidráulica.
* [docs/CODIGO_FUENTE.md](docs/CODIGO_FUENTE.md): Manual técnico exhaustivo del código fuente (desglose de paquetes, clases, métodos, algoritmos A*, eventos, adaptadores, frontend modular y suite de pruebas).
* [target/site/apidocs/index.html](target/site/apidocs/index.html): Documentación Javadoc HTML autogenerada de la API Java.
