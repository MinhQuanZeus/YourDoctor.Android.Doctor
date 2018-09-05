package com.yd.yourdoctorpartnerandroid.networks.banking;

public class RequestVerifyExchange {
    private String id;
    private String code;

    public RequestVerifyExchange(String id, String code) {
        this.id = id;
        this.code = code;
    }
}
