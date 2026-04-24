package com.parde4.climaapi.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "favorito")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class Favorito{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfavorito" )
    private Long id;

    @Column(name = "ciudad", nullable = false, length = 45)
    private String ciudad;

    @Column(name = "pais", nullable = false, length = 45)
    private String pais;

    @Column(name = "fecha_agregado")
    private LocalDateTime fechaAgregado;

    @Column(name = "usuario_idusuario")
    private Long usuarioId;

}
