package com.minimarket;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.minimarket.entity.Carrito;
import com.minimarket.entity.Producto;
import com.minimarket.entity.Usuario;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.repository.UsuarioRepository;
import com.minimarket.service.CarritoService;

@SpringBootTest
@AutoConfigureMockMvc
public class CarritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarritoService carritoService;

    @MockBean
    private UsuarioRepository usuarioRepository;

    @MockBean
    private ProductoRepository productoRepository;

    @MockBean
    private DataInitializer dataInitializer;

    // GET /api/carrito

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeListarCarrito() throws Exception {
        when(carritoService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeListarCarrito() throws Exception {
        when(carritoService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeListarCarrito() throws Exception {
        mockMvc.perform(get("/api/carrito"))
                .andExpect(status().isForbidden());
    }

    // GET /api/carrito/{id}

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeObtenerCarritoPorId() throws Exception {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoService.findById(1L)).thenReturn(carrito);
        mockMvc.perform(get("/api/carrito/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoNoPuedeObtenerCarritoPorId() throws Exception {
        mockMvc.perform(get("/api/carrito/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clientePuedeObtenerCarritoPorId() throws Exception {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoService.findById(1L)).thenReturn(carrito);
        mockMvc.perform(get("/api/carrito/1"))
                .andExpect(status().isOk());
    }

    // POST /api/carrito

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clientePuedeAgregarProductoAlCarrito() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setStock(20);

        Carrito carrito = new Carrito();
        carrito.setId(1L);
        carrito.setUsuario(usuario);
        carrito.setProducto(producto);
        carrito.setCantidad(3);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoService.agregarProducto(any(Carrito.class))).thenReturn(carrito);

        mockMvc.perform(post("/api/carrito")
                .contentType("application/json")
                .content("{\"usuarioId\":1,\"productoId\":1,\"cantidad\":3}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerenteNoPuedeAgregarProductoAlCarrito() throws Exception {
        mockMvc.perform(post("/api/carrito")
                .contentType("application/json")
                .content("{\"usuarioId\":1,\"productoId\":1,\"cantidad\":3}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoNoPuedeAgregarProductoAlCarrito() throws Exception {
        mockMvc.perform(post("/api/carrito")
                .contentType("application/json")
                .content("{\"usuarioId\":1,\"productoId\":1,\"cantidad\":3}"))
                .andExpect(status().isForbidden());
    }

    // PUT /api/carrito/{id}

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clientePuedeActualizarCarrito() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        Producto producto = new Producto();
        producto.setId(1L);

        Carrito existente = new Carrito();
        existente.setId(1L);
        existente.setUsuario(usuario);
        existente.setProducto(producto);
        existente.setCantidad(2);

        when(carritoService.findById(1L)).thenReturn(existente);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(carritoService.save(any(Carrito.class))).thenReturn(existente);

        mockMvc.perform(put("/api/carrito/1")
                .contentType("application/json")
                .content("{\"usuarioId\":1,\"productoId\":1,\"cantidad\":5}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerenteNoPuedeActualizarCarrito() throws Exception {
        mockMvc.perform(put("/api/carrito/1")
                .contentType("application/json")
                .content("{\"usuarioId\":1,\"productoId\":1,\"cantidad\":5}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoNoPuedeActualizarCarrito() throws Exception {
        mockMvc.perform(put("/api/carrito/1")
                .contentType("application/json")
                .content("{\"usuarioId\":1,\"productoId\":1,\"cantidad\":5}"))
                .andExpect(status().isForbidden());
    }

    // DELETE /api/carrito/{id}

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clientePuedeEliminarProductoDelCarrito() throws Exception {
        Carrito carrito = new Carrito();
        carrito.setId(1L);
        when(carritoService.findById(1L)).thenReturn(carrito);

        mockMvc.perform(delete("/api/carrito/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerenteNoPuedeEliminarProductoDelCarrito() throws Exception {
        mockMvc.perform(delete("/api/carrito/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoNoPuedeEliminarProductoDelCarrito() throws Exception {
        mockMvc.perform(delete("/api/carrito/1"))
                .andExpect(status().isForbidden());
    }
}