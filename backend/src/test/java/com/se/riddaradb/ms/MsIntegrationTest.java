package com.se.riddaradb.ms;

import com.se.riddaradb.saga.SagaRequestDto;
import com.se.riddaradb.saga.SagaResponseDto;
import com.se.riddaradb.saga.SagaService;
import com.se.riddaradb.sagaversion.SagaDate;
import com.se.riddaradb.sagaversion.SagaVersionRequestDto;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
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
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(username="admin", roles="ADMINISTRATOR")
@ActiveProfiles("test")
public class MsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MsService msService;

    @Autowired
    private SagaService sagaService;

    @Autowired
    private MsRepositoryService msRepositoryService;

    @Autowired
    private MsRepositoryMapper msRepositoryMapper;

    @AfterEach
    void cleanDatabase(){
        sagaService.deleteAll();
        msService.deleteAll();
        msRepositoryService.deleteAll();
    }

    @Test
    public void postMs_shouldReturnMsWithAllFieldsCorrect() throws Exception {

        MsDto result = postMs();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getMsRepositoryId()).isNotNull();
        assertThat(result.getName()).isEqualTo(createMsDto().getName());
        assertThat(result.getShelfmark()).isEqualTo(createMsDto().getShelfmark());
        assertThat(result.getDescription()).isEqualTo(createMsDto().getDescription());
        assertThat(result.getMsSagaDtos())
                .singleElement()
                .satisfies(element -> assertThat(element.getFolioNumber()).isEqualTo(createMsSagaDto().getFolioNumber()));

    }

    @Test
    public void putMs_shouldReturnMsWithAllFieldsCorrect() throws Exception {

        MsDto msDto = postMs();

        msDto.setName("Codex Wormianus");
        msDto.setShelfmark("AM 242 fol");
        msDto.setDescription("Mid-14th century");
        msDto.getMsSagaDtos().stream().findFirst().ifPresent(
                dto -> dto.setFolioNumber("1v - 100f"));

        MsSagaDto msSagaDto = msDto.getMsSagaDtos().stream().findFirst().orElseThrow();
        msSagaDto.setFolioNumber("1v - 200f");

        msDto.setMsSagaDtos(Set.of(msSagaDto));

        MvcResult mvcResult = mockMvc.perform(put("/ms/putmsentry")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(msDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MsDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MsDto.class);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getMsRepositoryId()).isNotNull();
        assertThat(result.getName()).isEqualTo(msDto.getName());
        assertThat(result.getShelfmark()).isEqualTo(msDto.getShelfmark());
        assertThat(result.getDescription()).isEqualTo(msDto.getDescription());
        assertThat(result.getMsSagaDtos())
                .singleElement()
                .satisfies(element -> assertThat(element.getFolioNumber()).isEqualTo(msSagaDto.getFolioNumber()));


    }

    @Test
    public void putMsRepository_shouldReturnMsRepositoryWithAllFieldsCorrect() throws Exception {

        MsDto msDto = postMs();

        MsRepositoryDto msRepositoryDto = msRepositoryService.getMsRepositoryById(msDto.getMsRepositoryId());

        msRepositoryDto.setName("Arnamagnaean Institute");
        msRepositoryDto.setCountry("Denmark");
        msRepositoryDto.setCity("Copenhagen");

        MvcResult mvcResult = mockMvc.perform(put("/msrepository/putmsrepository")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(msRepositoryDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MsRepositoryDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MsRepositoryDto.class);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getId()).isEqualTo(msRepositoryDto.getId());
        assertThat(result.getName()).isEqualTo(msRepositoryDto.getName());
    }

    @Test
    public void getMs_shouldReturnMs() throws Exception {

        postMs();

        MvcResult mvcResult = mockMvc.perform(get("/ms/getmsentries")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        Set<MsDto> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(result).isNotEmpty();
        assertThat(result)
            .singleElement()
            .satisfies(element -> assertThat(element.getId()).isNotNull());
    }

    @Test
    public void deleteMs_MsIsRemoved() throws Exception{

        MsDto msDto = postMs();

        mockMvc.perform(delete("/ms/deletemsentry/" + msDto.getId())
                        .with(csrf()))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/ms/getmsentries")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        Set<MsDto> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(result).isEmpty();
    }

    @Test
    public void deleteMsRepository_RepositoryAndMsAreRemoved() throws Exception{

        MsDto msDto = postMs();

        mockMvc.perform(delete("/msrepository/deletemsrepository/" + msDto.getMsRepositoryId())
                        .with(csrf()))
                .andExpect(status().isOk());

        //Has the repository been deleted?
        MvcResult mvcResult = mockMvc.perform(get("/msrepository/getmsrepositories"))
                .andExpect(status().isOk())
                .andReturn();
        Set<MsRepositoryDto> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertThat(result).isEmpty();

        //Has the associated manuscript been deleted?
        mvcResult = mockMvc.perform(get("/ms/getmsentries")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();
        result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});
        assertThat(result).isEmpty();
    }



    private MsDto postMs() throws Exception{

        //Set up MS repository
        MsRepositoryDto msRepositoryDto = createMsRepositoryDto();
        msRepositoryDto = msRepositoryService.saveMsRepository(msRepositoryDto);

        //Set up saga with saga version
        SagaRequestDto sagaRequestDto = createSagaRequestDto();
        sagaRequestDto.setSagaVersions(Set.of(createSagaVersionRequestDto()));
        SagaResponseDto sagaResponseDto = sagaService.saveSaga(sagaRequestDto);

        //Set up MS-saga join entity
        MsSagaDto msSagaDto = createMsSagaDto();
        msSagaDto.setSagaId(sagaResponseDto.getId());

        //Create MS DTO
        MsDto msDto = createMsDto();

        //Assign MS to repository and link with saga
        msDto.setMsRepositoryId(msRepositoryDto.getId());
        msDto.setMsSagaDtos(Set.of(msSagaDto));

        MvcResult mvcResult = mockMvc.perform(post("/ms/postmsentry")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(msDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MsDto.class);
    }

    private MsDto createMsDto(){
        return new MsDto(
                null,
                "Codex Regius",
                "GKS 2365 4to",
                "1600",
                "www.handrit.is",
                "Also known as Konungsbók"
        );
    }

    private SagaRequestDto createSagaRequestDto(){
        return new SagaRequestDto(
                null,
                "Title",
                "Translated title",
                "Page author",
                "Description",
                false,
                false
        );
    }

    private SagaVersionRequestDto createSagaVersionRequestDto() {
        return new SagaVersionRequestDto(
                null,
                "Title",
                "Abbreviation",
                "Description",
                SagaDate.UNDEFINED);
    }

    private MsSagaDto createMsSagaDto(){
        return new MsSagaDto(
                null,
                "10v - 30r",
                "note"
        );
    }

    private MsRepositoryDto createMsRepositoryDto(){
        return new MsRepositoryDto(
                null,
                "Gammel kongelig Samling",
                "Copenhagen",
                "Denmark");
    }


}
