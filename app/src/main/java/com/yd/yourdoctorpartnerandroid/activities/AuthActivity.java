package com.yd.yourdoctorpartnerandroid.activities;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.fragments.LoginFragment;
import com.yd.yourdoctorpartnerandroid.fragments.RegisterFragment;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Patient;
import com.yd.yourdoctorpartnerandroid.services.CheckNetWordChangeService;
import com.yd.yourdoctorpartnerandroid.utils.LoadDefaultModel;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.SocketUtils;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        if(SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null){
            FirebaseMessaging.getInstance().subscribeToTopic(SharedPrefs.getInstance().get("USER_INFO", Doctor.class).getDoctorId());
            LoadDefaultModel.getInstance().registerServiceCheckNetwork(getApplicationContext());
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else {
            ScreenManager.openFragment(getSupportFragmentManager(), new LoginFragment(), R.id.fl_auth, false, false);
        }
    }

}
