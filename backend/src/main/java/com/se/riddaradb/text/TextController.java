package com.se.riddaradb.text;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/text/")
public class TextController {

    final TextService textService;

    public TextController(TextService textService){
        this.textService = textService;
    }

    @GetMapping("gethometext")
    TextDto getHomeText(){
        return textService.getHomeText();
    }

    @GetMapping("getabouttext")
    TextDto getAboutText(){
        return textService.getAboutText();
    }

    @PutMapping("savehometext")
    TextDto saveHomeText(@RequestBody @Valid TextDto homeText){
        return textService.saveHomeText(homeText);
    }

    @PutMapping("saveabouttext")
    TextDto saveAboutText(@RequestBody @Valid TextDto aboutText){
        return textService.saveAboutText(aboutText);
    }
}
