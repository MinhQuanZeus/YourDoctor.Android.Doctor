package com.yd.yourdoctorpartnerandroid.networks.getHistoryVideoCall;


import com.yd.yourdoctorpartnerandroid.networks.getChatHistory.PatientID;

public class ObjectHistoryVideo {
    private String _id;
    private long timeStart;
    private long timeEnd;
    private String linkVideo;
    private PatientID patientId;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public long getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(long timeStart) {
        this.timeStart = timeStart;
    }

    public long getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(long timeEnd) {
        this.timeEnd = timeEnd;
    }

    public String getLinkVideo() {
        return linkVideo;
    }

    public void setLinkVideo(String linkVideo) {
        this.linkVideo = linkVideo;
    }

    public PatientID getPatientId() {
        return patientId;
    }

    public void setPatientId(PatientID patientId) {
        this.patientId = patientId;
    }
}
