package com.parde4.climaapi.dto;

import lombok.Data;
import java.util.List;

@Data
public class ForecastApiResponseDTO {

    private List<ForecastItem> list;

    @Data
    public static class ForecastItem {
        private MainData main;
        private List<Weather> weather;
        private String dt_txt;

        @Data
        public static class MainData {
            private double temp;
            private double feels_like;
        }

        @Data
        public static class Weather {
            private String description;
        }
    }
}
