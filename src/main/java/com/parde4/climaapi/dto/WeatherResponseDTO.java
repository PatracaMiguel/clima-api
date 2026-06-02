package com.parde4.climaapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class WeatherResponseDTO {
    
    private String name;
    private MainData main;
    private List<Weather> weather;
    private Sys sys;
    private Wind wind;

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

    @Data
    public static class Sys {
        private String country;
    }

    @Data
    public static class Wind {
        private double speed;
    }
}
