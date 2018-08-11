package com.yd.yourdoctorpartnerandroid.networks.getListNotification;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SenderId {
    @SerializedName("_id")
    @Expose
    private String id;
    private String avatar;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
