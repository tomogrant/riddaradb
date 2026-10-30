package com.se.riddaradb.text;

import com.se.riddaradb.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class TextService {

    final TextRepository textRepository;
    final TextMapper textMapper;

    public TextService(TextRepository textRepository, TextMapper textMapper){
        this.textRepository = textRepository;
        this.textMapper = textMapper;
    }

    public TextDto getHomeText(){
        TextEntity textEntity = textRepository.findById(1).orElseThrow(() ->
                new ResourceNotFoundException("Text entity not found"));

        return new TextDto(textEntity.getHomeText());
    }

    public TextDto getAboutText(){
        TextEntity textEntity = textRepository.findById(1).orElseThrow(() ->
                new ResourceNotFoundException("Text entity not found"));

        return new TextDto(textEntity.getAboutText());
    }

    public TextDto saveHomeText(TextDto homeText){
        TextEntity textEntity = textRepository.findById(1).orElse(new TextEntity());

        textEntity.setHomeText(homeText.getText());

        return textMapper.mapToHomeText(textRepository.save(textEntity));
    }

    public TextDto saveAboutText(TextDto aboutText){
        TextEntity textEntity = textRepository.findById(1).orElse(new TextEntity());

        textEntity.setAboutText(aboutText.getText());

        return textMapper.mapToAboutText(textRepository.save(textEntity));
    }

}
