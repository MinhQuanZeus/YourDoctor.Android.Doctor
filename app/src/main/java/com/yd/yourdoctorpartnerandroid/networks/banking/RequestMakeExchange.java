package com.yd.yourdoctorpartnerandroid.networks.banking;

public class RequestMakeExchange {
    private String userId;
    private int amount;
    private float remainMoney;
    private String nameBank;
    private String accountNumber;

    public RequestMakeExchange(String userId, int amount, float remainMoney, String nameBank, String accountNumber) {
        this.userId = userId;
        this.amount = amount;
        this.remainMoney = remainMoney;
        this.nameBank = nameBank;
        this.accountNumber = accountNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public float getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(float remainMoney) {
        this.remainMoney = remainMoney;
    }

    public String getNameBank() {
        return nameBank;
    }

    public void setNameBank(String nameBank) {
        this.nameBank = nameBank;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
}
