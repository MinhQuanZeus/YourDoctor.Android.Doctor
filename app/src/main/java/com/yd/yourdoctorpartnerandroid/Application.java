package com.yd.yourdoctorpartnerandroid;

import com.google.gson.Gson;

public class Application extends android.app.Application{
    private static Application mSelf;
    private Gson mGSon;

    public static Application self() {
        return mSelf;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSelf = this;
        mGSon = new Gson();
    }

    public Gson getGSon() {
        return mGSon;
    }
}
