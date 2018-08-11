package com.yd.yourdoctorpartnerandroid.networks.getPatientDetailService;

import com.yd.yourdoctorpartnerandroid.networks.getChatHistory.PatientID;

import java.util.List;

public class InformationPatient {
    private List<String> favoriteDoctors;
    private PatientID patientId;


    public List<String> getFavoriteDoctors() {
        return favoriteDoctors;
    }

    public void setFavoriteDoctors(List<String> favoriteDoctors) {
        this.favoriteDoctors = favoriteDoctors;
    }

    public PatientID getPatientId() {
        return patientId;
    }

    public void setPatientId(PatientID patientId) {
        this.patientId = patientId;
    }
}
