package com.matchme.srv.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matchme.srv.controller.AuthController;
import com.matchme.srv.dto.request.LoginRequestDTO;
import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.exception.DuplicateFieldException;
import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.security.WebSecurityConfig;
import com.matchme.srv.security.jwt.AuthEntryPointJwt;
import com.matchme.srv.security.jwt.JwtUtils;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.security.services.UserDetailsServiceImpl;
import com.matchme.srv.service.user.UserCreationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import(WebSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtils jwtUtils;

    @MockitoBean
    private UserCreationService creationService;

    @MockitoBean
    private UserDetailsServiceImpl userDetailsService;

    @MockitoBean
    private AuthEntryPointJwt unauthorizedHandler;

    @MockitoBean
    private Authentication authentication;
    
    @MockitoBean
    private UserDetailsImpl userDetails;

    @BeforeEach
    void setUp() {
        // Create a mock UserDetailsImpl
        userDetails = Mockito.mock(UserDetailsImpl.class);
        when(userDetails.getId()).thenReturn(1L);

        // Create a mock Authentication
        authentication = Mockito.mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        // Mock the securityUtils to return the user ID when getCurrentUserId is called
        when(authenticationManager.authenticate(any(Authentication.class))).thenReturn(authentication);
    }

    private static final String INVALID_EMAIL = "testexample.com";
    private static final String INVALID_PASSWORD = "test";
    private static final String INVALID_PHONE = "";

    private final ActivityLog mockedActivityLog = Mockito.mock(ActivityLog.class);

    @Test
    void shouldSuccessfullySignUpAndIn() throws Exception {
        String validEmail = "signupandin@test.com";
        String validPassword = "testtest";
        String validPhone = "+372 5341 4494";

        SignupRequestDTO signUpRequest = SignupRequestDTO.builder()
                .email(validEmail)
                .password(validPassword)
                .number(validPhone)
                .build();

        // Mocking the signup and signin behavior
        when(creationService.createUser(any(SignupRequestDTO.class))).thenReturn(mockedActivityLog);
        when(jwtUtils.generateJwtToken(any(Authentication.class))).thenReturn("dummy-jwt-token");

        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isCreated());

        LoginRequestDTO loginRequest = new LoginRequestDTO();
        loginRequest.setEmail(validEmail);
        loginRequest.setPassword(validPassword);

        mockMvc.perform(post("/api/auth/signin").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))).andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("dummy-jwt-token"));
    }

    // Tests duplicate email
    @Test
    void shouldReturnEmailAlreadyExists() throws Exception {
        String validEmail = "emailalreadyexists@test.com";
        String validPassword = "testtest";
        String validPhone = "+372 4556342";

        SignupRequestDTO signUpRequest = SignupRequestDTO.builder()
                .email(validEmail)
                .password(validPassword)
                .number(validPhone)
                .build();

        // Mocking the signup behavior
        when(creationService.createUser(any(SignupRequestDTO.class))).thenReturn(mockedActivityLog);
        
        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(signUpRequest)))
        .andExpect(status().isCreated());
        
        when(creationService.createUser(any(SignupRequestDTO.class)))
            .thenThrow(new DuplicateFieldException("email", "Email already exists"));
        
        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email already exists"));
    }

    // Tests the @Email constraint (which seems to be useless since it only checks if @ is present)
    @Test
    void shouldReturnInvalidEmail() throws Exception {
        String validPassword = "testtest";
        String validPhone = "+372 3246543";

        SignupRequestDTO signUpRequest = SignupRequestDTO.builder()
            .email(INVALID_EMAIL)
            .password(validPassword)
            .number(validPhone)
            .build();

        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.email").value("Email must be valid"));
    }

    // Tests the @Password constraint (which checks if the password is between 6 and 40 characters)
    @Test
    void shouldReturnInvalidPassword() throws Exception {
        String validEmail = "invalidpassword@test.com";
        String validPhone = "+372 3563443";

        SignupRequestDTO signUpRequest = SignupRequestDTO.builder()
                .email(validEmail)
                .password(INVALID_PASSWORD)
                .number(validPhone)
                .build();

        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest()).andExpect(jsonPath("$.password")
                        .value("Password must be between 6 and 40 characters"));
    }

    @Test
    void shouldReturnInvalidPhoneNumber() throws Exception {
        String validEmail = "invalidphonenumber@test.com";
        String validPassword = "testtest";

        SignupRequestDTO signUpRequest = SignupRequestDTO.builder()
            .email(validEmail)
            .password(validPassword)
            .number(INVALID_PHONE)
            .build();

        mockMvc.perform(post("/api/auth/signup").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signUpRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.number").value("Invalid phone number"));
    }
}
