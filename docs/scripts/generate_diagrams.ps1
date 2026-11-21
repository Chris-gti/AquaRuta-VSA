Add-Type -AssemblyName System.Drawing

$imgDir = "C:\Users\chris\Proectos\Recuperacion\docs\images"
if (-not (Test-Path $imgDir)) {
    New-Item -Path $imgDir -ItemType Directory -Force | Out-Null
}

function Create-ArchitectureDiagram {
    $bmp = New-Object System.Drawing.Bitmap(1200, 650)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit

    # Background
    $bgBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(250, 252, 255))
    $g.FillRectangle($bgBrush, 0, 0, 1200, 650)

    # Fonts
    $titleFont = New-Object System.Drawing.Font("Arial", 16, [System.Drawing.FontStyle]::Bold)
    $boxTitleFont = New-Object System.Drawing.Font("Arial", 12, [System.Drawing.FontStyle]::Bold)
    $itemFont = New-Object System.Drawing.Font("Arial", 10, [System.Drawing.FontStyle]::Regular)
    $noteFont = New-Object System.Drawing.Font("Arial", 9, [System.Drawing.FontStyle]::Italic)

    # Brushes & Pens
    $textDark = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(15, 23, 42))
    $textMuted = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(71, 85, 105))
    $borderPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(203, 213, 225), 2)
    $arrowPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(37, 99, 235), 2)
    $arrowPen.CustomEndCap = New-Object System.Drawing.Drawing2D.AdjustableArrowCap(5, 5)

    # Main Title
    $g.DrawString("Arquitectura Hexagonal (Puertos y Adaptadores) - AquaRuta VSA", $titleFont, $textDark, 220, 20)

    # Box 1: UI / REST (Left)
    $brushUI = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(239, 246, 255))
    $g.FillRectangle($brushUI, 40, 80, 230, 520)
    $g.DrawRectangle($borderPen, 40, 80, 230, 520)
    $g.DrawString("Capa de Interfaces (Web)", $boxTitleFont, $textDark, 55, 95)
    $uiItems = @(
        "• Leaflet.js (Cartografía OSM)",
        "• RoutingController (REST)",
        "• ReportController (REST)",
        "• WeatherController (REST)",
        "• SimulationController (REST)",
        "• IngestionController (REST)",
        "• GlobalExceptionHandler",
        "• OpenAPI 3.0 / Swagger UI"
    )
    $y = 135
    foreach ($item in $uiItems) {
        $g.DrawString($item, $itemFont, $textDark, 55, $y)
        $y += 30
    }

    # Box 2: Application (Center-Left)
    $brushApp = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(240, 253, 244))
    $g.FillRectangle($brushApp, 310, 80, 250, 520)
    $g.DrawRectangle($borderPen, 310, 80, 250, 520)
    $g.DrawString("Capa de Aplicación", $boxTitleFont, $textDark, 325, 95)
    $appItems = @(
        "• RoutingService",
        "  (Orquestador de Rutas A*)",
        "• WaterOutageService",
        "  (Gestión de Reportes)",
        "• SimulationService",
        "  (Inyección de Escenarios)",
        "• WeatherGridService",
        "  (Sincronización Pluvial)",
        "• MapIngestionService",
        "  (ETL y Grafo en Memoria)",
        "• DTOs Inmutables (Records)"
    )
    $y = 135
    foreach ($item in $appItems) {
        $g.DrawString($item, $itemFont, $textDark, 325, $y)
        $y += 28
    }

    # Box 3: Domain Core (Center-Right)
    $brushDom = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(254, 242, 242))
    $g.FillRectangle($brushDom, 600, 80, 260, 520)
    $g.DrawRectangle($borderPen, 600, 80, 260, 520)
    $g.DrawString("Núcleo de Dominio Puro", $boxTitleFont, $textDark, 615, 95)
    $domItems = @(
        "• Entidades y Value Objects:",
        "  - GeoPoint (Haversine)",
        "  - RoadNode (Cota msnm)",
        "  - RoadEdge (Impedancia)",
        "  - CitizenReport",
        "  - ValidatedAlert (Quórum)",
        "  - RoutePath (Métricas)",
        "• Patrón Strategy:",
        "  - RiskCalculationStrategy",
        "  - NormalWeatherStrategy",
        "  - FloodResilientStrategy",
        "• Puertos de Salida (Interfaces)"
    )
    $y = 135
    foreach ($item in $domItems) {
        $g.DrawString($item, $itemFont, $textDark, 615, $y)
        $y += 28
    }

    # Box 4: Infrastructure (Right)
    $brushInfra = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(248, 250, 252))
    $g.FillRectangle($brushInfra, 900, 80, 260, 520)
    $g.DrawRectangle($borderPen, 900, 80, 260, 520)
    $g.DrawString("Capa de Infraestructura", $boxTitleFont, $textDark, 915, 95)
    $infraItems = @(
        "• Motor de Grafos en RAM:",
        "  - JGraphT (DirectedGraph)",
        "  - GeoDistanceHeuristic",
        "  - AStarRoutingEngine",
        "• Persistencia Espacial:",
        "  - PostgreSQL 16 + PostGIS",
        "  - Hibernate Spatial (JTS)",
        "  - Índices GIST EPSG:4326",
        "• Servicios Externos:",
        "  - Overpass API (OSM)",
        "  - Open-Meteo Client",
        "  - ElevationService (Topografía)"
    )
    $y = 135
    foreach ($item in $infraItems) {
        $g.DrawString($item, $itemFont, $textDark, 915, $y)
        $y += 28
    }

    # Arrows between layers
    $g.DrawLine($arrowPen, 270, 250, 310, 250)
    $g.DrawLine($arrowPen, 560, 250, 600, 250)
    $g.DrawLine($arrowPen, 860, 250, 900, 250)
    $g.DrawLine($arrowPen, 560, 420, 900, 420)

    $diagPath = "$imgDir\diagrama_arquitectura.png"
    $bmp.Save($diagPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose()
    $bmp.Dispose()
    Write-Output "Architecture diagram generated: $diagPath"
}

