package com.example.sihuserapp;

import com.example.sihuserapp.Interfaces.InitialPingInterface;

public class ApiUtils {

    private ApiUtils() {}

    //TODO: Change acc to requirements
    public static final String BASE_URL = "http://192.168.42.154:8000/";

    public static InitialPingInterface getAPIService() {

        return RetrofitClient.getClient(BASE_URL).create(InitialPingInterface.class);
    }
}