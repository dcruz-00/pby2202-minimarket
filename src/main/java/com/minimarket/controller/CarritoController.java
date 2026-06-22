package com.minimarket.controller;

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

import com.minimarket.dto.CarritoRequestDTO;
import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.CarritoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @GetMapping
    public List<Carrito> listarCarrito() {
        return carritoService.findAll();
    }

    @PreAuthorize("hasAnyRole('GERENTE','CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<Carrito> obtenerCarritoPorId(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        return (carrito != null) ? ResponseEntity.ok(carrito) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<?> agregarProductoAlCarrito(@Valid @RequestBody CarritoRequestDTO request) {
        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElse(null);
        if (usuario == null) {
            return ResponseEntity.badRequest().body("Usuario no encontrado");
        }

        Producto producto = productoRepository.findById(request.getProductoId())
                .orElse(null);
        if (producto == null) {
            return ResponseEntity.badRequest().body("Producto no encontrado");
        }

        Carrito carrito = new Carrito();
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(request.getCantidad());

        return ResponseEntity.ok(carritoService.agregarProducto(carrito));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<Carrito> actualizarCarrito(@PathVariable Long id,
            @Valid @RequestBody CarritoRequestDTO request) {
        Carrito existente = carritoService.findById(id);
        if (existente == null) {
            return ResponseEntity.notFound().build();
        }

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId()).orElse(null);
        Producto producto = productoRepository.findById(request.getProductoId()).orElse(null);
        if (usuario == null || producto == null) {
            return ResponseEntity.badRequest().build();
        }

        existente.setUsuario(usuario);
        existente.setProducto(producto);
        existente.setCantidad(request.getCantidad());

        return ResponseEntity.ok(carritoService.save(existente));
    }

    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(@PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}