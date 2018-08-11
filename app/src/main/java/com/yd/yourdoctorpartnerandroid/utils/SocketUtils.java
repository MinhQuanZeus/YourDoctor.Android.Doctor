package com.yd.yourdoctorpartnerandroid.utils;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Patient;

import java.net.URISyntaxException;

public class SocketUtils {
    private final static String URL_SERVER = Config.URL_SOCKET;
    private Socket mSocket;
    private static SocketUtils socketUtils;

    public SocketUtils() {
        try {
            mSocket = IO.socket(URL_SERVER);

        } catch (URISyntaxException e) {

        }
    }

    public static SocketUtils getInstance(){
        if(socketUtils == null){
            socketUtils = new SocketUtils();
        }
        return socketUtils;
    }

    public Socket getSocket(){
        return mSocket;
    }

    public void reConnect(){

        if(SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null){
            getInstance().getSocket().connect();
            SocketUtils.getInstance().getSocket().emit("addUser",SharedPrefs.getInstance().get("USER_INFO", Doctor.class).getDoctorId(),2);
        }
    }

    public void disconnectConnect(){
        if(SharedPrefs.getInstance().get("USER_INFO", Doctor.class) != null){
            getInstance().getSocket().disconnect();
        }

    }
}
