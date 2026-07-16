package com.minimarket;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.minimarket.entity.Inventario;
import com.minimarket.entity.Producto;
import com.minimarket.repository.ProductoRepository;
import com.minimarket.service.InventarioService;

@SpringBootTest
@AutoConfigureMockMvc
public class InventarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private InventarioService inventarioService;

    @MockBean
    private ProductoRepository productoRepository;

    @MockBean
    private DataInitializer dataInitializer;    

    // GET /api/inventario

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeListarInventario() throws Exception {
        when(inventarioService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeListarInventario() throws Exception {
        when(inventarioService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeListarInventario() throws Exception {
        mockMvc.perform(get("/api/inventario"))
                .andExpect(status().isForbidden());
    }

    // GET /api/inventario/{id}

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeObtenerInventarioPorId() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        when(inventarioService.findById(1L)).thenReturn(inventario);
        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeObtenerInventarioPorId() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        when(inventarioService.findById(1L)).thenReturn(inventario);
        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeObtenerInventarioPorId() throws Exception {
        mockMvc.perform(get("/api/inventario/1"))
                .andExpect(status().isForbidden());
    }

    // POST /api/inventario

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeRegistrarMovimiento() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioService.save(any(Inventario.class))).thenAnswer(inv -> {
            Inventario saved = inv.getArgument(0);
            saved.setId(1L);
            saved.setFechaMovimiento(new Date());
            return saved;
        });

        mockMvc.perform(post("/api/inventario")
                .contentType("application/json")
                .content("{\"productoId\":1,\"cantidad\":10,\"tipoMovimiento\":\"Entrada\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeRegistrarMovimiento() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioService.save(any(Inventario.class))).thenAnswer(inv -> {
            Inventario saved = inv.getArgument(0);
            saved.setId(2L);
            saved.setFechaMovimiento(new Date());
            return saved;
        });

        mockMvc.perform(post("/api/inventario")
                .contentType("application/json")
                .content("{\"productoId\":1,\"cantidad\":5,\"tipoMovimiento\":\"Salida\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeRegistrarMovimiento() throws Exception {
        mockMvc.perform(post("/api/inventario")
                .contentType("application/json")
                .content("{\"productoId\":1,\"cantidad\":10,\"tipoMovimiento\":\"Entrada\"}"))
                .andExpect(status().isForbidden());
    }

    // PUT /api/inventario/{id}

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeActualizarMovimiento() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);

        Inventario existente = new Inventario();
        existente.setId(1L);

        when(inventarioService.findById(1L)).thenReturn(existente);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioService.save(any(Inventario.class))).thenReturn(existente);

        mockMvc.perform(put("/api/inventario/1")
                .contentType("application/json")
                .content("{\"productoId\":1,\"cantidad\":20,\"tipoMovimiento\":\"Entrada\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeActualizarMovimiento() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);

        Inventario existente = new Inventario();
        existente.setId(1L);

        when(inventarioService.findById(1L)).thenReturn(existente);
        when(productoRepository.findById(1L)).thenReturn(Optional.of(producto));
        when(inventarioService.save(any(Inventario.class))).thenReturn(existente);

        mockMvc.perform(put("/api/inventario/1")
                .contentType("application/json")
                .content("{\"productoId\":1,\"cantidad\":8,\"tipoMovimiento\":\"Salida\"}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeActualizarMovimiento() throws Exception {
        mockMvc.perform(put("/api/inventario/1")
                .contentType("application/json")
                .content("{\"productoId\":1,\"cantidad\":20,\"tipoMovimiento\":\"Entrada\"}"))
                .andExpect(status().isForbidden());
    }

    // DELETE /api/inventario/{id}

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeEliminarMovimiento() throws Exception {
        Inventario inventario = new Inventario();
        inventario.setId(1L);
        when(inventarioService.findById(1L)).thenReturn(inventario);

        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoNoPuedeEliminarMovimiento() throws Exception {
        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeEliminarMovimiento() throws Exception {
        mockMvc.perform(delete("/api/inventario/1"))
                .andExpect(status().isForbidden());
    }
}