package com.yd.yourdoctorpartnerandroid.networks.changeProfileDoctor;

import com.yd.yourdoctorpartnerandroid.networks.changeProfileDoctor.UpdateSuccess;

public class DoctorRespone {
    private UpdateSuccess informationDoctor;
    private boolean success;

    public UpdateSuccess getInformationDoctor() {
        return informationDoctor;
    }

    public void setInformationDoctor(UpdateSuccess informationDoctor) {
        this.informationDoctor = informationDoctor;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
