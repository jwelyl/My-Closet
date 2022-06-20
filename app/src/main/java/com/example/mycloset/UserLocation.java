package com.example.mycloset;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class UserLocation extends Thread {

    private String numOfRows = "12";
    private String pageNo = "1";
    private String type = "json";
    private String baseDate = "";
    private String baseTime = "";
    private String nx = "";
    private String ny = "";
    private int curtime = 0;
    String []day = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};

    private Weather weather = new Weather();

    public Weather getWeatherInfo(Context context, double latitude, double longitude) throws IOException, JSONException {

        // time
        Calendar calendar = Calendar.getInstance();
        TimeZone time;
        Date date = new Date();
        SimpleDateFormat format1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat format2 = new SimpleDateFormat("HHmm");

        weather.day = day[calendar.get(Calendar.DAY_OF_WEEK) - 1];
        time = TimeZone.getTimeZone("Asia/Seoul");
        format1.setTimeZone(time);
        baseDate = format1.format(date);
        format2.setTimeZone(time);
        baseTime = format2.format(date);
        curtime = Integer.parseInt(baseTime);

        if(curtime >= 200 && curtime < 500){
            baseTime = "0200";
        }
        else if(curtime >= 500 && curtime < 800){
            baseTime = "0500";
        }
        else if(curtime >= 800 && curtime < 1100){
            baseTime = "0800";
        }
        else if(curtime >= 1100 && curtime < 1400){
            baseTime = "1100";
        }
        else if(curtime >= 1400 && curtime < 1700){
            baseTime = "1400";
        }
        else if(curtime >= 1700 && curtime < 2000){
            baseTime = "1700";
        }
        else if(curtime >= 2000 && curtime < 2300){
            baseTime = "2000";
        }
        else{
            baseTime = "2300";
        }

        weather.basedate = baseDate;
        weather.basetime = baseTime;

        if(baseTime.equals("2300") && Integer.parseInt(format2.format(date)) < 2300){
            baseDate = (Integer.parseInt(baseDate) - 1) + "";
        }

        // latitude, longitude -> nx, ny

        double RE = 6371.00877;
        double GRID = 5.0;
        double SLAT1 = 30.0;
        double SLAT2 = 60.0;
        double OLON = 126.0;
        double OLAT = 38.0;
        double XO = 43;
        double YO = 136;

        double PI = Math.PI;
        double DEGRAD = PI / 180.0;
        double RADDEG = 180.0 / PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;


        double sn = Math.tan(PI * 0.25 + slat2 * 0.5) / Math.tan(PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(PI * 0.25 + (latitude) * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);

        double theta = longitude * DEGRAD - olon;
        if (theta > PI){
            theta -= 2.0 * PI;
        }
        if (theta < PI*(-1)) {
            theta += 2.0 * PI;
        }
        theta *= sn;

        nx = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5) + "";
        ny = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5) + "";

        // weather
        String apiUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst";
        String serviceKey = "zNZ0Nqkw3TrkPHHyxg37285a3u4Ll%2BnXLjeZYlrkCndjotl9cvWGJiKvST8U4nATR5Oak%2FPM38kcUYPZ59OlnA%3D%3D";

        StringBuilder urlBuilder = new StringBuilder(apiUrl);
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "="+serviceKey);
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode(numOfRows, "UTF-8")); //경도
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode(pageNo, "UTF-8")); //위도
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode(type, "UTF-8"));	/* 타입 */
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(baseDate, "UTF-8")); /* 조회하고싶은 날짜*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode(baseTime, "UTF-8")); /* 조회하고싶은 시간 AM 02시부터 3시간 단위 */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode(nx, "UTF-8")); //경도
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode(ny, "UTF-8")); //위도

        URL url = new URL(urlBuilder.toString());
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Content-type", "application/json");

        BufferedReader rd = null;
        if(conn.getResponseCode() == 200) {
            rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        } else {
            return weather;
        }

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }

        rd.close();
        conn.disconnect();
        String result= sb.toString();

        JSONObject mainObject = new JSONObject(result);
        JSONArray itemArray = mainObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

        for(int i=0; i<itemArray.length(); i++){
            JSONObject item = itemArray.getJSONObject(i);
            String category = item.getString("category");
            String value = item.getString("fcstValue");

            if(category.equals("TMP")){
                // temperature
                weather.temp = value;
            }

            else if(category.equals("TMX")){
                // max temperature
                weather.maxtemp = value;
            }

            else if(category.equals("TMN")){
                // min temperature
                weather.mintemp = value;
            }

            else if(category.equals("SKY")){
                // 1: 맑음 3: 구름많음 4: 흐림
                weather.sky = value;
            }

            else if(category.equals("POP")){
                // probability
                weather.rainprob = value;
            }

            else if(category.equals("REH")){
                weather.humidity = value;
            }

            else if(category.equals("WSD")){
                weather.windspeed = value;
            }

        }

        return weather;
    }

}
