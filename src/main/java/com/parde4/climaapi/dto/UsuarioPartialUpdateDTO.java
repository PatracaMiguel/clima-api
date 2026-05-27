package com.parde4.climaapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UsuarioPartialUpdateDTO {

    @Size(max = 45, message = "El nombre no puede exceder 45 caracteres")
    private String nombre;

    @Email(message = "El correo no es valido")
    @Size(min = 6, message = "El correo debe tener entre 6 y 45 caracteres")
    @Size(max = 45, message = "El correo no puede exceder 45 caracteres")
    private String correo;

    @Size(min = 6, max = 60, message = "La contrasena debe tener entre 6 y 60 caracteres")
    private String contrasena;
}
