package com.se.riddaradb.character;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;

@RestController
public class CharacterController {

    final CharacterService characterService;
    public CharacterController(CharacterService characterService) {
        this.characterService = characterService;
    }

    @GetMapping("/getcharacters")
    Collection<CharacterDto> getCharacterEntry(){
        return characterService.getCharacterEntries();
    }

    @GetMapping("/getcharacterbyid/{id}")
    CharacterDto getCharacterEntryById(@PathVariable int id){
        return characterService.getCharacterEntryById(id);
    }

    @PostMapping("/postcharacter")
    CharacterDto postCharacterEntry(@RequestBody @Valid CharacterDto characterDto){
        return characterService.saveCharacterEntry(characterDto);
    }

    @PutMapping("/putcharacter")
    CharacterDto putCharacterEntry(@RequestBody @Valid CharacterDto characterDto){
        return characterService.saveCharacterEntry(characterDto);
    }

    @DeleteMapping("/deletecharacter/{id}")
    void deleteCharacterEntry(@PathVariable int id){
        characterService.deleteCharacterEntryById(id);
    }
}
