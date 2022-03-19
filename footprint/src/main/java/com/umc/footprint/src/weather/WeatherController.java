package com.umc.footprint.src.weather;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.src.weather.model.GetWeatherReq;
import com.umc.footprint.src.weather.model.GetWeatherRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

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
    public BaseResponse<GetWeatherRes> GetWeather(@RequestBody String request) throws IOException {

            GetWeatherReq getWeatherReq = new ObjectMapper().readValue(request, GetWeatherReq.class);

            TimeZone default_time_zone = TimeZone.getTimeZone(ZoneId.of("Asia/Seoul"));
            TimeZone.setDefault(default_time_zone);

            // 현재 시간을 기준으로 발표 날짜와 발표 시간 도출
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));
            System.out.println("now = " + now);
            log.debug("now : {}",now);

            DateTimeFormatter Dateformatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter Timeformatter = DateTimeFormatter.ofPattern("HHmm");
            String dateNow = now.format(Dateformatter);     // 발표 날짜
            String timeNow = now.format(Timeformatter);      // 발표 시간

            int timeNowInteger = Integer.parseInt(timeNow);

            // 02시 기준 3시간 단위 발표 시간에 맞춰 시간대 조정
            if (timeNowInteger >= 211 && timeNowInteger < 511) {
                timeNow = timeNow.replace(timeNow, "0200");
            } else if (timeNowInteger >= 511 && timeNowInteger < 811) {
                timeNow = timeNow.replace(timeNow, "0500");
            } else if (timeNowInteger >= 811 && timeNowInteger < 1111) {
                timeNow = timeNow.replace(timeNow, "0800");
            } else if (timeNowInteger >= 1111 && timeNowInteger < 1411) {
                timeNow = timeNow.replace(timeNow, "1100");
            } else if (timeNowInteger >= 1411 && timeNowInteger < 1711) {
                timeNow = timeNow.replace(timeNow, "1400");
            } else if (timeNowInteger >= 1711 && timeNowInteger < 2011) {
                timeNow = timeNow.replace(timeNow, "1700");
            } else if (timeNowInteger >= 2011 && timeNowInteger < 2311) {
                timeNow = timeNow.replace(timeNow, "2000");
            } else if (timeNowInteger >= 2311 || timeNowInteger < 211) {
                timeNow = timeNow.replace(timeNow, "2300");
                if (timeNowInteger >= 0 && timeNowInteger < 211) { // 하루가 지나갈때 전날 날짜의 23시 발표로 가져옴
                    LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
                    log.debug("yesterday : {}",yesterday);
                    dateNow = yesterday.format(Dateformatter);
                }
            }

            String type = "JSON";    //조회하고 싶은 type(json, xml 중 고름)

            // url 주소
            String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";

            StringBuilder urlBuilder = new StringBuilder(apiUrl);
            urlBuilder.append("?" + URLEncoder.encode("serviceKey", "UTF-8") + "=" + serviceKey); /*Service Key*/
            urlBuilder.append("&" + URLEncoder.encode("pageNo", "UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
            urlBuilder.append("&" + URLEncoder.encode("numOfRows", "UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
            urlBuilder.append("&" + URLEncoder.encode("dataType", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
            urlBuilder.append("&" + URLEncoder.encode("base_date", "UTF-8") + "=" + URLEncoder.encode(dateNow, "UTF-8")); /*발표 날짜*/
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode(timeNow, "UTF-8")); /*발표 시각*/
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(getWeatherReq.getNx(), "UTF-8")); /*예보지점의 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(getWeatherReq.getNy(), "UTF-8")); /*예보지점의 Y 좌표값*/

            /*
             * GET방식으로 전송해서 파라미터 받아오기
             */
            GetWeatherRes getWeatherRes = new GetWeatherRes("-","-");

            URL url = new URL(urlBuilder.toString());

            for(int i=0; i < 5; i++){
                try {
                    System.out.println("url = " + url);

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

                    getWeatherRes = weatherService.getWeather(rd);

                    conn.disconnect();
                    
                    break;
                } catch (SocketTimeoutException ste){
                    log.info("http error");
                }
            }



        return new BaseResponse<>(getWeatherRes);
    }
}
