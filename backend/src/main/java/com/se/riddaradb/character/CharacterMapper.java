package com.se.riddaradb.character;

import com.se.riddaradb.location.LocationCharacterEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CharacterMapper {

    public CharacterDto mapToDto(CharacterEntity characterEntity){
        CharacterDto characterDto = new CharacterDto(
                characterEntity.getId(),
                characterEntity.getName(),
                characterEntity.getSpecies(),
                characterEntity.getSex(),
                characterEntity.getNotes()
        );

        for (LocationCharacterEntity locationCharacterEntity : characterEntity.getLocationCharacterEntities()){
            characterDto.getCharacterLocationDtos().add(new CharacterLocationDto(
                    locationCharacterEntity.getLocationEntity().getId(),
                    locationCharacterEntity.getLocationEntity().getName(),
                    locationCharacterEntity.getSignificantAssociation())
            );
        }

        characterDto.setSagaVersionIds(characterEntity.getSagaVersionEntities()
                .stream()
                .map(SagaVersionEntity::getId)
                .collect(Collectors.toSet()));

        return characterDto;
    }
}
