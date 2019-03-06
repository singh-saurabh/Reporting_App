package com.example.sihuserapp.Interfaces;

import com.example.sihuserapp.Objects.OTPBody;
import com.example.sihuserapp.Objects.OTPResponse;
import com.example.sihuserapp.Objects.VerifyOTPBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
public interface OTPInterface {
    @POST("/api/otp_request/")
    Call<OTPResponse> sendOTP(@Body OTPBody body);

    @POST("/api/verify/")
    Call<OTPResponse> verifyOTP(@Body VerifyOTPBody body);
}
