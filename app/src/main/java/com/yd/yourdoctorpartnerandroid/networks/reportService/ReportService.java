package com.yd.yourdoctorpartnerandroid.networks.reportService;

import com.yd.yourdoctorpartnerandroid.networks.favoriteDoctor.FavoriteRequest;
import com.yd.yourdoctorpartnerandroid.networks.favoriteDoctor.MainResponseFavorite;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ReportService {
    @POST("reports")
    Call<MainResponReport> reportService(@Header("Authorization") String jwt, @Body ReportRequest reportRequest);
}
