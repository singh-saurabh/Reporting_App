package com.example.sihuserapp.Interfaces;
import com.example.sihuserapp.Objects.UploadResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
public interface UploadImageInterface {
    @Multipart
    @POST("/api/photo/")
    Call<UploadResponse> uploadFile(@Part MultipartBody.Part file, @Part("id") RequestBody IdName);
}
