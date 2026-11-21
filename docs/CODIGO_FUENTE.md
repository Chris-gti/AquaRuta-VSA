# AquaRuta VSA — Documentación Técnica del Código Fuente

**Manual de Referencia de Clases, Métodos, Algoritmos y Módulos**  
*Sistema de Enrutamiento Vial Resiliente a Inundaciones y Monitoreo Hídrico de Villahermosa, Tabasco*  
*Proyecto Universitario de Alto Impacto Social — Problema de Atención Nacional #10 ("Crisis del agua")*

---

## Índice General

1. [Estructura y Organización de Paquetes](#1-estructura-y-organización-de-paquetes)
2. [Capa de Dominio (`com.aquaruta.domain`)](#2-capa-de-dominio-comaquarutadomain)
   * 2.1 [Modelos y Value Objects (`domain.model`)](#21-modelos-y-value-objects-domainmodel)
   * 2.2 [Patrón Strategy de Cálculo de Riesgo (`domain.strategy`)](#22-patrón-strategy-de-cálculo-de-riesgo-domainstrategy)
   * 2.3 [Patrón Observer y Eventos de Dominio (`domain.event`)](#23-patrón-observer-y-eventos-de-dominio-domainevent)
   * 2.4 [Puertos de Repositorio (`domain.repository`)](#24-puertos-de-repositorio-domainrepository)
3. [Capa de Infraestructura (`com.aquaruta.infrastructure`)](#3-capa-de-infraestructura-comaquarutainfrastructure)
   * 3.1 [Motor de Grafos y Algoritmo A* (`infrastructure.graph`)](#31-motor-de-grafos-y-algoritmo-a-infrastructuregraph)
   * 3.2 [Manejador de Consenso Espacio-Temporal (`infrastructure.event`)](#32-manejador-de-consenso-espacio-temporal-infrastructureevent)
   * 3.3 [Persistencia PostGIS y Entidades JPA (`infrastructure.persistence`)](#33-persistencia-postgis-y-entidades-jpa-infrastructurepersistence)
   * 3.4 [Servicios Externos y Clientes HTTP (`infrastructure.external`)](#34-servicios-externos-y-clientes-http-infrastructureexternal)
   * 3.5 [Configuración de Infraestructura (`infrastructure.config`)](#35-configuración-de-infraestructura-infrastructureconfig)
4. [Capa de Aplicación (`com.aquaruta.application`)](#4-capa-de-aplicación-comaquarutaapplication)
   * 4.1 [Servicios de Casos de Uso (`application.service`)](#41-servicios-de-casos-de-uso-applicationservice)
   * 4.2 [Data Transfer Objects (`application.dto`)](#42-data-transfer-objects-applicationdto)
5. [Capa de Interfaces Web (`com.aquaruta.interfaces.web`)](#5-capa-de-interfaces-web-comaquarutainterfacesweb)
   * 5.1 [Controladores REST](#51-controladores-rest)
   * 5.2 [Manejador Global de Excepciones](#52-manejador-global-de-excepciones)
6. [Frontend Cartográfico Modular (`src/main/resources/static/`)](#6-frontend-cartográfico-modular-srcmainresourcesstatic)
   * 6.1 [Arquitectura de Módulos JavaScript](#61-arquitectura-de-módulos-javascript)
   * 6.2 [Interacción con Leaflet.js y Capas Vectoriales](#62-interacción-con-leafletjs-y-capas-vectoriales)
7. [Suite de Pruebas Automatizadas (`src/test/java`)](#7-suite-de-pruebas-automatizadas-srctestjava)
8. [Generación de Javadoc y Comandos de Ciclo de Vida](#8-generación-de-javadoc-y-comandos-de-ciclo-de-vida)

---

## 1. Estructura y Organización de Paquetes

El código fuente sigue rigurosamente los principios de **Arquitectura Hexagonal (Puertos y Adaptadores)** y **Clean Architecture**. El objetivo fundamental es que el núcleo de negocio (Dominio y Grafos) sea completamente independiente de la base de datos, del framework web y de las librerías de infraestructura externa.

```
com.aquaruta
├── AquaRutaApplication.java                   # Punto de entrada Spring Boot
├── domain/                                     # NÚCLEO DE DOMINIO PURO (Sin dependencias externas)
│   ├── model/                                  # Entidades y Value Objects de negocio
│   ├── strategy/                               # Patrón Strategy (Cálculo de impedancias)
│   ├── event/                                  # Eventos de dominio y puertos de publicación
│   └── repository/                             # Puertos de salida (Interfaces de repositorios)
├── application/                                # CAPA DE APLICACIÓN (Casos de uso)
│   ├── service/                                # Servicios orquestadores de negocio
│   └── dto/                                    # Records inmutables de transferencia de datos
├── infrastructure/                             # CAPA DE INFRAESTRUCTURA (Adaptadores técnicos)
│   ├── graph/                                  # JGraphT, Red Vial en RAM y Algoritmo A*
│   ├── event/                                  # Adaptador de eventos de Spring y Consenso
│   ├── persistence/                            # PostGIS, JPA Entities y Spring Data Repositories
│   ├── external/                               # OpenStreetMap, Open-Elevation y Open-Meteo
│   └── config/                                 # Configuración OpenAPI, RestTemplate y Spatial
└── interfaces/
    └── web/                                    # CAPA DE ENTRADA (Controladores REST y ExceptionHandler)
```

---

## 2. Capa de Dominio (`com.aquaruta.domain`)

El paquete de dominio encapsula las reglas esenciales de hidrología urbana, topografía y topología vial sin importar ninguna librería de Spring, Hibernate o persistencia.

### 2.1 Modelos y Value Objects (`domain.model`)

* [`GeoPoint`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/model/GeoPoint.java): Record inmutable que encapsula una coordenada geográfica $(\text{latitud}, \text{longitud})$ en WGS84 (`EPSG:4326`) con cálculo de distancia Haversine ($R = 6,371,000\text{ m}$).
* [`RoadNode`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/model/RoadNode.java): Representa una intersección o vértice del grafo vial con altimetría topográfica (`elevationMsnm`).
* [`RoadEdge`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/model/RoadEdge.java): Arco vial dirigido entre dos nodos con longitud métrica, factor de riesgo base, peso dinámico actual y estado de bloqueo.
* [`CitizenReport`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/model/CitizenReport.java): Reporte individual ciudadano (`id`, `reportType`, `description`, `latitude`, `longitude`, `createdAt`, `status`, `clusterId`).
* [`ValidatedAlert`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/model/ValidatedAlert.java): Alerta oficial confirmada mediante consenso espacio-temporal cuando se alcanza el quórum de $\ge 3$ reportes en 200m.
* [`RoutePath`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/model/RoutePath.java): Record inmutable que modela la ruta óptima calculada (nodos, distancia total, tiempo estimado, tramos evitados y elevación promedio).
* [`WeatherCell`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/model/WeatherCell.java): Cuadrante de monitoreo meteorológico de Villahermosa (Norte, Centro, Gaviotas, Tamulté).

### 2.2 Patrón Strategy de Cálculo de Riesgo (`domain.strategy`)

* [`RiskCalculationStrategy`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/strategy/RiskCalculationStrategy.java): Interfaz que define el contrato formal de cálculo de impedancia vial.
* [`NormalWeatherRiskStrategy`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/strategy/NormalWeatherRiskStrategy.java): Calcula el tiempo de viaje ideal en segundos bajo condiciones secas basales según jerarquía vial y velocidad máxima.
* [`FloodResilientRiskStrategy`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/strategy/FloodResilientRiskStrategy.java): Aplica penalización altimétrica exponencial ($\Psi_{\text{cota}}$), amplificación pluvial ($\Phi_{\text{lluvia}}$) y saturación por reportes ciudadanos ($\Omega_{\text{reportes}}$).
* [`RiskStrategyFactory`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/strategy/RiskStrategyFactory.java): Fábrica que entrega la estrategia adecuada según el modo solicitado (`resilientMode`).

### 2.3 Patrón Observer y Puertos (`domain.event` y `domain.repository`)

* [`OutageReportEvent`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/event/OutageReportEvent.java) y [`OutageClusterConfirmedEvent`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/event/OutageClusterConfirmedEvent.java): Eventos de dominio para la recepción y confirmación de incidencias.
* [`OutageEventPublisherPort`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/event/OutageEventPublisherPort.java): Puerto de salida para desacoplar la publicación de eventos.
* Puertos de repositorio: [`RoadNodeRepositoryPort`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/repository/RoadNodeRepositoryPort.java), [`RoadEdgeRepositoryPort`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/repository/RoadEdgeRepositoryPort.java), [`CitizenReportRepositoryPort`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/repository/CitizenReportRepositoryPort.java), [`ValidatedAlertRepositoryPort`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/repository/ValidatedAlertRepositoryPort.java) y [`WeatherCellRepositoryPort`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/domain/repository/WeatherCellRepositoryPort.java).

---

## 3. Capa de Infraestructura (`com.aquaruta.infrastructure`)

* [`RoadNetworkGraph`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/infrastructure/graph/RoadNetworkGraph.java): Grafo dirigido en memoria con JGraphT (`SimpleDirectedWeightedGraph`) y tablas hash concurrentes (`ConcurrentHashMap`).
* [`GeoDistanceHeuristic`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/infrastructure/graph/GeoDistanceHeuristic.java): Heurística admisible y monótona para A* dividiendo la distancia Haversine entre la velocidad física máxima de 22.22 m/s (80 km/h).
* [`AStarRoutingEngine`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/infrastructure/graph/AStarRoutingEngine.java): Orquesta el algoritmo de búsqueda de ruta más corta en el grafo.
* [`OutageConsensusHandler`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/infrastructure/event/OutageConsensusHandler.java): Manejador reactivo de quórum (3 reportes en $\le 200\text{ m}$ y $\le 2\text{ horas}$) que calcula el centroide baricéntrico.
* Persistencia PostGIS: Entidades con geometrías JTS (Point, LineString, Polygon) en SRID 4326 y repositorios Spring Data con predicados espaciales nativos (`ST_DWithin`, `ST_Distance`).
* Clientes externos: [`ElevationService`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/infrastructure/external/elevation/ElevationService.java), [`OverpassApiClient`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/infrastructure/external/overpass/OverpassApiClient.java) y [`OpenMeteoClient`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/infrastructure/external/weather/OpenMeteoClient.java).

---

## 4. Capa de Aplicación (`com.aquaruta.application`)

* [`RoutingService`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/application/service/RoutingService.java): Caso de uso de cálculo de trayectorias A*.
* [`WaterOutageService`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/application/service/WaterOutageService.java): Gestión y consulta de reportes ciudadanos y alertas confirmadas.
* [`SimulationService`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/application/service/SimulationService.java): Inyección interactiva de lluvia pluvial y ráfagas de quórum para demostración en vivo.
* [`MapIngestionService`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/application/service/MapIngestionService.java): Carga inicial de la red arterial hacia la RAM al iniciar la aplicación.
* DTOs inmutables: [`RouteRequestDto`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/application/dto/RouteRequestDto.java), [`RouteResponseDto`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/application/dto/RouteResponseDto.java), [`ReportCreateDto`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/application/dto/ReportCreateDto.java), [`ValidatedAlertResponseDto`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/application/dto/ValidatedAlertResponseDto.java), etc.

---

## 5. Interfaces Web y Frontend

* Controladores REST: [`RoutingController`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/interfaces/web/RoutingController.java), [`ReportController`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/interfaces/web/ReportController.java), [`WeatherController`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/interfaces/web/WeatherController.java), [`SimulationController`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/interfaces/web/SimulationController.java), [`IngestionController`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/java/com/aquaruta/interfaces/web/IngestionController.java).
* Frontend Cartográfico Modular: [`app.js`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/resources/static/js/app.js) (toast anti-apilamiento), [`map-manager.js`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/resources/static/js/map-manager.js) (Leaflet con teselas oficiales OSM sin marcas de agua), [`routing-ui.js`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/resources/static/js/routing-ui.js), [`simulation-ui.js`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/resources/static/js/simulation-ui.js) y [`api-client.js`](file:///C:/Users/chris/Proectos/Recuperacion/src/main/resources/static/js/api-client.js).

---

## 6. Suite de Pruebas Automatizadas

El proyecto incluye 14 pruebas unitarias y de integración que validan el comportamiento algorítmico:
* [`AStarRoutingEngineTest`](file:///C:/Users/chris/Proectos/Recuperacion/src/test/java/com/aquaruta/infrastructure/graph/AStarRoutingEngineTest.java): Desvío vial dinámico ante lluvia severa.
* [`RiskStrategyTest`](file:///C:/Users/chris/Proectos/Recuperacion/src/test/java/com/aquaruta/domain/strategy/RiskStrategyTest.java): Verificación de penalizaciones por cota baja y saturación de reportes.
* [`OutageConsensusHandlerTest`](file:///C:/Users/chris/Proectos/Recuperacion/src/test/java/com/aquaruta/infrastructure/event/OutageConsensusHandlerTest.java): Validación de quórum de 3 reportes en radio $\le 200\text{ m}$.
* [`GeoPointTest`](file:///C:/Users/chris/Proectos/Recuperacion/src/test/java/com/aquaruta/domain/model/GeoPointTest.java): Precisión métrica de la fórmula de Haversine.
* [`ElevationServiceTest`](file:///C:/Users/chris/Proectos/Recuperacion/src/test/java/com/aquaruta/infrastructure/external/elevation/ElevationServiceTest.java): Verificación de cotas críticas en Villahermosa.
* [`SimulationServiceTest`](file:///C:/Users/chris/Proectos/Recuperacion/src/test/java/com/aquaruta/application/service/SimulationServiceTest.java): Inyección de escenarios y ráfagas.
* [`StaticResourceTest`](file:///C:/Users/chris/Proectos/Recuperacion/src/test/java/com/aquaruta/interfaces/web/StaticResourceTest.java): Verificación de recursos estáticos en classpath.
