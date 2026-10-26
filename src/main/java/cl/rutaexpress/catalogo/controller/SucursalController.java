package cl.rutaexpress.catalogo.controller;

import cl.rutaexpress.catalogo.dto.SucursalRequest;
import cl.rutaexpress.catalogo.dto.SucursalResponse;
import cl.rutaexpress.catalogo.service.SucursalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalogo/sucursales")
public class SucursalController {

    private final SucursalService sucursalService;

    public SucursalController(SucursalService sucursalService) {
        this.sucursalService = sucursalService;
    }

    @GetMapping
    public ResponseEntity<List<SucursalResponse>> listar(
            @RequestParam(required = false, defaultValue = "false") Boolean activas) {
        return ResponseEntity.ok(sucursalService.listar(activas));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SucursalResponse> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(sucursalService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<SucursalResponse> crear(@Valid @RequestBody SucursalRequest request) {
        SucursalResponse creada = sucursalService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SucursalResponse> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody SucursalRequest request) {
        return ResponseEntity.ok(sucursalService.actualizar(id, request));
    }
}
