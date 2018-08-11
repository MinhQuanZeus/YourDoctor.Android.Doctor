package com.yd.yourdoctorandroid.networks.getDetailDoctor;


import com.yd.yourdoctorandroid.models.Certification;
import com.yd.yourdoctorandroid.models.Specialist;

import java.util.List;

public class DetailDoctor {
    private List<Certification> certificates;

    public List<Certification> getCertificates() {
        return certificates;
    }

    public void setCertificates(List<Certification> certificates) {
        this.certificates = certificates;
    }

    public List<Specialist> getIdSpecialist() {
        return idSpecialist;
    }

    public void setIdSpecialist(List<Specialist> idSpecialist) {
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

    private List<Specialist> idSpecialist;
    private String universityGraduate;
    private String yearGraduate;
    private String placeWorking;
    private float currentRating;
}
