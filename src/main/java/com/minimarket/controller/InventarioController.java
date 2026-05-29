package com.minimarket.controller;

import com.minimarket.entity.Inventario;
import com.minimarket.service.InventarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden acceder a estos endpoints
    @GetMapping
    public List<Inventario> listarMovimientosDeInventario() {
        return inventarioService.findAll();
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden acceder a estos endpoints
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerMovimientoPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return (inventario != null) ? ResponseEntity.ok(inventario) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden registrar movimientos de inventario
    @PostMapping
    public Inventario registrarMovimiento(@RequestBody Inventario inventario) {
        return inventarioService.save(inventario);
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden actualizar movimientos de inventario
    @PutMapping("/{id}")
    public ResponseEntity<Inventario> actualizarMovimiento(@PathVariable Long id, @RequestBody Inventario inventario) {
        Inventario existente = inventarioService.findById(id);
        if (existente != null) {
            inventario.setId(id);
            return ResponseEntity.ok(inventarioService.save(inventario));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('GERENTE')") // Solo GERENTE pueden eliminar movimientos de inventario
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
