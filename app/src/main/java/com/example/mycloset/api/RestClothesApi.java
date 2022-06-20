package com.example.mycloset.api;

import com.example.mycloset.entity.Clothes;
import com.example.mycloset.entity.Member;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RestClothesApi {
    @POST("clothes/insert")
    Call<Clothes> insertOne(@Body Map<String, String> map);

    @GET("clothes/select/{id}")
    Call<Clothes> selectOne(@Path("id") Long id);

    @GET("clothes/select")
    Call<List<Clothes>> selectAll();

    @GET("/clothes/select/top")
    Call<List<Clothes>> selectTop();

    @GET("/clothes/select/bottom")
    Call<List<Clothes>> selectBottom();

    @GET("/clothes/select/coat")
    Call<List<Clothes>> selectCoat();

    @GET("/clothes/select/shoes")
    Call<List<Clothes>> selectShoes();

    @GET("/clothes/select/bag")
    Call<List<Clothes>> selectBag();

    @GET("/clothes/select/accessory")
    Call<List<Clothes>> selectAccessory();

    @PUT("clothes/modify/{id}")
    Call<Clothes> updateOne(@Path("id") Long id, @Body Map<String, String> map);

    @DELETE("clothes/remove/{id}")
    Call<ResponseBody> deleteOne(@Path("id") Long id);
}
