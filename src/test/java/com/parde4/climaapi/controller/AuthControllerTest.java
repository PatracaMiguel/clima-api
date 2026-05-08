package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.LoginRequest;
import com.parde4.climaapi.dto.LoginResponse;
import com.parde4.climaapi.service.AuthService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void loginExitosoDebeRetornar200() throws Exception {
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

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

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
    void loginConCorreoIncorrectoDebeRetornarError() throws Exception {
                                String requestBody = """
                                                                {
                                                                        "correo": "incorrecto@gmail.com",
                                                                        "contrasena": "123456"
                                                                }
                                                                """;

        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new RuntimeException("El correo no está registrado"));

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("El correo no está registrado"));
    }

    @Test
    void loginConContrasenaIncorrectaDebeRetornarError() throws Exception {
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
                .andExpect(jsonPath("$.error").value("Error de negocio"))
                .andExpect(jsonPath("$.mensaje").value("La contraseña es incorrecta"));
    }
}