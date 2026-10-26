package cl.rutaexpress.catalogo.controller;

import cl.rutaexpress.catalogo.dto.ActualizarTarifaZonaRequest;
import cl.rutaexpress.catalogo.dto.TarifaZonaResponse;
import cl.rutaexpress.catalogo.entity.ZonaTarifa;
import cl.rutaexpress.catalogo.service.TarifaZonaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/tarifas-zonas")
public class TarifaZonaController {

    private final TarifaZonaService tarifaZonaService;

    public TarifaZonaController(TarifaZonaService tarifaZonaService) {
        this.tarifaZonaService = tarifaZonaService;
    }

    @GetMapping
    public ResponseEntity<List<TarifaZonaResponse>> listar() {
        return ResponseEntity.ok(tarifaZonaService.listar());
    }

    @GetMapping("/{zona}")
    public ResponseEntity<TarifaZonaResponse> obtenerPorZona(@PathVariable ZonaTarifa zona) {
        return ResponseEntity.ok(tarifaZonaService.obtenerPorZona(zona));
    }

    @PutMapping("/{zona}")
    public ResponseEntity<TarifaZonaResponse> actualizar(
            @PathVariable ZonaTarifa zona,
            @Valid @RequestBody ActualizarTarifaZonaRequest request) {
        return ResponseEntity.ok(tarifaZonaService.actualizar(zona, request));
    }
}
