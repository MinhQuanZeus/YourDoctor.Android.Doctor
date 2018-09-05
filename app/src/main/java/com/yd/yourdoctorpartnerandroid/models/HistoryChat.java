package com.yd.yourdoctorpartnerandroid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yd.yourdoctorpartnerandroid.networks.getChatHistory.PatientID;
import com.yd.yourdoctorpartnerandroid.networks.getListHistoryChat.PatientIdHistory;

public class HistoryChat {
    @SerializedName("_id")
    @Expose
    private String id;
    private int status;
    private PatientIdHistory patientId;
    private String contentTopic;
    private long updatedAt;
    private long createdAt;


    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getContentTopic() {
        return contentTopic;
    }

    public void setContentTopic(String contentTopic) {
        this.contentTopic = contentTopic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public PatientIdHistory getPatientID() {
        return patientId;
    }

    public void setPatientID(PatientIdHistory patientID) {
        this.patientId = patientID;
    }

}
