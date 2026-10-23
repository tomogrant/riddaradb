package com.se.riddaradb.sagaversion;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class SagaVersionService {

    final SagaVersionRepository sagaVersionRepository;
    final SagaVersionMapper sagaVersionMapper;

    public SagaVersionService(SagaVersionRepository sagaVersionRepository, SagaVersionMapper sagaVersionMapper) {
        this.sagaVersionRepository = sagaVersionRepository;
        this.sagaVersionMapper = sagaVersionMapper;
    }

    public Set<SagaVersionResponseDto> getSagaVersions(){
        return sagaVersionRepository.findAll()
                .stream()
                .map(sagaVersionMapper::mapToDto)
                .collect(Collectors.toSet());
    }

    public Set<SagaVersionTitleDto> getSagaVersionTitles(){
        return sagaVersionRepository.findAll()
                .stream()
                .map(sagaVersionMapper::mapToTitleDto)
                .collect(Collectors.toSet());
    }

    public SagaVersionResponseDto getSagaVersionById(int id){
        if (sagaVersionRepository.findById(id).isPresent()){
            return sagaVersionMapper.mapToDto(sagaVersionRepository.findById(id).get());
        }
        else {
            return null;
        }
    }

    public void deleteSagaVersionById(int id) {

        if (sagaVersionRepository.existsById(id)) {
            sagaVersionRepository.deleteById(id);
        } else {
            System.out.println("Record not found in database.");
        }
    }
}
