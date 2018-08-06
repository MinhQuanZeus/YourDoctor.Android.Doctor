package com.yd.yourdoctorandroid.networks.changePassword;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface ChangePasswordService {
    //http://192.168.124.109:3000/api/
    @PUT("users/changePassword")
    Call<PasswordResponse> changePasswordService(@Header("Authorization") String jwt, @Body PasswordRequest passwordRequest);
}
