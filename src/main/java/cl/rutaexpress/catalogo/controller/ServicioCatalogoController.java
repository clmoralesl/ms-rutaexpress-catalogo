package cl.rutaexpress.catalogo.controller;

import cl.rutaexpress.catalogo.dto.ActualizarServicioRequest;
import cl.rutaexpress.catalogo.dto.CrearServicioRequest;
import cl.rutaexpress.catalogo.dto.ServicioResponse;
import cl.rutaexpress.catalogo.service.ServicioCatalogoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/servicios")
public class ServicioCatalogoController {

    private final ServicioCatalogoService service;

    public ServicioCatalogoController(ServicioCatalogoService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<ServicioResponse>> listarServicios() {
        return ResponseEntity.ok(service.listarServiciosActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServicioResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(service.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<ServicioResponse> crearServicio(@Valid @RequestBody CrearServicioRequest request) {
        ServicioResponse creado = service.crearServicio(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ServicioResponse> actualizarServicio(
            @PathVariable Long id,
            @Valid @RequestBody ActualizarServicioRequest request) {
        return ResponseEntity.ok(service.actualizarServicio(id, request));
    }

    /**
     * Endpoint atomico y thread-safe para descontar 1 cupo de capacidad diaria.
     * Si la capacidad llega a 0, responde HTTP 409 Conflict ("Capacidad de flota agotada").
     */
    @PatchMapping("/{id}/descontar-capacidad")
    public ResponseEntity<ServicioResponse> descontarCapacidad(@PathVariable Long id) {
        return ResponseEntity.ok(service.descontarCapacidad(id));
    }

    @PatchMapping("/{id}/restablecer-capacidad")
    public ResponseEntity<ServicioResponse> restablecerCapacidad(@PathVariable Long id) {
        return ResponseEntity.ok(service.restablecerCapacidad(id));
    }
}
