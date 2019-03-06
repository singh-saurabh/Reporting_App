package com.example.sihuserapp.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class OTPBody {
    @SerializedName("phone")
    @Expose
    private String phone;
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public OTPBody(String phone){
        this.phone=phone;
    }
}
