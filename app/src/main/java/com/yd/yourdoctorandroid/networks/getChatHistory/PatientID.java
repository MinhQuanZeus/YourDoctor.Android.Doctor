package com.yd.yourdoctorandroid.networks.getChatHistory;

public class PatientID {
    private String _id;
    private String firstName;
    private String lastName;
    private String middleName;
    private String address;
    private String birthday;
    private String phoneNumber;
    private long remainMoney;
    private int gender;
    private String avatar;

    public PatientID() {
    }

    public PatientID(String _id, String firstName, String lastName, String middleName, String address, String birthday, String phoneNumber, long remainMoney, int gender, String avatar) {
        this._id = _id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.middleName = middleName;
        this.address = address;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;
        this.remainMoney = remainMoney;
        this.gender = gender;
        this.avatar = avatar;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public long getRemainMoney() {
        return remainMoney;
    }

    public void setRemainMoney(long remainMoney) {
        this.remainMoney = remainMoney;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
