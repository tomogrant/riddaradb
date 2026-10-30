package com.se.riddaradb.text;

import org.springframework.stereotype.Service;

@Service
public class TextMapper {


    public TextDto mapToHomeText(TextEntity textEntity){
        return new TextDto(textEntity.getHomeText());
    }

    public TextDto mapToAboutText(TextEntity textEntity){
        return new TextDto(textEntity.getAboutText());
    }
}
