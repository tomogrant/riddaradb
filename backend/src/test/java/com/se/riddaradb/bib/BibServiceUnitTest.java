package com.se.riddaradb.bib;

import com.se.riddaradb.saga.SagaEntity;
import com.se.riddaradb.saga.SagaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BibServiceUnitTest {

    @Mock
    private BibRepository bibRepository;

    @Mock
    private SagaRepository sagaRepository;

    @Mock
    private BibMapper bibMapper;

    @InjectMocks
    private BibService bibService;

    //Name consists of: method, code path (if applicable), desired outcome
    @Test
    public void saveBibEntry_whenUpdating_updatesExistingEntity(){

        //-------------------------------------------------
        //  Arrange
        //-------------------------------------------------

        BibEntity bibEntity = new BibEntity();
        bibEntity.setId(10);

        BibDto bibDto = new BibDto();
        bibDto.setId(10);
        bibDto.setSagaIds(Set.of());

        SagaEntity sagaEntity = new SagaEntity();
        sagaEntity.addBib(bibEntity);

        when(bibRepository.existsById(10)).thenReturn(true);
        when(bibRepository.findById(10)).thenReturn(Optional.of(bibEntity));

        when(sagaRepository.findAll()).thenReturn(List.of(sagaEntity));

        when(bibRepository.save(bibEntity)).thenReturn(bibEntity);

        when(bibMapper.mapToDto(bibEntity)).thenReturn(bibDto);

        //-------------------------------------------------
        //  Act
        //-------------------------------------------------
        bibService.saveBibEntry(bibDto);

        //-------------------------------------------------
        //  Assert
        //-------------------------------------------------

        //Check if bib entity exists for removal from sagas
        verify(bibRepository).existsById(10);

        //ID is not null; find bib entity
        verify(bibRepository).findById(10);

        //Since entity exists already, a new one shouldn't be mapped from the DTO
        verify(bibMapper, never()).mapFromDto(bibDto);

        //Check if existing bib entity mapped to DTO
        verify(bibMapper).mapToDto(bibEntity);

        verify(bibRepository).save(bibEntity);
    }

    @Test
    public void saveBibEntry_whenCreating_mapsNewEntityFromDto(){

        //-------------------------------------------------
        //  Arrange
        //-------------------------------------------------

        BibEntity bibEntity = new BibEntity();

        BibDto bibDto = new BibDto();
        bibDto.setSagaIds(Set.of());

        when(bibRepository.save(bibEntity)).thenReturn(bibEntity);
        when(bibMapper.mapFromDto(bibDto)).thenReturn(bibEntity);
        when(bibMapper.mapToDto(bibEntity)).thenReturn(bibDto);

        //-------------------------------------------------
        //  Act
        //-------------------------------------------------
        bibService.saveBibEntry(bibDto);

        //-------------------------------------------------
        //  Assert
        //-------------------------------------------------

        //Shouldn't be able to check if bib entity exists by ID, as ID is null
        verify(bibRepository, never()).existsById(anyInt());

        //ID is null, so bib entity should never be loaded
        verify(bibRepository, never()).findById(anyInt());

        //Since entity doesn't already exist, a new one should be mapped from the DTO
        verify(bibMapper).mapFromDto(bibDto);

        //Check if existing bib entity mapped to DTO
        verify(bibMapper).mapToDto(bibEntity);

        verify(bibRepository).save(bibEntity);
    }

    @Test
    public void deleteBibEntry_deletesAndDetachesFromSagas(){

        //-------------------------------------------------
        //  Arrange
        //-------------------------------------------------
        SagaEntity saga = new SagaEntity();

        BibEntity bib = new BibEntity();
        bib.setId(1);

        saga.addBib(bib);

        when(sagaRepository.findAll()).thenReturn(List.of(saga));

        //-------------------------------------------------
        //  Act
        //-------------------------------------------------
        bibService.deleteBibEntryById(1);

        //-------------------------------------------------
        //  Assert
        //-------------------------------------------------
        verify(bibRepository).deleteById(1);
        verify(sagaRepository).findAll();
        assertThat(saga.getBibEntities()).isEmpty();
    }
}
