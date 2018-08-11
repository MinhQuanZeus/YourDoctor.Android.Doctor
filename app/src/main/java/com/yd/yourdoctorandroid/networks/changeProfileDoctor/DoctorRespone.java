package com.yd.yourdoctorandroid.networks.changeProfileDoctor;

import com.yd.yourdoctorandroid.networks.changeProfileDoctor.UpdateSuccess;

public class DoctorRespone {
    private UpdateSuccess updateSuccess;
    private boolean success;

    public UpdateSuccess getUpdateSuccess() {
        return updateSuccess;
    }

    public void setUpdateSuccess(UpdateSuccess updateSuccess) {
        this.updateSuccess = updateSuccess;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
