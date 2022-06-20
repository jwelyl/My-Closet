package com.example.mycloset;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


public class HomeActivity extends AppCompatActivity{

    private Intent intent;
    private Weather weather = new Weather();
    private UserLocation userLocation = new UserLocation();;
    private int curtime = 0;

    static final int PERMISSIONS = 0x00000001;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private Location curlocation;
    private Geocoder geocoder;
    private Boolean permission_accepted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_activity_home);

        CheckPermission();

        // Convert page when button is clicked
        // home page
        ImageButton home_button = (ImageButton) findViewById(R.id.home_button);
        home_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        });

        // style page
        ImageButton style_button = (ImageButton) findViewById(R.id.style_button);
        style_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), StyleActivity.class);
            startActivity(intent);
        });

        // closet page
        ImageButton closet_button = (ImageButton) findViewById(R.id.closet_button);
        closet_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), ClosetActivity.class);
            startActivity(intent);
        });

        // diary page
        ImageButton diary_button = (ImageButton) findViewById(R.id.diary_button);
        diary_button.setOnClickListener(view -> {
            intent = new Intent(getApplicationContext(), DiaryActivity.class);
            startActivity(intent);
        });

    }

    public void CheckPermission(){

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permission_accepted = false;
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "위치 권한이 설정되어 있지 않습니다", Toast.LENGTH_LONG).show();
            }

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS);
        }
        else{
            permission_accepted = true;
        }

        if(permission_accepted) {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            //isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            //isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            curlocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (curlocation == null) {
                Toast.makeText(this, "위치 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show();
                return;
            }

            latitude = curlocation.getLatitude();
            longitude = curlocation.getLongitude();

//            Toast.makeText(this, "위도 = " + latitude, Toast.LENGTH_LONG).show();
//            Toast.makeText(this, "경도 = " + longitude, Toast.LENGTH_LONG).show();
//            System.out.println("위도 = " + latitude);
//            System.out.println("경도 = " + longitude);

            ExecutorService executor = Executors.newSingleThreadExecutor();
            executor.submit(() -> {
                try {
                    weather = userLocation.getWeatherInfo(getApplicationContext(), latitude, longitude);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            executor.shutdown();

            try {
                if (executor.awaitTermination(20, TimeUnit.SECONDS)) {
                    // find address
                    geocoder = new Geocoder(this, Locale.KOREA);
                    List<Address> address = null;
                    try {
                        address = geocoder.getFromLocation(latitude, longitude, 3);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if (address != null) {
                        weather.address = address.get(0).getAddressLine(0);
                        weather.address = weather.address.split(" ")[1] + " " + weather.address.split(" ")[2];
                    }
                    if (weather.basetime.equals("")) {
                        Toast.makeText(this, "날씨 정보를 가져올 수 없습니다.", Toast.LENGTH_LONG).show();
                        return;
                    }
                    setWeatherBox();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case PERMISSIONS:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "권한 설정 완료", Toast.LENGTH_LONG).show();
                    permission_accepted = true;
                    CheckPermission();
                } else {
                    Toast.makeText(this, "위치 권한 없음", Toast.LENGTH_LONG).show();
                    permission_accepted = false;
                }
                break;
        }
    }

    public void setWeatherBox(){
        // set background color
        LinearLayout weatherBox = (LinearLayout) findViewById(R.id.weatherBox);
        curtime = Integer.parseInt(weather.basetime);

        if(curtime >= 500 && curtime < 1100){
            weatherBox.setBackgroundResource(R.drawable.ic_weather_dawn);
        }
        else if(curtime >= 1100 && curtime < 1600){
            weatherBox.setBackgroundResource(R.drawable.ic_weather_morning);
        }
        else if(curtime >= 1600 && curtime < 2000){
            weatherBox.setBackgroundResource(R.drawable.ic_weather_afternoon);
        }
        else{
            weatherBox.setBackgroundResource(R.drawable.ic_weather_night);
        }


        // weather
        TextView weatherBox_date = (TextView) findViewById(R.id.weatherBox_date);
        weatherBox_date.setText(weather.basedate.substring(4,6) + "." + weather.basedate.substring(6) + " " +  weather.day);

        TextView weatherBox_temperature = (TextView) findViewById(R.id.weatherBox_temperature);
        weatherBox_temperature.setText(weather.temp + "º");

        ImageView weatherBox_picture = (ImageView) findViewById(R.id.weatherBox_picture);
        TextView weatherBox_weather = (TextView) findViewById(R.id.weatherBox_weather);

        if(weather.sky.equals("1")) {
            weatherBox_picture.setBackgroundResource(R.drawable.ic_weather_sunny);
            weatherBox_weather.setText("맑음");
        }
        if(weather.sky.equals("3")) {
            weatherBox_picture.setBackgroundResource(R.drawable.ic_weather_sunnyandcloudy);
            weatherBox_weather.setText("구름많음");
        }
        else{
            weatherBox_picture.setBackgroundResource(R.drawable.ic_weather_cloudy);
            weatherBox_weather.setText("흐림");
        }

        TextView weatherBox_location = (TextView) findViewById(R.id.weatherBox_location);
        weatherBox_location.setText(weather.address);

        TextView weatherBox_rain = (TextView) findViewById(R.id.weatherBox_rain);
        weatherBox_rain.setText(weather.rainprob + "%");

        TextView weatherBox_humidity = (TextView) findViewById(R.id.weatherBox_humidity);
        weatherBox_humidity.setText(weather.humidity + "%");

        TextView weatherBox_wind = (TextView) findViewById(R.id.weatherBox_wind);
        weatherBox_wind.setText(weather.windspeed + "m/s");
    }
}
