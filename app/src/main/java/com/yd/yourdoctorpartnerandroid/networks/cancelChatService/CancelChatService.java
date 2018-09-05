package com.yd.yourdoctorpartnerandroid.networks.cancelChatService;

import com.yd.yourdoctorpartnerandroid.networks.getAllTypesAdvisory.MainObjectTypeAdivosry;
import com.yd.yourdoctorpartnerandroid.networks.getDoctorRankingSpecialist.MainObjectRanking;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface CancelChatService {
    @GET("chatshistorys/doctorDenyRequestChat/{id_chat}?")
    Call<MainResponseCancelChat> cancelChatService(@Header("Authorization") String jwt, @Path("id_chat") String id_chat);
}
