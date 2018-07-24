package com.yd.yourdoctorandroid.networks.getPatientDetailService;

import com.yd.yourdoctorandroid.networks.getDoctorRankingSpecialist.MainObjectRanking;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface GetPatientDetailService {
    @GET("patients/getInformationPatientById/{id_patient}?")
    Call<MainObjectDetailPatient> getPatientDetailService(@Path("id_patient") String id_patient);
}
