package com.yd.yourdoctorpartnerandroid.networks.registerDoctor;

import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Patient;
import com.yd.yourdoctorpartnerandroid.networks.favoriteDoctor.FavoriteRequest;
import com.yd.yourdoctorpartnerandroid.networks.models.AuthResponse;
import com.yd.yourdoctorpartnerandroid.networks.models.CommonErrorResponse;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RegisterDoctorService {
    @POST("doctors")
    Call<RegisterResponse> register(@Body DoctorRegister doctorRequest);
}
