package com.aquaruta.infrastructure.external.overpass;

import com.aquaruta.domain.model.GeoPoint;
import com.aquaruta.domain.model.RoadEdge;
import com.aquaruta.domain.model.RoadNode;
import com.aquaruta.infrastructure.external.elevation.ElevationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Cliente para ingesta de datos viales desde OpenStreetMap mediante Overpass API.
 * Incluye dataset calibrado de fallback para Villahermosa Centro.
 */
@Component
public class OverpassApiClient {

    private static final Logger log = LoggerFactory.getLogger(OverpassApiClient.class);

    private final RestTemplate restTemplate;
    private final ElevationService elevationService;
    private final String overpassUrl;

    public OverpassApiClient(RestTemplate restTemplate,
                             ElevationService elevationService,
                             @Value("${aquaruta.apis.overpass-url:https://overpass-api.de/api/interpreter}") String overpassUrl) {
        this.restTemplate = restTemplate;
        this.elevationService = elevationService;
        this.overpassUrl = overpassUrl;
    }

    public record IngestedGraphData(List<RoadNode> nodes, List<RoadEdge> edges) {}

    /**
     * Descarga la red vial dentro del Bounding Box de Villahermosa Centro.
     */
    public IngestedGraphData fetchRoadNetwork(double minLat, double minLon, double maxLat, double maxLon) {
        try {
            String query = String.format(
                    "[out:json][timeout:15];(way[\"highway\"~\"motorway|trunk|primary|secondary|tertiary|residential\"](%.4f,%.4f,%.4f,%.4f););out body;>;out skel qt;",
                    minLat, minLon, maxLat, maxLon
            );
            String url = overpassUrl + "?data=" + query;
            log.info("Consultando Overpass API para Villahermosa Centro...");

            JsonNode root = restTemplate.getForObject(url, JsonNode.class);
            if (root != null && root.has("elements")) {
                return parseOverpassElements(root.get("elements"));
            }
        } catch (Exception e) {
            log.warn("Overpass API no disponible o excedió tiempo límite ({}). Activando red arterial calibrada de Villahermosa.", e.getMessage());
        }

        return generateCalibratedVillahermosaNetwork();
    }

    private IngestedGraphData parseOverpassElements(JsonNode elements) {
        Map<Long, RoadNode> nodeMap = new HashMap<>();
        List<RoadEdge> edges = new ArrayList<>();

        for (JsonNode elem : elements) {
            if ("node".equals(elem.path("type").asText())) {
                long id = elem.path("id").asLong();
                double lat = elem.path("lat").asDouble();
                double lon = elem.path("lon").asDouble();
                double elev = elevationService.getElevationForCoordinate(lat, lon);
                nodeMap.put(id, new RoadNode(id, lat, lon, elev));
            }
        }

        int edgeCounter = 1;
        for (JsonNode elem : elements) {
            if ("way".equals(elem.path("type").asText())) {
                JsonNode nodesArray = elem.path("nodes");
                JsonNode tags = elem.path("tags");
                String name = tags.path("name").asText("Sin Nombre");
                String highway = tags.path("highway").asText("unclassified");
                boolean oneway = "yes".equalsIgnoreCase(tags.path("oneway").asText("no"));
                int maxspeed = tags.path("maxspeed").asInt(40);

                for (int i = 0; i < nodesArray.size() - 1; i++) {
                    long uId = nodesArray.get(i).asLong();
                    long vId = nodesArray.get(i + 1).asLong();

                    RoadNode u = nodeMap.get(uId);
                    RoadNode v = nodeMap.get(vId);

                    if (u != null && v != null) {
                        double dist = u.toGeoPoint().distanceTo(v.toGeoPoint());
                        if (dist > 0.0) {
                            String edgeId = "WAY_" + (edgeCounter++);
                            edges.add(new RoadEdge(edgeId, uId, vId, name, highway, dist, oneway, maxspeed, 1.0));
                        }
                    }
                }
            }
        }

        log.info("Procesados {} nodos y {} tramos desde Overpass API.", nodeMap.size(), edges.size());
        return new IngestedGraphData(new ArrayList<>(nodeMap.values()), edges);
    }

