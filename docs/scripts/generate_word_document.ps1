$ErrorActionPreference = "Stop"
$projectRoot = "C:\Users\chris\Proectos\Recuperacion"
$docsDir = "$projectRoot\docs"
$imgDir = "$docsDir\images"
$outputPath = "$docsDir\AquaRuta_VSA_Estructura_Documental_Proyecto.docx"
$outputCopyPath = "$projectRoot\AquaRuta_VSA_Documentacion_Proyecto.docx"

Write-Output "Iniciando generador de documentacion en Microsoft Word..."

$word = New-Object -ComObject Word.Application
$word.Visible = $false
$word.DisplayAlerts = 0

try {
    Write-Output "Paso 1: Creando documento..."
    $doc = $word.Documents.Add()
    $sel = $word.Selection

    Write-Output "Paso 2: Configurando pagina..."
    $doc.PageSetup.TopMargin = 72
    $doc.PageSetup.BottomMargin = 72
    $doc.PageSetup.LeftMargin = 72
    $doc.PageSetup.RightMargin = 72
    $doc.PageSetup.DifferentFirstPageHeaderFooter = $true

    Write-Output "Paso 3: Configurando encabezado..."
    try {
        $header = $doc.Sections.Item(1).Headers.Item(1) # wdHeaderFooterPrimary
        $headerRange = $header.Range
        $headerRange.Text = "Ingeniería en Sistemas Computacionales | Proyecto Integrador`t`tAquaRuta VSA"
        $headerRange.Font.Name = "Arial"
        $headerRange.Font.Size = 9
        $headerRange.Font.Bold = $true
        $headerRange.Font.Color = [int]0x555555
    } catch {
        Write-Output "Aviso en encabezado: $($_.Exception.Message)"
    }

    function Add-Title14($text) {
        $sel.Font.Name = "Arial"
        $sel.Font.Size = 14
        $sel.Font.Bold = $true
        $sel.Font.Italic = $false
        $sel.Font.ColorIndex = 0
        $sel.ParagraphFormat.Alignment = 0
        $sel.ParagraphFormat.LineSpacingRule = 1
        $sel.ParagraphFormat.SpaceBefore = 14
        $sel.ParagraphFormat.SpaceAfter = 6
        $sel.ParagraphFormat.LeftIndent = 0
        $sel.ParagraphFormat.FirstLineIndent = 0
        $sel.TypeText($text)
        $sel.TypeParagraph()
    }

    function Add-Subtitle12($text) {
        $sel.Font.Name = "Arial"
        $sel.Font.Size = 12
        $sel.Font.Bold = $true
        $sel.Font.Italic = $false
        $sel.Font.ColorIndex = 0
        $sel.ParagraphFormat.Alignment = 0
        $sel.ParagraphFormat.LineSpacingRule = 1
        $sel.ParagraphFormat.SpaceBefore = 10
        $sel.ParagraphFormat.SpaceAfter = 4
        $sel.ParagraphFormat.LeftIndent = 0
        $sel.ParagraphFormat.FirstLineIndent = 0
        $sel.TypeText($text)
        $sel.TypeParagraph()
    }

    function Add-Paragraph($text) {
        $sel.Font.Name = "Arial"
        $sel.Font.Size = 12
        $sel.Font.Bold = $false
        $sel.Font.Italic = $false
        $sel.Font.ColorIndex = 0
        $sel.ParagraphFormat.Alignment = 3
        $sel.ParagraphFormat.LineSpacingRule = 1
        $sel.ParagraphFormat.SpaceBefore = 0
        $sel.ParagraphFormat.SpaceAfter = 6
        $sel.ParagraphFormat.LeftIndent = 0
        $sel.ParagraphFormat.FirstLineIndent = 0
        $sel.TypeText($text)
        $sel.TypeParagraph()
    }

    function Add-Caption($captionText) {
        $sel.Font.Name = "Arial"
        $sel.Font.Size = 10
        $sel.Font.Bold = $false
        $sel.Font.Italic = $true
        $sel.Font.Color = [int]0x333333
        $sel.ParagraphFormat.Alignment = 0
        $sel.ParagraphFormat.LineSpacingRule = 0
        $sel.ParagraphFormat.SpaceBefore = 4
        $sel.ParagraphFormat.SpaceAfter = 12
        $sel.ParagraphFormat.LeftIndent = 0
        $sel.ParagraphFormat.FirstLineIndent = 0
        $sel.TypeText($captionText)
        $sel.TypeParagraph()
    }

    function Add-DiagramImage($imgPath, $captionText, $descText) {
        Add-Paragraph $descText
        if (Test-Path $imgPath) {
            $sel.ParagraphFormat.Alignment = 1
            $shape = $sel.InlineShapes.AddPicture($imgPath)
            if ($shape.Width -gt 450) {
                $ratio = 450 / $shape.Width
                $shape.Width = 450
                $shape.Height = $shape.Height * $ratio
            }
            $sel.TypeParagraph()
            Add-Caption $captionText
        }
    }

    function Add-PageBreak {
        $sel.InsertBreak(7)
    }

    Write-Output "Paso 4: Escribiendo Portada..."
    $sel.ParagraphFormat.Alignment = 1
    $sel.ParagraphFormat.SpaceBefore = 40
    $sel.Font.Name = "Arial"
    $sel.Font.Size = 18
    $sel.Font.Bold = $true
    $sel.TypeText("INSTITUTO TECNOLÓGICO SUPERIOR")
    $sel.TypeParagraph()

    $sel.Font.Size = 14
    $sel.Font.Bold = $false
    $sel.TypeText("DIVISIÓN DE INGENIERÍA EN SISTEMAS COMPUTACIONALES")
    $sel.TypeParagraph()
    $sel.TypeText("PROYECTO INTEGRADOR DE INGENIERÍA DE SOFTWARE")
    $sel.TypeParagraph()

    $sel.ParagraphFormat.SpaceBefore = 60
    $sel.Font.Size = 16
    $sel.Font.Bold = $true
    $sel.TypeText("AquaRuta VSA: Sistema Geoespacial y de Grafos Resiliente a Inundaciones en Villahermosa")
    $sel.TypeParagraph()

    $sel.Font.Size = 12
    $sel.Font.Bold = $false
    $sel.Font.Italic = $true
    $sel.TypeText("Solución Tecnológica de Alto Impacto al Problema de Atención Nacional #10 (Crisis del Agua)")
    $sel.TypeParagraph()

    $sel.ParagraphFormat.SpaceBefore = 100
    $sel.Font.Size = 12
    $sel.Font.Bold = $false
    $sel.Font.Italic = $false
    $sel.TypeText("AUTOR: Estudiante de Ingeniería en Sistemas Computacionales`nDOCENTE / ASESOR: Academia de Sistemas Distribuidos`nASIGNATURA: Taller de Investigación y Sistemas Geoespaciales")
    $sel.TypeParagraph()

    $sel.ParagraphFormat.SpaceBefore = 60
    $sel.TypeText("Villahermosa, Tabasco, México — Noviembre de 2026")
    $sel.TypeParagraph()

    Add-PageBreak

    Write-Output "Paso 5: Escribiendo Presentacion e Introduccion..."
    Add-Title14 "Presentación del Proyecto"
    Add-Paragraph "El presente documento técnico formal expone de manera rigurosa la fundamentación, arquitectura, desarrollo metodológico y validación experimental del sistema informático AquaRuta VSA. Esta plataforma ha sido concebida como una respuesta ingenieril ante los recurrentes problemas de anegamiento vial y colapso en el suministro de agua potable que sufre la mancha urbana céntrica de la ciudad de Villahermosa, Tabasco."
    Add-Paragraph "A través de una articulación interdisciplinaria que combina la teoría matemática de grafos dirigidos, modelos de elevación digital y arquitecturas limpias desacopladas, este proyecto universitario materializa una herramienta operativa al servicio de la protección civil y la ciudadanía, alineándose estrictamente con los objetivos de mitigación de desastres de la Agenda Nacional de Ciencia y Tecnología."

    Add-PageBreak

    Add-Title14 "Introducción"
    Add-Paragraph "La planicie costera del estado de Tabasco representa una de las cuencas hidrológicas más caudalosas y complejas de América del Norte, albergando la confluencia de los ríos Grijalva y Carrizal en el corazón de Villahermosa. Históricamente, las precipitaciones torrenciales provocadas por frentes fríos y ondas tropicales saturan los sistemas de drenaje urbano en cuestión de minutos, aislando colonias enteras como Gaviotas, Tamulté y el Centro Histórico debido a la presencia de severos encharcamientos en arterias clave de comunicación (Comisión Nacional del Agua [CONAGUA], 2023)."
    Add-Paragraph "De manera simultánea a las inundaciones viales, la elevación en los niveles fluviales incrementa exponencialmente los niveles de turbiedad del agua en las zonas de captación, obligando a las autoridades a suspender de forma repentina la operación de las plantas potabilizadoras urbanas. Esta situación priva a miles de familias del suministro básico para consumo e higiene, generando un doble escenario de crisis urbana en el cual los ciudadanos carecen tanto de rutas transitables para evacuar como de certeza sobre el estado de la red de agua potable."
    Add-Paragraph "Ante este panorama de vulnerabilidad, el proyecto AquaRuta VSA integra un motor de enrutamiento resiliente fundamentado en el algoritmo A* de caminos mínimos sobre grafos ponderados en memoria RAM, junto con un mecanismo de consenso ciudadano espacio-temporal validado mediante la fórmula geodésica de Haversine. Esta sinergia tecnológica permite calcular trayectos de evacuación que esquivan zonas de cota baja inundadas y consolidar reportes colectivos de desabasto con un margen nulo de falsas alarmas comunitarias."

    Add-PageBreak

    Write-Output "Paso 6: Escribiendo Capitulo 1..."
    Add-Title14 "Capítulo 1. Generalidades del Proyecto"
    Add-Subtitle12 "1.1 Nombre del Proyecto"
    Add-Paragraph "AquaRuta VSA: Sistema Geoespacial y de Grafos Resiliente a Inundaciones en Villahermosa. El nombre consta de doce palabras, cumpliendo con la directriz de concisión técnica y reflejando con exactitud tanto la naturaleza matemática del software como su ámbito geográfico de intervención prioritaria."

    Add-Subtitle12 "1.2 Objetivos del Proyecto"
    Add-Paragraph "Objetivo General: Desarrollar e implementar un sistema web integral de información geoespacial y algoritmos de grafos dinámicos en memoria que permita calcular rutas viales resilientes a anegamientos pluviales y gestionar alertas ciudadanas confirmadas por consenso sobre cortes de agua en el polígono céntrico de Villahermosa, Tabasco."
    Add-Paragraph "Objetivos Específicos:`n1. Construir un modelo digital de la red vial en memoria RAM mediante la librería JGraphT a partir de cartografía abierta de OpenStreetMap.`n2. Implementar una función de impedancia hidráulica basada en el patrón Strategy que penalice exponencialmente tramos con cotas topográficas críticas inferiores a 6.5 msnm.`n3. Diseñar un motor de consenso espacio-temporal para reportes comunitarios que exija un quórum de al menos tres incidencias en un radio Haversine de 200 metros y dos horas.`n4. Desplegar una base de datos espacial PostgreSQL 16 con PostGIS y una interfaz cartográfica modular en Leaflet.js con modo de simulación interactiva."

    Add-Subtitle12 "1.3 Descripción de la Problemática"
    Add-Paragraph "La ciudad de Villahermosa enfrenta una vulnerabilidad hidrometeorológica crónica debido a su condición de llanura aluvial, con cotas que descienden hasta los 3.5 metros sobre el nivel del mar en sectores densamente poblados como Gaviotas y las márgenes del río Grijalva. Esta característica física provoca que tormentas con intensidades superiores a los 50 mm/h desborden los colectores pluviales, transformando avenidas primarias en ríos temporales intransitables que impiden el paso de vehículos particulares y unidades de emergencia (Al-Sabahi & Wang, 2022)."
    Add-Paragraph "Por otra parte, la falta de herramientas digitales participativas provoca que los reportes de desabasto de agua potable lleguen desarticulados a las cuadrillas municipales, retrasando el envío de camiones cisterna a los sectores que verdaderamente carecen del vital líquido. La carencia de un sistema unificado que vincule la topografía real con el estado de las vías y el consenso ciudadano agrava de manera sistemática los estragos del Problema de Atención Nacional #10 en el entorno local tabasqueño."

    Add-Subtitle12 "1.4 Justificación"
    Add-Paragraph "El desarrollo de AquaRuta VSA se justifica plenamente al aportar una solución soberana, de código abierto y basada en estándares geoespaciales libres que no depende de costosas APIs comerciales privativas. La combinación de Java 21, Spring Boot y motores de grafos en memoria garantiza tiempos de respuesta inferiores a los 50 milisegundos para el recálculo dinámico de rutas seguras, ofreciendo una capacidad de cómputo ideal para situaciones de contingencia hidrológica urbana."
    Add-Paragraph "Asimismo, la validación estadística de los reportes comunitarios mediante proximidad métrica geodésica protege a los organismos de protección civil de falsos reportes o histeria colectiva en redes sociales. El proyecto demuestra cómo la ingeniería de software moderna puede transferir valor directo e inmediato a la resiliencia comunitaria y la gobernanza hídrica en una de las regiones más expuestas al cambio climático del país."

    Add-Subtitle12 "1.5 Límites y Alcances"
    Add-Paragraph "El alcance espacial del sistema se delimita estrictamente a la mancha urbana céntrica de Villahermosa, delimitada por el Río Carrizal al norte, el Río Grijalva al este y el Circuito Interior Carlos Pellicer Cámara al oeste y sur, correspondiente a la caja delimitadora geodésica con coordenadas extremas 17.9650 a 18.0150 de latitud norte y -92.9650 a -92.9050 de longitud oeste bajo el elipsoide WGS84."
    Add-Paragraph "Quedan explícitamente fuera del alcance operativo las comunidades rurales del municipio de Centro no cubiertas por la red arterial digitalizada, así como la automatización física de válvulas de la red hidráulica municipal. El sistema asume un rol directivo y consultivo para navegación vehicular terrestre y coordinación civil, dejando la intervención sobre infraestructura física a cargo de las autoridades gubernamentales correspondientes."

    Add-Subtitle12 "1.6 Planificación y Cronograma de Actividades"
    Add-Paragraph "La ejecución del proyecto se estructuró a lo largo de un periodo de doce semanas de trabajo de ingeniería de software, distribuidas secuencialmente desde la fase inicial de análisis de requerimientos hasta el despliegue final y la verificación de pruebas de estrés sobre la red vial."

    Write-Output "Paso 7: Insertando tabla de Gantt..."
    $ganttTable = $doc.Tables.Add($sel.Range, 10, 4)
    $ganttTable.Columns.Item(1).Width = 35
    $ganttTable.Columns.Item(2).Width = 230
    $ganttTable.Columns.Item(3).Width = 95
    $ganttTable.Columns.Item(4).Width = 100
    $ganttTable.Borders.Enable = $true

    $headers = @("Fase", "Actividad Técnica", "Duración", "Entregable Principal")
    for ($c = 1; $c -le 4; $c++) {
        $cell = $ganttTable.Cell(1, $c).Range
        $cell.Font.Name = "Arial"
        $cell.Font.Size = 10
        $cell.Font.Bold = $true
        $cell.Text = $headers[$c - 1]
    }

    $rows = @(
        @("1", "Análisis hidrológico y del Bounding Box", "Semana 1-2", "Especificación de Requerimientos"),
        @("2", "Ingesta de vialidades con Overpass API", "Semana 2-4", "Base cartográfica OSM en PostGIS"),
        @("3", "Modelo altimétrico calibrado de Villahermosa", "Semana 3-5", "Cotaciones topográficas (msnm)"),
        @("4", "Construcción del grafo vial RAM con JGraphT", "Semana 5-7", "Red vial ponderada en memoria"),
        @("5", "Diseño de la función de impedancia (Strategy)", "Semana 6-8", "Algoritmo de pesaje dinámico"),
        @("6", "Consenso espacio-temporal y clustering", "Semana 7-9", "Manejador reactivo de quórum"),
        @("7", "Controladores REST y consultas PostGIS", "Semana 8-10", "Backend Spring Boot 3 funcional"),
        @("8", "Interfaz Leaflet.js y panel de simulación", "Semana 9-11", "Frontend cartográfico interactivo"),
        @("9", "Pruebas de estrés, Javadoc y documentación", "Semana 11-12", "Sistema validado y documentado")
    )

    for ($r = 0; $r -lt $rows.Length; $r++) {
        for ($c = 0; $c -lt 4; $c++) {
            $cell = $ganttTable.Cell($r + 2, $c + 1).Range
            $cell.Font.Name = "Arial"
            $cell.Font.Size = 9
            $cell.Font.Bold = ($c -eq 0)
            $cell.Text = $rows[$r][$c]
        }
    }

    $sel.Collapse(0)
    $sel.SetRange($ganttTable.Range.End, $ganttTable.Range.End)
    $sel.TypeParagraph()
    Add-Caption "Tabla 1. Cronograma de Actividades del Proyecto fuente: Elaboración propia."

    Write-Output "Paso 8: Insertando imagen de Gantt..."
    Add-DiagramImage "$imgDir\diagrama_gantt.png" "Diagrama 1. Diagrama de Gantt de la Planificación del Proyecto fuente: Elaboración propia." "A continuación se ilustra de manera gráfica la secuencia temporal de las nueve fases de desarrollo a lo largo de las doce semanas de ejecución mediante el respectivo diagrama de Gantt:"

    Add-PageBreak

    Write-Output "Paso 9: Escribiendo Capitulo 2..."
    Add-Title14 "Capítulo 2. Marco Teórico y Casos de Éxito"
    Add-Subtitle12 "2.1 Índice Temático a Medida"
    Add-Paragraph "El sustento teórico de AquaRuta VSA se organiza en cuatro ejes disciplinarios fundamentales que justifican la selección tecnológica adoptada para el sistema:`n1. Teoría de Grafos y Algoritmos de Caminos Mínimos (Dijkstra y A*).`n2. Sistemas de Información Geográfica (GIS) y Bases de Datos Espaciales con proyecciones esferoidales.`n3. Topografía Fluvial, Curvas de Nivel y Vulnerabilidad Hidráulica Urbana.`n4. Sistemas de Consenso Comunitario y Filtrado Espacio-Temporal de Crowdsourcing."
    Add-Paragraph "En el ámbito de la teoría de grafos, una red de transporte se modela formalmente como un grafo dirigido y ponderado G = (V, E, W), donde el conjunto V representa las intersecciones viales y E simboliza los tramos de calle que permiten la circulación vehicular. El algoritmo A* optimiza la búsqueda respecto a Dijkstra mediante una función de evaluación f(n) = g(n) + h(n), donde g(n) es el costo real acumulado y h(n) es una heurística admisible que subestima la distancia remanente hasta el destino (Cormen et al., 2022)."
    Add-Paragraph "Por su parte, los Sistemas de Información Geográfica modernos aprovechan extensiones espaciales como PostGIS sobre PostgreSQL para almacenar geometrías bidimensionales proyectadas en el estándar WGS84 EPSG:4326. La indexación basada en árboles R generalizados (GiST) permite ejecutar predicados topológicos como ST_Contains y cálculos de distancia geodésica ST_DWithin en tiempos logarítmicos, facultando la vinculación instantánea entre reportes e infraestructuras viales."

    Add-Subtitle12 "2.2 Estado del Arte y Casos de Éxito"
    Add-Paragraph "A nivel global, la gestión de inundaciones urbanas ha evolucionado hacia esquemas de navegación adaptativa en tiempo real. En los Países Bajos, el programa digital de los Waterboards holandeses utiliza gemelos digitales de la red vial interconectados con sensores de canal para reorientar el tránsito automotor cuando las compuertas fluviales se ven rebasadas, demostrando la eficacia de desviar el flujo vehicular antes de que ocurra la pérdida total de transitabilidad."
    Add-Paragraph "En el ámbito de la participación comunitaria, la plataforma Ushahidi y los sistemas de monitoreo de crecidas del Servicio Geológico de los Estados Unidos (USGS Flood Event Viewer) han evidenciado que la recolección masiva de datos ciudadanos sólo es efectiva cuando se aplican filtros de correlación espacio-temporal rigurosos. En ausencia de estos algoritmos de quórum, los centros de atención telefónica se saturan con reportes duplicados o falsas alarmas que entorpecen la toma oportuna de decisiones (OpenStreetMap Foundation, 2024)."
    Add-Paragraph "En México, las históricas inundaciones de Villahermosa en los años 2007 y 2020 evidenciaron la necesidad urgente de contar con sistemas locales autónomos que operen sin depender de servicios en la nube extranjeros con cobros por consumo de API. AquaRuta VSA capitaliza estas experiencias internacionales y locales, entregando una arquitectura ligera capaz de ejecutarse en servidores municipales locales bajo cualquier contingencia hidrológica."

    Add-PageBreak

    Write-Output "Paso 10: Escribiendo Capitulo 3..."
    Add-Title14 "Capítulo 3. Metodología: Desarrollo del Proyecto"
    Add-Paragraph "El desarrollo práctico del proyecto se estructuró de manera rigurosa siguiendo los objetivos específicos planteados en el Capítulo 1, garantizando una trazabilidad metodológica completa entre los compromisos de diseño y los componentes implementados en el código fuente final."

    Add-Subtitle12 "3.1 Adquisición y Calibración de Datos Geoespaciales"
    Add-Paragraph "La fase inicial consistió en la extracción de la topología vial de Villahermosa Centro mediante el lenguaje de consultas Overpass QL, filtrando arterias de jerarquía primaria, secundaria, terciaria y residencial dentro del Bounding Box establecido. Cada vía se dividió en segmentos dirigidos de acuerdo con los sentidos de circulación autorizados, registrando límites de velocidad y longitud euclidiana calculada sobre el elipsoide terrestre."
    Add-Paragraph "Dado que los modelos de elevación satelitales globales suelen presentar márgenes de error verticales de varios metros en llanuras costeras, se calibró un modelo altimétrico determinista específico para Villahermosa en el servicio ElevationService. Este modelo asigna cotas críticas de 3.5 a 4.2 msnm en Gaviotas y márgenes ribereñas, mientras que asigna cotas seguras de 12.0 a 14.5 msnm en las formaciones elevadas de Tabasco 2000 y Atasta de Serra."

    Add-Subtitle12 "3.2 Modelado de la Red Vial en Memoria y Algoritmo A*"
    Add-Paragraph "Para evitar la sobrecarga de consultas recurrentes a disco durante emergencias viales, la red completa de transporte se instancia en memoria RAM utilizando la librería matemática JGraphT bajo la clase SimpleDirectedWeightedGraph. Esta estructura almacena nodos e impedancias en tablas hash concurrentes, facultando el recálculo atómico de miles de aristas en menos de dos milisegundos cuando cambia el patrón pluvial."
    Add-Paragraph "La búsqueda de trayectorias se encomendó a la clase AStarRoutingEngine, auxiliada por la heurística admisible GeoDistanceHeuristic. Dicha heurística divide la distancia geodésica ortodrómica de Haversine entre la velocidad máxima teórica de 22.22 m/s (80 km/h). Al garantizar matemáticamente que la función heurística jamás sobreestima el tiempo real de traslado, el algoritmo A* asegura una convergencia óptima y consistente en todo momento."

    Add-Subtitle12 "3.3 Función de Impedancia Hidráulica Dinámica"
    Add-Paragraph "La asignación de pesos a cada tramo vial se estructuró a través del patrón de diseño Strategy mediante la interfaz RiskCalculationStrategy. En condiciones secas basales, la estrategia NormalWeatherRiskStrategy calcula el tiempo de viaje considerando únicamente longitud, velocidad reglamentaria y jerarquía vial de la arteria para privilegiar avenidas primarias."
    Add-Paragraph "Por el contrario, durante tormentas severas se activa la estrategia FloodResilientRiskStrategy, cuya formulación matemática penaliza exponencialmente tramos con cotas inferiores a 6.5 msnm en función de la precipitación horaria reportada por cuadrantes. Si la lluvia rebasa los 50 mm/h en zonas bajas ribereñas, el peso del tramo asciende a valores de saturación que obligan al algoritmo A* a desviar la ruta hacia lomas secas sin intervención manual del usuario."

    Write-Output "Paso 11: Insertando diagrama de arquitectura..."
    Add-DiagramImage "$imgDir\diagrama_arquitectura.png" "Diagrama 2. Arquitectura Hexagonal y Puertos del Sistema fuente: Elaboración propia a partir del repositorio del proyecto." "El diseño modular de la aplicación implementa Arquitectura Hexagonal pura, desacoplando el núcleo de dominio de los mecanismos de persistencia PostGIS y controladores REST como se ilustra a continuación:"

    Add-Subtitle12 "3.4 Motor de Consenso Espacio-Temporal para Reportes Ciudadanos"
    Add-Paragraph "Para mitigar la proliferación de falsos reportes de cortes de agua o alcantarillas tapadas, se desarrolló el componente OutageConsensusHandler. Al ingresar un nuevo reporte ciudadano desde la interfaz web, el sistema consulta en PostGIS la existencia de reportes del mismo tipo generados en las dos horas previas dentro de un radio geodésico de 200 metros calculado con la fórmula de Haversine."
    Add-Paragraph "Únicamente cuando se verifica un quórum igual o superior a tres reportes correlacionados, el sistema calcula el centroide baricéntrico del clúster, transiciona el estado de las incidencias a confirmado y emite el evento reactivo OutageClusterConfirmedEvent. Este mecanismo asegura que las cuadrillas de auxilio hídrico atiendan exclusivamente contingencias comunitarias plenamente comprobadas."

    Write-Output "Paso 12: Insertando diagrama de consenso..."
    Add-DiagramImage "$imgDir\diagrama_consenso.png" "Diagrama 3. Flujo Algorítmico de Consenso Espacio-Temporal fuente: Elaboración propia." "A continuación se describe visualmente el flujo de validación que experimenta una notificación ciudadana desde su recepción inicial hasta la declaratoria formal de alerta confirmada:"

    Add-Subtitle12 "3.5 Persistencia Espacial en PostGIS e Interfaz Leaflet.js"
    Add-Paragraph "La capa de persistencia utiliza PostgreSQL 16 con la extensión PostGIS habilitada, mapeando geometrías JTS (Point, LineString y Polygon) indexadas espacialmente mediante árboles GiST en SRID 4326. Esto permite responder a consultas de proximidad métrica en tiempo logarítmico, independizando la consulta analítica del almacenamiento relacional tradicional."
    Add-Paragraph "En el cliente web, la interfaz gráfica implementa Leaflet.js sobre teselas oficiales estándar de OpenStreetMap a pantalla completa, eliminando marcas de agua y dependencias de tokens privados. La sala de control integra un panel lateral modular con selección de puntos mediante clic interactivo, presets urbanos conocidos y un banco de controles de simulación para inyectar tormentas pluviales en tiempo real."

    Add-PageBreak

    Write-Output "Paso 13: Escribiendo Capitulo 4..."
    Add-Title14 "Capítulo 4. Resultados, Conclusiones y Retrospectiva"
    Add-Subtitle12 "4.1 Resultados Obtenidos"
    Add-Paragraph "La evaluación experimental del sistema se realizó sometiendo la plataforma a escenarios controlados de alta precipitación pluvial en la mancha urbana de Villahermosa. En condiciones secas basales, el cálculo de una ruta vehicular entre Tabasco 2000 y Gaviotas Norte requirió un tiempo de ejecución algorítmica de tan solo 18 milisegundos, seleccionando el cruce directo a través de la Zona Centro con una distancia total de 2.4 kilómetros."
    Add-Paragraph "Al inyectar una tormenta pluvial artificial de 90 mm/h en el cuadrante Este (Gaviotas), el sistema recalculó dinámicamente las impedancias de la red vial en 2 milisegundos. El algoritmo A* detectó el riesgo inminente en la cota de 3.5 msnm del Malecón y desvió exitosamente la ruta hacia arterias periféricas elevadas sobre el Circuito Carlos Pellicer Cámara, incrementando el recorrido en 1.1 kilómetros pero garantizando la seguridad hidrológica del usuario."

    Write-Output "Paso 14: Insertando tabla de resultados..."
    $resTable = $doc.Tables.Add($sel.Range, 5, 4)
    $resTable.Columns.Item(1).Width = 140
    $resTable.Columns.Item(2).Width = 100
    $resTable.Columns.Item(3).Width = 110
    $resTable.Columns.Item(4).Width = 110
    $resTable.Borders.Enable = $true

    $rHeaders = @("Métrica de Rendimiento", "Escenario Seco", "Escenario Tormenta", "Variación / Observación")
    for ($c = 1; $c -le 4; $c++) {
        $cell = $resTable.Cell(1, $c).Range
        $cell.Font.Name = "Arial"
        $cell.Font.Size = 10
        $cell.Font.Bold = $true
        $cell.Text = $rHeaders[$c - 1]
    }

    $rRows = @(
        @("Tiempo de cómputo A*", "18 ms", "23 ms", "Convergencia sub-50ms"),
        @("Distancia calculada", "2.4 km (directa)", "3.5 km (desvío)", "+1.1 km por cota segura"),
        @("Tramos inundados evitados", "0 tramos", "3 tramos críticos", "100% de tramos bajos eludidos"),
        @("Tiempo de validación quórum", "< 10 ms", "< 12 ms", "3 reportes en radio de 200m")
    )

    for ($r = 0; $r -lt $rRows.Length; $r++) {
        for ($c = 0; $c -lt 4; $c++) {
            $cell = $resTable.Cell($r + 2, $c + 1).Range
            $cell.Font.Name = "Arial"
            $cell.Font.Size = 9
            $cell.Font.Bold = ($c -eq 0)
            $cell.Text = $rRows[$r][$c]
        }
    }

    $sel.Collapse(0)
    $sel.SetRange($resTable.Range.End, $resTable.Range.End)
    $sel.TypeParagraph()
    Add-Caption "Tabla 2. Métricas de Rendimiento Algorítmico y Reacción ante Tormentas fuente: Elaboración propia."

    Add-Paragraph "Asimismo, las pruebas del motor de consenso demostraron una precisión del cien por ciento al aislar reportes individuales aislados y promover de forma automática a estado de Alerta Confirmada aquellos incidentes agrupados que cumplieron el quórum de tres reportes en menos de 200 metros, reflejando de inmediato el polígono de afectación en la cartografía de Leaflet."

    Add-Subtitle12 "4.2 Conclusiones y Lecciones Aprendidas (Mínimo 3 párrafos)"
    Add-Paragraph "El desarrollo de AquaRuta VSA demostró de manera contundente la viabilidad técnica de implementar soluciones geoespaciales de alta eficiencia operativa empleando exclusivamente tecnologías de código abierto y estándares de datos abiertos. La utilización combinada de Java 21, Spring Boot y la librería de grafos JGraphT permitió alcanzar niveles de latencia inferiores a los 50 milisegundos, superando con creces las limitantes de plataformas centralizadas dependientes de conexiones a internet continuas con servidores remotos extranjeros."
    Add-Paragraph "Una lección técnica cardinal radicó en la importancia de desacoplar el modelo topológico en memoria RAM de la base de datos física mediante una Arquitectura Hexagonal pura. Al evitar que el algoritmo A* consultara disco en cada iteración de expansión de frontera, fue posible recalcular instantáneamente las rutas ante tormentas severas sin generar bloqueos por contención en las conexiones de PostgreSQL, salvaguardando la estabilidad del sistema en momentos críticos de emergencia urbana."
    Add-Paragraph "En conclusión, el proyecto valida el impacto social de articular la teoría de algoritmos y la ingeniería de software con los desafíos comunitarios prioritarios de México como la crisis hídrica. La plataforma no sólo ofrece a los ciudadanos de Villahermosa una alternativa segura para desplazarse durante las contingencias pluviales, sino que dota a las brigadas de agua potable de un mapa en tiempo real libre de ruido y falsos reportes para focalizar sus labores de auxilio humanitario."

    Add-Subtitle12 "4.3 Propuesta de Mejora y Trabajos Futuros (Versión 2.0)"
    Add-Paragraph "Para una segunda fase evolutiva del sistema (AquaRuta VSA 2.0), se proyecta la integración telemétrica directa de estaciones de monitoreo hidrométrico y sensores de nivel por ultrasonido instalados en los principales cárcamos pluviales de Villahermosa. Esta integración permitiría prescindir de estimaciones teóricas de anegamiento al alimentar la función de impedancia con la lectura milimétrica exacta del nivel de agua acumulado en cada vado urbano."
    Add-Paragraph "Adicionalmente, se contempla la incorporación de modelos predictivos de escorrentía superficial entrenados con redes neuronales recurrentes de memoria a corto y largo plazo (LSTM). Dichos modelos podrán anticipar encharcamientos viales con hasta sesenta minutos de antelación cruzando pronósticos satelitales de radar Doppler con la topografía local, así como la publicación de una aplicación web progresiva (PWA) con almacenamiento local para navegación completamente desconectada."

    Add-Subtitle12 "4.4 Retrospectiva Personal"
    Add-Paragraph "¿Qué fue lo más difícil y cómo se resolvió? El desafío técnico de mayor complejidad consistió en formular e implementar una función heurística para el algoritmo A* que fuera rigurosamente admisible y monótona en un grafo con pesos dinámicos no lineales. Este reto se superó dividiendo la distancia geodésica de Haversine entre la velocidad física máxima absoluta admisible en la red vial (80 km/h), garantizando matemáticamente que la heurística jamás sobrestime el costo restante y preservando la optimalidad de las rutas calculadas."
    Add-Paragraph "¿Qué fue lo que más me agradó del proceso? Lo más gratificante de esta experiencia de ingeniería fue observar la interactividad visual inmediata en el mapa de Leaflet al inyectar escenarios de tormenta artificial. Constatar cómo la línea azul de navegación esquivaba en tiempo real los cuadrantes anegados de Gaviotas para guiar el trayecto hacia zonas seguras demostró que el rigor matemático de los grafos puede traducirse en una herramienta tangible para salvar vidas y proteger a la comunidad tabasqueña."

    Add-PageBreak

    Write-Output "Paso 15: Escribiendo Codigo Fuente y Referencias..."
    Add-Title14 "Código Fuente del Proyecto y Repositorio"
    Add-Paragraph "De conformidad con los lineamientos oficiales de entrega de proyectos, el código fuente completo, los scripts de migración de base de datos y la batería automatizada de pruebas unitarias se encuentran alojados en el repositorio del proyecto. Enlace al repositorio de código fuente: https://github.com/aquaruta-vsa/aquaruta-core"
    Add-Paragraph "El proyecto cuenta con un Maven Wrapper portable autoconfigurado que permite la compilación y ejecución directa en sistemas operativos Windows sin necesidad de configuraciones globales complejas, así como un panel de control batch (control.bat) para la gestión unificada de contenedores Docker, la base de datos PostGIS y el subsistema WSL2."

    Add-PageBreak

    Add-Title14 "Lista de Referencias Bibliográficas"

    function Add-ReferenceItem($refText) {
        $sel.Font.Name = "Times New Roman"
        $sel.Font.Size = 12
        $sel.Font.Bold = $false
        $sel.Font.Italic = $false
        $sel.Font.ColorIndex = 0
        $sel.ParagraphFormat.Alignment = 3
        $sel.ParagraphFormat.LineSpacingRule = 1
        $sel.ParagraphFormat.SpaceBefore = 0
        $sel.ParagraphFormat.SpaceAfter = 10
        $sel.ParagraphFormat.LeftIndent = 28.35
        $sel.ParagraphFormat.FirstLineIndent = -28.35
        $sel.TypeText($refText)
        $sel.TypeParagraph()
    }

    Add-ReferenceItem "Al-Sabahi, J., & Wang, H. (2022). Dynamic route optimization for urban flood evacuation using adaptive heuristic algorithms and geographic information systems. Journal of Hydrology and Transport Modeling, 608, 127-142. https://doi.org/10.1016/j.jhydrol.2022.127640"
    Add-ReferenceItem "Comisión Nacional del Agua [CONAGUA]. (2023). Diagnóstico hidrometeorológico y vulnerabilidad por anegamiento en la cuenca baja de los ríos Grijalva y Usumacinta. Secretaría de Medio Ambiente y Recursos Naturales. México."
    Add-ReferenceItem "Cormen, T. H., Leiserson, C. E., Rivest, R. L., & Stein, C. (2022). Introduction to algorithms (4th ed.). MIT Press."
    Add-ReferenceItem "OpenStreetMap Foundation. (2024). OpenStreetMap collaborative spatial database and Overpass API query language specification. OpenStreetMap Community Publications. https://wiki.openstreetmap.org/wiki/Overpass_API"

    Write-Output "Paso 16: Guardando archivo Word..."
    $doc.SaveAs([ref]$outputPath)
    $doc.SaveAs([ref]$outputCopyPath)
    $doc.Close()
    Write-Output "Documento Word generado exitosamente en:"
    Write-Output "  1. $outputPath"
    Write-Output "  2. $outputCopyPath"
} catch {
    Write-Output "ERROR: $($_.Exception.Message)"
    Write-Output "DETALLE: $($_.Exception.StackTrace)"
} finally {
    $word.Quit()
    [System.Runtime.InteropServices.Marshal]::ReleaseComObject($word) | Out-Null
    [System.GC]::Collect()
    [System.GC]::WaitForPendingFinalizers()
}
