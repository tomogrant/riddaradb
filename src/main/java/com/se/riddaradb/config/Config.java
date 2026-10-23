package com.se.riddaradb.config;

public class Config {

    public Config(){}

    public Config(boolean siteLocked) {
        this.siteLocked = siteLocked;
    }

    private boolean siteLocked;

    public boolean isSiteLocked() {
        return siteLocked;
    }

    public void setSiteLocked(boolean siteLocked) {
        this.siteLocked = siteLocked;
    }
}
