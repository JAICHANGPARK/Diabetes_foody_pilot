package com.dreamwalker.diabetesfoodypilot.model;

public class AppVersion {
    String success;
    String version;

    public AppVersion(String success, String version) {
        this.success = success;
        this.version = version;
    }

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
