package com.umc.footprint.src.weather;

import com.umc.footprint.config.BaseResponse;
import com.umc.footprint.src.weather.model.GetWeatherReq;
import com.umc.footprint.src.weather.model.GetWeatherRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Slf4j
@RestController
@RequestMapping("/weather")
public class WeatherController {

    // 인증 키
    @Value("${weather.service-key}")
    private String serviceKey;

    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetWeatherRes> GetWeather(@RequestBody GetWeatherReq getWeatherReq) throws IOException, JSONException {

        try {
            // 현재 시간을 기준으로 발표 날짜와 발표 시간 도출
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

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
            urlBuilder.append("&" + URLEncoder.encode("base_time", "UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); /*발표 시각*/
            urlBuilder.append("&" + URLEncoder.encode("nx", "UTF-8") + "=" + URLEncoder.encode(getWeatherReq.getNx(), "UTF-8")); /*예보지점의 X 좌표값*/
            urlBuilder.append("&" + URLEncoder.encode("ny", "UTF-8") + "=" + URLEncoder.encode(getWeatherReq.getNy(), "UTF-8")); /*예보지점의 Y 좌표값*/

            /*
             * GET방식으로 전송해서 파라미터 받아오기
             */
            URL url = new URL(urlBuilder.toString());

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Content-type", "application/json");
            log.debug("Weather-Response-Code : {}", conn.getResponseCode());

            BufferedReader rd;
            if (conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
                rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                rd = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            rd.close();
            conn.disconnect();
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
                    weather = "구름많음";
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

            return new BaseResponse<>(getWeatherRes);

        } catch (IOException io){
            return new BaseResponse<>(new GetWeatherRes("-","-"));
        } catch (JSONException json){
            return new BaseResponse<>(new GetWeatherRes("-","-"));
        }

    }
}
