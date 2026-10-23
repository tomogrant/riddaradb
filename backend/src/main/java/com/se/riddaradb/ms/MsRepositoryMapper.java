package com.se.riddaradb.ms;

import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class MsRepositoryMapper {

    public MsRepositoryDto mapToDto(MsRepositoryEntity msRepositoryEntity){
        MsRepositoryDto msRepositoryDto = new MsRepositoryDto();

        msRepositoryDto.setId(msRepositoryEntity.getId());
        msRepositoryDto.setName(msRepositoryEntity.getName());
        msRepositoryDto.setCity(msRepositoryEntity.getCity());
        msRepositoryDto.setCountry(msRepositoryEntity.getCountry());

        msRepositoryDto.setMsIds(msRepositoryEntity.getMsEntities()
                .stream()
                .map(MsEntity::getId)
                .collect(Collectors.toSet()));

        return msRepositoryDto;
    }

}
