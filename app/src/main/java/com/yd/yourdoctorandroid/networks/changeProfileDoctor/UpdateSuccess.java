package com.yd.yourdoctorandroid.networks.changeProfileDoctor;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UpdateSuccess {
    @SerializedName("_id")
    @Expose
    private String id;
    private String birthday;
    private String address;
    private String placeWorking;
    private String avatar;
    private int gender;

    public UpdateSuccess(String id, String birthday, String address, String placeWorking, String avatar, int gender) {
        this.id = id;
        this.birthday = birthday;
        this.address = address;
        this.placeWorking = placeWorking;
        this.avatar = avatar;
        this.gender = gender;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceWorking() {
        return placeWorking;
    }

    public void setPlaceWorking(String placeWorking) {
        this.placeWorking = placeWorking;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
