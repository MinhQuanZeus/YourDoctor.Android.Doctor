package com.yd.yourdoctorandroid.networks.checkStatusChatService;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface CheckStatusChatService {
    //http://192.168.124.109:3000/api/
    @POST("chatshistorys/checkStatusChatsHistory")
    Call<ListNotDoneResponse> checkStatusChatService(@Header("Authorization") String jwt, @Body ListRequest listRequest);

}
