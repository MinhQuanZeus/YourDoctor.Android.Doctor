package com.yd.yourdoctorandroid.networks.getPatientDetailService;

import com.yd.yourdoctorandroid.networks.getChatHistory.PatientID;

import java.util.List;

public class InformationPatient {
    private List<String> favoriteDoctors;
    private String _id;
    private PatientID patientId;

    public InformationPatient(List<String> favoriteDoctors, String _id, PatientID patientId) {
        this.favoriteDoctors = favoriteDoctors;
        this._id = _id;
        this.patientId = patientId;
    }

    public List<String> getFavoriteDoctors() {
        return favoriteDoctors;
    }

    public void setFavoriteDoctors(List<String> favoriteDoctors) {
        this.favoriteDoctors = favoriteDoctors;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public PatientID getPatientId() {
        return patientId;
    }

    public void setPatientId(PatientID patientId) {
        this.patientId = patientId;
    }
}
