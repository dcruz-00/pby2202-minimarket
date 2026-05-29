package com.minimarket.controller;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PreAuthorize ("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden acceder a estos endpoints
    @GetMapping
    public List<Venta> listarVentas() {
        return ventaService.findAll();
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden acceder a estos endpoints
    @GetMapping("/{id}")
    public ResponseEntity<Venta> obtenerVentaPorId(@PathVariable Long id) {
        Venta venta = ventaService.findById(id);
        return (venta != null) ? ResponseEntity.ok(venta) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden guardar ventas
    @PostMapping
    public Venta guardarVenta(@RequestBody Venta venta) {
        return ventaService.save(venta);
    }
}
