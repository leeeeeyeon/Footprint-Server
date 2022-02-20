package com.umc.footprint.src.weather.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GetWeatherRes {
    private String temperature;
    private String weather;
}
