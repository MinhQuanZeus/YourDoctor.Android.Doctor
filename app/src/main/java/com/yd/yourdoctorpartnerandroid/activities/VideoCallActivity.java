package com.yd.yourdoctorpartnerandroid.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nhancv.npermission.NPermission;
import com.yd.yourdoctorpartnerandroid.R;
import com.yd.yourdoctorpartnerandroid.fragments.IncomingCallFragment;
import com.yd.yourdoctorpartnerandroid.managers.ScreenManager;
import com.yd.yourdoctorpartnerandroid.models.TypeCall;
import com.yd.yourdoctorpartnerandroid.models.VideoCallSession;

public class VideoCallActivity extends AppCompatActivity implements NPermission.OnPermissionResult {

    private String myId;
    private String number = "";
    private String callerIdChat = "";
    private String username;
    private String type;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_call);
        VideoCallSession videoCallSession = (VideoCallSession) getIntent().getSerializableExtra("VideoCallSession");
        if (videoCallSession.getType() == TypeCall.ANSWER) {
            ScreenManager.openFragment(getSupportFragmentManager(), new IncomingCallFragment().setVideoCallSession(videoCallSession), R.id.fl_video_call, false, true);
        }
    }

    @Override
    public void onPermissionResult(String s, boolean b) {

    }
}
