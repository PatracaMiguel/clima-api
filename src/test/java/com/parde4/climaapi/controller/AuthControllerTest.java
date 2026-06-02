package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.LoginRequest;
import com.parde4.climaapi.dto.LoginResponse;
import com.parde4.climaapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void auth01LoginConCorreoYContrasenaValidosDebeRetornarInicioExitoso() throws Exception {
        String requestBody = """
                {
                    "correo": "miguel@gmail.com",
                    "contrasena": "123456"
                }
                """;

        LoginResponse response = new LoginResponse(
                "Inicio de sesión exitoso",
                1,
                "Miguel",
                "miguel@gmail.com"
        );

        when(authService.login(any(LoginRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Inicio de sesión exitoso"))
                .andExpect(jsonPath("$.idUsuario").value(1))
                .andExpect(jsonPath("$.nombre").value("Miguel"))
                .andExpect(jsonPath("$.correo").value("miguel@gmail.com"));
    }

    @Test
    void auth02LoginConCorreoNoRegistradoDebeRetornarErrorDeNegocio() throws Exception {
        String requestBody = """
                {
                    "correo": "noexiste@gmail.com",
                    "contrasena": "123456"
                }
                """;

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("El correo no está registrado"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("El correo no está registrado"));
    }

    @Test
    void auth03LoginConContrasenaIncorrectaDebeRetornarErrorDeNegocio() throws Exception {
        String requestBody = """
                {
                    "correo": "miguel@gmail.com",
                    "contrasena": "incorrecta"
                }
                """;

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("La contraseña es incorrecta"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("La contraseña es incorrecta"));
    }

    @Test
    void auth04LoginConCorreoVacioDebeRetornarErrorDeValidacion() throws Exception {
        String requestBody = """
                {
                    "correo": "",
                    "contrasena": "123456"
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes.correo").value("El correo es obligatorio"));

        verify(authService, never()).login(any(LoginRequest.class));
    }

    @Test
    void auth05LoginConContrasenaVaciaDebeRetornarErrorDeValidacion() throws Exception {
        String requestBody = """
                {
                    "correo": "miguel@gmail.com",
                    "contrasena": ""
                }
                """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Error de validación"))
                .andExpect(jsonPath("$.mensajes.contrasena").value("La contraseña es obligatoria"));

        verify(authService, never()).login(any(LoginRequest.class));
    }
}