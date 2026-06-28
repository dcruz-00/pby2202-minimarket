package com.minimarket;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.minimarket.entity.Venta;
import com.minimarket.service.VentaService;

@SpringBootTest
@AutoConfigureMockMvc
public class VentaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VentaService ventaService;

    private static final String VENTA_JSON = "{}";

    // GET /api/ventas -> GERENTE y EMPLEADO pueden; CLIENTE 403

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeListarVentas() throws Exception {
        when(ventaService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeListarVentas() throws Exception {
        when(ventaService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeListarVentas() throws Exception {
        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isForbidden());
    }

    // POST /api/ventas -> GERENTE y EMPLEADO pueden; CLIENTE 403

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeCrearVenta() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        when(ventaService.save(org.mockito.ArgumentMatchers.any())).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                .contentType("application/json")
                .content(VENTA_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeCrearVenta() throws Exception {
        Venta venta = new Venta();
        venta.setId(1L);
        when(ventaService.save(org.mockito.ArgumentMatchers.any())).thenReturn(venta);

        mockMvc.perform(post("/api/ventas")
                .contentType("application/json")
                .content(VENTA_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeCrearVenta() throws Exception {
        mockMvc.perform(post("/api/ventas")
                .contentType("application/json")
                .content(VENTA_JSON))
                .andExpect(status().isForbidden());
    }
}