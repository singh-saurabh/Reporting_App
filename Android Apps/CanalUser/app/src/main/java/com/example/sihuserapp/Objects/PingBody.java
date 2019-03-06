package com.example.sihuserapp.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PingBody {

    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("phone_number")
    @Expose
    private String phoneNumber;

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    @SerializedName("water_body_type")
    @Expose
    private String waterType;


    public PingBody(String text,String latitude,String longitude,String phoneNumber, String waterType)
    {
        this.text=text;
        this.latitude=latitude;
        this.longitude=longitude;
        this.phoneNumber=phoneNumber;
        this.waterType=waterType;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

}