    /**
     * Genera la red arterial principal calibrada para Villahermosa Centro (BBox 17.9650 a 18.0150).
     */
    public IngestedGraphData generateCalibratedVillahermosaNetwork() {
        List<RoadNode> nodes = new ArrayList<>();
        List<RoadEdge> edges = new ArrayList<>();

        // Hitos y vértices principales de Villahermosa
        addNode(nodes, 1001L, 17.9985, -92.9372); // Tabasco 2000 (Pirámide Pemex)
        addNode(nodes, 1002L, 17.9945, -92.9335); // Paseo Tabasco esq. Ruiz Cortines
        addNode(nodes, 1003L, 17.9922, -92.9305); // Catedral del Señor de Tabasco
        addNode(nodes, 1004L, 17.9890, -92.9250); // Parque Juárez / Zona Luz (Centro)
        addNode(nodes, 1005L, 17.9870, -92.9205); // Malecón Carlos A. Madrazo (Bajo - vulnerable)
        addNode(nodes, 1006L, 17.9850, -92.9120); // Puente Grijalva 1 (Cruce a Gaviotas)
        addNode(nodes, 1007L, 17.9835, -92.9090); // Sector Gaviotas Norte (Cota crítica baja 3.8m)
        addNode(nodes, 1008L, 17.9780, -92.9350); // Tamulté de las Barrancas
        addNode(nodes, 1009L, 17.9720, -92.9420); // Circuito Interior Carlos Pellicer Cámara Sur
        addNode(nodes, 1010L, 18.0050, -92.9450); // Carrizal Norte (Río Carrizal)
        addNode(nodes, 1011L, 17.9950, -92.9220); // Av. Gregorio Méndez esq. Mina
        addNode(nodes, 1012L, 17.9895, -92.9340); // Av. 27 de Febrero esq. Paseo Tabasco

        // Conexiones arteriales
        addEdge(edges, "E_TABASCO_1", 1001L, 1002L, "Paseo Tabasco (Norte)", "primary", 50, false);
        addEdge(edges, "E_TABASCO_2", 1002L, 1003L, "Paseo Tabasco (Catedral)", "primary", 50, false);
        addEdge(edges, "E_TABASCO_3", 1003L, 1012L, "Paseo Tabasco esq. 27 de Feb", "primary", 50, false);
        addEdge(edges, "E_27FEB_1", 1012L, 1004L, "Av. 27 de Febrero (Hacia Centro)", "secondary", 40, false);
        addEdge(edges, "E_CENTRO_MALECON", 1004L, 1005L, "Calle Juárez hacia Malecón", "residential", 30, false);
        addEdge(edges, "E_PUENTE_GAV", 1005L, 1006L, "Puente Grijalva 1", "primary", 50, false);
        addEdge(edges, "E_GAVIOTAS_1", 1006L, 1007L, "Paseo de la Choca (Gaviotas)", "secondary", 40, false);
        addEdge(edges, "E_MENDEZ_1", 1011L, 1004L, "Av. Gregorio Méndez (Centro)", "primary", 45, false);
        addEdge(edges, "E_MENDEZ_2", 1002L, 1011L, "Av. Gregorio Méndez (Oeste)", "primary", 45, false);
        addEdge(edges, "E_RUIZCORTINES_1", 1010L, 1002L, "Blvd. Adolfo Ruiz Cortines (Carrizal a Tabasco)", "trunk", 60, false);
        addEdge(edges, "E_RUIZCORTINES_2", 1002L, 1011L, "Blvd. Adolfo Ruiz Cortines (Centro)", "trunk", 60, false);
        addEdge(edges, "E_TAMULTE_1", 1012L, 1008L, "Av. Gregorio Méndez hacia Tamulté", "secondary", 40, false);
        addEdge(edges, "E_PELLICER_1", 1008L, 1009L, "Prol. 27 de Febrero a Circuito Pellicer", "primary", 50, false);
        addEdge(edges, "E_PELLICER_2", 1009L, 1010L, "Circuito Interior Carlos Pellicer Cámara Oeste", "trunk", 70, false);

        log.info("Red arterial calibrada de Villahermosa generada: {} nodos, {} aristas.", nodes.size(), edges.size());
        return new IngestedGraphData(nodes, edges);
    }

    private void addNode(List<RoadNode> list, long id, double lat, double lon) {
        double elev = elevationService.getElevationForCoordinate(lat, lon);
        list.add(new RoadNode(id, lat, lon, elev));
    }

    private void addEdge(List<RoadEdge> list, String id, long u, long v, String name, String type, int speed, boolean oneway) {
        GeoPoint pu = new GeoPoint(getLat(u), getLon(u));
        GeoPoint pv = new GeoPoint(getLat(v), getLon(v));
        double dist = Math.round(pu.distanceTo(pv) * 10.0) / 10.0;
        list.add(new RoadEdge(id, u, v, name, type, Math.max(50.0, dist), oneway, speed, 1.0));
    }

    private double getLat(long id) {
        return switch ((int) id) {
            case 1001 -> 17.9985;
            case 1002 -> 17.9945;
            case 1003 -> 17.9922;
            case 1004 -> 17.9890;
            case 1005 -> 17.9870;
            case 1006 -> 17.9850;
            case 1007 -> 17.9835;
            case 1008 -> 17.9780;
            case 1009 -> 17.9720;
            case 1010 -> 18.0050;
            case 1011 -> 17.9950;
            case 1012 -> 17.9895;
            default -> 17.9900;
        };
    }

    private double getLon(long id) {
        return switch ((int) id) {
            case 1001 -> -92.9372;
            case 1002 -> -92.9335;
            case 1003 -> -92.9305;
            case 1004 -> -92.9250;
            case 1005 -> -92.9205;
            case 1006 -> -92.9120;
            case 1007 -> -92.9090;
            case 1008 -> -92.9350;
            case 1009 -> -92.9420;
            case 1010 -> -92.9450;
            case 1011 -> -92.9220;
            case 1012 -> -92.9340;
            default -> -92.9300;
        };
    }
}
