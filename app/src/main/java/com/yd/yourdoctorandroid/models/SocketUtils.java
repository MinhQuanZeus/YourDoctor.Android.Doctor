package com.yd.yourdoctorandroid.models;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.yd.yourdoctorandroid.utils.SharedPrefs;

import java.net.URISyntaxException;

public class SocketUtils {
    //http://192.168.124.109:3000
    //https://your-doctor-test2.herokuapp.com
    private final static String URL_SERVER = "http://192.168.124.103:3000";
    private Socket mSocket;
    private static com.yd.yourdoctorandroid.utils.SocketUtils socketUtils;

    public SocketUtils() {
        try {
            mSocket = IO.socket(URL_SERVER);

        } catch (URISyntaxException e) {

        }
    }

    public static com.yd.yourdoctorandroid.utils.SocketUtils getInstance(){
        if(socketUtils == null){
            socketUtils = new com.yd.yourdoctorandroid.utils.SocketUtils();
        }
        return socketUtils;
    }

    public Socket getSocket(){
        return mSocket;
    }

    public void reConnect(){
        getInstance().getSocket().connect();
        if(SharedPrefs.getInstance().get("USER_INFO", Patient.class) != null)
        com.yd.yourdoctorandroid.utils.SocketUtils.getInstance().getSocket().emit("addUser", SharedPrefs.getInstance().get("USER_INFO", Patient.class).getId());
    }

    public void disconnectConnect(){
        getInstance().getSocket().disconnect();
    }
}
