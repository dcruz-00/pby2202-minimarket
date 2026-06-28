package com.minimarket;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.minimarket.entity.Producto;
import com.minimarket.service.ProductoService;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    private static final String PRODUCTO_JSON =
            "{\"nombre\":\"Coca-Cola\",\"precio\":1500.0,\"stock\":10}";

    // GET /api/productos -> GERENTE, EMPLEADO, CLIENTE permitidos; sin auth bloqueado

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeListarProductos() throws Exception {
        when(productoService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeListarProductos() throws Exception {
        when(productoService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clientePuedeListarProductos() throws Exception {
        when(productoService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isOk());
    }

    @Test
    void sinAutenticacionNoPuedeListarProductos() throws Exception {
        mockMvc.perform(get("/api/productos"))
                .andExpect(status().isForbidden());
    }

    // POST /api/productos -> solo GERENTE; EMPLEADO y CLIENTE 403

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeCrearProducto() throws Exception {
        Producto producto = new Producto();
        producto.setId(1L);
        producto.setNombre("Coca-Cola");
        producto.setPrecio(1500.0);
        producto.setStock(10);

        when(productoService.save(org.mockito.ArgumentMatchers.any())).thenReturn(producto);

        mockMvc.perform(post("/api/productos")
                .contentType("application/json")
                .content(PRODUCTO_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoNoPuedeCrearProducto() throws Exception {
        mockMvc.perform(post("/api/productos")
                .contentType("application/json")
                .content(PRODUCTO_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeCrearProducto() throws Exception {
        mockMvc.perform(post("/api/productos")
                .contentType("application/json")
                .content(PRODUCTO_JSON))
                .andExpect(status().isForbidden());
    }

    // PUT /api/productos/{id} -> GERENTE y EMPLEADO pueden; CLIENTE 403

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeActualizarProducto() throws Exception {
        Producto existente = new Producto();
        existente.setId(1L);
        when(productoService.findById(1L)).thenReturn(existente);
        when(productoService.save(org.mockito.ArgumentMatchers.any())).thenReturn(existente);

        mockMvc.perform(put("/api/productos/1")
                .contentType("application/json")
                .content(PRODUCTO_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeActualizarProducto() throws Exception {
        Producto existente = new Producto();
        existente.setId(1L);
        when(productoService.findById(1L)).thenReturn(existente);
        when(productoService.save(org.mockito.ArgumentMatchers.any())).thenReturn(existente);

        mockMvc.perform(put("/api/productos/1")
                .contentType("application/json")
                .content(PRODUCTO_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeActualizarProducto() throws Exception {
        mockMvc.perform(put("/api/productos/1")
                .contentType("application/json")
                .content(PRODUCTO_JSON))
                .andExpect(status().isForbidden());
    }

    // DELETE /api/productos/{id} -> solo GERENTE; EMPLEADO y CLIENTE 403

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeEliminarProducto() throws Exception {
        Producto existente = new Producto();
        existente.setId(1L);
        when(productoService.findById(1L)).thenReturn(existente);

        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoNoPuedeEliminarProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeEliminarProducto() throws Exception {
        mockMvc.perform(delete("/api/productos/1"))
                .andExpect(status().isForbidden());
    }
}