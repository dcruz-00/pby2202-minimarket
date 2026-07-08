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

import com.minimarket.dto.CarritoRequestDTO;
import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.CarritoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Tag(name = "Carrito", description = "Operaciones para gestionar el carrito de compras de los clientes")
@RestController
@RequestMapping("/api/carrito")
public class CarritoController {

    @Autowired
    private CarritoService carritoService;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProductoRepository productoRepository;

    @Operation(summary = "Listar carritos", description = "Obtiene la lista completa de carritos registrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de carritos obtenida correctamente", content = @Content(schema = @Schema(implementation = Carrito.class)))
    })
    @PreAuthorize("hasAnyRole('GERENTE', 'EMPLEADO')")
    @GetMapping
    public CollectionModel<EntityModel<Carrito>> listarCarrito() {
        List<EntityModel<Carrito>> carritos = carritoService.findAll().stream()
                .map(carrito -> EntityModel.of(carrito,
                        linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(carrito.getId())).withSelfRel(),
                        linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("lista-carrito")))
                .collect(Collectors.toList());

        return CollectionModel.of(carritos,
                linkTo(methodOn(CarritoController.class).listarCarrito()).withSelfRel());
    }

    @Operation(summary = "Obtener carrito por ID", description = "Busca y retorna un carrito especifico segun su identificador.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito encontrado", content = @Content(schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content)
    })
    @PreAuthorize("hasAnyRole('GERENTE','CLIENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> obtenerCarritoPorId(
            @Parameter(description = "Identificador unico del carrito", example = "1") @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito == null) {
            return ResponseEntity.notFound().build();
        }

        EntityModel<Carrito> recurso = EntityModel.of(carrito,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(id)).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("lista-carrito"),
                linkTo(methodOn(CarritoController.class).eliminarProductoDelCarrito(id)).withRel("eliminar-carrito"));

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Agregar producto al carrito", description = "Anade un producto al carrito de un usuario, indicando la cantidad deseada. Solo disponible para el rol CLIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Producto agregado correctamente al carrito", content = @Content(schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "400", description = "Usuario o producto no encontrado", content = @Content)
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @PostMapping
    public ResponseEntity<?> agregarProductoAlCarrito(
            @Parameter(description = "Datos del producto a agregar: usuarioId, productoId y cantidad") @Valid @RequestBody CarritoRequestDTO request) {
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

        Carrito guardado = carritoService.agregarProducto(carrito);

        EntityModel<Carrito> recurso = EntityModel.of(guardado,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(guardado.getId())).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("lista-carrito"));

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Actualizar carrito", description = "Actualiza los datos de un carrito existente (usuario, producto y cantidad). Solo disponible para el rol CLIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Carrito actualizado correctamente", content = @Content(schema = @Schema(implementation = Carrito.class))),
            @ApiResponse(responseCode = "400", description = "Usuario o producto no encontrado", content = @Content),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content)
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @PutMapping("/{id}")
    public ResponseEntity<EntityModel<Carrito>> actualizarCarrito(
            @Parameter(description = "Identificador unico del carrito", example = "1") @PathVariable Long id,
            @Parameter(description = "Datos actualizados del carrito: usuarioId, productoId y cantidad") @Valid @RequestBody CarritoRequestDTO request) {
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

        Carrito actualizado = carritoService.save(existente);

        EntityModel<Carrito> recurso = EntityModel.of(actualizado,
                linkTo(methodOn(CarritoController.class).obtenerCarritoPorId(id)).withSelfRel(),
                linkTo(methodOn(CarritoController.class).listarCarrito()).withRel("lista-carrito"));

        return ResponseEntity.ok(recurso);
    }

    @Operation(summary = "Eliminar producto del carrito", description = "Elimina un carrito (producto) segun su identificador. Solo disponible para el rol CLIENTE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Carrito eliminado correctamente", content = @Content),
            @ApiResponse(responseCode = "404", description = "Carrito no encontrado", content = @Content)
    })
    @PreAuthorize("hasRole('CLIENTE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarProductoDelCarrito(
            @Parameter(description = "Identificador unico del carrito", example = "1") @PathVariable Long id) {
        Carrito carrito = carritoService.findById(id);
        if (carrito != null) {
            carritoService.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}