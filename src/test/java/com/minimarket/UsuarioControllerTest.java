package com.minimarket;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

import com.minimarket.entity.Usuario;
import com.minimarket.service.UsuarioService;

@SpringBootTest
@AutoConfigureMockMvc
public class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UsuarioService usuarioService;

    // GET /api/usuarios

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeListarUsuarios() throws Exception {
        when(usuarioService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoPuedeListarUsuarios() throws Exception {
        when(usuarioService.findAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeListarUsuarios() throws Exception {
        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isForbidden());
    }

    // POST /api/usuarios

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeCrearUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setUsername("nuevo");
        usuario.setPassword("123456");

        when(usuarioService.save(org.mockito.ArgumentMatchers.any())).thenReturn(usuario);

        mockMvc.perform(post("/api/usuarios")
                .contentType("application/json")
                .content("{\"username\":\"nuevo\",\"password\":\"123456\",\"roles\":[]}"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoNoPuedeCrearUsuario() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                .contentType("application/json")
                .content("{\"username\":\"nuevo\",\"password\":\"123456\",\"roles\":[]}"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeCrearUsuario() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                .contentType("application/json")
                .content("{\"username\":\"nuevo\",\"password\":\"123456\",\"roles\":[]}"))
                .andExpect(status().isForbidden());
    }

    // DELETE /api/usuarios/{id}

    @Test
    @WithMockUser(roles = "GERENTE")
    void gerentePuedeEliminarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        when(usuarioService.findById(1L)).thenReturn(Optional.of(usuario));

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "EMPLEADO")
    void empleadoNoPuedeEliminarUsuario() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "CLIENTE")
    void clienteNoPuedeEliminarUsuario() throws Exception {
        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isForbidden());
    }
}