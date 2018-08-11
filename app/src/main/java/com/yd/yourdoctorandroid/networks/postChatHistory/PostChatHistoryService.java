package com.yd.yourdoctorandroid.networks.postChatHistory;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface PostChatHistoryService {
    @POST("chatshistorys")
    Call<ChatHistoryResponse> addChatHistory(@Header("Authorization") String jwt, @Body ChatHistory chatHistory);
}
