package com.yd.yourdoctorpartnerandroid.networks.banking;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface MakeRequestExchange {
    @POST("bankinghistorys/doctorWithdrawal")
    Call<ResponseMakeExchange> makeRequestExchange(@Header("Authorization") String jwt, @Body RequestMakeExchange requestMakeExchange);
}
