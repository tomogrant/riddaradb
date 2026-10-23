package com.se.riddaradb.config;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/config/")
public class ConfigController {

    final ConfigService configService;

    public ConfigController(ConfigService configService){
        this.configService = configService;
    }

    @GetMapping("getconfig")
    public Config getConfig(){
        return configService.getConfig();
    }
}