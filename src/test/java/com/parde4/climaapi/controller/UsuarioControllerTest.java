package com.parde4.climaapi.controller;

import com.parde4.climaapi.exception.GlobalExceptionHandler;
import com.parde4.climaapi.model.Usuario;
import com.parde4.climaapi.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
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
    @DisplayName("USR-01 - Debe registrar usuario con datos válidos")
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
    @DisplayName("USR-02 - Debe retornar error cuando el correo está duplicado")
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
    @DisplayName("USR-03 - Debe retornar error de validación cuando el nombre está vacío")
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
    @DisplayName("USR-04 - Debe retornar error de validación cuando el correo está vacío")
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
    @DisplayName("USR-05 - Debe retornar error de validación cuando la contraseña está vacía")
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
    @DisplayName("USR-06 - Debe obtener usuario por id existente")
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
    @DisplayName("USR-07 - Debe retornar usuario no encontrado cuando el id no existe")
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
    @DisplayName("USR-08 - Debe actualizar usuario con datos válidos")
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
    @DisplayName("USR-09 - Debe eliminar usuario con id existente")
    void usr09EliminarUsuarioConIdExistenteDebeEliminarCorrectamente() throws Exception {
        mockMvc.perform(delete("/usuarios/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Usuario eliminado correctamente"));
    }

    @Test
    @DisplayName("USR-10 - Debe retornar usuario no encontrado al eliminar id inexistente")
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
    @DisplayName("USR-11 - Debe validar que el nombre no exceda 45 caracteres")
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
    @DisplayName("USR-12 - Debe permitir nombre de 45 caracteres")
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
    @DisplayName("USR-13 - Debe permitir contraseña de 60 caracteres")
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
    @DisplayName("USR-14 - Debe validar contraseña de 61 caracteres")
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
    @DisplayName("USR-15 - Debe permitir contraseña de 6 caracteres")
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
    @DisplayName("USR-16 - Debe validar contraseña de 5 caracteres")
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
    @DisplayName("USR-17 - Debe retornar error al actualizar con correo duplicado")
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
    @DisplayName("USR-18 - Debe permitir correo de 45 caracteres")
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
    @DisplayName("USR-19 - Debe validar correo de 46 caracteres")
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
    @DisplayName("USR-20 - Debe permitir correo de 6 caracteres")
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
    @DisplayName("USR-21 - Debe validar correo de 5 caracteres")
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