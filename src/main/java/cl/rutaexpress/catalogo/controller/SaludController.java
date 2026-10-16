package cl.rutaexpress.catalogo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
public class SaludController {

    @GetMapping({"/api/publico/salud", "/actuator/health"})
    public ResponseEntity<Map<String, Object>> verificarSalud() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "estado", "UP",
                "servicio", "ms-rutaexpress-catalogo",
                "timestamp", LocalDateTime.now().toString(),
                "mensaje", "Microservicio de catalogo y tarifas de flota operativo"
        ));
    }
}
