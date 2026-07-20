package com.harshit.moviebooking;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.harshit.moviebooking.config.RoleDataInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthAndMovieApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void userCannotCreateMovieButAdminCan() throws Exception {
        String registerBody = """
                {
                  "username": "apitestuser",
                  "email": "apitest@example.com",
                  "password": "Password@123",
                  "firstName": "Api",
                  "lastName": "Test",
                  "dateOfBirth": "2000-01-01"
                }
                """;

        MvcResult registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.userId").isNumber())
                .andReturn();

        String userToken = objectMapper.readTree(registerResult.getResponse().getContentAsString())
                .get("token")
                .asText();

        mockMvc.perform(post("/api/movies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson()))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson()))
                .andExpect(status().isForbidden());

        MvcResult adminLogin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email": "%s",
                                  "password": "%s"
                                }
                                """.formatted(
                                RoleDataInitializer.ADMIN_EMAIL,
                                RoleDataInitializer.ADMIN_PASSWORD)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andReturn();

        String adminToken = objectMapper.readTree(adminLogin.getResponse().getContentAsString())
                .get("token")
                .asText();
        assertThat(adminToken).isNotBlank();

        MvcResult createResult = mockMvc.perform(post("/api/movies")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(movieJson()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.title").value("Interstellar"))
                .andReturn();

        JsonNode created = objectMapper.readTree(createResult.getResponse().getContentAsString());
        long movieId = created.get("id").asLong();

        mockMvc.perform(get("/api/movies/" + movieId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Interstellar"));

        mockMvc.perform(get("/api/movies?page=0&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    private static String movieJson() {
        return """
                {
                  "title": "Interstellar",
                  "synopsis": "A team travels through a wormhole in space.",
                  "releaseDate": "2014-11-07",
                  "runtimeMinutes": 169,
                  "language": "English",
                  "countryOfOrigin": "USA",
                  "contentRating": "PG_13",
                  "posterUrl": "https://example.com/poster.jpg"
                }
                """;
    }
}
