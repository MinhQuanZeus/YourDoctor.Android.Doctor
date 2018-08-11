package com.yd.yourdoctorpartnerandroid.networks.getPaymentHistory;

import java.util.List;

public class MainHistoryPaymentResponse {
    private List<ObjectPaymentResponse> listPaymentHistory;

    public List<ObjectPaymentResponse> getListPaymentHistory() {
        return listPaymentHistory;
    }

    public void setListPaymentHistory(List<ObjectPaymentResponse> listPaymentHistory) {
        this.listPaymentHistory = listPaymentHistory;
    }
}
