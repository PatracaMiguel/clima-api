package com.parde4.climaapi.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FavoritoRequestDTO {

    @NotBlank(message = "La ciudad es obligatoria")
    private String ciudad;

    @NotBlank(message = "El país es obligatorio")
    private String pais;

}
