package com.example.mycloset.api;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataService {
    private static DataService instance = new DataService();

    private DataService() { }

//    private final String BASE_URL = "http://10.0.2.2:8080/";
    private String BASE_URL = "http://15.164.56.248:8080/";
    Retrofit retrofitClient = new Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(new OkHttpClient.Builder().build())
        .build();

    public static DataService getInstance() {
        return instance;
    }

    RestClothesApi restClothesApi = retrofitClient.create(RestClothesApi.class);
    RestMemberApi restMemberApi = retrofitClient.create(RestMemberApi.class);
    RestPictureApi restPictureApi = retrofitClient.create(RestPictureApi.class);

    public RestClothesApi getRestClothesApi() {
        return restClothesApi;
    }

    public RestMemberApi getRestMemberApi() {
        return restMemberApi;
    }

    public RestPictureApi getRestPictureApi() {
        return restPictureApi;
    }
}
