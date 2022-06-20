package com.example.mycloset.api;

import com.example.mycloset.entity.Member;
import com.example.mycloset.entity.Picture;

import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface RestPictureApi {
    @POST("picture/insert")
    Call<Picture> insertOne(@Body Map<String, String> map);

    @GET("picture/select/{id}")
    Call<Picture> selectOne(@Path("id") Long id);

    @GET("picture/select")
    Call<List<Picture>> selectAll();

    @PUT("picture/modify/{id}")
    Call<Picture> updateOne(@Path("id") Long id, @Body Map<String, String> map);

    @DELETE("picture/remove/{id}")
    Call<ResponseBody> deleteOne(@Path("id") Long id);
}
