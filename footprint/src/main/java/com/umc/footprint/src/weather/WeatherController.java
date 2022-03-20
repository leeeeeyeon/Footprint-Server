package com.umc.footprint.src.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.src.weather.model.PostWeatherReq;
import com.umc.footprint.src.weather.model.PostWeatherRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;


@Slf4j
@RestController
@RequestMapping("/weather")
public class WeatherController {

    private final WeatherService weatherService;

    @Autowired
    public WeatherController(WeatherService weatherService){
        this.weatherService = weatherService;
    }

    // 인증 키
    @Value("${weather.service-key}")
    private String serviceKey;

    @ResponseBody
    @PostMapping("")
    public BaseResponse<PostWeatherRes> PostWeather(@RequestBody String request) throws IOException {

        PostWeatherReq postWeatherReq = new ObjectMapper().readValue(request, PostWeatherReq.class);

        String apiUrl = "https://api.openweathermap.org/data/2.5/weather";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?"+URLEncoder.encode("lat", "UTF-8") + "=" + URLEncoder.encode(postWeatherReq.getNx(), "UTF-8"));
        urlBuilder.append("&"+URLEncoder.encode("lon", "UTF-8") + "=" + URLEncoder.encode(postWeatherReq.getNy(), "UTF-8"));
        urlBuilder.append("&"+URLEncoder.encode("appid", "UTF-8") + "=" + serviceKey);
        urlBuilder.append("&"+URLEncoder.encode("units", "UTF-8") + "=" + URLEncoder.encode("metric", "UTF-8"));

        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setConnectTimeout(1000);
        conn.setReadTimeout(1000);
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        conn.connect();

        BufferedReader rd;
        if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
        }

        PostWeatherRes postWeatherRes = weatherService.postWeather(rd);

        return new BaseResponse<>(postWeatherRes);
    }
    
}
