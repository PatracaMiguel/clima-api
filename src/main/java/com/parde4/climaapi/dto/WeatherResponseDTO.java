package com.parde4.climaapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class WeatherResponseDTO {
    
    private MainData main;
    private List<Weather> weather;

    @Data
    public static class MainData {
        private double temp;       
        private double feels_like; 
    }

    @Data
    public static class Weather {
        private String main;        
        private String description; 
    }
}