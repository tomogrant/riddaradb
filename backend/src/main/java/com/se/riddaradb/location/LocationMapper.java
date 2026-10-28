package com.se.riddaradb.location;

import com.se.riddaradb.sagaversion.SagaVersionEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class LocationMapper {

    public LocationDto mapToDto(LocationEntity locationEntity){
        LocationDto locationDto = new LocationDto(
                locationEntity.getId(),
                locationEntity.getName(),
                locationEntity.getTranslation(),
                locationEntity.getContinent(),
                locationEntity.getNotes()
        );

        //Character name is added here so that it doesn't have to be retrieved separately in the frontend
        for (LocationCharacterEntity locationCharacterEntity : locationEntity.getLocationCharacterEntities()){
            locationDto.getLocationCharacterDtos().add(new LocationCharacterDto(
                    locationCharacterEntity.getCharacterEntity().getId(),
                    locationCharacterEntity.getCharacterEntity().getName(),
                    locationCharacterEntity.getSignificantAssociation())
            );
        }

        locationDto.setSagaVersionIds(locationEntity.getSagaVersionEntities()
                .stream()
                .map(SagaVersionEntity::getId)
                .collect(Collectors.toSet()));

        return locationDto;
    }
}
