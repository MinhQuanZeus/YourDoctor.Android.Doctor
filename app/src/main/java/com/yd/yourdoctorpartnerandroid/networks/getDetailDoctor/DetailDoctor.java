package com.yd.yourdoctorpartnerandroid.networks.getDetailDoctor;


import com.yd.yourdoctorpartnerandroid.models.Certification;
import com.yd.yourdoctorpartnerandroid.models.SpecialistDoctor;

import java.util.List;

public class DetailDoctor {
    private List<Certification> certificates;
    private List<SpecialistDoctor> idSpecialist;
    private String universityGraduate;
    private String yearGraduate;
    private String placeWorking;
    private float currentRating;

    public List<Certification> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certification> certificates) {
        this.certificates = certificates;
    }

    public List<SpecialistDoctor> getIdSpecialist() {
        return idSpecialist;
    }

    public void setIdSpecialist(List<SpecialistDoctor> idSpecialist) {
        this.idSpecialist = idSpecialist;
    }

    public String getUniversityGraduate() {
        return universityGraduate;
    }

    public void setUniversityGraduate(String universityGraduate) {
        this.universityGraduate = universityGraduate;
    }

    public String getYearGraduate() {
        return yearGraduate;
    }

    public void setYearGraduate(String yearGraduate) {
        this.yearGraduate = yearGraduate;
    }

    public String getPlaceWorking() {
        return placeWorking;
    }

    public void setPlaceWorking(String placeWorking) {
        this.placeWorking = placeWorking;
    }

    public Float getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(Float currentRating) {
        this.currentRating = currentRating;
    }


}
