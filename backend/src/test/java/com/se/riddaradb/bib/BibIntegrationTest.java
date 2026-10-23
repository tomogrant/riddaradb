package com.se.riddaradb.bib;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DirtiesContext
@AutoConfigureMockMvc
@ActiveProfiles("test")
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@WithMockUser(username="admin", roles="ADMINISTRATOR")
class BibIntegrationTest {

    @BeforeEach
    void setup() {
        bibService.deleteAll();
    }
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BibService bibService;

    @Test
    public void postBibEntry_shouldReturnBibEntryWithAllFieldsCorrect() throws Exception {

        BibDto bibDtoResult = postBib();

        assertThat(bibDtoResult.getId()).isNotNull();
        checkBibFields(createBibDto(), bibDtoResult);
    }

    @Test
    public void getBibEntries_shouldReturnBibEntries() throws Exception {

        postBib();

        MvcResult mvcResult = mockMvc.perform(get("/bibentries/getbibentries")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        Set<BibDto> bibDtoResult = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(bibDtoResult).isNotEmpty();
        assertThat(bibDtoResult)
                .singleElement()
                .satisfies(element -> assertThat(element.getId()).isNotNull());
    }

    @Test
    public void putBibEntry_shouldReturnBibEntryWithAllFieldsUpdated() throws Exception {

        BibDto bibDto = postBib();

        bibDto.setAuthors("Author new");
        bibDto.setTitle("Title new");

        MvcResult mvcResult = mockMvc.perform(put("/bibentries/putbibentry")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bibDto)))
                .andExpect(status().isOk())
                .andReturn();

        BibDto bibDtoResult = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BibDto.class);

        assertThat(bibDtoResult.getId()).isEqualTo(bibDto.getId());
        checkBibFields(bibDto, bibDtoResult);
    }

    @Test
    public void deleteBibEntry_bibEntryIsRemoved() throws Exception{

        BibDto bibDto = postBib();

        mockMvc.perform(delete("/bibentries/deletebibentry/" + bibDto.getId())
                        .with(csrf()))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/bibentries/getbibentries")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        Set<BibDto> bibDtoResult = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(bibDtoResult).isEmpty();
    }

    void checkBibFields(BibDto bibDto, BibDto bibDtoResult){
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

    BibDto postBib() throws Exception{

        MvcResult mvcResult = mockMvc.perform(post("/bibentries/postbibentry")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBibDto())))
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), BibDto.class);
    }

    BibDto createBibDto(){
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
}
