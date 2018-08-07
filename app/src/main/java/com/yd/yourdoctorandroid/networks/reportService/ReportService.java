package com.yd.yourdoctorandroid.networks.reportService;

import com.yd.yourdoctorandroid.networks.favoriteDoctor.FavoriteRequest;
import com.yd.yourdoctorandroid.networks.favoriteDoctor.MainResponseFavorite;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ReportService {
    @POST("reports")
    Call<MainResponReport> reportService(@Header("Authorization") String jwt, @Body ReportRequest reportRequest);
}
