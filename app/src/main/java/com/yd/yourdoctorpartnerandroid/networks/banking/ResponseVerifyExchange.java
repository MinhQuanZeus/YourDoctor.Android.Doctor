package com.yd.yourdoctorpartnerandroid.networks.banking;

public class ResponseVerifyExchange {
    private boolean status;
    private String message;
    private float oldRemainMoney;

    public ResponseVerifyExchange(String message, float oldRemainMoney) {
        this.message = message;
        this.oldRemainMoney = oldRemainMoney;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public float getOldRemainMoney() {
        return oldRemainMoney;
    }

    public void setOldRemainMoney(float oldRemainMoney) {
        this.oldRemainMoney = oldRemainMoney;
    }

    public boolean isStatus() {
        return status;
    }
}
