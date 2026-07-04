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

import com.minimarket.entity.Categoria;
import com.minimarket.service.CategoriaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Categorías", description = "Operaciones para gestionar las categorías de productos del minimarket")
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @Operation(summary = "Listar categorías", description = "Obtiene la lista completa de categorías registradas en el minimarket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida correctamente", content = @Content(schema = @Schema(implementation = Categoria.class)))
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO', 'CLIENTE')") // Solo GERENTE, EMPLEADO y CLIENTE pueden acceder a
                                                                  // estos endpoints
    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaService.findAll();
    }

    @Operation(summary = "Obtener categoría por ID", description = "Busca y retorna una categoría específica según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría encontrada", content = @Content(schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE','EMPLEADO','CLIENTE')") // Solo GERENTE, EMPLEADO y CLIENTE pueden acceder a
                                                                // estos endpoints
    @GetMapping("/{id}")
    public ResponseEntity<Categoria> obtenerCategoriaPorId(
            @Parameter(description = "Identificador único de la categoría", example = "1") @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        return (categoria != null) ? ResponseEntity.ok(categoria) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear categoría", description = "Registra una nueva categoría en el sistema. Solo disponible para el rol GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría creada correctamente", content = @Content(schema = @Schema(implementation = Categoria.class)))
    })
    @PreAuthorize("hasRole('GERENTE')") // Solo GERENTE pueden guardar categorías
    @PostMapping
    public Categoria guardarCategoria(
            @Parameter(description = "Datos de la categoría a crear") @RequestBody Categoria categoria) {
        return categoriaService.save(categoria);
    }

    @Operation(summary = "Actualizar categoría", description = "Actualiza los datos de una categoría existente según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categoría actualizada correctamente", content = @Content(schema = @Schema(implementation = Categoria.class))),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden actualizar categorías
    @PutMapping("/{id}")
    public ResponseEntity<Categoria> actualizarCategoria(
            @Parameter(description = "Identificador único de la categoría", example = "1") @PathVariable Long id,
            @Parameter(description = "Datos actualizados de la categoría") @RequestBody Categoria categoria) {
        Categoria categoriaExistente = categoriaService.findById(id);
        if (categoriaExistente != null) {
            categoria.setId(id);
            return ResponseEntity.ok(categoriaService.save(categoria));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar categoría", description = "Elimina una categoría del sistema según su identificador. Solo disponible para el rol GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Categoría eliminada correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Categoría no encontrada", content = @Content)
    })
    @PreAuthorize("hasRole('GERENTE')") // Solo GERENTE puede eliminar categorías
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCategoria(
            @Parameter(description = "Identificador único de la categoría", example = "1") @PathVariable Long id) {
        Categoria categoria = categoriaService.findById(id);
        if (categoria != null) {
            categoriaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}