package com.matchme.srv.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchme.srv.dto.request.LoginRequestDTO;
import com.matchme.srv.dto.request.SignupRequestDTO;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Mock
    private Authentication authentication;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String INVALID_EMAIL = "testexample.com";
    private static final String INVALID_PASSWORD = "test";
    private static final String INVALID_PHONE = "";

    // Public routes
    @Test
    void shouldReturnPublicContent() throws Exception {
        this.mockMvc.perform(get("/api/test/all")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Public Content.")));
    }

    // Protected routes
    // Should return 401 because we are not logged in.
    @Test
    void shouldReturnUnauthorized() throws Exception {
        this.mockMvc.perform(get("/api/test/user")).andDo(print()).andExpect(status().isUnauthorized());
        this.mockMvc.perform(get("/api/test/mod")).andDo(print()).andExpect(status().isUnauthorized());
        this.mockMvc.perform(get("/api/test/admin")).andDo(print()).andExpect(status().isUnauthorized());
    }

    // Should return 200 and string "User Content."
    @Test
    @WithMockUser(username = "user", authorities = {"ROLE_USER"})
    void shouldReturnUserContent() throws Exception {
        this.mockMvc.perform(get("/api/test/user")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("User Content.")));
    }

    // Should return 200 and string "Moderator Board."
    @Test
    @WithMockUser(username = "moderator", authorities = {"ROLE_MODERATOR"})
    void shouldReturnModeratorContent() throws Exception {
        this.mockMvc.perform(get("/api/test/mod")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Moderator Board.")));
    }

    // Should return 200 and string "Admin Content."
    @Test
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMIN"})
    void shouldReturnAdminContent() throws Exception {
        this.mockMvc.perform(get("/api/test/admin")
                .principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Admin Board.")));
    }

    @Test
    void shouldSuccessfullySignUpAndIn() throws Exception {
        String validEmail = "signupandin@test.com";
        String validPassword = "testtest";
        String validPhone = "+372 5341 4494";

        SignupRequestDTO signUpRequest = new SignupRequestDTO();
        signUpRequest.setEmail(validEmail);
        signUpRequest.setPassword(validPassword);
        signUpRequest.setNumber(validPhone);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(validEmail);
        loginRequest.setPassword(validPassword);

        mockMvc.perform(post("/api/auth/signin")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    // Tests duplicate email
    @Test
    void shouldReturnEmailAlreadyExists() throws Exception {
        String validEmail = "emailalreadyexists@test.com";
        String validPassword = "testtest";
        String validPhone = "+372 4556342";

        SignupRequestDTO signUpRequest = new SignupRequestDTO();
        signUpRequest.setEmail(validEmail);
        signUpRequest.setPassword(validPassword);
        signUpRequest.setNumber(validPhone);

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email already exists"));
    }

    // Tests the @Email constraint (which seems to be useless since it only checks if @ is present)
    @Test
    void shouldReturnInvalidEmail() throws Exception {
        String validPassword = "testtest";
        String validPhone = "+372 3246543";

        SignupRequestDTO signUpRequest = new SignupRequestDTO();
        signUpRequest.setEmail(INVALID_EMAIL);
        signUpRequest.setPassword(validPassword);
        signUpRequest.setNumber(validPhone);
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email must be valid"));
    }

    // Tests the @Password constraint (which checks if the password is between 6 and 40 characters)
    @Test
    void shouldReturnInvalidPassword() throws Exception {
        String validEmail = "invalidpassword@test.com";
        String validPhone = "+372 3563443";

        SignupRequestDTO signUpRequest = new SignupRequestDTO();
        signUpRequest.setEmail(validEmail);
        signUpRequest.setPassword(INVALID_PASSWORD);
        signUpRequest.setNumber(validPhone);
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.password").value("Password must be between 6 and 40 characters"));
    }

    // Tests if the phone number is not empty
    // TODO: Improve shouldReturnInvalidPhoneNumber
    // Could add length checking.
    @Test
    void shouldReturnInvalidPhoneNumber() throws Exception {
        String validEmail = "invalidphonenumber@test.com";
        String validPassword = "testtest";

        SignupRequestDTO signUpRequest = new SignupRequestDTO();
        signUpRequest.setEmail(validEmail);
        signUpRequest.setPassword(validPassword);
        signUpRequest.setNumber(INVALID_PHONE);
        mockMvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.number").value("Phone number cannot be empty"));
    }
}
