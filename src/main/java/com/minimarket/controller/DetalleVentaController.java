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

import com.minimarket.entity.DetalleVenta;
import com.minimarket.service.DetalleVentaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Detalle de Ventas", description = "Operaciones para gestionar el detalle de productos incluidos en cada venta")
@RestController
@RequestMapping("/api/detalle-ventas")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Operation(summary = "Listar detalles de venta", description = "Obtiene la lista completa de los detalles de venta registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de detalles de venta obtenida correctamente", content = @Content(schema = @Schema(implementation = DetalleVenta.class)))
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden acceder a estos endpoints
    @GetMapping
    public List<DetalleVenta> listarDetalleVentas() {
        return detalleVentaService.findAll();
    }

    @Operation(summary = "Obtener detalle de venta por ID", description = "Busca y retorna un detalle de venta específico según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de venta encontrado", content = @Content(schema = @Schema(implementation = DetalleVenta.class))),
            @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden acceder a estos endpoints
    @GetMapping("/{id}")
    public ResponseEntity<DetalleVenta> obtenerDetalleVentaPorId(
            @Parameter(description = "Identificador único del detalle de venta", example = "1") @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        return (detalleVenta != null) ? ResponseEntity.ok(detalleVenta) : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Crear detalle de venta", description = "Registra un nuevo detalle de venta. Disponible para los roles GERENTE y EMPLEADO.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de venta creado correctamente", content = @Content(schema = @Schema(implementation = DetalleVenta.class)))
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden guardar detalles de venta
    @PostMapping
    public DetalleVenta guardarDetalleVenta(
            @Parameter(description = "Datos del detalle de venta a crear") @RequestBody DetalleVenta detalleVenta) {
        return detalleVentaService.save(detalleVenta);
    }

    @Operation(summary = "Actualizar detalle de venta", description = "Actualiza los datos de un detalle de venta existente según su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Detalle de venta actualizado correctamente", content = @Content(schema = @Schema(implementation = DetalleVenta.class))),
            @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')") // Solo GERENTE y EMPLEADO pueden acceder a estos endpoints
    @PutMapping("/{id}")
    public ResponseEntity<DetalleVenta> actualizarDetalleVenta(
            @Parameter(description = "Identificador único del detalle de venta", example = "1") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del detalle de venta") @RequestBody DetalleVenta detalleVenta) {
        DetalleVenta existente = detalleVentaService.findById(id);
        if (existente != null) {
            detalleVenta.setId(id);
            return ResponseEntity.ok(detalleVentaService.save(detalleVenta));
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Eliminar detalle de venta", description = "Elimina un detalle de venta según su identificador. Solo disponible para el rol GERENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Detalle de venta eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Detalle de venta no encontrado", content = @Content)
    })
    @PreAuthorize("hasRole('GERENTE')") // Solo GERENTE pueden eliminar detalles de venta
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarDetalleVenta(
            @Parameter(description = "Identificador único del detalle de venta", example = "1") @PathVariable Long id) {
        DetalleVenta detalleVenta = detalleVentaService.findById(id);
        if (detalleVenta != null) {
            detalleVentaService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}