package com.yd.yourdoctorpartnerandroid.utils;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.yd.yourdoctorpartnerandroid.Constants;
import com.yd.yourdoctorpartnerandroid.models.Doctor;
import com.yd.yourdoctorpartnerandroid.models.Patient;

import java.net.URISyntaxException;

public class SocketUtils {
    private final static String URL_SERVER = Constants.URL_SOCKET;
    private Socket mSocket;
    private static SocketUtils socketUtils;
    private String roomId;

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

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
            if(!getSocket().connected()){
                getInstance().getSocket().connect();
                SocketUtils.getInstance().getSocket().emit("addUser",SharedPrefs.getInstance().get("USER_INFO", Doctor.class).getDoctorId(),2);
                if(roomId != null){
                    getSocket().emit("joinRoom", roomId);
                }
            }

        }
    }

    public Boolean checkIsConnected(){
        if(getSocket().connected()){
            return true;
        }
        return false;
    }

    public void closeConnect(){
        if(SharedPrefs.getInstance().get("USER_INFO", Patient.class) != null){
            getInstance().getSocket().disconnect();
        }

    }
}
