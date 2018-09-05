package com.yd.yourdoctorpartnerandroid.utils;

import android.content.Context;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.util.Log;
import android.widget.Toast;

import com.yd.yourdoctorpartnerandroid.models.Specialist;
import com.yd.yourdoctorpartnerandroid.models.TypeAdvisory;
import com.yd.yourdoctorpartnerandroid.networks.RetrofitFactory;
import com.yd.yourdoctorpartnerandroid.networks.getAllTypesAdvisory.GetAllTypesAdvisoryService;
import com.yd.yourdoctorpartnerandroid.networks.getAllTypesAdvisory.MainObjectTypeAdivosry;
import com.yd.yourdoctorpartnerandroid.networks.getSpecialistService.GetSpecialistService;
import com.yd.yourdoctorpartnerandroid.networks.getSpecialistService.MainObjectSpecialist;
import com.yd.yourdoctorpartnerandroid.services.CheckNetWordChangeService;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoadDefaultModel {

    private List<Specialist> specialists;
    private List<TypeAdvisory> typeAdvisories;

    private static LoadDefaultModel loadDefaultModel;

    private CheckNetWordChangeService checkNetWordChangeService;
    private IntentFilter intentFilter;

    public LoadDefaultModel() {
        intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        checkNetWordChangeService = new CheckNetWordChangeService();
    }

    public void registerServiceCheckNetwork(Context context){
        context.registerReceiver(checkNetWordChangeService, intentFilter);
    }

    public void unregisterServiceCheckNetwork(Context context){
        context.unregisterReceiver(checkNetWordChangeService);
    }

    public List<Specialist> getSpecialists() {

        return specialists;
    }

    public List<TypeAdvisory> getTypeAdvisories() {

        return typeAdvisories;
    }

    public void setSpecialists(List<Specialist> specialists) {
        this.specialists = specialists;
    }

    public void setTypeAdvisories(List<TypeAdvisory> typeAdvisories) {
        this.typeAdvisories = typeAdvisories;
    }


    public static LoadDefaultModel getInstance() {
        if (loadDefaultModel == null) {
            loadDefaultModel = new LoadDefaultModel();
        }
        return loadDefaultModel;
    }

}
