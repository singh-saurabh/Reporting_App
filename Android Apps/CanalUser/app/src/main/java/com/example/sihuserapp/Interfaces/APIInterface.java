package com.example.sihuserapp.Interfaces;

import com.example.sihuserapp.Objects.MediaResponse;
import com.example.sihuserapp.Objects.PingResponse;

import java.util.ArrayList;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("/api/request/phonequery/")
    Call<ArrayList<PingResponse>> getList(@Query("phone_number") String phone);
    /*@GET("/api/request/phonequery/")
    Call<ArrayList<PingResponse>> getList(@Query("phone_number") String phone);*/

    @GET("/api/photo/{photoid}/")
    Call<MediaResponse> Photo(@Path("photoid") Integer photoid);
}
