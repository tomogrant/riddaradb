package com.se.riddaradb.saga;

import com.se.riddaradb.bib.BibDto;
import com.se.riddaradb.bib.BibEntity;
import com.se.riddaradb.bib.BibService;
import com.se.riddaradb.ms.MsDto;
import com.se.riddaradb.ms.MsService;
import com.se.riddaradb.sagaversion.SagaDate;
import com.se.riddaradb.sagaversion.SagaVersionRequestDto;
import com.se.riddaradb.sagaversion.SagaVersionResponseDto;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="admin", roles="ADMINISTRATOR")
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)

public class SagaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BibService bibService;

    @Autowired
    private MsService msService;

    @Autowired
    private SagaService sagaService;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanDatabase(){
        sagaService.deleteAll();
        bibService.deleteAll();
        msService.deleteAll();
    }

    @Test
    public void postSaga_shouldReturnSagaWithAllFieldsValid() throws Exception {

        SagaResponseDto result = postSaga();

        SagaVersionResponseDto sagaVersionResponseDto = result.getSagaVersions().stream().findFirst().orElseThrow();
        BibDto bibDtoResult = result.getBibDtos().stream().findFirst().orElseThrow();
        SagaMsDto sagaMsDtoResult = result.getSagaMsDtos().stream().findFirst().orElseThrow();

        checkSagaFields(createSagaRequestDto(), result);
        checkSagaVersionFields(createSagaVersionRequestDto(), sagaVersionResponseDto);
        checkBibFields(createBibDto(), bibDtoResult);
        checkSagaMsFields(createSagaMsDto(), sagaMsDtoResult);
    }

    @Test
    public void getSaga_shouldRetrieveSaga() throws Exception{

        SagaResponseDto sagaResponseDto = postSaga();

        MvcResult mvcResult = mockMvc.perform(get("/sagas/getsagabyid/" + sagaResponseDto.getId())
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SagaResponseDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(result).isNotNull();
    }

    @Test
    public void putSaga_shouldReturnSagaWithAllFieldsUpdated() throws Exception{

        SagaResponseDto sagaResponseDto = postSaga();

        SagaRequestDto sagaRequestDto = new SagaRequestDto(
                sagaResponseDto.getId(),
                "New title",
                "New translated title",
                "Page author",
                "New description",
                false
        );

        SagaVersionRequestDto sagaVersionRequestDto = new SagaVersionRequestDto(
                sagaResponseDto.getSagaVersions().stream().findFirst().orElseThrow().getId(),
                "New title",
                "New abbreviation",
                "New description",
                SagaDate.UNDEFINED);

        sagaRequestDto.setSagaVersions(Set.of(sagaVersionRequestDto));
        sagaRequestDto.setBibIds(Set.of(sagaResponseDto.getBibDtos().stream().findFirst().orElseThrow().getId()));
        sagaRequestDto.setSagaMsDtos(sagaResponseDto.getSagaMsDtos());

        MvcResult mvcResult = mockMvc.perform(put("/sagas/putsaga")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sagaRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        SagaResponseDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SagaResponseDto.class);

        SagaVersionResponseDto sagaVersionResponseDto = result.getSagaVersions().stream().findFirst().orElseThrow();
        BibDto bibDtoResult = result.getBibDtos().stream().findFirst().orElseThrow();
        SagaMsDto sagaMsDtoResult = result.getSagaMsDtos().stream().findFirst().orElseThrow();

        checkSagaFields(sagaRequestDto, result);
        checkSagaVersionFields(sagaVersionRequestDto, sagaVersionResponseDto);
        checkBibFields(createBibDto(), bibDtoResult);
        checkSagaMsFields(createSagaMsDto(), sagaMsDtoResult);
    }

    @Test
    public void deleteSaga_findAllShouldBeEmpty() throws Exception{

        SagaResponseDto sagaResponseDto = postSaga();

        mockMvc.perform(delete("/sagas/deletesaga/" + sagaResponseDto.getId())
                        .with(csrf()))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/sagas/getsagas")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        Set<SagaResponseDto> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(result).isEmpty();
    }

    //Post saga
    private SagaResponseDto postSaga() throws Exception{

        SagaRequestDto sagaRequestDto = createSagaRequestDto();
        SagaVersionRequestDto sagaVersionRequestDto = createSagaVersionRequestDto();
        BibDto bibDto = createBibDto();
        MsDto msDto = createMsDto();
        SagaMsDto sagaMsDto = createSagaMsDto();

        //Save to get ID in order to link with saga entity
        BibDto savedBibDto = bibService.saveBibEntry(bibDto);

        //Save to get ID in order to link with saga-MS join entity
        msDto = msService.saveMsEntry(msDto);
        sagaMsDto.setMsId(msDto.getId());

        //Service constructs new saga version entity based on DTO provided
        sagaRequestDto.setSagaVersions(Set.of(sagaVersionRequestDto));

        //Service links saga to existing MS via the SagaMs join entity
        sagaRequestDto.setSagaMsDtos(Set.of(sagaMsDto));

        //Service links saga to existing bibliography using just ID
        sagaRequestDto.setBibIds(Set.of(savedBibDto.getId()));

        MvcResult mvcResult = mockMvc.perform(post("/sagas/postsaga")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sagaRequestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), SagaResponseDto.class);
    }

    //Check methods
    private void checkSagaFields(SagaRequestDto sagaRequestDto, SagaResponseDto sagaResponseDto){
        assertThat(sagaResponseDto.getId()).isNotNull();
        assertThat(sagaResponseDto.getTitle()).isEqualTo(sagaRequestDto.getTitle());
        assertThat(sagaResponseDto.getDescription()).isEqualTo(sagaRequestDto.getDescription());
        assertThat(sagaResponseDto.getTranslated()).isEqualTo(sagaRequestDto.getTranslated());
    }

    private void checkSagaVersionFields(SagaVersionRequestDto sagaVersionRequestDto, SagaVersionResponseDto sagaVersionResponseDto){
        assertThat(sagaVersionResponseDto.getSagaId()).isNotNull();
        assertThat(sagaVersionResponseDto.getTitle()).isEqualTo(sagaVersionRequestDto.getTitle());
        assertThat(sagaVersionResponseDto.getDescription()).isEqualTo(sagaVersionRequestDto.getDescription());
        assertThat(sagaVersionResponseDto.getDate()).isEqualTo(sagaVersionRequestDto.getDate());
    }

    private void checkBibFields(BibDto bibDto, BibDto bibDtoResult){
        assertThat(bibDtoResult.getId()).isNotNull();
        assertThat(bibDtoResult.getPublicationType()).isEqualTo(bibDto.getPublicationType());
        assertThat(bibDtoResult.getAuthors()).isEqualTo(bibDto.getAuthors());
        assertThat(bibDtoResult.getEditors()).isEqualTo(bibDto.getEditors());
        assertThat(bibDtoResult.getTranslators()).isEqualTo(bibDto.getTranslators());
        assertThat(bibDtoResult.getTitle()).isEqualTo(bibDto.getTitle());
        assertThat(bibDtoResult.getUrl()).isEqualTo(bibDto.getUrl());
        assertThat(bibDtoResult.getBookEditors()).isEqualTo(bibDto.getBookEditors());
        assertThat(bibDtoResult.getBook()).isEqualTo(bibDto.getBook());
        assertThat(bibDtoResult.getBookSeries()).isEqualTo(bibDto.getBookSeries());
        assertThat(bibDtoResult.getVolume()).isEqualTo(bibDto.getVolume());
        assertThat(bibDtoResult.getNumOfVolumes()).isEqualTo(bibDto.getNumOfVolumes());
        assertThat(bibDtoResult.getPublisher()).isEqualTo(bibDto.getPublisher());
        assertThat(bibDtoResult.getPublicationYear()).isEqualTo(bibDto.getPublicationYear());
        assertThat(bibDtoResult.getPageNumbers()).isEqualTo(bibDto.getPageNumbers());
        assertThat(bibDtoResult.getRecommended()).isEqualTo(bibDto.getRecommended());
        assertThat(bibDtoResult.getDescription()).isEqualTo(bibDto.getDescription());
    }

    private void checkSagaMsFields(SagaMsDto sagaMsDto, SagaMsDto sagaMsDtoResult){
        assertThat(sagaMsDtoResult.getMsId()).isNotNull();
        assertThat(sagaMsDtoResult.getFolioNumber()).isEqualTo(sagaMsDto.getFolioNumber());
        assertThat(sagaMsDtoResult.getNote()).isEqualTo(sagaMsDto.getNote());
    }

    //Factory methods
    private SagaRequestDto createSagaRequestDto(){
        return new SagaRequestDto(
                null,
                "Title",
                "Translated title",
                "Page author",
                "Description",
                false);
    }

    private SagaVersionRequestDto createSagaVersionRequestDto() {
        return new SagaVersionRequestDto(
                null,
                "Title",
                "Abbrev",
                "Description",
                SagaDate.UNDEFINED);
    }

    private BibDto createBibDto() {
        return new BibDto(
                null,
                BibEntity.PublicationType.JOURNAL_ARTICLE,
                "Author",
                "Editor",
                "Translator",
                "Title",
                "www.url.com",
                "Book editors",
                "Journal title",
                "Book series",
                "10",
                "2",
                "Cambridge",
                "Cambridge University Press",
                "2026",
                "10-22",
                false,
                "Description");
    }

    private MsDto createMsDto() {
        return new MsDto(
                null,
                "Name",
                "Shelfmark",
                "1300",
                "www.handrit.is",
                "Description");
    }

    private SagaMsDto createSagaMsDto() {
        return new SagaMsDto(
                null,
                "Shelfmark",
                "Folio number",
                "1800",
                "note");
    }
}
