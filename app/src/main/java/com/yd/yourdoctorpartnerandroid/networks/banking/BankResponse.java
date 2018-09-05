package com.yd.yourdoctorpartnerandroid.networks.banking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankResponse {
    @SerializedName("_id")
    @Expose
    private String id;
    private String nameBank;
    private String nameBankEnglish;
    private String nameTransaction;
    private long createdAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNameBank() {
        return nameBank;
    }

    public void setNameBank(String nameBank) {
        this.nameBank = nameBank;
    }

    public String getNameBankEnglish() {
        return nameBankEnglish;
    }

    public void setNameBankEnglish(String nameBankEnglish) {
        this.nameBankEnglish = nameBankEnglish;
    }

    public String getNameTransaction() {
        return nameTransaction;
    }

    public void setNameTransaction(String nameTransaction) {
        this.nameTransaction = nameTransaction;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
