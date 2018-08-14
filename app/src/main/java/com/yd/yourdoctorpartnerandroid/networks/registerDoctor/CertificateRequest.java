package com.yd.yourdoctorpartnerandroid.networks.registerDoctor;

public class CertificateRequest {
    private String name;
    private String pathImage;

    public CertificateRequest(String name, String pathImage) {
        this.name = name;
        this.pathImage = pathImage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPathImage() {
        return pathImage;
    }

    public void setPathImage(String pathImage) {
        this.pathImage = pathImage;
    }
}
