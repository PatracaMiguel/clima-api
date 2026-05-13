package com.parde4.climaapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Historial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idhistorial")
    private Integer id;

    @Column(name = "ciudad", nullable = false, length = 45)
    private String ciudad;

    @Column(name = "temperatura", nullable = false)
    private Double temperatura;

    @Column(name = "fecha_consulta")
    private LocalDateTime fechaConsulta;

    @Column(name = "usuario_idusuario")
    private Integer usuarioId;
}
