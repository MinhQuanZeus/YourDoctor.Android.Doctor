package com.yd.yourdoctorpartnerandroid.networks.banking;

public class ResponseMakeExchange {
    private boolean status;
    private String message;
    private String bankingId;
    private boolean success;

    public boolean isStatus() {
        return status;
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

    public String getBankingId() {
        return bankingId;
    }

    public void setBankingId(String bankingId) {
        this.bankingId = bankingId;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
