package cl.rutaexpress.catalogo.controller;

import cl.rutaexpress.catalogo.dto.CotizacionRequest;
import cl.rutaexpress.catalogo.dto.CotizacionResponse;
import cl.rutaexpress.catalogo.service.CotizacionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalogo/cotizaciones")
public class CotizacionController {

    private final CotizacionService cotizacionService;

    public CotizacionController(CotizacionService cotizacionService) {
        this.cotizacionService = cotizacionService;
    }

    @PostMapping
    public ResponseEntity<CotizacionResponse> cotizar(@Valid @RequestBody CotizacionRequest request) {
        CotizacionResponse respuesta = cotizacionService.cotizar(request);
        return ResponseEntity.ok(respuesta);
    }
}
