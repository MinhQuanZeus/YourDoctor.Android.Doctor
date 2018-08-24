package com.yd.yourdoctorpartnerandroid.networks.services;

import com.yd.yourdoctorpartnerandroid.networks.models.AuthResponse;
import com.yd.yourdoctorpartnerandroid.networks.models.Login;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface LoginService {
    @POST("auth/login")
    Call<AuthResponse> register(@Body Login patient);
}
