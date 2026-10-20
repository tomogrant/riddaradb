package com.se.riddaradb.character;

import com.se.riddaradb.location.LocationEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CharacterMapper {

    public CharacterDto mapToDto(CharacterEntity characterEntity){
        CharacterDto characterDto = new CharacterDto(characterEntity.getId(), characterEntity.getName(), characterEntity.getSpecies(), characterEntity.getRealWorldRef());

        characterDto.setLocationIds(characterEntity.getLocationEntities()
                .stream()
                .map(LocationEntity::getId)
                .collect(Collectors.toSet()));

        characterDto.setSagaVersionIds(characterEntity.getSagaVersionEntities()
                .stream()
                .map(SagaVersionEntity::getId)
                .collect(Collectors.toSet()));

        return characterDto;
    }

    public CharacterEntity mapFromDto(CharacterDto characterDto){
        return new CharacterEntity(characterDto.getId(), characterDto.getName(), characterDto.getSpecies(), characterDto.getRealWorldRef());
    }
}
