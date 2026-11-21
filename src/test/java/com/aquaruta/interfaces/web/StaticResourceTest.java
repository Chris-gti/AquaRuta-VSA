package com.aquaruta.interfaces.web;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class StaticResourceTest {

    @Test
    @DisplayName("Verifica que los archivos estáticos de la Fase 4 existen en el classpath de Spring Boot")
    void testStaticAssetsExist() {
        var indexResource = getClass().getResource("/static/index.html");
        var cssResource = getClass().getResource("/static/css/styles.css");
        var jsAppResource = getClass().getResource("/static/js/app.js");
        var jsApiResource = getClass().getResource("/static/js/api-client.js");
        var jsMapResource = getClass().getResource("/static/js/map-manager.js");
        var jsRouteResource = getClass().getResource("/static/js/routing-ui.js");
        var jsSimResource = getClass().getResource("/static/js/simulation-ui.js");

        assertNotNull(indexResource, "index.html debe existir en /static");
        assertNotNull(cssResource, "styles.css debe existir en /static/css");
        assertNotNull(jsAppResource, "app.js debe existir en /static/js");
        assertNotNull(jsApiResource, "api-client.js debe existir en /static/js");
        assertNotNull(jsMapResource, "map-manager.js debe existir en /static/js");
        assertNotNull(jsRouteResource, "routing-ui.js debe existir en /static/js");
        assertNotNull(jsSimResource, "simulation-ui.js debe existir en /static/js");
    }
}
