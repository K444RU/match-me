package com.matchme.srv.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchme.srv.ERole.ERole;
import com.matchme.srv.Role.Role;
import com.matchme.srv.payload.request.LoginRequest;
import com.matchme.srv.payload.request.SignupRequest;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  private static final String INVALID_EMAIL = "testexample.com";
  private static final String INVALID_PASSWORD = "test";
  private static final String INVALID_PHONE = "";

  @BeforeEach
  void setup() {
    Role userRole = new Role();
    userRole.setName(ERole.ROLE_USER);
  }

  @Test
  void shouldReturnPublicContent() throws Exception {
    this.mockMvc.perform(get("/api/test/all")).andDo(print()).andExpect(status().isOk())
        .andExpect(content().string(containsString("Public Content.")));
  }

  @Test
  void shouldReturnUnauthorized() throws Exception {
    this.mockMvc.perform(get("/api/test/user")).andDo(print()).andExpect(status().isUnauthorized());
    this.mockMvc.perform(get("/api/test/mod")).andDo(print()).andExpect(status().isUnauthorized());
    this.mockMvc.perform(get("/api/test/admin")).andDo(print()).andExpect(status().isUnauthorized());
  }

  @Test
  void shouldReturnUserContent() throws Exception {
    // TODO:
    // this.mockMvc.perform(get("/api/test/user")).andDo(print()).andExpect(status().isOk())
    // .andExpect(content().string(containsString("User Content.")));
  }

  @Test
  void shouldReturnModeratorContent() throws Exception {
    // TODO:
    // this.mockMvc.perform(get("/api/test/mod")).andDo(print()).andExpect(status().isOk())
    // .andExpect(content().string(containsString("Moderator Content.")));
  }

  @Test
  void shouldReturnAdminContent() throws Exception {
    // TODO:
    // this.mockMvc.perform(get("/api/test/admin")).andDo(print()).andExpect(status().isOk())
    // .andExpect(content().string(containsString("Admin Content.")));
  }

  @Test
  void shouldSuccessfullySignUpAndIn() throws Exception {
    String VALID_EMAIL = "signupandin@test.com";
    String VALID_PASSWORD = "testtest";
    String VALID_PHONE = "+372 3454443";

    SignupRequest signUpRequest = new SignupRequest();
    signUpRequest.setEmail(VALID_EMAIL);
    signUpRequest.setPassword(VALID_PASSWORD);
    signUpRequest.setNumber(VALID_PHONE);

    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isOk());

    LoginRequest loginRequest = new LoginRequest();
    loginRequest.setEmail(VALID_EMAIL);
    loginRequest.setPassword(VALID_PASSWORD);

    mockMvc.perform(post("/api/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());
  }

  @Test
  void shouldReturnEmailAlreadyExists() throws Exception {
    String VALID_EMAIL = "emailalreadyexists@test.com";
    String VALID_PASSWORD = "testtest";
    String VALID_PHONE = "+372 4556342";

    SignupRequest signUpRequest = new SignupRequest();
    signUpRequest.setEmail(VALID_EMAIL);
    signUpRequest.setPassword(VALID_PASSWORD);
    signUpRequest.setNumber(VALID_PHONE);

    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isOk());

    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value("Error: email is already taken!"));
  }

  @Test
  void shouldReturnInvalidEmail() throws Exception {
    String VALID_PASSWORD = "testtest";
    String VALID_PHONE = "+372 3246543";

    SignupRequest signUpRequest = new SignupRequest();
    signUpRequest.setEmail(INVALID_EMAIL);
    signUpRequest.setPassword(VALID_PASSWORD);
    signUpRequest.setNumber(VALID_PHONE);
    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("Email must be valid"));
  }

  @Test
  void shouldReturnInvalidPassword() throws Exception {
    String VALID_EMAIL = "invalidpassword@test.com";
    String VALID_PHONE = "+372 3563443";

    SignupRequest signUpRequest = new SignupRequest();
    signUpRequest.setEmail(VALID_EMAIL);
    signUpRequest.setPassword(INVALID_PASSWORD);
    signUpRequest.setNumber(VALID_PHONE);
    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.password").value("Password must be between 6 and 40 characters"));
  }

  @Test
  void shouldReturnInvalidPhoneNumber() throws Exception {
    String VALID_EMAIL = "invalidphonenumber@test.com";
    String VALID_PASSWORD = "testtest";

    SignupRequest signUpRequest = new SignupRequest();
    signUpRequest.setEmail(VALID_EMAIL);
    signUpRequest.setPassword(VALID_PASSWORD);
    signUpRequest.setNumber(INVALID_PHONE);
    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.number").value("Phone number cannot be empty"));
  }
}
