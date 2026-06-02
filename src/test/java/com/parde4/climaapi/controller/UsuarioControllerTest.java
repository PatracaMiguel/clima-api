package com.parde4.climaapi.controller;

import com.parde4.climaapi.exception.GlobalExceptionHandler;
import com.parde4.climaapi.model.Usuario;
import com.parde4.climaapi.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
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

    private String usuarioJson(String nombre, String correo, String contrasena) {
        return """
                {
                    "nombre": "%s",
                    "correo": "%s",
                    "contrasena": "%s"
                }
                """.formatted(nombre, correo, contrasena);
    }

    private Usuario crearUsuario(Integer id, String nombre, String correo, String contrasena) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNombre(nombre);
        usuario.setCorreo(correo);
        usuario.setContrasena(contrasena);
        usuario.setFechaCreado(LocalDateTime.now());
        return usuario;
    }

    @Test
    void usr01CrearUsuarioConDatosValidosDebeRegistrarCorrectamente() throws Exception {
        Usuario usuario = crearUsuario(1, "Miguel", "miguel@gmail.com", "123456");

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", "miguel@gmail.com", "123456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Miguel"))
                .andExpect(jsonPath("$.correo").value("miguel@gmail.com"));
    }

    @Test
    void usr02CrearUsuarioConCorreoDuplicadoDebeRetornarErrorDeNegocio() throws Exception {
        when(usuarioService.crearUsuario(any(Usuario.class)))
                .thenThrow(new RuntimeException("El correo ya está registrado"));

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", "miguel@gmail.com", "123456")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("El correo ya está registrado"));
    }

    @Test
    void usr03CrearUsuarioConNombreVacioDebeRetornarErrorDeValidacion() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("", "miguel@gmail.com", "123456")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes.nombre").value("El nombre es obligatorio"));

        verify(usuarioService, never()).crearUsuario(any(Usuario.class));
    }

    @Test
    void usr04CrearUsuarioConCorreoVacioDebeRetornarErrorDeValidacion() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", "", "123456")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"));

        verify(usuarioService, never()).crearUsuario(any(Usuario.class));
    }

    @Test
    void usr05CrearUsuarioConContrasenaVaciaDebeRetornarErrorDeValidacion() throws Exception {
        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", "miguel@gmail.com", "")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"));

        verify(usuarioService, never()).crearUsuario(any(Usuario.class));
    }

    @Test
    void usr06ObtenerUsuarioConIdExistenteDebeRetornarDatosDelUsuario() throws Exception {
        Usuario usuario = crearUsuario(1, "Miguel", "miguel@gmail.com", "123456");

        when(usuarioService.obtenerUsuario(1)).thenReturn(usuario);

        mockMvc.perform(get("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Miguel"))
                .andExpect(jsonPath("$.correo").value("miguel@gmail.com"));
    }

    @Test
    void usr07ObtenerUsuarioConIdInexistenteDebeRetornarUsuarioNoEncontrado() throws Exception {
        when(usuarioService.obtenerUsuario(99))
                .thenThrow(new RuntimeException("Usuario no encontrado"));

        mockMvc.perform(get("/usuarios/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }

    @Test
    void usr08ActualizarUsuarioConDatosValidosDebeActualizarCorrectamente() throws Exception {
        Usuario usuario = crearUsuario(1, "Miguel Actualizado", "nuevo@gmail.com", "123456");

        when(usuarioService.actualizarUsuario(eq(1), any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel Actualizado", "nuevo@gmail.com", "123456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario actualizado correctamente"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Miguel Actualizado"))
                .andExpect(jsonPath("$.correo").value("nuevo@gmail.com"));
    }

    @Test
    void usr09EliminarUsuarioConIdExistenteDebeEliminarCorrectamente() throws Exception {
        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario eliminado correctamente"));
    }

    @Test
    void usr10EliminarUsuarioConIdInexistenteDebeRetornarUsuarioNoEncontrado() throws Exception {
        org.mockito.Mockito.doThrow(new RuntimeException("Usuario no encontrado"))
                .when(usuarioService).eliminarUsuario(99);

        mockMvc.perform(delete("/usuarios/99"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("Usuario no encontrado"));
    }

    @Test
    void usr11CrearUsuarioConNombreDe46CaracteresDebeRetornarErrorDeValidacion() throws Exception {
        String nombre = "A".repeat(46);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson(nombre, "miguel@gmail.com", "123456")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes.nombre").value("El nombre no puede exceder 45 caracteres"));

        verify(usuarioService, never()).crearUsuario(any(Usuario.class));
    }

    @Test
    void usr12CrearUsuarioConNombreDe45CaracteresDebeRegistrarCorrectamente() throws Exception {
        String nombre = "A".repeat(45);
        Usuario usuario = crearUsuario(1, nombre, "miguel@gmail.com", "123456");

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson(nombre, "miguel@gmail.com", "123456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.nombre").value(nombre));
    }

    @Test
    void usr13CrearUsuarioConContrasenaDe60CaracteresDebeRegistrarCorrectamente() throws Exception {
        String contrasena = "A".repeat(60);
        Usuario usuario = crearUsuario(1, "Miguel", "miguel@gmail.com", contrasena);

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", "miguel@gmail.com", contrasena)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"));
    }

    @Test
    void usr14CrearUsuarioConContrasenaDe61CaracteresDebeRetornarErrorDeValidacion() throws Exception {
        String contrasena = "A".repeat(61);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", "miguel@gmail.com", contrasena)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes.contrasena").value("La contraseña debe tener entre 6 y 60 caracteres"));

        verify(usuarioService, never()).crearUsuario(any(Usuario.class));
    }

    @Test
    void usr15CrearUsuarioConContrasenaDe6CaracteresDebeRegistrarCorrectamente() throws Exception {
        String contrasena = "123456";
        Usuario usuario = crearUsuario(1, "Miguel", "miguel@gmail.com", contrasena);

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", "miguel@gmail.com", contrasena)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"));
    }

    @Test
    void usr16CrearUsuarioConContrasenaDe5CaracteresDebeRetornarErrorDeValidacion() throws Exception {
        String contrasena = "12345";

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", "miguel@gmail.com", contrasena)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes.contrasena").value("La contraseña debe tener entre 6 y 60 caracteres"));

        verify(usuarioService, never()).crearUsuario(any(Usuario.class));
    }

    @Test
    void usr17ActualizarUsuarioConCorreoDuplicadoDebeRetornarErrorDeNegocio() throws Exception {
        when(usuarioService.actualizarUsuario(eq(1), any(Usuario.class)))
                .thenThrow(new RuntimeException("El correo ya está registrado"));

        mockMvc.perform(put("/usuarios/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", "existente@gmail.com", "123456")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("El correo ya está registrado"));
    }

    @Test
    void usr18CrearUsuarioConCorreoDe45CaracteresDebeRegistrarCorrectamente() throws Exception {
        String correo = "a".repeat(35) + "@gmail.com";
        Usuario usuario = crearUsuario(1, "Miguel", correo, "123456");

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", correo, "123456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.correo").value(correo));
    }

    @Test
    void usr19CrearUsuarioConCorreoDe46CaracteresDebeRetornarErrorDeValidacion() throws Exception {
        String correo = "a".repeat(36) + "@gmail.com";

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", correo, "123456")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes.correo").value("El correo no puede exceder 45 caracteres"));

        verify(usuarioService, never()).crearUsuario(any(Usuario.class));
    }

    @Test
    void usr20CrearUsuarioConCorreoDe6CaracteresDebeRegistrarCorrectamente() throws Exception {
        String correo = "a@b.co";
        Usuario usuario = crearUsuario(1, "Miguel", correo, "123456");

        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(usuario);

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", correo, "123456")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario registrado correctamente"))
                .andExpect(jsonPath("$.correo").value(correo));
    }

    @Test
    void usr21CrearUsuarioConCorreoDe5CaracteresDebeRetornarErrorDeValidacion() throws Exception {
        String correo = "a@b.c";

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(usuarioJson("Miguel", correo, "123456")))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes.correo").value("El correo debe tener entre 6 y 45 caracteres"));

        verify(usuarioService, never()).crearUsuario(any(Usuario.class));
    }
}