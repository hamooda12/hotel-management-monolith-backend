package com.example.hotalproject.integration;

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

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BookingPaymentFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void fullHappyPath_bookingToPaymentSuccess_shouldConfirmBooking() throws Exception {
        String guestToken = loginAndGetAccessToken("guest@hotel.local", "Guest@123");

        Long hotelId = extractFirstIdFromPagedContent("/api/hotels", "id");
        Long roomTypeId = extractFirstIdFromPagedContent("/api/room-types", "id");

        LocalDate checkIn = LocalDate.now().plusDays(10);
        LocalDate checkOut = LocalDate.now().plusDays(12);

        mockMvc.perform(post("/api/availability/check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "hotelId": %d,
                                  "roomTypeId": %d,
                                  "checkinDate": "%s",
                                  "checkoutDate": "%s",
                                  "guests": 1
                                }
                                """.formatted(hotelId, roomTypeId, checkIn, checkOut)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").exists());

        MvcResult bookingCreateResult = mockMvc.perform(post("/api/bookings")
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "roomTypeId": %d,
                                  "checkIn": "%s",
                                  "checkOut": "%s",
                                  "guests": 1
                                }
                                """.formatted(roomTypeId, checkIn, checkOut)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn();

        JsonNode bookingJson = objectMapper.readTree(bookingCreateResult.getResponse().getContentAsString());
        long bookingId = bookingJson.get("id").asLong();

        MvcResult paymentIntentResult = mockMvc.perform(post("/api/payments/intent")
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "bookingId": %d
                                }
                                """.formatted(bookingId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("INITIATED"))
                .andReturn();

        JsonNode paymentJson = objectMapper.readTree(paymentIntentResult.getResponse().getContentAsString());
        long paymentId = paymentJson.get("id").asLong();

        mockMvc.perform(post("/api/payments/{paymentId}/simulate", paymentId)
                        .header("Authorization", "Bearer " + guestToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "outcome":"SUCCESS"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));

        mockMvc.perform(get("/api/bookings/{bookingId}", bookingId)
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
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

    private Long extractFirstIdFromPagedContent(String endpoint, String fieldName) throws Exception {
        MvcResult result = mockMvc.perform(get(endpoint))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode root = objectMapper.readTree(result.getResponse().getContentAsString());
        JsonNode content = root.get("content");
        assertThat(content).isNotNull();
        assertThat(content.isArray()).isTrue();
        assertThat(content.size()).isGreaterThan(0);
        return content.get(0).get(fieldName).asLong();
    }
}
