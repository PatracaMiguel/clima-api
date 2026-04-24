package com.parde4.climaapi.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class FavoritoResponseDTO {
    private Long id;
    private String ciudad;
    private String pais;
    private LocalDateTime fechaAgregado;
    private Long usuarioId;

}