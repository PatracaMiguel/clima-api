package com.parde4.climaapi.dto;

import lombok.Data;

@Data
public class ForecastSlotDTO {
    private String hora;
    private double temp;
    private double feels_like;
    private String descripcion;
}
