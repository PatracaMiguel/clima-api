package com.parde4.climaapi.controller;

import com.parde4.climaapi.exception.GlobalExceptionHandler;
import com.parde4.climaapi.model.Favorito;
import com.parde4.climaapi.service.FavoritoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FavoritoController.class)
@Import(GlobalExceptionHandler.class)
class FavoritoControllerTest {

    private static final int USUARIO_ID = 1;
    private static final String MENSAJE_FAVORITO_GUARDADO = "Favorito guardado correctamente";
    private static final String MENSAJE_GUARDAR_REQUIERE_SESION = "Debe iniciar sesi\u00f3n antes de guardar favoritos";
    private static final String MENSAJE_VER_REQUIERE_SESION = "Debe iniciar sesi\u00f3n antes de ver favoritos";
    private static final String MENSAJE_CIUDAD_NO_ENCONTRADA =
            "No se encontr\u00f3 informaci\u00f3n clim\u00e1tica para la ciudad ingresada , cheque bien el nombre e intente nuevamente.";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FavoritoService favoritoService;

    @Test
    void guardar_DatosValidosConUsuarioAutenticado_RetornaFavoritoGuardado() throws Exception {
        Favorito favorito = crearFavorito(1, "Coatzacoalcos", "Mexico", USUARIO_ID);

        when(favoritoService.guardarFavorito(any(Favorito.class))).thenReturn(favorito);

        mockMvc.perform(post("/favoritos")
                        .sessionAttr("usuarioId", USUARIO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "ciudad": "Coatzacoalcos",
                          "pais": "Mexico"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_FAVORITO_GUARDADO))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.ciudad").value("Coatzacoalcos"))
                .andExpect(jsonPath("$.pais").value("Mexico"))
                .andExpect(jsonPath("$.usuarioId").value(USUARIO_ID));

        verify(favoritoService).guardarFavorito(any(Favorito.class));
    }

    @Test
    void guardar_DatosValidosSinSesion_DebeIniciarSesion() throws Exception {
        mockMvc.perform(post("/favoritos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "ciudad": "Coatzacoalcos",
                          "pais": "Mexico"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_GUARDAR_REQUIERE_SESION));
    }

    @Test
    void guardar_CiudadInexistente_RetornaMensajeDeCiudadNoEncontrada() throws Exception {
        when(favoritoService.guardarFavorito(any(Favorito.class)))
                .thenThrow(ciudadNoEncontrada());

        mockMvc.perform(post("/favoritos")
                        .sessionAttr("usuarioId", USUARIO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "ciudad": "CiudadInexistente",
                          "pais": "Mexico"
                        }
                        """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_CIUDAD_NO_ENCONTRADA));
    }

    @Test
    void guardar_CiudadVacia_RetornaCiudadObligatoria() throws Exception {
        mockMvc.perform(post("/favoritos")
                        .sessionAttr("usuarioId", USUARIO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "ciudad": "",
                          "pais": "Mexico"
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensajes.ciudad").value("La ciudad es obligatoria"));
    }

    @Test
    void guardar_PaisVacio_RetornaPaisObligatorio() throws Exception {
        mockMvc.perform(post("/favoritos")
                        .sessionAttr("usuarioId", USUARIO_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                          "ciudad": "Coatzacoalcos",
                          "pais": ""
                        }
                        """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensajes.pais").value("El pa\u00eds es obligatorio"));
    }

    @Test
    void listar_UsuarioAutenticado_RetornaFavoritosDelUsuario() throws Exception {
        Favorito favorito = crearFavorito(1, "Coatzacoalcos", "Mexico", USUARIO_ID);

        when(favoritoService.listarPorUsuario(USUARIO_ID)).thenReturn(List.of(favorito));

        mockMvc.perform(get("/favoritos").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].ciudad").value("Coatzacoalcos"))
                .andExpect(jsonPath("$[0].pais").value("Mexico"))
                .andExpect(jsonPath("$[0].usuarioId").value(USUARIO_ID));
    }

    @Test
    void listar_SinSesion_DebeIniciarSesion() throws Exception {
        mockMvc.perform(get("/favoritos"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value(MENSAJE_VER_REQUIERE_SESION));
    }

    @Test
    void eliminar_IdValido_RetornaFavoritoEliminado() throws Exception {
        mockMvc.perform(delete("/favoritos/1").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.mensaje").value("Favorito eliminado correctamente"));

        verify(favoritoService).eliminarFavorito(1, USUARIO_ID);
    }

    @Test
    void eliminar_FavoritoDeOtroUsuario_RetornaErrorDeNegocio() throws Exception {
        doThrow(new RuntimeException("Error de negocio"))
                .when(favoritoService).eliminarFavorito(1, USUARIO_ID);

        mockMvc.perform(delete("/favoritos/1").sessionAttr("usuarioId", USUARIO_ID))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Error de negocio"));
    }

    private Favorito crearFavorito(Integer id, String ciudad, String pais, Integer usuarioId) {
        Favorito favorito = new Favorito();
        favorito.setId(id);
        favorito.setCiudad(ciudad);
        favorito.setPais(pais);
        favorito.setUsuarioId(usuarioId);
        favorito.setFechaAgregado(LocalDateTime.now());
        return favorito;
    }

    private HttpClientErrorException ciudadNoEncontrada() {
        return HttpClientErrorException.create(
                HttpStatus.NOT_FOUND,
                "Not Found",
                HttpHeaders.EMPTY,
                new byte[0],
                StandardCharsets.UTF_8);
    }
}