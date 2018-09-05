package com.yd.yourdoctorpartnerandroid.networks.changeProfileDoctor;

public class DoctorRequest {
    private String doctorId;
    private String birthday;
    private String address;
    private String placeWorking;
    private String avatar;
    private int gender;

    public DoctorRequest(String id,String birthday, String address, String placeWorking, String avatar, int gender) {
        this.doctorId = id;
        this.birthday = birthday;
        this.address = address;
        this.placeWorking = placeWorking;
        this.avatar = avatar;
        this.gender = gender;
    }

    public String getId() {
        return doctorId;
    }

    public void setId(String id) {
        this.doctorId = id;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceWorking() {
        return placeWorking;
    }

    public void setPlaceWorking(String placeWorking) {
        this.placeWorking = placeWorking;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }
}
