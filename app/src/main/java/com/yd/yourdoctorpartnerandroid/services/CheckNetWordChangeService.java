package com.yd.yourdoctorpartnerandroid.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.utils.LoadDefaultModel;
import com.yd.yourdoctorpartnerandroid.utils.NetworkUtils;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.SocketUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CheckNetWordChangeService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (NetworkUtils.isOnline(context)) {
            if (NetworkUtils.isOnline(context)) {
                if (SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null) {
                    if(!SocketUtils.getInstance().checkIsConnected()){
                        SocketUtils.getInstance().reConnect();
                    }
                }

            }
        }
    }

}



