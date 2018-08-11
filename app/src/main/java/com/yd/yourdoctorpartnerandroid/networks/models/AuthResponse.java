package com.yd.yourdoctorpartnerandroid.networks.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AuthResponse {
    private String message;
    @SerializedName("jwt_token")
    @Expose
    private String jwtToken;
    @SerializedName("user")
    @Expose
    private Doctor doctor;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    @Override
    public String toString() {
        return "AuthResponse{" +
                "message='" + message + '\'' +
                ", jwtToken='" + jwtToken + '\'' +
                ", doctor=" + doctor.toString() +
                '}';
    }

    public AuthResponse(String message, String jwtToken, Doctor doctor) {

        this.message = message;
        this.jwtToken = jwtToken;
        this.doctor = doctor;
    }
}
