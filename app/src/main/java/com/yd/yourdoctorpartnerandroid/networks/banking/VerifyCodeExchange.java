package com.yd.yourdoctorpartnerandroid.networks.banking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface VerifyCodeExchange {
    @POST("bankinghistorys/checkCodeVerify")
    Call<ResponseVerifyExchange> verifyCodeExchange(@Header("Authorization") String jwt, @Body RequestVerifyExchange requestVerifyExchange);
}
