package com.se.riddaradb.location;

import com.se.riddaradb.character.CharacterRepository;
import com.se.riddaradb.sagaversion.SagaVersionRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;

@Service
public class LocationService {

    final LocationRepository locationRepository;
    final SagaVersionRepository sagaVersionRepository;
    final CharacterRepository characterRepository;
    final LocationMapper locationMapper;

    public LocationService(LocationRepository locationRepository, SagaVersionRepository sagaVersionRepository, CharacterRepository characterRepository, LocationMapper locationMapper) {
        this.locationRepository = locationRepository;
        this.sagaVersionRepository = sagaVersionRepository;
        this.characterRepository = characterRepository;
        this.locationMapper = locationMapper;
    }

    public Collection<LocationDto> getLocationEntries(){
        return locationRepository.findAll()
                .stream()
                .map(locationMapper::mapToDto)
                .toList();
    }

    public LocationDto getLocationEntryById(int id){
        if (locationRepository.findById(id).isPresent()){
            return locationMapper.mapToDto(locationRepository.findById(id).get());
        }
        else {
            return null;
        }
    }

    public LocationDto saveLocationEntry(LocationDto locationDto){
        LocationEntity locationEntity = locationMapper.mapFromDto(locationDto);
        locationEntity.setSagaVersionEntities(new HashSet<>(sagaVersionRepository.findAllById(locationDto.getSagaVersionIds())));
        locationEntity.setCharacterEntities(new HashSet<>(characterRepository.findAllById(locationDto.getCharacterIds())));
        return locationMapper.mapToDto(locationRepository.save(locationEntity));
    }

    public void deleteLocationEntryById(int id) {

        locationRepository.deleteById(id);
    }

}


