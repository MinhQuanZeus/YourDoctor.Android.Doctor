package com.yd.yourdoctorpartnerandroid;

import android.content.Intent;
import android.util.Log;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.gson.Gson;
import com.yd.yourdoctorpartnerandroid.activities.VideoCallActivity;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.TypeCall;
import com.yd.yourdoctorpartnerandroid.models.VideoCallSession;
import com.yd.yourdoctorpartnerandroid.utils.RxScheduler;
import com.yd.yourdoctorpartnerandroid.utils.SharedPrefs;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

public class DoctorApplication extends android.app.Application{
    private static DoctorApplication mSelf;
    private Gson mGSon;
    private Doctor userInfo;
    private Socket mSocket;
    {
        try {
            mSocket = IO.socket(Constants.VIDEO_CALL_SERVER_URL);
            if (mSocket != null){
                mSocket.on("connect", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        RxScheduler.runOnUi(o -> {
                            try {
                                userInfo = SharedPrefs.getInstance().get("USER_INFO", Doctor.class);
                                if (userInfo != null) {
                                    JSONObject obj = new JSONObject();
                                    try {
                                        obj.put("id", "register");
                                        obj.put("userId", userInfo.getDoctorId());
                                        obj.put("name", userInfo.getFullName());
                                        obj.put("avatar", userInfo.getAvatar());
                                        obj.put("type", 2);
                                        mSocket.emit("register", obj);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } catch (Exception e) {

                            }
                        });
                    }
                });

                mSocket.on("incomingCall", new Emitter.Listener() {
                    @Override
                    public void call(Object... args) {
                        RxScheduler.runOnUi(o -> {
                            try {
                                JSONObject data = new JSONObject(args[0].toString());
                                Log.d("YourDoctorApplication", args[0].toString());
                                VideoCallSession videoCallSession = new VideoCallSession(data.getString("from"), data.getString("callerName"), data.getString("callerAvatar"),
                                        userInfo.getDoctorId(), userInfo.getFullName(), userInfo.getAvatar(), TypeCall.ANSWER);
                                Intent intent = new Intent(DoctorApplication.this, VideoCallActivity.class);
                                intent.putExtra("VideoCallSession", videoCallSession);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                DoctorApplication.self().startActivity(intent);
                            } catch (Exception e) {
                                Log.d("MainActivityCall", e.toString());
                            }
                        });
                    }
                });
            }
        } catch (URISyntaxException e) {
            Log.d("YourDoctorApplication", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static DoctorApplication self() {
        return mSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSelf = this;
        mGSon = new Gson();
        mSocket.connect();
    }

    public Gson getGSon() {
        return mGSon;
    }
    public Socket getSocket() {
        return mSocket;
    }
}
