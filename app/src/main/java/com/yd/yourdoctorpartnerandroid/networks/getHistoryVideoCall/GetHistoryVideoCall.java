package com.yd.yourdoctorpartnerandroid.networks.getHistoryVideoCall;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetHistoryVideoCall {
    @GET("videcallhistories/getHistoryVideoCallDoctor/{id_doctor}?")
    Call<MainObjectHistoryVideo> getHistoryVideoCall(@Header("Authorization") String jwt,
                                                     @Path("id_doctor") String id_doctor,
                                                     @Query("pageSize") String number_item,
                                                     @Query("page") String number_page);
}
