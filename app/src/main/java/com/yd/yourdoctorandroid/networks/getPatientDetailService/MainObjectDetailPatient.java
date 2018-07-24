package com.yd.yourdoctorandroid.networks.getPatientDetailService;

import java.util.List;

public class MainObjectDetailPatient {
    private List<InformationPatient> informationPatient;

    public MainObjectDetailPatient(List<InformationPatient> informationPatient) {
        this.informationPatient = informationPatient;
    }

    public List<InformationPatient> getInformationPatient() {
        return informationPatient;
    }

    public void setInformationPatient(List<InformationPatient> informationPatient) {
        this.informationPatient = informationPatient;
    }
}
