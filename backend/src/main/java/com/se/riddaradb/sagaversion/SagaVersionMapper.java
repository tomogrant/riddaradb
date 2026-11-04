package com.se.riddaradb.sagaversion;

import com.se.riddaradb.character.CharacterMapper;
import com.se.riddaradb.location.LocationMapper;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class SagaVersionMapper {

    final CharacterMapper characterMapper;
    final LocationMapper locationMapper;

    public SagaVersionMapper(CharacterMapper characterMapper, LocationMapper locationMapper){
        this.characterMapper = characterMapper;
        this.locationMapper = locationMapper;
    }

    //Consumes DB entity and produces response for frontend
    public SagaVersionResponseDto mapToDto(SagaVersionEntity sagaVersionEntity){
        SagaVersionResponseDto sagaVersionResponseDto = new SagaVersionResponseDto(
                sagaVersionEntity.getId(),
                sagaVersionEntity.getTitle(),
                sagaVersionEntity.getAbbreviation(),
                sagaVersionEntity.getDescription(),
                sagaVersionEntity.getDate());

        if (sagaVersionEntity.getSagaEntity() != null)
            sagaVersionResponseDto.setSagaId(sagaVersionEntity.getSagaEntity().getId());

        for (SagaVersionMotifEntity sagaVersionMotifEntity : sagaVersionEntity.getSagaVersionMotifEntities()){
            sagaVersionResponseDto.getSagaMotifs().add(new SagaVersionMotifDto
                    (sagaVersionMotifEntity.getMotifEntity().getId(),
                            sagaVersionMotifEntity.getMotifEntity().getMotifCode(),
                            sagaVersionMotifEntity.getMotifEntity().getMotifName(),
                            sagaVersionMotifEntity.getPageChapterNumber(),
                            sagaVersionMotifEntity.getInBoberg()));
        }

        sagaVersionResponseDto.setCharacterNames(sagaVersionEntity.getCharacterEntities()
                .stream()
                .map(characterMapper::mapToNameDto)
                .collect(Collectors.toSet()));

        sagaVersionResponseDto.setLocationNames(sagaVersionEntity.getLocationEntities()
                .stream()
                .map(locationMapper::mapToNameDto)
                .collect(Collectors.toSet()));

        return sagaVersionResponseDto;
    }

    public SagaVersionTitleDto mapToTitleDto(SagaVersionEntity sagaVersionEntity){
        return new SagaVersionTitleDto(
                sagaVersionEntity.getId(),
                sagaVersionEntity.getSagaEntity().getId(),
                sagaVersionEntity.getSagaEntity().getPublished(),
                sagaVersionEntity.getTitle(),
                sagaVersionEntity.getAbbreviation()
        );
    }
}
