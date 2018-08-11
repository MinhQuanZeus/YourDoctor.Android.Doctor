package com.yd.yourdoctorandroid.networks.getDetailSpecialist;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GetSpecialistDetailService {
    @GET("specialists/getDetailSpecialist/{specialistId}?")
    Call<MainObjectSpecialistDetail> getSpecialistDetailService(@Path("specialistId") String specialistId);
}
