package com.yd.yourdoctorandroid.networks.getListHistoryChat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientIdHistory {
    @SerializedName("_id")
    @Expose
    private String id;
    private String firstName;
    private String middleName;
    private String lastName;
    private String avatar;

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getFullName(){
        return firstName + " " + middleName +" " + lastName;
    }
}
