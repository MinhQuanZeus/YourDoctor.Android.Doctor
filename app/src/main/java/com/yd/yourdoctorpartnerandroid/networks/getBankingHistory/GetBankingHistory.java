package com.yd.yourdoctorpartnerandroid.networks.getBankingHistory;

import com.yd.yourdoctorpartnerandroid.networks.getDoctorDetailProfile.MainObjectDetailDoctor;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetBankingHistory {
    @GET("bankinghistorys/getHistoryBanking/{doctorID}?")
    Call<MainOjectBankingHistory> GetBankingHistory(@Header("Authorization") String jwt,
                                                    @Path("doctorID") String id_doctor,
                                                    @Query("pageSize") String pageSize,
                                                    @Query("page") String page);
}
