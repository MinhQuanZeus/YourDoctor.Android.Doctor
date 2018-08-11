package com.yd.yourdoctorandroid.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.yd.yourdoctorandroid.networks.getChatHistory.PatientID;
import com.yd.yourdoctorandroid.networks.getListHistoryChat.PatientIdHistory;

public class HistoryChat {
    @SerializedName("_id")
    @Expose
    private String id;
    private int status;
    private PatientIdHistory patientId;
    private String contentTopic;
    private String updatedAt;
    private String createdAt;


    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
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

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
