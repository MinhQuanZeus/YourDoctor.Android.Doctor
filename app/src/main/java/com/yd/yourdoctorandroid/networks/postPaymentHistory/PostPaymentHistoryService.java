package com.yd.yourdoctorandroid.networks.postPaymentHistory;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PostPaymentHistoryService {
    //192.168.124.106
    //http://192.168.124.106:3000/api/
    @POST("paymentshistorys")
    Call<PaymentResponse> addPaymentHistory(@Header("Authorization") String jwt, @Body PaymentHistory paymentHistory);
}
