package com.se.riddaradb.location;

import com.se.riddaradb.character.CharacterRepository;
import com.se.riddaradb.exceptions.ResourceNotFoundException;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import com.se.riddaradb.sagaversion.SagaVersionRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class LocationService {

    final LocationRepository locationRepository;
    final SagaVersionRepository sagaVersionRepository;
    final CharacterRepository characterRepository;
    final LocationMapper locationMapper;
    final LocationCharacterRepository locationCharacterRepository;

    public LocationService(LocationRepository locationRepository,
                           SagaVersionRepository sagaVersionRepository,
                           CharacterRepository characterRepository,
                           LocationMapper locationMapper,
                           LocationCharacterRepository locationCharacterRepository) {
        this.locationRepository = locationRepository;
        this.sagaVersionRepository = sagaVersionRepository;
        this.characterRepository = characterRepository;
        this.locationMapper = locationMapper;
        this.locationCharacterRepository = locationCharacterRepository;
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

        LocationEntity locationEntity = new LocationEntity();

        locationEntity.setName(locationDto.getName());
        locationEntity.setTranslation(locationDto.getTranslation());
        locationEntity.setContinent(locationDto.getContinent());
        locationEntity.setNotes(locationDto.getNotes());

        //Character entries
        for (LocationCharacterDto locationCharacterDto : locationDto.getLocationCharacterDtos()){
            characterRepository.findById(locationCharacterDto.getCharacterId()).ifPresent(characterEntity ->
                    locationEntity.addCharacter(characterEntity, locationCharacterDto.getSignificantAssociation()));
        }

        for (Integer id : locationDto.getSagaVersionIds()){
            sagaVersionRepository.findById(id).ifPresent(sagaVersion -> sagaVersion.addLocation(locationEntity));
        }

        return locationMapper.mapToDto(locationRepository.save(locationEntity));
    }

    public LocationDto updateLocationEntry(LocationDto locationDto){

        LocationEntity locationEntity = locationRepository.findById(locationDto.getId()).orElseThrow(() ->
                new ResourceNotFoundException("Location entity not found in database"));

        locationEntity.setName(locationDto.getName());
        locationEntity.setTranslation(locationDto.getTranslation());
        locationEntity.setContinent(locationDto.getContinent());
        locationEntity.setNotes(locationDto.getNotes());

        updateCharacters(locationEntity, locationDto);
        updateSagaVersions(locationEntity, locationDto);


        return locationMapper.mapToDto(locationRepository.save(locationEntity));
    }


    void updateCharacters(LocationEntity locationEntity, LocationDto locationDto){

        //Map of location-character join entities already associated with this location. Indexed by character ID.
        Map<Integer, LocationCharacterEntity> currentLocationCharacters = locationEntity.getLocationCharacterEntities()
                .stream()
                .collect(Collectors.toMap(locationCharacterEntity -> locationCharacterEntity.getCharacterEntity().getId(), Function.identity()));

        //IDs of characters to be joined to this location
        Set<Integer> newLocationCharacterIds = locationDto.getLocationCharacterDtos()
                .stream()
                .map(LocationCharacterDto::getCharacterId)
                .collect(Collectors.toSet());

        //For each location-character entity attached to location...
        for (LocationCharacterDto locationCharacterDto : locationDto.getLocationCharacterDtos()){
            LocationCharacterEntity locationCharacterCurrent = currentLocationCharacters.get(locationCharacterDto.getCharacterId());

            //Location-character exists already. Update.
            if (locationCharacterCurrent != null){
                locationCharacterCurrent.setSignificantAssociation(locationCharacterDto.getSignificantAssociation());
            }

            //Location-character entity does not exist in DB. Fetch character and attach to location.
            else{
                characterRepository.findById(locationCharacterDto.getCharacterId()).ifPresent(character -> {
                    locationEntity.addCharacter(character, locationCharacterDto.getSignificantAssociation());
                });
            }
        }

        //If set of IDs does not include characters previously associated with
        // this location, remove them.
        locationCharacterRepository.findByLocationEntityId(locationDto.getId()).forEach(locationCharacter -> {
            if (!newLocationCharacterIds.contains(locationCharacter.getCharacterEntity().getId())){
                locationEntity.removeCharacter(locationCharacter.getCharacterEntity());
            }
        });
    }

    private void updateSagaVersions(LocationEntity locationEntity, LocationDto locationDto){

        Set<Integer> newIds = locationDto.getSagaVersionIds();

        for (SagaVersionEntity sagaVersionEntity : locationEntity.getSagaVersionEntities()){
            if (!newIds.contains(sagaVersionEntity.getId())){
                sagaVersionEntity.removeLocation(locationEntity);
            }
        }

        for (Integer sagaVersionId : newIds){
            sagaVersionRepository.findById(sagaVersionId).ifPresent(sagaVersion ->
                    sagaVersion.addLocation(locationEntity));
        }
    }

    public void deleteLocationEntryById(int id) {

        //Remove locations from characters
        locationRepository.findById(id).ifPresent(locationEntity -> {
            for (LocationCharacterEntity locationCharacterEntity : new HashSet<>(locationEntity.getLocationCharacterEntities())) {
                locationEntity.removeCharacter(locationCharacterEntity.getCharacterEntity());
            };

            locationEntity.getSagaVersionEntities().forEach(sagaVersion -> {
                sagaVersion.removeLocation(locationEntity);
            });

            locationRepository.delete(locationEntity);
        });
    }
}


