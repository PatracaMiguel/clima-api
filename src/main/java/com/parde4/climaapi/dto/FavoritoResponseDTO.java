package com.parde4.climaapi.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FavoritoResponseDTO {
    private Integer id;
    private String ciudad;
    private String pais;
    private LocalDateTime fechaAgregado;
    private Integer usuarioId;
    private String mensaje;

}