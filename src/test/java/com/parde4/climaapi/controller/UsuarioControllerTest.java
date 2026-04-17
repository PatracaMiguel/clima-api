package com.parde4.climaapi.controller;

import com.parde4.climaapi.exception.GlobalExceptionHandler;
import com.parde4.climaapi.model.Usuario;
import com.parde4.climaapi.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.time.LocalDateTime;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UsuarioController.class)
@Import(GlobalExceptionHandler.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Debe crear usuario")
    void crearUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Miguel");
        usuario.setCorreo("miguel@gmail.com");
        usuario.setContrasena("123456");
        usuario.setFechaCreado(LocalDateTime.now());

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        String body = """
        {
          "nombre": "Miguel",
          "correo": "miguel@gmail.com",
          "contrasena": "123456"
        }
        """;

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Miguel"))
                .andExpect(jsonPath("$.correo").value("miguel@gmail.com"));
    }

    @Test
    @DisplayName("Debe obtener usuario por id")
    void obtenerUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Miguel");
        usuario.setCorreo("miguel@gmail.com");
        usuario.setFechaCreado(LocalDateTime.now());

        when(usuarioService.obtenerUsuario(1L)).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Miguel"))
                .andExpect(jsonPath("$.correo").value("miguel@gmail.com"));
    }

    @Test
    @DisplayName("Debe actualizar usuario")
    void actualizarUsuario() throws Exception {
        Usuario usuario = new Usuario();
        usuario.setId(1L);
        usuario.setNombre("Actualizado");
        usuario.setCorreo("nuevo@gmail.com");
        usuario.setFechaCreado(LocalDateTime.now());

        when(usuarioService.actualizarUsuario(eq(1L), any(Usuario.class))).thenReturn(usuario);

        String body = """
        {
          "nombre": "Actualizado",
          "correo": "nuevo@gmail.com",
          "contrasena": "123456"
        }
        """;

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Actualizado"))
                .andExpect(jsonPath("$.correo").value("nuevo@gmail.com"));
    }
}