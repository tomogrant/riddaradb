package com.se.riddaradb.ms;

import com.se.riddaradb.saga.SagaMsDto;
import com.se.riddaradb.saga.SagaMsEntity;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class MsMapper {

    public MsDto mapToDto(MsEntity msEntity){
        MsDto msDto = new MsDto(msEntity.getId(), msEntity.getName(), msEntity.getShelfmark(), msEntity.getDate(), msEntity.getEntryLink(), msEntity.getDescription());

        //Set saga entries
        for (SagaMsEntity sagaMsEntity : msEntity.getSagaMsEntities()){
            msDto.getMsSagaDtos().add(new MsSagaDto
                    (sagaMsEntity.getSagaEntity().getId(),
                            sagaMsEntity.getFolioNumber(),
                            sagaMsEntity.getNote()));
        }

        if (msEntity.getMsRepositoryEntity() != null){
            msDto.setMsRepositoryId(msEntity.getMsRepositoryEntity().getId());
        }
        else{
            msDto.setMsRepositoryId(null);
        }

        return msDto;
    }
}
