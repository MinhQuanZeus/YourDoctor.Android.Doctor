package com.yd.yourdoctorpartnerandroid.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessaging;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.fragments.LoginFragment;
import com.yd.yourdoctorpartnerandroid.fragments.RegisterFragment;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Patient;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;
import com.yd.yourdoctorpartnerandroid.utils.SocketUtils;

public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
//        if (SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null
//                && SharedPrefs.getInstance().get("JWT_TOKEN", String.class) != null) {
//            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            Doctor doctor = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
//            FirebaseMessaging.getInstance().subscribeToTopic(doctor.getDoctorId());
//            SocketUtils.getInstance().reConnect();
//            startActivity(intent);
//        } else {
//            ScreenManager.openFragment(getSupportFragmentManager(), new LoginFragment(), R.id.fl_auth, false, false);
//        }

        ScreenManager.openFragment(getSupportFragmentManager(), new LoginFragment(), R.id.fl_auth, false, false);
    }
}
