package com.se.riddaradb.location;

import com.se.riddaradb.sagaversion.SagaVersionEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class LocationMapper {

    public LocationDto mapToDto(LocationEntity locationEntity){
        LocationDto locationDto = new LocationDto(locationEntity.getId(), locationEntity.getName());

        for (LocationCharacterEntity locationCharacterEntity : locationEntity.getLocationCharacterEntities()){
            locationDto.getLocationCharacterDtos().add(new LocationCharacterDto(
                    locationCharacterEntity.getCharacterEntity().getId(),
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
