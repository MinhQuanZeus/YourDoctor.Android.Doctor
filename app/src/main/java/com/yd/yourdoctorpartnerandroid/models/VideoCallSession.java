package com.yd.yourdoctorpartnerandroid.models;

import java.io.Serializable;

public class VideoCallSession implements Serializable {
    private String callerId;
    private String callerName;
    private String callerAvatar;
    private String calleeId;
    private String calleeName;
    private String calleeAvatar;
    private TypeCall type;

    public VideoCallSession(String callerId, String callerName, String callerAvatar, String calleeId, String calleeName, String calleeAvatar, TypeCall type) {
        this.callerId = callerId;
        this.callerName = callerName;
        this.callerAvatar = callerAvatar;
        this.calleeId = calleeId;
        this.calleeName = calleeName;
        this.calleeAvatar = calleeAvatar;
        this.type = type;
    }

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

    public String getCallerName() {
        return callerName;
    }

    public void setCallerName(String callerName) {
        this.callerName = callerName;
    }

    public String getCallerAvatar() {
        return callerAvatar;
    }

    public void setCallerAvatar(String callerAvatar) {
        this.callerAvatar = callerAvatar;
    }

    public String getCalleeId() {
        return calleeId;
    }

    public void setCalleeId(String calleeId) {
        this.calleeId = calleeId;
    }

    public String getCalleeName() {
        return calleeName;
    }

    public void setCalleeName(String calleeName) {
        this.calleeName = calleeName;
    }

    public String getCalleeAvatar() {
        return calleeAvatar;
    }

    public void setCalleeAvatar(String calleeAvatar) {
        this.calleeAvatar = calleeAvatar;
    }

    public TypeCall getType() {
        return type;
    }

    public void setType(TypeCall type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "VideoCallSession{" +
                "callerId='" + callerId + '\'' +
                ", callerName='" + callerName + '\'' +
                ", callerAvatar='" + callerAvatar + '\'' +
                ", calleeId='" + calleeId + '\'' +
                ", calleeName='" + calleeName + '\'' +
                ", calleeAvatar='" + calleeAvatar + '\'' +
                ", type=" + type +
                '}';
    }
}
