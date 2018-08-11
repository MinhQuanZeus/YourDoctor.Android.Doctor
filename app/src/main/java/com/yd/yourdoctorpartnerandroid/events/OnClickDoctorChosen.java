package com.yd.yourdoctorpartnerandroid.events;


import com.yd.yourdoctorpartnerandroid.networks.models.Doctor;

public class OnClickDoctorChosen {
    private Doctor doctorModel;

    public OnClickDoctorChosen(Doctor doctorModel) {
        this.doctorModel = doctorModel;
    }

    public Doctor getdoctorChosenModel() {
        return doctorModel;
    }

    public void setDoctorModelModel(Doctor doctorModel) {
        this.doctorModel = doctorModel;
    }
}