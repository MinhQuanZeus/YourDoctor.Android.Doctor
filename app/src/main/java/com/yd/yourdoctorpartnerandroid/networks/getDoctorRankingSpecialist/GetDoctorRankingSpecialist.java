package com.yd.yourdoctorpartnerandroid.networks.getDoctorRankingSpecialist;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetDoctorRankingSpecialist {
    @GET("doctors/getDoctorRankingBySpecialist/{id_specialist}?")
    Call<MainObjectRanking> getDoctorRankingSpecialist(@Header("Authorization") String jwt, @Path("id_specialist") String id_specialist, @Query("pageSize") String number_item, @Query("page") String number_page);
}
