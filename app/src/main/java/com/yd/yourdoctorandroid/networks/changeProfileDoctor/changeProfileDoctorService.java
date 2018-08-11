package com.yd.yourdoctorandroid.networks.changeProfileDoctor;



import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface changeProfileDoctorService {
    @PUT("doctors")
    Call<DoctorRespone> changeProfileDoctorService(@Header("Authorization") String jwt , @Body DoctorRequest doctorRequest);
}
