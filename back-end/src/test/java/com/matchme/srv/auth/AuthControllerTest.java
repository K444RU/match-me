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
import com.matchme.srv.dto.request.LoginRequestDTO;
import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.model.user.Role;

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

  // Runs before running test(s)
  @BeforeEach
  void setup() {
    Role userRole = new Role();
    userRole.setName(Role.UserRole.ROLE_USER);
  }

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
  void shouldReturnUserContent() throws Exception {
    // TODO: Finish shouldReturnUserContent
    // this.mockMvc.perform(get("/api/test/user")).andDo(print()).andExpect(status().isOk())
    // .andExpect(content().string(containsString("User Content.")));
  }

  // Should return 200 and string "Moderator Content."
  @Test
  void shouldReturnModeratorContent() throws Exception {
    // TODO: Finish shouldReturnModeratorContent
    // this.mockMvc.perform(get("/api/test/mod")).andDo(print()).andExpect(status().isOk())
    // .andExpect(content().string(containsString("Moderator Content.")));
  }

  // Should return 200 and string "Admin Content."
  @Test
  void shouldReturnAdminContent() throws Exception {
    // TODO: Finish shouldReturnAdminContent
    // this.mockMvc.perform(get("/api/test/admin")).andDo(print()).andExpect(status().isOk())
    // .andExpect(content().string(containsString("Admin Content.")));
  }

  @Test
  void shouldSuccessfullySignUpAndIn() throws Exception {
    String VALID_EMAIL = "signupandin@test.com";
    String VALID_PASSWORD = "testtest";
    String VALID_PHONE = "+372 3454443";

    // Successful sign up should return status 200 with
    // "message": "User registered successfully!"
    SignupRequestDTO signUpRequest = new SignupRequestDTO();
    signUpRequest.setEmail(VALID_EMAIL);
    signUpRequest.setPassword(VALID_PASSWORD);
    signUpRequest.setNumber(VALID_PHONE);

    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isOk());

    // Successful sign in should return status 200 with token, type, id, email and roles
    LoginRequestDTO loginRequest = new LoginRequestDTO();
    loginRequest.setEmail(VALID_EMAIL);
    loginRequest.setPassword(VALID_PASSWORD);

    mockMvc.perform(post("/api/auth/signin")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists());
  }

  // Tests duplicate email
  @Test
  void shouldReturnEmailAlreadyExists() throws Exception {
    String VALID_EMAIL = "emailalreadyexists@test.com";
    String VALID_PASSWORD = "testtest";
    String VALID_PHONE = "+372 4556342";

    SignupRequestDTO signUpRequest = new SignupRequestDTO();
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

  // Tests the @Email constraint (which seems to be useless since it only checks if @ is present)
  @Test
  void shouldReturnInvalidEmail() throws Exception {
    String VALID_PASSWORD = "testtest";
    String VALID_PHONE = "+372 3246543";

    SignupRequestDTO signUpRequest = new SignupRequestDTO();
    signUpRequest.setEmail(INVALID_EMAIL);
    signUpRequest.setPassword(VALID_PASSWORD);
    signUpRequest.setNumber(VALID_PHONE);
    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.email").value("Email must be valid"));
  }

  // Tests the @Password constraint (which checks if the password is between 6 and 40 characters)
  @Test
  void shouldReturnInvalidPassword() throws Exception {
    String VALID_EMAIL = "invalidpassword@test.com";
    String VALID_PHONE = "+372 3563443";

    SignupRequestDTO signUpRequest = new SignupRequestDTO();
    signUpRequest.setEmail(VALID_EMAIL);
    signUpRequest.setPassword(INVALID_PASSWORD);
    signUpRequest.setNumber(VALID_PHONE);
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
    String VALID_EMAIL = "invalidphonenumber@test.com";
    String VALID_PASSWORD = "testtest";

    SignupRequestDTO signUpRequest = new SignupRequestDTO();
    signUpRequest.setEmail(VALID_EMAIL);
    signUpRequest.setPassword(VALID_PASSWORD);
    signUpRequest.setNumber(INVALID_PHONE);
    mockMvc.perform(post("/api/auth/signup")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isBadRequest()).andExpect(jsonPath("$.number").value("Phone number cannot be empty"));
  }
}
