package com.umc.footprint.src.weather.model;

import lombok.*;

@Getter
@NoArgsConstructor
public class PostWeatherReq {

    private String nx;
    private String ny;

    @Builder
    public PostWeatherReq(String nx, String ny){
        this.nx = nx;
        this.ny = ny;
    }

}
