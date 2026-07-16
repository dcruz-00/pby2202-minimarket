package com.minimarket.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
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

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Productos", description = "Operaciones para gestionar el catalogo de productos del minimarket")
@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @Operation(summary = "Listar productos", description = "Obtiene la lista completa de productos registrados en el minimarket.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de productos obtenida correctamente")
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO', 'CLIENTE')")
    @GetMapping
    public CollectionModel<EntityModel<Producto>> listarProductos() {
        List<EntityModel<Producto>> productos = productoService.findAll().stream()
                .map(producto -> EntityModel.of(producto,
                        linkTo(methodOn(ProductoController.class).obtenerProductoPorId(producto.getId())).withSelfRel(),
                        linkTo(methodOn(ProductoController.class).listarProductos()).withRel("lista-productos")))
                .collect(Collectors.toList());

        return CollectionModel.of(productos,
                linkTo(methodOn(ProductoController.class).listarProductos()).withSelfRel());
    }

    @Operation(summary = "Obtener producto por ID", description = "Busca y retorna un producto especifico segun su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO', 'CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> obtenerProductoPorId(
            @Parameter(description = "Identificador unico del producto", example = "1") @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Producto> recurso = EntityModel.of(producto,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("lista-productos"),
                linkTo(methodOn(ProductoController.class).actualizarProducto(id, null)).withRel("actualizar-producto"),
                linkTo(methodOn(ProductoController.class).eliminarProducto(id)).withRel("eliminar-producto"));

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Crear producto", description = "Registra un nuevo producto en el catalogo. Solo disponible para el rol GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto creado correctamente")
    })
    @PreAuthorize("hasRole('GERENTE')")
    @PostMapping
    public EntityModel<Producto> guardarProducto(
            @Parameter(description = "Datos del producto a crear") @RequestBody Producto producto) {
        Producto guardado = productoService.save(producto);
        return EntityModel.of(guardado,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(guardado.getId())).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("lista-productos"));
    }

    @Operation(summary = "Actualizar producto", description = "Actualiza los datos de un producto existente segun su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto actualizado correctamente"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Producto>> actualizarProducto(
            @Parameter(description = "Identificador unico del producto", example = "1") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del producto") @RequestBody Producto producto) {
        Producto productoExistente = productoService.findById(id);
        if (productoExistente == null) {
            return ResponseEntity.notFound().build();
        }
        producto.setId(id);
        Producto actualizado = productoService.save(producto);

        EntityModel<Producto> recurso = EntityModel.of(actualizado,
                linkTo(methodOn(ProductoController.class).obtenerProductoPorId(id)).withSelfRel(),
                linkTo(methodOn(ProductoController.class).listarProductos()).withRel("lista-productos"));

        return ResponseEntity.ok(recurso);
    }

    @Operation(
            summary = "Eliminar producto",
            description = "Elimina un producto del catalogo segun su identificador. Solo disponible para el rol GERENTE."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Producto eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado", content = @Content)
    })
    @PreAuthorize("hasRole('GERENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProducto(
        @Parameter(description = "Identificador unico del producto", example = "1")
        @PathVariable Long id) {
        Producto producto = productoService.findById(id);
        if (producto != null) {
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}