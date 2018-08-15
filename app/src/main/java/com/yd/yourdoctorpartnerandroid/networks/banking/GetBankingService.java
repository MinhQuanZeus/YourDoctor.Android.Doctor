package com.yd.yourdoctorpartnerandroid.networks.banking;

import com.yd.yourdoctorpartnerandroid.networks.getChatHistory.MainObjectChatHistory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GetBankingService {

    @GET("banks")
    Call<MainBanking> getAllBanks(@Header("Authorization") String jwt);
}
