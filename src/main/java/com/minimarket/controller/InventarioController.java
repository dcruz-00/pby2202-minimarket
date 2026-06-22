package com.minimarket.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.minimarket.dto.InventarioRequestDTO;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.InventarioService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ProductoRepository productoRepository;

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @GetMapping
    public List<Inventario> listarMovimientosDeInventario() {
        return inventarioService.findAll();
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @GetMapping("/{id}")
    public ResponseEntity<Inventario> obtenerMovimientoPorId(@PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        return (inventario != null) ? ResponseEntity.ok(inventario) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @PostMapping
    public ResponseEntity<?> registrarMovimiento(@Valid @RequestBody InventarioRequestDTO request) {
        Producto producto = productoRepository.findById(request.getProductoId())
                .orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado");
        }

        Inventario inventario = new Inventario();
        inventario.setProducto(producto);
        inventario.setCantidad(request.getCantidad());
        inventario.setTipoMovimiento(request.getTipoMovimiento());
        inventario.setFechaMovimiento(new Date());

        return ResponseEntity.ok(inventarioService.save(inventario));
    }

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMovimiento(@PathVariable Long id, @Valid @RequestBody InventarioRequestDTO request) {
        Inventario existente = inventarioService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        Producto producto = productoRepository.findById(request.getProductoId()).orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado");
        }

        existente.setProducto(producto);
        existente.setCantidad(request.getCantidad());
        existente.setTipoMovimiento(request.getTipoMovimiento());
        existente.setFechaMovimiento(new Date());

        return ResponseEntity.ok(inventarioService.save(existente));
    }

    @PreAuthorize("hasRole('GERENTE')")
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