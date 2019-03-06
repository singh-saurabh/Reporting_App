package com.example.sihuserapp;

import com.example.sihuserapp.Interfaces.UploadImageInterface;

public class UploadUtils {
    public UploadUtils(){}

    private static final String BASE_URL = ApiUtils.BASE_URL;

    public static UploadImageInterface getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(UploadImageInterface.class);
    }
}
