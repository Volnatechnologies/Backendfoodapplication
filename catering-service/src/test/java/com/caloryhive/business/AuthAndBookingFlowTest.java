package com.caloryhive.business;

import static org.assertj.core.api.Assertions.assertThat;

import com.caloryhive.business.auth.dto.RegisterRequest;
import com.caloryhive.business.auth.dto.LoginRequest;
import com.caloryhive.business.auth.dto.AuthResponse;
import com.caloryhive.business.catering.dto.CreateBookingRequest;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthAndBookingFlowTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldRegisterAndLoginSuccessfully() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Alice");
        request.setLastName("Manager");
        request.setEmail("alice.manager@example.com");
        request.setPassword("StrongPass123!");

        ResponseEntity<AuthResponse> response = restTemplate.postForEntity("/api/auth/register", request,
                AuthResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getToken()).isNotBlank();

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("alice.manager@example.com");
        loginRequest.setPassword("StrongPass123!");

        ResponseEntity<AuthResponse> login = restTemplate.postForEntity("/api/auth/login", loginRequest,
                AuthResponse.class);
        assertThat(login.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(login.getBody()).isNotNull();
        assertThat(login.getBody().getToken()).isNotBlank();
    }

    @Test
    void shouldRejectBookingWithZeroGuests() {
        RegisterRequest request = new RegisterRequest();
        request.setFirstName("Bob");
        request.setLastName("Staff");
        request.setEmail("bob.staff@example.com");
        request.setPassword("StrongPass123!");

        ResponseEntity<AuthResponse> register = restTemplate.postForEntity("/api/auth/register", request,
                AuthResponse.class);
        String token = register.getBody().getToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token);

        CreateBookingRequest bookingRequest = new CreateBookingRequest();
        bookingRequest.setEventName("Test Event");
        bookingRequest.setEventTypeId(UUID.randomUUID());
        bookingRequest.setEventDate(LocalDate.of(2026, 10, 24));
        bookingRequest.setEventTime(LocalTime.of(10, 0));
        bookingRequest.setGuestCount(0);
        bookingRequest.setVenueAddress("123 Main St");
        bookingRequest.setMenuPackageId(UUID.randomUUID());

        ResponseEntity<String> response = restTemplate.exchange(
                "/api/catering/bookings",
                org.springframework.http.HttpMethod.POST,
                new HttpEntity<>(bookingRequest, headers),
                String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}
