package com.example.sihuserapp;

import com.example.sihuserapp.Interfaces.APIInterface;

public class PostListUtil {
    private PostListUtil(){

    }
    public static final String BASE_URL = ApiUtils.BASE_URL;

    public static APIInterface getAPIService(){
        return RetrofitClient.getClient(BASE_URL).create(APIInterface.class);
    }
}
