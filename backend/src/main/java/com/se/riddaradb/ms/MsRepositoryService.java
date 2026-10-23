package com.se.riddaradb.ms;

import com.se.riddaradb.exceptions.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class MsRepositoryService {

    final MsRepositoryMapper msRepositoryMapper;
    final MsRepositoryRepository msRepositoryRepository;
    final MsRepository msRepository;

    MsRepositoryService(MsRepositoryMapper msRepositoryMapper, MsRepository msRepository, MsRepositoryRepository msRepositoryRepository){
        this.msRepositoryMapper = msRepositoryMapper;
        this.msRepository = msRepository;
        this.msRepositoryRepository = msRepositoryRepository;
    }

    Set<MsRepositoryDto> getMsRepositories(){
        return msRepositoryRepository.findAll()
                .stream()
                .map(msRepositoryMapper::mapToDto)
                .collect(Collectors.toSet());
    }

    MsRepositoryDto getMsRepositoryById(int id){
        return msRepositoryMapper.mapToDto(msRepositoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("MS repo not found in database")));
    }

    MsRepositoryDto saveMsRepository(MsRepositoryDto msRepositoryDto){
        MsRepositoryEntity msRepositoryEntity = new MsRepositoryEntity();

        msRepositoryEntity.setId(msRepositoryDto.getId());
        msRepositoryEntity.setName(msRepositoryDto.getName());
        msRepositoryEntity.setCity(msRepositoryDto.getCity());
        msRepositoryEntity.setCountry(msRepositoryDto.getCountry());

        return msRepositoryMapper.mapToDto(msRepositoryRepository.save(msRepositoryEntity));
    }

    MsRepositoryDto updateMsRepository(MsRepositoryDto msRepositoryDto){
        MsRepositoryEntity msRepositoryEntity = msRepositoryRepository.findById(msRepositoryDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("MS repo not found in database"));

        msRepositoryEntity.setName(msRepositoryDto.getName());
        msRepositoryEntity.setCity(msRepositoryDto.getCity());
        msRepositoryEntity.setCountry(msRepositoryDto.getCountry());

        return msRepositoryMapper.mapToDto(msRepositoryEntity);
    }

    void deleteMsRepositoryById(int id) throws AccessDeniedException {

        boolean isAdmin = Objects.requireNonNull(SecurityContextHolder
                        .getContext()
                        .getAuthentication())
                .getAuthorities()
                .stream()
                .anyMatch(auth ->
                        Objects.equals(auth.getAuthority(), "ROLE_ADMINISTRATOR"));

        MsRepositoryEntity msRepositoryEntity = msRepositoryRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("MS repo not found in DB"));

        //Admins can delete all MS repositories. Regular users can only delete them if they're empty.
        if (isAdmin || msRepositoryEntity.getMsEntities().isEmpty()){
            msRepositoryRepository.deleteById(id);
        }
        else {
            throw new AccessDeniedException("Unathorised MS repo deletion request");
        }

    }

    void deleteAll(){ msRepositoryRepository.deleteAll(); }

}
