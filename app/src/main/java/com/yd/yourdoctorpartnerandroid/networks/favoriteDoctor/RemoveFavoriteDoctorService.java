package com.yd.yourdoctorpartnerandroid.networks.favoriteDoctor;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.PUT;

public interface RemoveFavoriteDoctorService {
    @PUT("patients/removeFavoriteDoctor")
    Call<MainResponseFavorite> addFavoriteDoctor(@Header("Authorization") String jwt, @Body FavoriteRequest favoriteRequest);
}
