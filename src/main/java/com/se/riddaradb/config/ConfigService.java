package com.se.riddaradb.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ConfigService {

    @Value("${app.site-locked}")
    private boolean siteLocked;

    public Config getConfig(){
        Config config = new Config();

        config.setSiteLocked(siteLocked);

        return config;
    }
}
