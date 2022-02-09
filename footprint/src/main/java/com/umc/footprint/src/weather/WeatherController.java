package com.umc.footprint.src.weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/weather")
public class WeatherController {

    @ResponseBody
    @GetMapping("")
    public void lookUpWeather() throws IOException, JSONException {

        // 현재 시간을 기준으로 발표 날짜와 발표 시간 도출
        LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Seoul"));

        DateTimeFormatter Dateformatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter Timeformatter = DateTimeFormatter.ofPattern("HHmm");
        String dateNow = now.format(Dateformatter);     // 발표 날짜
        String timeNow = now.format(Timeformatter);      // 발표 시간
        System.out.println("dateNow = " + dateNow);
        System.out.println("timeNow = " + timeNow);

        int timeNowInteger = Integer.parseInt(timeNow);

        // 02시 기준 3시간 단위 발표 시간에 맞춰 시간대 조정
        if( timeNowInteger >= 211 && timeNowInteger < 511 ) {
            timeNow = timeNow.replace(timeNow, "0200");
            System.out.println("02/timeNow = " + timeNow);
        }
        else if( timeNowInteger >= 511 && timeNowInteger < 811 ) {
            timeNow = timeNow.replace(timeNow, "0500");
            System.out.println("05/timeNow = " + timeNow);
        }
        else if( timeNowInteger >= 811 && timeNowInteger < 1111 ) {
            timeNow = timeNow.replace(timeNow, "0800");
            System.out.println("08/timeNow = " + timeNow);
        }
        else if( timeNowInteger >= 1111 && timeNowInteger < 1411 ) {
            timeNow = timeNow.replace(timeNow, "1100");
            System.out.println("11/timeNow = " + timeNow);
        }
        else if( timeNowInteger >= 1411 && timeNowInteger < 1711 ) {
            timeNow = timeNow.replace(timeNow, "1400");
            System.out.println("14/timeNow = " + timeNow);
        }
        else if( timeNowInteger >= 1711 && timeNowInteger < 2011 ) {
            timeNow = timeNow.replace(timeNow, "1700");
            System.out.println("17/timeNow = " + timeNow);
        }
        else if( timeNowInteger >= 2011 && timeNowInteger < 2311 ) {
            timeNow = timeNow.replace(timeNow, "2000");
            System.out.println("20/timeNow = " + timeNow);
        }
        else if( timeNowInteger >= 2311 || timeNowInteger < 211  ){
            timeNow = timeNow.replace(timeNow,"2300");
            System.out.println("23/timeNow = " + timeNow);
            if(timeNow.compareTo("0000") >= 0 && timeNow.compareTo("0211") == -1) { // 하루가 지나갈때 전날 날짜의 23시 발표로 가져옴
                LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
                dateNow = yesterday.format(Dateformatter);
            }
        }

        System.out.println("AfterdateNow = " + dateNow);
        System.out.println("AftertimeNow = " + timeNow);

        String nx = "37";	//위도
        String ny = "127";	//경도
        String type = "JSON";	//조회하고 싶은 type(json, xml 중 고름)

        // url 주소
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        // 인증 키
        String serviceKey = "V%2B1MlI%2BZLP5u0ofnxvGVJTDzfOdPeQJLx1HCCC93OAP%2FMcupiIw1U%2F%2B7E1OUAeVqcFEkqwgkdSRHiMReIf8zVA%3D%3D";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(type, "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(dateNow, "UTF-8")); /*발표 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("0500", "UTF-8")); /*발표 시각*/
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); /*예보지점의 Y 좌표값*/

        /*
         * GET방식으로 전송해서 파라미터 받아오기
         */
        URL url = new URL(urlBuilder.toString());

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");
        System.out.println("Response code: " + conn.getResponseCode());

        BufferedReader rd;
        if(conn.getResponseCode() >= 200 && conn.getResponseCode() <= 300) {
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
        String result= sb.toString();
        System.out.println("result = " + result);
        
        //======= 이 밑에 부터는 json에서 데이터 파싱해 오는 부분 =====//
        // response 키를 가지고 데이터를 파싱
        JSONObject jsonObj_1 = new JSONObject(result);
        String response = jsonObj_1.getString("response");
        System.out.println("response = " + response);
        
        // response 로 부터 body 찾기
        JSONObject jsonObj_2 = new JSONObject(response);
        String body = jsonObj_2.getString("body");

        // body 로 부터 items 찾기
        JSONObject jsonObj_3 = new JSONObject(body);
        String items = jsonObj_3.getString("items");
        System.out.println("ITEMS : "+items);

        // items로 부터 itemlist 를 받기
        JSONObject jsonObj_4 = new JSONObject(items);
        JSONArray jsonArray = jsonObj_4.getJSONArray("item");

        String weather = "";
        String rain = "";
        String temperature = "";

        for(int i=0;i<jsonArray.length();i++){
            jsonObj_4 = jsonArray.getJSONObject(i);
            String fcstValue = jsonObj_4.getString("fcstValue");
            String category = jsonObj_4.getString("category");

            if(category.equals("SKY")){
                weather = "현재 날씨는 ";
                if(fcstValue.equals("1")) {
                    weather += "맑은 상태로 ";
                }else if(fcstValue.equals("3")) {
                    weather += "구름이 많은 상태로 ";
                }else if(fcstValue.equals("4")) {
                    weather += "흐린 상태로 ";
                }
            }

            if(category.equals("PTY")){
                rain = "현재 강수는 ";
                if(fcstValue.equals("0")) {
                    rain += "없음 ";
                }else if(fcstValue.equals("1")) {
                    rain += "비 ";
                }else if(fcstValue.equals("2")) {
                    rain += "비/눈 ";
                }else if(fcstValue.equals("3")) {
                    rain += "눈 ";
                }else if(fcstValue.equals("4")) {
                    rain += "소나기 ";
                }

            }


            if(category.equals("TMP")){
                temperature = "기온은 "+fcstValue+"℃ 입니다.";
            }
        }
        System.out.println("@@@@ WEATER_TAG : " + weather + rain + temperature);

    }

}
