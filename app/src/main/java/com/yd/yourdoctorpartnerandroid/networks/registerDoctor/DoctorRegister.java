package com.yd.yourdoctorpartnerandroid.networks.registerDoctor;

import com.yd.yourdoctorpartnerandroid.models.Certification;
import com.yd.yourdoctorpartnerandroid.models.Specialist;

import java.util.ArrayList;

public class DoctorRegister {

    private String phoneNumber;
    private String password;
    private String firstName;
    private String middleName;
    private String lastName;
    private String avatar;
    private String birthday;
    private int gender;
    private String address;
    private int status;
    private float remainMoney;
    private float currentRating;
    private ArrayList<CertificateRequest> certificates;
    private ArrayList<SpecialistDoctor> idSpecialist;
    private String universityGraduate;
    private String yearGraduate;
    private String placeWorking;

    public DoctorRegister() {
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public float getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(float remainMoney) {
        this.remainMoney = remainMoney;
    }

    public float getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(float currentRating) {
        this.currentRating = currentRating;
    }

    public ArrayList<CertificateRequest> getCertificates() {
        return certificates;
    }

    public void setCertificates(ArrayList<CertificateRequest> certificates) {
        this.certificates = certificates;
    }

    public ArrayList<SpecialistDoctor> getIdSpecialist() {
        return idSpecialist;
    }

    public void setIdSpecialist(ArrayList<SpecialistDoctor> idSpecialist) {
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
}