function Create-ConsensusDiagram {
    $bmp = New-Object System.Drawing.Bitmap(1100, 450)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit

    $bgBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(255, 255, 255))
    $g.FillRectangle($bgBrush, 0, 0, 1100, 450)

    $titleFont = New-Object System.Drawing.Font("Arial", 15, [System.Drawing.FontStyle]::Bold)
    $boxTitleFont = New-Object System.Drawing.Font("Arial", 11, [System.Drawing.FontStyle]::Bold)
    $itemFont = New-Object System.Drawing.Font("Arial", 9, [System.Drawing.FontStyle]::Regular)

    $textDark = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(15, 23, 42))
    $borderPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(203, 213, 225), 2)
    $arrowPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(239, 68, 68), 2)
    $arrowPen.CustomEndCap = New-Object System.Drawing.Drawing2D.AdjustableArrowCap(5, 5)

    $g.DrawString("Flujo Algorítmico de Consenso Espacio-Temporal de Reportes", $titleFont, $textDark, 230, 20)

    # Step 1: Recepción
    $b1 = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(238, 242, 255))
    $g.FillRectangle($b1, 40, 80, 210, 300)
    $g.DrawRectangle($borderPen, 40, 80, 210, 300)
    $g.DrawString("1. Reporte Ciudadano", $boxTitleFont, $textDark, 55, 100)
    $t1 = @("• Tipo de incidencia:", "  CORTE_AGUA,", "  BAJA_PRESION o", "  CALLE_INUNDADA", "• Coordenadas GPS (WGS84)", "• Marca temporal exacta", "• Estado: PENDIENTE")
    $y = 140
    foreach ($line in $t1) { $g.DrawString($line, $itemFont, $textDark, 55, $y); $y += 24 }

    # Step 2: Filtrado
    $b2 = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(254, 243, 199))
    $g.FillRectangle($b2, 290, 80, 220, 300)
    $g.DrawRectangle($borderPen, 290, 80, 220, 300)
    $g.DrawString("2. Filtro Espacio-Temporal", $boxTitleFont, $textDark, 305, 100)
    $t2 = @("• Consulta candidatos:", "  Mismo tipo de evento", "• Ventana de tiempo:", "  Delta t <= 2.0 horas", "• Distancia Haversine:", "  Radio d <= 200.0 metros", "• Descarte de falsos positivos")
    $y = 140
    foreach ($line in $t2) { $g.DrawString($line, $itemFont, $textDark, 305, $y); $y += 24 }

    # Step 3: Evaluación Quórum
    $b3 = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(254, 226, 226))
    $g.FillRectangle($b3, 550, 80, 230, 300)
    $g.DrawRectangle($borderPen, 550, 80, 230, 300)
    $g.DrawString("3. Evaluación de Quórum", $boxTitleFont, $textDark, 565, 100)
    $t3 = @("• Condición de confirmación:", "  Cardinalidad k >= 3 reportes", "• Cálculo de Centroide:", "  Promedio baricéntrico de", "  latitudes y longitudes", "• Radio oficial: 200m", "• Transición a CONFIRMADO")
    $y = 140
    foreach ($line in $t3) { $g.DrawString($line, $itemFont, $textDark, 565, $y); $y += 24 }

    # Step 4: Alerta Oficial
    $b4 = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(220, 252, 231))
    $g.FillRectangle($b4, 820, 80, 240, 300)
    $g.DrawRectangle($borderPen, 820, 80, 240, 300)
    $g.DrawString("4. Alerta Oficial Validada", $boxTitleFont, $textDark, 835, 100)
    $t4 = @("• Creación ValidatedAlert", "• Emisión de evento reactivo:", "  OutageClusterConfirmedEvent", "• Notificación Toast a usuarios", "• Dibujo en mapa Leaflet:", "  Círculo rojo punteado", "• Penalización en grafo A*")
    $y = 140
    foreach ($line in $t4) { $g.DrawString($line, $itemFont, $textDark, 835, $y); $y += 24 }

    # Arrows
    $g.DrawLine($arrowPen, 250, 230, 290, 230)
    $g.DrawLine($arrowPen, 510, 230, 550, 230)
    $g.DrawLine($arrowPen, 780, 230, 820, 230)

    $diagPath = "$imgDir\diagrama_consenso.png"
    $bmp.Save($diagPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose()
    $bmp.Dispose()
    Write-Output "Consensus diagram generated: $diagPath"
}

