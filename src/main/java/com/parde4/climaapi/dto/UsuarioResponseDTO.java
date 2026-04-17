package com.parde4.climaapi.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponseDTO {

    private Long id;
    private String nombre;
    private String correo;
    private LocalDateTime fechaCreado;
}
