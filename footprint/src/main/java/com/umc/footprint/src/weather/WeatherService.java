package com.umc.footprint.src.weather;

import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.src.weather.model.GetWeatherReq;
import com.umc.footprint.src.weather.model.GetWeatherRes;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

@Slf4j
@Service
public class WeatherService {
    
    public GetWeatherRes getWeather(BufferedReader rd){

        try{

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            rd.close();
            String result = sb.toString();

            //======= 이 밑에 부터는 json에서 데이터 파싱해 오는 부분 =====//
            // response 키를 가지고 데이터를 파싱
            JSONObject jsonObj_1 = new JSONObject(result);
            String response = jsonObj_1.getString("response");
            log.debug("response : {}",response);

            // response 로 부터 body 찾기
            JSONObject jsonObj_2 = new JSONObject(response);
            String body = jsonObj_2.getString("body");

            // body 로 부터 items 찾기
            JSONObject jsonObj_3 = new JSONObject(body);
            String items = jsonObj_3.getString("items");
            log.debug("items : {}",items);

            System.out.println("items = " + items);

            // items로 부터 itemlist 를 받기
            JSONObject jsonObj_4 = new JSONObject(items);
            JSONArray jsonArray = jsonObj_4.getJSONArray("item");

            String weather = "";
            String rain = "";
            float wind = 0;
            String temperature = "";

            for (int i = 0; i < jsonArray.length(); i++) {
                jsonObj_4 = jsonArray.getJSONObject(i);
                String fcstValue = jsonObj_4.getString("fcstValue");
                String category = jsonObj_4.getString("category");

                if (category.equals("SKY")) {
                    if (fcstValue.equals("1")) { // 맑음
                        weather = "1";
                    } else if (fcstValue.equals("3")) {   // 구름 많음
                        weather = "3";
                    } else if (fcstValue.equals("4")) {   // 흐림
                        weather = "4";
                    }
                }

                if (category.equals("PTY")) {
                    if (fcstValue.equals("0")) {    // 없음
                        rain = "0";
                    } else if (fcstValue.equals("1")) {  // 비
                        rain = "1";
                    } else if (fcstValue.equals("2")) {  // 비/눈
                        rain = "2";
                    } else if (fcstValue.equals("3")) {  // 눈
                        rain = "3";
                    } else if (fcstValue.equals("4")) {  // 소나기
                        rain = "4";
                    }
                }

                if (category.equals("WSD")) {
                    wind = Float.parseFloat(fcstValue);
                }

                if (category.equals("TMP")) {
                    temperature = fcstValue;
                }

            }

            if(rain == "0"){
                if(wind > 13)
                    weather = "바람";
                else if(weather == "1")
                    weather = "맑음";
                else if(weather == "3")
                    weather = "구름 많음";
                else if(weather == "4")
                    weather = "흐림";
            }
            else if(rain == "1")
                weather = "비";
            else if(rain == "2")
                weather = "비/눈";
            else if(rain == "3")
                weather = "눈";
            else if(rain == "4")
                weather = "소나기";

            log.debug("temperature : {} || weather : {}",temperature,weather);

            GetWeatherRes getWeatherRes = new GetWeatherRes(temperature,weather);

            System.out.println("getWeatherRes.getWeather() = " + getWeatherRes.getWeather());
            System.out.println("getWeatherRes.getTemperature() = " + getWeatherRes.getTemperature());

            return getWeatherRes;
        } catch(JSONException json){
            return new GetWeatherRes("-","-");
        } catch (IOException io){
            return new GetWeatherRes("-","-");
        }

    }


}
