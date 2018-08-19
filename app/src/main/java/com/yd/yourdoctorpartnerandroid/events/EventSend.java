package com.yd.yourdoctorpartnerandroid.events;

public class EventSend {
    // 1: thay doi thong tin nguoi dung hien tai
    // 2: thay doi noi dung chat
    //3 : update list notify ở màn notification
    private int type;

    public EventSend(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
