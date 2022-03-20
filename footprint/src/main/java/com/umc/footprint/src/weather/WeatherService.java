package com.umc.footprint.src.weather;

import com.umc.footprint.src.weather.model.PostWeatherRes;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;

@Slf4j
@Service
public class WeatherService {

    public PostWeatherRes postWeather(BufferedReader rd) {

        String result = "";

        try {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            rd.close();
            result = sb.toString();
        } catch (IOException io) {
            System.out.println("io = " + io);
        }

        String temp = getTemp(result);
        String weather = "-";

        String isRain = checkRain(result);
        String isSnow = checkSnow(result);

        if(!isRain.equals("none")){
            return PostWeatherRes.builder()
                    .temperature(temp)
                    .weather("비")
                    .build();
        } else if (!isSnow.equals("none")){
            return PostWeatherRes.builder()
                    .temperature(temp)
                    .weather("눈")
                    .build();
        } else {
            weather = getMain(result);

            return PostWeatherRes.builder()
                    .temperature(temp)
                    .weather(weather)
                    .build();
        }

    }

    public String checkRain(String result){

        String isRain = "";

        try {
            JSONObject resultJson = new JSONObject(result);

            String rain = resultJson.getString("rain");
            JSONObject rainJson = new JSONObject(rain);
            String oneHourRain = rainJson.getString("1h");

            return oneHourRain;
        } catch (JSONException json){
            isRain = "none";

            return isRain;
        }
    }

    public String checkSnow(String result){

        String isSnow = "";

        try {
            JSONObject resultJson = new JSONObject(result);

            String snow = resultJson.getString("snow");
            JSONObject snowJson = new JSONObject(snow);
            String oneHourSnow = snowJson.getString("1h");

            return oneHourSnow;
        } catch (JSONException json){
            isSnow = "none";

            return isSnow;
        }
    }

    public String getTemp(String result){

        String temp = "-";
        try {
            JSONObject resultJson = new JSONObject(result);

            // main 중 temp 부분 가져오기
            String main = resultJson.getString("main");
            JSONObject mainJson = new JSONObject(main);
            temp = mainJson.getString("temp");

            // temp float to int
            float tempFloat = Float.parseFloat(temp);
            int tempInt = Math.round(tempFloat);
            temp = Integer.toString(tempInt);

            return temp;
        } catch(JSONException json){
            log.debug("getTemp JSON ERROR");
            return temp;
        }

    }

    public String getMain(String result){

        //======= 이 밑에 부터는 json에서 데이터 파싱해 오는 부분 =====//
        // response 키를 가지고 데이터를 파싱
        String weather = "-";

        try {
            JSONObject resultJson = new JSONObject(result);

            // wind 중 speed 부분 가져오기
            String wind = resultJson.getString("wind");
            JSONObject windJson = new JSONObject(wind);
            String speed = windJson.getString("speed");

            // cloud 중 all 부분 가져오기
            String clouds = resultJson.getString("clouds");
            JSONObject cloudsJson = new JSONObject(clouds);
            String all = cloudsJson.getString("all");

            if(Float.parseFloat(speed) > 13)
                weather = "바람";
            else if(Float.parseFloat(all) > 80)
                weather = "흐림";
            else if(Float.parseFloat(all) > 60)
                weather = "구름 많음";
            else
                weather = "맑음";

            return weather;

        } catch (JSONException json){
            log.debug("getMain JSON ERROR");
            return weather;
        }

    }

}
