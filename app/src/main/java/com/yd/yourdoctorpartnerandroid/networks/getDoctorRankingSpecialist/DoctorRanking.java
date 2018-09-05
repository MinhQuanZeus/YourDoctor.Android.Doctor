package com.yd.yourdoctorpartnerandroid.networks.getDoctorRankingSpecialist;

public class DoctorRanking {

    private DoctorID doctorId;

    private float currentRating;

    public DoctorID getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(DoctorID doctorId) {
        this.doctorId = doctorId;
    }

    public float getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(float currentRating) {
        this.currentRating = currentRating;
    }
}