function Create-LogoBadge {
    $bmp = New-Object System.Drawing.Bitmap(320, 70)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit

    $brushBlue = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(30, 58, 138))
    $g.FillRectangle($brushBlue, 0, 0, 320, 70)

    $penGold = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(245, 158, 11), 3)
    $g.DrawRectangle($penGold, 2, 2, 316, 66)

    $fontTitle = New-Object System.Drawing.Font("Arial", 15, [System.Drawing.FontStyle]::Bold)
    $fontSub = New-Object System.Drawing.Font("Arial", 8, [System.Drawing.FontStyle]::Bold)
    $whiteBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)
    $goldBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(254, 240, 138))

    $g.DrawString("AquaRuta VSA", $fontTitle, $whiteBrush, 15, 10)
    $g.DrawString("SISTEMA HÍDRICO & VIAL RESILIENTE", $fontSub, $goldBrush, 15, 40)

    $logoPath = "$imgDir\logo_aquaruta.png"
    $bmp.Save($logoPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose()
    $bmp.Dispose()
    Write-Output "Logo badge generated: $logoPath"
}

function Create-GanttDiagram {
    $bmp = New-Object System.Drawing.Bitmap(1150, 480)
    $g = [System.Drawing.Graphics]::FromImage($bmp)
    $g.SmoothingMode = [System.Drawing.Drawing2D.SmoothingMode]::AntiAlias
    $g.TextRenderingHint = [System.Drawing.Text.TextRenderingHint]::ClearTypeGridFit

    $bgBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::White)
    $g.FillRectangle($bgBrush, 0, 0, 1150, 480)

    $titleFont = New-Object System.Drawing.Font("Arial", 14, [System.Drawing.FontStyle]::Bold)
    $headerFont = New-Object System.Drawing.Font("Arial", 10, [System.Drawing.FontStyle]::Bold)
    $rowFont = New-Object System.Drawing.Font("Arial", 9, [System.Drawing.FontStyle]::Regular)

    $textDark = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(15, 23, 42))
    $gridPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(226, 232, 240), 1)
    $borderPen = New-Object System.Drawing.Pen([System.Drawing.Color]::FromArgb(148, 163, 184), 1)
    $barBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(37, 99, 235))
    $barDoneBrush = New-Object System.Drawing.SolidBrush([System.Drawing.Color]::FromArgb(16, 185, 129))

    $g.DrawString("Diagrama de Gantt — Planificación de Actividades (Semanas 1 a 12)", $titleFont, $textDark, 250, 20)

    # Header columns
    $g.DrawString("Fase / Actividad de Ingeniería", $headerFont, $textDark, 30, 65)
    for ($w = 1; $w -le 12; $w++) {
        $x = 420 + ($w - 1) * 58
        $g.DrawString("Sem $w", $headerFont, $textDark, $x + 6, 65)
        $g.DrawLine($gridPen, $x, 60, $x, 450)
    }
    $g.DrawLine($borderPen, 20, 90, 1120, 90)

    $tasks = @(
        @{ Name = "1. Análisis de Requerimientos y PAN #10"; Start = 1; End = 2; Done = $true },
        @{ Name = "2. Delimitación Bounding Box y OSM Overpass"; Start = 2; End = 4; Done = $true },
        @{ Name = "3. Modelo Altimétrico y Topografía Calibrada"; Start = 3; End = 5; Done = $true },
        @{ Name = "4. Motor de Grafos JGraphT en RAM y A*"; Start = 5; End = 7; Done = $true },
        @{ Name = "5. Función de Impedancia Hidráulica (Strategy)"; Start = 6; End = 8; Done = $true },
        @{ Name = "6. Consenso Espacio-Temporal y Clustering"; Start = 7; End = 9; Done = $true },
        @{ Name = "7. Base de Datos Espacial PostGIS 16"; Start = 8; End = 10; Done = $true },
        @{ Name = "8. Interfaz Leaflet.js y Sala de Control"; Start = 9; End = 11; Done = $true },
        @{ Name = "9. Pruebas Unitarias y Documentación Formal"; Start = 11; End = 12; Done = $true }
    )

    $y = 100
    foreach ($t in $tasks) {
        $g.DrawString($t.Name, $rowFont, $textDark, 30, $y + 4)
        $barX = 420 + ($t.Start - 1) * 58 + 4
        $barW = ($t.End - $t.Start + 1) * 58 - 8
        $b = if ($t.Done) { $barDoneBrush } else { $barBrush }
        $g.FillRectangle($b, $barX, $y + 2, $barW, 22)
        $g.DrawLine($gridPen, 20, $y + 30, 1120, $y + 30)
        $y += 36
    }
    $g.DrawRectangle($borderPen, 20, 60, 1100, 400)

    $ganttPath = "$imgDir\diagrama_gantt.png"
    $bmp.Save($ganttPath, [System.Drawing.Imaging.ImageFormat]::Png)
    $g.Dispose()
    $bmp.Dispose()
    Write-Output "Gantt diagram generated: $ganttPath"
}

Create-ArchitectureDiagram
Create-ConsensusDiagram
Create-LogoBadge
Create-GanttDiagram
