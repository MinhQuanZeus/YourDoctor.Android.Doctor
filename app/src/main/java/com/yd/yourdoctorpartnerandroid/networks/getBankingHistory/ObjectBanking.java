package com.yd.yourdoctorpartnerandroid.networks.getBankingHistory;

public class ObjectBanking {
    private int status;
    private String _id;
    private String userId;
    private float amount;
    private float remainMoney;
    private int type;
    private String nameBank;
    private String accountNumber;
    private long createdAt;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getAmount() {
        return (int) Math.round(amount);
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public int getRemainMoney() {
        return (int) Math.round(remainMoney);
    }

    public void setRemainMoney(float remainMoney) {
        this.remainMoney = remainMoney;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
