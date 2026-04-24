package com.parde4.climaapi.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.parde4.climaapi.exception.GlobalExceptionHandler;
import com.parde4.climaapi.model.Favorito;
import com.parde4.climaapi.service.FavoritoService;

@WebMvcTest(FavoritoController.class)
@Import(GlobalExceptionHandler.class)
class FavoritoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoritoService favoritoService;

    @Test
    @DisplayName("Debe crear favorito")
    void crearFavorito() throws Exception {
        Favorito favorito = new Favorito();
        favorito.setId(1L);
        favorito.setCiudad("Coatzacoalcos");
        favorito.setPais("Mexico");
        favorito.setFechaAgregado(LocalDateTime.now());
        favorito.setUsuarioId(1L);

        when(favoritoService.guardar(any(Favorito.class))).thenReturn(favorito);

        String body = """
        {
          "ciudad": "Coatzacoalcos",
          "pais": "Mexico",
          "usuarioId": 1
        }
        """;

        mockMvc.perform(post("/favoritos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ciudad").value("Coatzacoalcos"))
                .andExpect(jsonPath("$.pais").value("Mexico"))
                .andExpect(jsonPath("$.usuarioId").value(1));
    }

    @Test
    @DisplayName("Debe listar favoritos por usuario")
    void listarFavoritosPorUsuario() throws Exception {
        Favorito favorito = new Favorito();
        favorito.setId(1L);
        favorito.setCiudad("Coatzacoalcos");
        favorito.setPais("Mexico");
        favorito.setFechaAgregado(LocalDateTime.now());
        favorito.setUsuarioId(1L);

        when(favoritoService.listarPorUsuario(1L)).thenReturn(List.of(favorito));

        mockMvc.perform(get("/favoritos/usuario/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ciudad").value("Coatzacoalcos"))
                .andExpect(jsonPath("$[0].pais").value("Mexico"))
                .andExpect(jsonPath("$[0].usuarioId").value(1));
    }

    @Test
    @DisplayName("Debe eliminar favorito")
    void eliminarFavorito() throws Exception {
        doNothing().when(favoritoService).eliminar(1L);

        mockMvc.perform(delete("/favoritos/1"))
                .andExpect(status().isOk());
    }
}