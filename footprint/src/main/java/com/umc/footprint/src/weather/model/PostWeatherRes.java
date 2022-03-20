package com.umc.footprint.src.weather.model;

import lombok.*;

@Getter
@NoArgsConstructor
public class PostWeatherRes {
    private String temperature;
    private String weather;

    @Builder
    public PostWeatherRes(String temperature, String weather){
        this.temperature = temperature;
        this.weather = weather;
    }
}
