package com.example.sihuserapp.Objects;

import java.sql.Timestamp;
import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PingResponse {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("text")
    @Expose
    private String text;
    @SerializedName("timestamp")
    @Expose
    private Timestamp timestamp;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("latitude")
    @Expose
    private String latitude;
    @SerializedName("longitude")
    @Expose
    private String longitude;
    @SerializedName("status")
    @Expose
    private String statusChoice;
    @SerializedName("media")
    @Expose
    private List<Integer> media = null;
    @SerializedName("assigned_to")
    private String assignedTo;
    @SerializedName("phone_number")
    @Expose
    private String phone;

    public String getWaterType() {
        return waterType;
    }

    public void setWaterType(String waterType) {
        this.waterType = waterType;
    }

    @SerializedName("water_body_type")
    @Expose
    private String waterType;


    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public String getStatusChoice() {
        return statusChoice;
    }

    public void setStatusChoice(String statusChoice) {
        this.statusChoice = statusChoice;
    }

    public List<Integer> getMedia() {
        return media;
    }

    public void setMedia(List<Integer> media) {
        this.media = media;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}