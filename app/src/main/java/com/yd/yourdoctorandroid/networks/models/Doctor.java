package com.yd.yourdoctorandroid.networks.models;

import java.util.ArrayList;
import java.util.Date;

public class Doctor {
    private String id;
    private String phoneNumber;
    private String password;
    private String lastName;
    private String firstName;
    private String middleName;
    private Date birthday;
    private String address;
    private int status;
    private String avatar;
    private long remain_money;
    private float currentRating;
    private ArrayList<Certification> certificates;
    private ArrayList<String> idSpecialist;
    private String universityGraduate;
    private int yearGraduate;
    private String placeWorking;

    public Doctor(String phoneNumber, String password, String lastName, String firstName, String middleName, Date birthday, String address, long remain_money, float currentRating, ArrayList<String> idSpecialist, String universityGraduate, int yearGraduate, String placeWorking) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.birthday = birthday;
        this.address = address;
        this.remain_money = remain_money;
        this.currentRating = currentRating;
        this.idSpecialist = idSpecialist;
        this.universityGraduate = universityGraduate;
        this.yearGraduate = yearGraduate;
        this.placeWorking = placeWorking;
    }

    public Doctor() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
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

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public long getRemain_money() {
        return remain_money;
    }

    public void setRemain_money(long remain_money) {
        this.remain_money = remain_money;
    }

    public float getCurrentRating() {
        return currentRating;
    }

    public void setCurrentRating(float currentRating) {
        this.currentRating = currentRating;
    }

    public ArrayList<Certification> getCertificates() {
        return certificates;
    }

    public void setCertificates(ArrayList<Certification> certificates) {
        this.certificates = certificates;
    }

    public ArrayList<String> getIdSpecialist() {
        return idSpecialist;
    }

    public void setIdSpecialist(ArrayList<String> idSpecialist) {
        this.idSpecialist = idSpecialist;
    }

    public String getUniversityGraduate() {
        return universityGraduate;
    }

    public void setUniversityGraduate(String universityGraduate) {
        this.universityGraduate = universityGraduate;
    }

    public int getYearGraduate() {
        return yearGraduate;
    }

    public void setYearGraduate(int yearGraduate) {
        this.yearGraduate = yearGraduate;
    }

    public String getPlaceWorking() {
        return placeWorking;
    }

    public void setPlaceWorking(String placeWorking) {
        this.placeWorking = placeWorking;
    }
}