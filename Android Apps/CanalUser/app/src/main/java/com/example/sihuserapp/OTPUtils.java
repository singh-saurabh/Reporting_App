package com.example.sihuserapp;

import com.example.sihuserapp.Interfaces.OTPInterface;

public class OTPUtils {
    private OTPUtils(){}

    public static final String BASE_URL = ApiUtils.BASE_URL;

    public static OTPInterface getAPIService(){
        return RetrofitClient.getClient(BASE_URL).create(OTPInterface.class);
    }
}
