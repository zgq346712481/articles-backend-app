package by.andd3dfx.controllers;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import by.andd3dfx.ArticlesBackendAppApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(classes = ArticlesBackendAppApplication.class)
@WebAppConfiguration
class AuthorControllerIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    public void setup() {
        mockMvc = webAppContextSetup(webApplicationContext)
            .apply(springSecurity())
            .build();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void readAuthorForAdmin() throws Exception {
        mockMvc.perform(get("/authors/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("Тихон")))
            .andExpect(jsonPath("$.lastName", is("Задонский")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void readAuthorForUser() throws Exception {
        mockMvc.perform(get("/authors/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is(1)))
            .andExpect(jsonPath("$.firstName", is("Тихон")))
            .andExpect(jsonPath("$.lastName", is("Задонский")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void readAbsentAuthor() throws Exception {
        String message = mockMvc.perform(get("/authors/345"))
            .andExpect(status().isNotFound())
            .andReturn().getResolvedException().getMessage();
        assertThat(message, containsString("Could not find an author by id=345"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void readAuthorsForAdmin() throws Exception {
        mockMvc.perform(get("/authors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(11)));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void readAuthorsForUser() throws Exception {
        mockMvc.perform(get("/authors"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(11)));
    }
}
