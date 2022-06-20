package com.example.mycloset.api;

import com.example.mycloset.entity.Member;

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

public interface RestMemberApi {
    @POST("member/insert")
    Call<Member> insertOne(@Body Map<String, String> map);

    @GET("member/select/{id}")
    Call<Member> selectOne(@Path("id") Long id);

    @GET("member/select")
    Call<List<Member>> selectAll();

    @PUT("member/modify/{id}")
    Call<Member> updateOne(@Path("id") Long id, @Body Map<String, String> map);

    @DELETE("member/remove/{id}")
    Call<ResponseBody> deleteOne(@Path("id") Long id);
}
