package com.example.sihuserapp.Objects;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MediaResponse {

    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("canalquery")
    @Expose
    private Integer canalquery;

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getCanalquery() {
        return canalquery;
    }

    public void setCanalquery(Integer canalquery) {
        this.canalquery = canalquery;
    }

}