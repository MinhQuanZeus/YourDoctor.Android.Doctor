package com.yd.yourdoctorpartnerandroid.networks.getRuleIntroAPI;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GetRuleIntroService {
    @GET("IntroduceAndRule")
    Call<MainObjectRule> getIntroduceAndRule();
}
