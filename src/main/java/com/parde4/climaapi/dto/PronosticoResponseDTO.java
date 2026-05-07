package com.parde4.climaapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class PronosticoResponseDTO {
    private WeatherResponseDTO actual;
    private List<ForecastSlotDTO> pronostico;
}
