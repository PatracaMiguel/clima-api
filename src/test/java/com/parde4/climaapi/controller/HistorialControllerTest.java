package com.parde4.climaapi.controller;

import com.parde4.climaapi.exception.GlobalExceptionHandler;
import com.parde4.climaapi.model.Historial;
import com.parde4.climaapi.service.HistorialService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HistorialController.class)
@Import(GlobalExceptionHandler.class)
class HistorialControllerTest {

    private static final Integer USUARIO_ID = 1;
    private static final String MENSAJE_SESION_REQUERIDA = "Debe iniciar sesi\u00f3n";
    private static final String MENSAJE_ELIMINAR_REQUIERE_SESION =
            "Debe iniciar sesi\u00f3n antes de eliminar el historial";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HistorialService historialService;

    @Test
    @DisplayName("HIS-01 - GET /historial con usuario autenticado")
    void obtenerHistorial_UsuarioAutenticado_RetornaHistorialDelUsuario() throws Exception {
        Historial historial = crearHistorial(1, "Veracruz", 25.0, USUARIO_ID);

        when(historialService.obtenerPorUsuario(USUARIO_ID)).thenReturn(List.of(historial));

        mockMvc.perform(get("/historial").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ciudad").value("Veracruz"))
                .andExpect(jsonPath("$[0].temperatura").value(25.0))
                .andExpect(jsonPath("$[0].usuarioId").value(USUARIO_ID));
    }

    @Test
    @DisplayName("HIS-02 - GET /historial sin usuario autenticado")
    void obtenerHistorial_SinSesion_DebeIniciarSesion() throws Exception {
        mockMvc.perform(get("/historial"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_SESION_REQUERIDA));
    }

    @Test
    @DisplayName("HIS-03 - GET /historial con usuario sin consultas")
    void obtenerHistorial_UsuarioSinConsultas_RetornaHistorialVacio() throws Exception {
        when(historialService.obtenerPorUsuario(USUARIO_ID)).thenReturn(List.of());

        mockMvc.perform(get("/historial").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("HIS-04 - DELETE /historial con usuario autenticado")
    void eliminarHistorial_UsuarioAutenticado_RetornaHistorialEliminadoCorrectamente() throws Exception {
        mockMvc.perform(delete("/historial").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Historial eliminado correctamente"));

        verify(historialService).eliminarHistorialPorUsuario(USUARIO_ID);
    }

    @Test
    @DisplayName("HIS-05 - DELETE /historial sin usuario autenticado")
    void eliminarHistorial_SinSesion_DebeIniciarSesionAntesDeEliminar() throws Exception {
        mockMvc.perform(delete("/historial"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_ELIMINAR_REQUIERE_SESION));
    }

    private Historial crearHistorial(Integer id, String ciudad, Double temperatura, Integer usuarioId) {
        Historial historial = new Historial();
        historial.setId(id);
        historial.setCiudad(ciudad);
        historial.setTemperatura(temperatura);
        historial.setUsuarioId(usuarioId);
        historial.setFechaConsulta(LocalDateTime.now());
        return historial;
    }
}
