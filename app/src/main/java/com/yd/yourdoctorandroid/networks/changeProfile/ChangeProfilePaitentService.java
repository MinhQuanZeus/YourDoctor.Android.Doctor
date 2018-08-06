package com.yd.yourdoctorandroid.networks.changeProfile;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface ChangeProfilePaitentService {
    //192.168.124.109
    //http://192.168.124.109:3000/api/
    @PUT("users")
    Call<PatientResponse> changeProfilePaitentService(@Header("Authorization") String jwt, @Body PatientRequest patientRequest);
}
