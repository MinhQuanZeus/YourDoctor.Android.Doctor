package com.yd.yourdoctorpartnerandroid.networks.getAllTypesAdvisory;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;

public interface GetAllTypesAdvisoryService {
    @GET("typeadvisorys/getAllTypeAdvisories")
    Call<MainObjectTypeAdivosry> getMainObjectTypeAdvisories(@Header("Authorization") String jwt);
}
