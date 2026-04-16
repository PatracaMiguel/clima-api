package com.parde4.climaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idusuario", nullable = false)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 45)
    private String nombre;

    @Column(name = "correo", nullable = false, length = 45)
    private String correo;

    @Column(name = "contrasena", nullable = false, length = 60)
    private String contrasena;

    @Column(name = "fecha_creado")
    private LocalDateTime fechaCreado;
}