package com.yd.yourdoctorandroid.networks.getAllTypesAdvisory;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetAllTypesAdvisoryService {
    @GET("typeadvisorys/getAllTypeAdvisories")
    Call<MainObjectTypeAdivosry> getMainObjectTypeAdvisories();
}