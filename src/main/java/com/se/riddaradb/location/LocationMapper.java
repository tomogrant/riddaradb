package com.se.riddaradb.location;

import com.se.riddaradb.character.CharacterEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class LocationMapper {

    public LocationDto mapToDto(LocationEntity locationEntity){
        LocationDto locationDto = new LocationDto(locationEntity.getId(), locationEntity.getName(), locationEntity.getType());

        locationDto.setCharacterIds(locationEntity.getCharacterEntities()
                .stream()
                .map(CharacterEntity::getId)
                .collect(Collectors.toSet()));

        locationDto.setSagaVersionIds(locationEntity.getSagaVersionEntities()
                .stream()
                .map(SagaVersionEntity::getId)
                .collect(Collectors.toSet()));

        return locationDto;
    }

    public LocationEntity mapFromDto(LocationDto locationDto){
        return new LocationEntity(locationDto.getId(), locationDto.getName(), locationDto.getType());
    }
}
