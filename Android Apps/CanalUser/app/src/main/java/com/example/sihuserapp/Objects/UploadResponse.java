package com.example.sihuserapp.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UploadResponse {
    @SerializedName("Data")
    @Expose
    private String data;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
