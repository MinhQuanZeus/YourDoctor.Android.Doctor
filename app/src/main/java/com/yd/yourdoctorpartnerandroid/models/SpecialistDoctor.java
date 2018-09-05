package com.yd.yourdoctorpartnerandroid.models;

public class SpecialistDoctor {
    private String specialistId;
    private String name;

    public SpecialistDoctor(String specialistId, String name) {
        this.specialistId = specialistId;
        this.name = name;
    }

    public String getSpecialistId() {
        return specialistId;
    }

    public void setSpecialistId(String specialistId) {
        this.specialistId = specialistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
