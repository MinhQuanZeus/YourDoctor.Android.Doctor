package com.yd.yourdoctorpartnerandroid.networks.getDetailDoctor;

import com.yd.yourdoctorpartnerandroid.networks.getChatHistory.MainObjectChatHistory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;

public interface GetDetailDoctorService {
    @GET("doctors/getDetailDoctor/{id}?")
    Call<MainDetailDoctor> getDetailDoctor(@Header("Authorization") String jwt, @Path("id") String doctorId);
}
