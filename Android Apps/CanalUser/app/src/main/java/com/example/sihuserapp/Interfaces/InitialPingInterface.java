package com.example.sihuserapp.Interfaces;

import com.example.sihuserapp.Objects.PingBody;
import com.example.sihuserapp.Objects.PingResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface InitialPingInterface {

        @POST("/api/request/create/")
        Call<PingResponse> uploadFile(@Body PingBody body);
}
