package com.se.riddaradb.saga;

import com.se.riddaradb.bib.BibEntity;
import com.se.riddaradb.bib.BibRepository;
import com.se.riddaradb.ms.MsEntity;
import com.se.riddaradb.ms.MsRepository;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import com.se.riddaradb.sagaversion.SagaVersionRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SagaServiceUnitTest {

    @Mock
    private SagaRepository sagaRepository;

    @Mock
    private MsRepository msRepository;

    @Mock
    private BibRepository bibRepository;

    @Mock
    private SagaMsRepository sagaMsRepository;

    @Mock
    private SagaMapper sagaMapper;

    @InjectMocks
    private SagaService sagaService;

    @Test
    public void saveSaga_attachesAllTables(){

        //Arrange
        SagaRequestDto sagaRequestDto = new SagaRequestDto();

        MsEntity msEntity = new MsEntity();
        msEntity.setId(1);
        SagaMsDto sagaMsDto = new SagaMsDto(1);

        BibEntity bibEntity = new BibEntity();

        SagaVersionRequestDto sagaVersionRequestDto = new SagaVersionRequestDto();

        sagaRequestDto.setSagaVersions(Set.of(sagaVersionRequestDto));
        sagaRequestDto.setSagaMsDtos(Set.of(sagaMsDto));

        when(msRepository.findById(anyInt())).thenReturn(Optional.of(msEntity));
        when(bibRepository.findAllById(anySet())).thenReturn(List.of(bibEntity));

        //Act
        sagaService.saveSaga(sagaRequestDto);

        //Assert
        ArgumentCaptor<SagaEntity> captor = ArgumentCaptor.forClass(SagaEntity.class);
        verify(sagaRepository).save(captor.capture());
        SagaEntity savedSagaEntity = captor.getValue();

        assertThat(savedSagaEntity.getSagaMsEntities()).isNotEmpty();
        assertThat(savedSagaEntity.getBibEntities()).isNotEmpty();
        assertThat(savedSagaEntity.getSagaVersionEntities()).isNotEmpty();

        verify(sagaRepository).save(any(SagaEntity.class));
    }

    @Test
    public void updateSaga_changesExistingEntityAndTables(){

        //Arrange

        //Set up saga entity
        SagaEntity sagaEntity = new SagaEntity();
        sagaEntity.setId(1);
        sagaEntity.setTitle("Old saga title");
        sagaEntity.setDescription("Old description");
        sagaEntity.setTranslated(false);

        SagaVersionEntity sagaVersionEntity = new SagaVersionEntity();
        sagaVersionEntity.setId(1);
        sagaVersionEntity.setTitle("Old title");
        sagaEntity.addSagaVersion(sagaVersionEntity);

        MsEntity msEntity = new MsEntity();
        msEntity.setId(1);
        sagaEntity.addMs(msEntity, "old folio number", "old note");
        SagaMsEntity sagaMsEntity = new SagaMsEntity(sagaEntity, msEntity, "old folio number", "old note");

        BibEntity bibEntity = new BibEntity();
        bibEntity.setId(1);
        bibEntity.setTitle("Old title");
        sagaEntity.addBib(bibEntity);

        BibEntity updatedBibEntity = new BibEntity();
        updatedBibEntity.setId(1);
        updatedBibEntity.setTitle("New title");

        //Set up saga DTO
        SagaRequestDto sagaRequestDto = new SagaRequestDto();
        sagaRequestDto.setId(1);
        sagaRequestDto.setTitle("New saga title");
        sagaRequestDto.setDescription("New description");
        sagaRequestDto.setTranslated(true);

        SagaVersionRequestDto sagaVersionRequestDto = new SagaVersionRequestDto();
        sagaVersionRequestDto.setId(1);
        sagaVersionRequestDto.setTitle("New title");
        sagaRequestDto.setSagaVersions(Set.of(sagaVersionRequestDto));

        SagaMsDto sagaMsDto = new SagaMsDto(1);
        sagaMsDto.setFolioNumber("New folio number");
        sagaMsDto.setNote("New note");
        sagaRequestDto.setSagaMsDtos(Set.of(sagaMsDto));

        sagaRequestDto.setBibIds(Set.of(1));

        when(sagaRepository.findById(1)).thenReturn(Optional.of(sagaEntity));
        when(bibRepository.findAllById(Set.of(1))).thenReturn(List.of(updatedBibEntity));
        when(sagaMsRepository.findBySagaEntityId(1)).thenReturn(Set.of(sagaMsEntity));

        //Act
        sagaService.updateSaga(sagaRequestDto);

        //Assert
        ArgumentCaptor<SagaEntity> captor = ArgumentCaptor.forClass(SagaEntity.class);
        verify(sagaRepository).save(captor.capture());
        SagaEntity savedSagaEntity = captor.getValue();

        assertThat(savedSagaEntity.getTitle()).isEqualTo(sagaRequestDto.getTitle());
        assertThat(savedSagaEntity.getDescription()).isEqualTo(sagaRequestDto.getDescription());
        assertThat(savedSagaEntity.getTranslated()).isEqualTo(sagaRequestDto.getTranslated());

        assertThat(savedSagaEntity.getSagaVersionEntities())
                .singleElement()
                .satisfies(element -> assertThat(element.getTitle())
                        .isEqualTo(sagaVersionRequestDto.getTitle()));

        assertThat(savedSagaEntity.getBibEntities())
                .singleElement()
                .satisfies(element -> assertThat(element.getTitle())
                        .isEqualTo(updatedBibEntity.getTitle()));

        assertThat(savedSagaEntity.getSagaMsEntities())
                .singleElement()
                .satisfies(element -> assertThat(element.getFolioNumber())
                        .isEqualTo(sagaMsDto.getFolioNumber()));

        verify(sagaRepository).save(any(SagaEntity.class));
    }

    @Test
    public void updateSaga_addsNewTables(){
        //Arrange

        //Set up saga entity
        SagaEntity sagaEntity = new SagaEntity();
        sagaEntity.setId(1);
        sagaEntity.setTitle("Old saga title");
        sagaEntity.setDescription("Old description");
        sagaEntity.setTranslated(false);

        //Set up saga DTO (tables added)
        SagaRequestDto sagaRequestDto = new SagaRequestDto();
        sagaRequestDto.setId(1);
        sagaRequestDto.setTitle("New saga title");
        sagaRequestDto.setDescription("New description");
        sagaRequestDto.setTranslated(true);

        SagaVersionRequestDto sagaVersionRequestDto = new SagaVersionRequestDto();
        sagaVersionRequestDto.setId(null);
        sagaVersionRequestDto.setTitle("New title");
        sagaRequestDto.setSagaVersions(Set.of(sagaVersionRequestDto));

        SagaMsDto sagaMsDto = new SagaMsDto(1);
        sagaMsDto.setFolioNumber("New folio number");
        sagaRequestDto.setSagaMsDtos(Set.of(sagaMsDto));

        BibEntity bibEntity = new BibEntity();
        bibEntity.setId(1);
        bibEntity.setTitle("Bib title");

        MsEntity msEntity = new MsEntity();
        msEntity.setId(1);
        msEntity.setName("MS title");

        sagaRequestDto.setBibIds(Set.of(1));

        when(sagaRepository.findById(1)).thenReturn(Optional.of(sagaEntity));
        when(bibRepository.findAllById(Set.of(1))).thenReturn(List.of(bibEntity));
        when(msRepository.findById(1)).thenReturn(Optional.of(msEntity));

        //Act
        sagaService.updateSaga(sagaRequestDto);

        //Assert
        ArgumentCaptor<SagaEntity> captor = ArgumentCaptor.forClass(SagaEntity.class);
        verify(sagaRepository).save(captor.capture());
        SagaEntity savedSagaEntity = captor.getValue();

        assertThat(savedSagaEntity.getSagaVersionEntities()).isNotEmpty();
        assertThat(savedSagaEntity.getSagaVersionEntities().stream().findFirst().orElseThrow().getTitle()).isEqualTo(sagaVersionRequestDto.getTitle());

        assertThat(savedSagaEntity.getBibEntities()).isNotEmpty();
        assertThat(savedSagaEntity.getBibEntities().stream().findFirst().orElseThrow().getTitle()).isEqualTo(bibEntity.getTitle());

        assertThat(savedSagaEntity.getSagaMsEntities()).isNotEmpty();
        assertThat(savedSagaEntity.getSagaMsEntities().stream().findFirst().orElseThrow().getFolioNumber()).isEqualTo(sagaMsDto.getFolioNumber());
    }

    @Test
    public void updateSaga_removesOldTables(){
        //Arrange

        //Set up saga entity
        SagaEntity sagaEntity = new SagaEntity();
        sagaEntity.setId(1);
        sagaEntity.setTitle("Old saga title");
        sagaEntity.setDescription("Old description");
        sagaEntity.setTranslated(false);

        SagaVersionEntity sagaVersionEntity = new SagaVersionEntity();
        sagaVersionEntity.setId(1);
        sagaVersionEntity.setTitle("Old title");
        sagaEntity.addSagaVersion(sagaVersionEntity);

        MsEntity msEntity = new MsEntity();
        msEntity.setId(1);
        sagaEntity.addMs(msEntity, "old folio number", "old note");
        SagaMsEntity sagaMsEntity = new SagaMsEntity(sagaEntity, msEntity, "old folio number", "old note");

        BibEntity bibEntity = new BibEntity();
        bibEntity.setId(1);
        bibEntity.setTitle("Old title");
        sagaEntity.addBib(bibEntity);

        //Set up saga DTO (tables removed)
        SagaRequestDto sagaRequestDto = new SagaRequestDto();
        sagaRequestDto.setId(1);
        sagaRequestDto.setTitle("New saga title");
        sagaRequestDto.setDescription("New description");
        sagaRequestDto.setTranslated(true);

        sagaRequestDto.setSagaVersions(new HashSet<>());
        sagaRequestDto.setSagaMsDtos(new HashSet<>());

        when(sagaRepository.findById(1)).thenReturn(Optional.of(sagaEntity));
        when(msRepository.findById(1)).thenReturn(Optional.of(msEntity));
        when(sagaMsRepository.findBySagaEntityId(1)).thenReturn(Set.of(sagaMsEntity));

        //Act
        sagaService.updateSaga(sagaRequestDto);

        //Assert
        ArgumentCaptor<SagaEntity> captor = ArgumentCaptor.forClass(SagaEntity.class);
        verify(sagaRepository).save(captor.capture());
        SagaEntity savedSagaEntity = captor.getValue();

        assertThat(savedSagaEntity.getSagaVersionEntities()).isEmpty();
        assertThat(savedSagaEntity.getBibEntities()).isEmpty();
        assertThat(savedSagaEntity.getSagaMsEntities()).isEmpty();
    }
}