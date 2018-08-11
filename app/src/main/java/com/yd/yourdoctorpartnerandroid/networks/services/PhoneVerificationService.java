package com.yd.yourdoctorpartnerandroid.networks.services;

import com.yd.yourdoctorpartnerandroid.networks.models.CommonSuccessResponse;
import com.yd.yourdoctorpartnerandroid.networks.models.PhoneVerification;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface PhoneVerificationService {
    @POST("phone/sms")
    Call<CommonSuccessResponse> register(@Body PhoneVerification patient);
}
