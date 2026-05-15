package com.parde4.climaapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioRequestDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    @Email(message = "El correo no es válido")
    @Size(min = 5 , max = 45 , message = "El correo debe tener entre 5 y 45 caracteres")
    private String correo;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6 , max = 60 , message = "La contraseña debe tener entre 6 y 60 caracteres")
    private String contrasena;
}
