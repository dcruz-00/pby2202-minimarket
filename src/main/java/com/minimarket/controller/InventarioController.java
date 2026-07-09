package com.minimarket.controller;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import java.util.Date;
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

import com.minimarket.dto.InventarioRequestDTO;
import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.InventarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@Tag(name = "Inventario", description = "Operaciones para gestionar los movimientos de inventario del minimarket")
@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private ProductoRepository productoRepository;

    @Operation(summary = "Listar movimientos de inventario", description = "Obtiene la lista completa de movimientos de inventario registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de movimientos obtenida correctamente")
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @GetMapping
    public CollectionModel<EntityModel<Inventario>> listarMovimientosDeInventario() {
        List<EntityModel<Inventario>> movimientos = inventarioService.findAll().stream()
                .map(inventario -> EntityModel.of(inventario,
                        linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(inventario.getId()))
                                .withSelfRel(),
                        linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario())
                                .withRel("lista-inventario")))
                .collect(Collectors.toList());

        return CollectionModel.of(movimientos,
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario()).withSelfRel());
    }

    @Operation(summary = "Obtener movimiento por ID", description = "Busca y retorna un movimiento de inventario específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento encontrado"),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Inventario>> obtenerMovimientoPorId(
            @Parameter(description = "Identificador único del movimiento de inventario", example = "1") @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Inventario> recurso = EntityModel.of(inventario,
                linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(id)).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario())
                        .withRel("lista-inventario"),
                linkTo(methodOn(InventarioController.class).eliminarMovimiento(id)).withRel("eliminar-movimiento"));

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Registrar movimiento de inventario", description = "Registra un nuevo movimiento (Entrada o Salida) para un producto existente. Solo disponible para los roles GERENTE y EMPLEADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento registrado correctamente"),
            @ApiResponse(responseCode = "400", description = "Producto no encontrado o datos inválidos", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @PostMapping
    public ResponseEntity<?> registrarMovimiento(
            @Parameter(description = "Datos del movimiento: productoId, cantidad y tipoMovimiento ('Entrada' o 'Salida')") @Valid @RequestBody InventarioRequestDTO request) {
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

        Inventario guardado = inventarioService.save(inventario);

        EntityModel<Inventario> recurso = EntityModel.of(guardado,
                linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(guardado.getId())).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario())
                        .withRel("lista-inventario"));

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Actualizar movimiento de inventario", description = "Actualiza los datos de un movimiento de inventario existente según su identificador. Solo disponible para los roles GERENTE y EMPLEADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimiento actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Producto no encontrado o datos inválidos", content = @Content),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarMovimiento(
            @Parameter(description = "Identificador único del movimiento de inventario", example = "1") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del movimiento: productoId, cantidad y tipoMovimiento ('Entrada' o 'Salida')") @Valid @RequestBody InventarioRequestDTO request) {
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

        Inventario actualizado = inventarioService.save(existente);

        EntityModel<Inventario> recurso = EntityModel.of(actualizado,
                linkTo(methodOn(InventarioController.class).obtenerMovimientoPorId(id)).withSelfRel(),
                linkTo(methodOn(InventarioController.class).listarMovimientosDeInventario())
                        .withRel("lista-inventario"));

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Eliminar movimiento de inventario", description = "Elimina un movimiento de inventario según su identificador. Solo disponible para el rol GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Movimiento eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Movimiento no encontrado", content = @Content)
    })
    @PreAuthorize("hasRole('GERENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarMovimiento(
            @Parameter(description = "Identificador único del movimiento de inventario", example = "1") @PathVariable Long id) {
        Inventario inventario = inventarioService.findById(id);
        if (inventario != null) {
            inventarioService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}