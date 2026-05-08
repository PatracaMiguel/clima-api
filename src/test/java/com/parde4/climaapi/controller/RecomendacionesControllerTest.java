package com.parde4.climaapi.controller;

import com.parde4.climaapi.dto.RecomendacionResponseDTO;
import com.parde4.climaapi.exception.GlobalExceptionHandler;
import com.parde4.climaapi.service.RecomendacionService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecomendacionController.class)
@Import(GlobalExceptionHandler.class)
class RecomendacionesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private RecomendacionService recomendacionService;

    @Test
    @DisplayName("Debe obtener recomendaciones para clima despejado")
    void obtenerRecomendacionesClear() throws Exception {

        RecomendacionResponseDTO response = new RecomendacionResponseDTO();

        response.setCiudad("Cancun");
        response.setTemperatura(32.0);
        response.setClima("Clear");
        response.setDescripcion("clear sky");

        response.setRecomendacionRopa(
                "Se recomienda usar ropa ligera, fresca y de colores claros como  playera de algodón, short o pantalón ligero."
        );

        response.setRecomendacionAccesorios(
                "Lleve lentes de sol, gorra o sombrero, bloqueador solar y botella de agua."
        );

        response.setMensaje(
                "El cielo está despejado , aproveche el buen clima, pero protégase del sol. " +
                "El clima está caluroso usa ropa fresca, lentes de sol y toma agua durante el día."
        );

        when(recomendacionService.recomendarClimaCiudad("Cancun"))
                .thenReturn(response);

        mockMvc.perform(get("/recomendaciones/Cancun")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ciudad").value("Cancun"))
                .andExpect(jsonPath("$.temperatura").value(32.0))
                .andExpect(jsonPath("$.clima").value("Clear"))
                .andExpect(jsonPath("$.descripcion").value("clear sky"))
                .andExpect(jsonPath("$.recomendacionRopa").exists())
                .andExpect(jsonPath("$.recomendacionAccesorios").exists())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("Debe obtener recomendaciones para clima lluvioso")
    void obtenerRecomendacionesRain() throws Exception {

        RecomendacionResponseDTO response = new RecomendacionResponseDTO();

        response.setCiudad("Londres");
        response.setTemperatura(18.0);
        response.setClima("Rain");
        response.setDescripcion("light rain");

        response.setRecomendacionRopa(
                "Se recomienda usar impermeable o chamarra resistente al agua, pantalón de secado rápido y zapatos con suela antideslizante"
        );

        response.setRecomendacionAccesorios(
                "Lleve sombrilla, mochila impermeabl, cubrezapatos y una bolsa plástica para proteger celular o documentos si lleva alguno"
        );

        response.setMensaje(
                "Está lloviendo afuera , salga con tiempo y evita zonas encharcadas  " +
                "El clima es fresco usa ropa cómoda, una chamarra ligera puede ser útil si estarás fuera por la tarde."
        );

        when(recomendacionService.recomendarClimaCiudad("Londres"))
                .thenReturn(response);

        mockMvc.perform(get("/recomendaciones/Londres")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ciudad").value("Londres"))
                .andExpect(jsonPath("$.clima").value("Rain"))
                .andExpect(jsonPath("$.descripcion").value("light rain"))
                .andExpect(jsonPath("$.recomendacionRopa").exists())
                .andExpect(jsonPath("$.recomendacionAccesorios").exists())
                .andExpect(jsonPath("$.mensaje").exists());
    }
}