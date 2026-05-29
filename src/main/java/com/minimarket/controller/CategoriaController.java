package com.minimarket.controller;

import com.minimarket.entity.Categoria;
import com.minimarket.service.CategoriaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PreAuthorize ("hasAnyRole('GERENTE', 'EMPLEADO', 'CLIENTE')") // Solo GERENTE, EMPLEADO y CLIENTE pueden acceder a estos endpoints
    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaService.findAll();
    }

    @PreAuthorize("hasAnyRole('GERENTE','EMPLEADO','CLIENTE')") // Solo GERENTE, EMPLEADO y CLIENTE pueden acceder a estos endpoints
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        return (categoria != null) ? ResponseEntity.ok(categoria) : ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasRole('GERENTE')") // Solo GERENTE pueden guardar categorías
    @PostMapping
    public Categoria guardarCategoria(@RequestBody Categoria categoria) {
        return categoriaService.save(categoria);
    }

    @PreAuthorize ("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden actualizar categorías
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(@PathVariable Long id, @RequestBody Categoria categoria) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            categoria.setId(id);
            return ResponseEntity.ok(categoriaService.save(categoria));
        }
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize ("hasRole('GERENTE')") // Solo GERENTE puede eliminar categorías
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(@PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            categoriaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
