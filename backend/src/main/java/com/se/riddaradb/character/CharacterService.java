package com.se.riddaradb.character;

import com.se.riddaradb.exceptions.ResourceNotFoundException;
import com.se.riddaradb.location.*;
import com.se.riddaradb.sagaversion.SagaVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Transactional
@Service
public class CharacterService {

    final LocationRepository locationRepository;
    final SagaVersionRepository sagaVersionRepository;
    final CharacterRepository characterRepository;
    final CharacterMapper characterMapper;
    final LocationCharacterRepository locationCharacterRepository;

    public CharacterService(LocationRepository locationRepository, SagaVersionRepository sagaVersionRepository, CharacterRepository characterRepository, CharacterMapper characterMapper, LocationCharacterRepository locationCharacterRepository) {
        this.locationRepository = locationRepository;
        this.sagaVersionRepository = sagaVersionRepository;
        this.characterRepository = characterRepository;
        this.characterMapper = characterMapper;
        this.locationCharacterRepository = locationCharacterRepository;
    }

    public Collection<CharacterDto> getCharacterEntries(){
        return characterRepository.findAll()
                .stream()
                .map(characterMapper::mapToDto)
                .toList();
    }

    public CharacterDto getCharacterEntryById(int id){
        if (characterRepository.findById(id).isPresent()){
            return characterMapper.mapToDto(characterRepository.findById(id).get());
        }
        else {
            return null;
        }
    }

    public CharacterDto saveCharacterEntry(CharacterDto characterDto){
        CharacterEntity characterEntity = new CharacterEntity();

        characterEntity.setName(characterDto.getName());
        characterEntity.setSpecies(characterDto.getSpecies());
        characterEntity.setSex(characterDto.getSex());

        //Locations
        for (CharacterLocationDto characterLocationDto : characterDto.getCharacterLocationDtos()){
            locationRepository.findById(characterLocationDto.getLocationId()).ifPresent(locationEntity ->
                    locationEntity.addCharacter(characterEntity, characterLocationDto.getSignificantAssociation()));
        }

        characterEntity.setSagaVersionEntities(new HashSet<>(sagaVersionRepository.findAllById(characterDto.getSagaVersionIds())));
        return characterMapper.mapToDto(characterRepository.save(characterEntity));
    }

    public CharacterDto updateCharacterEntry(CharacterDto characterDto){
        CharacterEntity characterEntity = characterRepository.findById(characterDto.getId()).orElseThrow(()
                -> new ResourceNotFoundException("Character entry not found in DB"));

        characterEntity.setId(characterDto.getId());
        characterEntity.setName(characterDto.getName());
        characterEntity.setSpecies(characterDto.getSpecies());
        characterEntity.setSex(characterDto.getSex());
        characterEntity.setSagaVersionEntities(new HashSet<>(sagaVersionRepository.findAllById(characterDto.getSagaVersionIds())));

        updateLocation(characterEntity, characterDto);

        return characterMapper.mapToDto(characterRepository.save(characterEntity));
    }

    void updateLocation(CharacterEntity characterEntity, CharacterDto characterDto) {

        //Map of location-character join entities already associated with this character. Indexed by location ID.
        Map<Integer, LocationCharacterEntity> currentLocationCharacters = characterEntity.getLocationCharacterEntities()
                .stream()
                .collect(Collectors.toMap(locationCharacterEntity -> locationCharacterEntity.getLocationEntity().getId(), Function.identity()));

        //IDs of locations to be joined to this character
        Set<Integer> newCharacterLocationIds = characterDto.getCharacterLocationDtos()
                .stream()
                .map(CharacterLocationDto::getLocationId)
                .collect(Collectors.toSet());

        //For each location-character entity attached to character...
        for (CharacterLocationDto characterLocationDto : characterDto.getCharacterLocationDtos()) {
            LocationCharacterEntity locationCharacterCurrent = currentLocationCharacters.get(characterLocationDto.getLocationId());

            //Location-character entity exists already. Update.
            if (locationCharacterCurrent != null) {
                locationCharacterCurrent.setSignificantAssociation(characterLocationDto.getSignificantAssociation());
            }

            //Location-character entity does not exist yet. Add.
            else {
                locationRepository.findById(characterLocationDto.getLocationId()).ifPresent(locationEntity -> {
                    locationEntity.addCharacter(characterEntity, characterLocationDto.getSignificantAssociation());
                });
            }
        }

        //If set of IDs does not include locations previously associated with
        // this character, remove them.
        locationCharacterRepository.findByCharacterEntityId(characterDto.getId()).forEach(locationCharacter -> {
            if (!newCharacterLocationIds.contains(locationCharacter.getLocationEntity().getId())) {
                locationRepository.findById(locationCharacter.getLocationEntity().getId()).ifPresent(locationEntity
                        -> locationEntity.removeCharacter(characterEntity)
                );
            }
        });
    }

    public void deleteCharacterEntryById(int id) {
        locationRepository.deleteById(id);
    }
}


