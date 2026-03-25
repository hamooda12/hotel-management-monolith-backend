package com.example.hotalproject.security;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void login_shouldReturnAccessAndRefreshTokens() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email":"guest@hotel.local",
                                  "password":"Guest@123"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.role").value("GUEST"));
    }

    @Test
    void protectedEndpoint_withoutToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/bookings/guest-history"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void protectedEndpoint_withInvalidToken_shouldReturnUnauthorized() throws Exception {
        mockMvc.perform(get("/api/bookings/guest-history")
                        .header("Authorization", "Bearer invalid.token.value"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void guestAccessingAdminBookingsEndpoint_shouldReturnForbidden() throws Exception {
        String guestToken = loginAndGetAccessToken("guest@hotel.local", "Guest@123");

        mockMvc.perform(get("/api/bookings")
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void refresh_thenLogout_thenRefreshAgain_shouldFail() throws Exception {
        String refreshToken = loginAndGetRefreshToken("guest@hotel.local", "Guest@123");

        MvcResult refreshResult = mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken":"%s"
                                }
                                """.formatted(refreshToken)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andReturn();

        JsonNode refreshJson = objectMapper.readTree(refreshResult.getResponse().getContentAsString());
        String rotatedRefreshToken = refreshJson.get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken":"%s"
                                }
                                """.formatted(rotatedRefreshToken)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "refreshToken":"%s"
                                }
                                """.formatted(rotatedRefreshToken)))
                .andExpect(status().isUnauthorized());
    }

    private String loginAndGetAccessToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email":"%s",
                                  "password":"%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = jsonNode.get("accessToken").asText();
        assertThat(token).isNotBlank();
        return token;
    }

    private String loginAndGetRefreshToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "email":"%s",
                                  "password":"%s"
                                }
                                """.formatted(email, password)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode jsonNode = objectMapper.readTree(result.getResponse().getContentAsString());
        String token = jsonNode.get("refreshToken").asText();
        assertThat(token).isNotBlank();
        return token;
    }
}
