package com.se.riddaradb.auth;

import com.se.riddaradb.auth.user.UserDto;
import com.se.riddaradb.auth.user.UserService;
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
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserService userService;

    @AfterEach
    void cleanDatabase(){
        userService.deleteAllContributors();
        System.out.println("Users in DB: " + userService.getUsers().size());
    }

    @Test
    public void postUser_shouldReturnUserWithAllFieldsCorrect() throws Exception {

        UserDto result = postUser();

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo(createContributor().getUsername());
        assertThat(result.getEmail()).isEqualTo(createContributor().getEmail());
        assertThat(result.getFirstName()).isEqualTo(createContributor().getFirstName());
        assertThat(result.getLastNames()).isEqualTo(createContributor().getLastNames());
        //Check that password is encoded
        assertThat(result.getPassword()).isNotEqualTo(createContributor().getPassword());
        assertThat(userService.adminExists()).isTrue();
    }

    @Test
    public void putUser_shouldReturnUserWithAllFieldsCorrect() throws Exception {

        UserDto userDto = postUser();

        userDto.setUsername("New username");
        userDto.setEmail("newemail@newwebsite.com");
        userDto.setFirstName("New first name");
        userDto.setLastNames("New last names");
        userDto.setPassword("New password");

        MvcResult mvcResult = mockMvc.perform(put("/admin/putuser")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        UserDto result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);

        assertThat(result.getId()).isNotNull();
        assertThat(result.getUsername()).isEqualTo(userDto.getUsername());
        assertThat(result.getEmail()).isEqualTo(userDto.getEmail());
        assertThat(result.getFirstName()).isEqualTo(userDto.getFirstName());
        assertThat(result.getLastNames()).isEqualTo(userDto.getLastNames());
        //Check that password is encoded
        assertThat(result.getPassword()).isNotEqualTo(userDto.getPassword());
        assertThat(userService.adminExists()).isTrue();
    }

    @Test
    public void getUser_shouldReturnUser() throws Exception {

        postUser();

        MvcResult mvcResult = mockMvc.perform(get("/admin/getusers")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        Set<UserDto> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(result).isNotEmpty();
        assertThat(result)
                .singleElement()
                .satisfies(element -> assertThat(element.getId()).isNotNull());
        assertThat(userService.adminExists()).isTrue();
    }


    UserDto postUser() throws Exception {

        MvcResult mvcResult = mockMvc.perform(post("/admin/postuser")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createContributor())))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        return objectMapper.readValue(mvcResult.getResponse().getContentAsString(), UserDto.class);
    }

    @Test
    public void deleteUser_UserIsRemoved() throws Exception{

        UserDto userDto = postUser();

        mockMvc.perform(delete("/admin/deleteuser/" + userDto.getUsername())
                        .with(csrf()))
                .andExpect(status().isOk());

        MvcResult mvcResult = mockMvc.perform(get("/admin/getusers")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andReturn();

        Set<UserDto> result = objectMapper.readValue(mvcResult.getResponse().getContentAsString(), new TypeReference<>(){});

        assertThat(result).isEmpty();
        assertThat(userService.adminExists()).isTrue();
    }

    UserDto createContributor(){
        return new UserDto(
                null,
                "User",
                "user@fakewebsite.com",
                "Usurius",
                "U. User",
                "password"
        );
    }
}
