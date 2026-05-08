package com.parde4.climaapi.dto;
import lombok.Data;

@Data
public class RecomendacionResponseDTO {
  private String ciudad;
  private Double temperatura;
  private String clima;
  private String descripcion;
  private String recomendacionRopa;
  private String recomendacionAccesorios;
  private String mensaje;
}
