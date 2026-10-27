package com.se.riddaradb.motif;

import com.se.riddaradb.saga.SagaRequestDto;
import com.se.riddaradb.saga.SagaResponseDto;
import com.se.riddaradb.saga.SagaService;
import com.se.riddaradb.sagaversion.SagaDate;
import com.se.riddaradb.sagaversion.SagaVersionEntity;
import com.se.riddaradb.sagaversion.SagaVersionRequestDto;
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
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="admin", roles="ADMINISTRATOR")
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class MotifIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MotifService motifService;

    @Autowired
    private SagaService sagaService;

    @AfterEach
    void cleanDatabase(){
        sagaService.deleteAll();
        motifService.deleteAll();
    }

    @Test
    public void postMotif_shouldReturnMotifWithAllFieldsCorrect() throws Exception {

        MotifDto result = postMotif();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getMotifCode()).isEqualTo(createMotifDto().getMotifCode());
        assertThat(result.getMotifName()).isEqualTo(createMotifDto().getMotifName());
        assertThat(result.getDescription()).isEqualTo(createMotifDto().getDescription());
        assertThat(result.getHasChildren()).isEqualTo(createMotifDto().getHasChildren());
        assertThat(result.getMotifSagas())
                .singleElement()
                .satisfies(element -> assertThat(element.getPageChapterNumber()).isEqualTo(createMotifSagaVersionDto().getPageChapterNumber()));

    }

    @Test
    public void putMotif_shouldReturnMotifWithAllFieldsCorrect() throws Exception {

        MotifDto motifDto = postMotif();

        motifDto.setMotifCode("New motif code");
        motifDto.setMotifName("New motif name");
        motifDto.setDescription("New description");

        MotifSagaVersionDto motifSagaVersionDto = motifDto.getMotifSagas().stream().findFirst().orElseThrow();
        motifSagaVersionDto.setPageChapterNumber("New page chapter number");

        motifDto.setMotifSagas(Set.of(motifSagaVersionDto));

        MvcResult mvcResult = mockMvc.perform(put("/motifs/putmotif")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(motifDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        MotifDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MotifDto.class);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getMotifCode()).isEqualTo(motifDto.getMotifCode());
        assertThat(result.getMotifName()).isEqualTo(motifDto.getMotifName());
        assertThat(result.getDescription()).isEqualTo(motifDto.getDescription());
        assertThat(result.getHasChildren()).isEqualTo(motifDto.getHasChildren());
        assertThat(result.getMotifSagas())
                .singleElement()
                .satisfies(element -> assertThat(element.getPageChapterNumber()).isEqualTo(motifSagaVersionDto.getPageChapterNumber()));

    }

    @Test
    public void getMotifs_shouldReturnMotifs() throws Exception {

        postMotif();

        MvcResult mvcResult = mockMvc.perform(get("/motifs/getmotifs")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        Set<MotifDto> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(result).isNotEmpty();
        assertThat(result)
            .singleElement()
            .satisfies(element -> assertThat(element.getId()).isNotNull());
    }

    @Test
    public void deleteMotif_motifIsRemoved() throws Exception{

        MotifDto motifDto = postMotif();

        mockMvc.perform(delete("/motifs/deletemotif/" + motifDto.getId())
                        .with(csrf()))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/motifs/getmotifs")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        Set<MotifDto> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(result).isEmpty();
    }



    private MotifDto postMotif() throws Exception{

        MotifDto motifDto = createMotifDto();

        //Set up saga with saga version
        SagaRequestDto sagaRequestDto = createSagaRequestDto();
        sagaRequestDto.setSagaVersions(Set.of(createSagaVersionRequestDto()));
        SagaResponseDto sagaResponseDto = sagaService.saveSaga(sagaRequestDto);

        MotifSagaVersionDto motifSagaVersionDto = createMotifSagaVersionDto();
        motifSagaVersionDto.setSagaVersionId(sagaResponseDto
                .getSagaVersions()
                .stream()
                .findFirst()
                .orElseThrow()
                .getId());

        motifDto.setMotifSagas(Set.of(motifSagaVersionDto));

        MvcResult mvcResult = mockMvc.perform(post("/motifs/postmotif")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(motifDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), MotifDto.class);
    }

    private MotifDto createMotifDto(){
        return new MotifDto(
                null,
                "Motif code",
                "Motif name",
                "Description",
                false
        );
    }

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

    private MotifSagaVersionDto createMotifSagaVersionDto(){
        return new MotifSagaVersionDto(
                null,
                "Page/chapter number",
                true
        );
    }


}
