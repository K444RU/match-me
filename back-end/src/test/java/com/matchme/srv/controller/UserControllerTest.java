package com.matchme.srv.controller;

import static com.matchme.srv.TestDataFactory.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;
import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.user.UserCreationService;
import com.matchme.srv.service.user.UserProfileService;
import com.matchme.srv.service.user.UserQueryService;
import com.matchme.srv.service.user.UserSettingsService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

  private MockMvc mockMvc;

  @Mock private UserCreationService creationService;

  @Mock private UserQueryService queryService;

  @Mock private UserProfileService profileService;

  @Mock private UserSettingsService settingsService;

  @Mock private ConnectionService connectionService;

  @Mock private Authentication authentication;

  @Mock private SecurityUtils securityUtils;

  @InjectMocks private UserController userController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();
  }

  @Nested
  @DisplayName("Tests requiring authentication")
  class AuthenticatedTests {
    @BeforeEach
    void setUp() {
      when(securityUtils.getCurrentUserId(any(Authentication.class)))
          .thenAnswer(
              invocation -> {
                UserDetailsImpl userDetails =
                    (UserDetailsImpl) ((Authentication) invocation.getArgument(0)).getPrincipal();
                return userDetails.getId();
              });
    }

    @Nested
    @DisplayName("getCurrentUser Tests")
    class GetCurrentUserTests {
      @Test
      @DisplayName("Should return current user details when authenticated")
      void getCurrentUser_WhenAuthenticated_ReturnsUserDetails() throws Exception {
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
        CurrentUserResponseDTO response = createCurrentUserResponse();

        when(queryService.getCurrentUserDTO(DEFAULT_USER_ID, DEFAULT_USER_ID)).thenReturn(response);

        mockMvc
            .perform(
                get("/api/users/{targetId}", DEFAULT_USER_ID)
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.id", is(DEFAULT_USER_ID.intValue())),
                jsonPath("$.email", is(DEFAULT_EMAIL)),
                jsonPath("$.firstName", is(DEFAULT_FIRST_NAME)),
                jsonPath("$.lastName", is(DEFAULT_LAST_NAME)),
                jsonPath("$.alias", is(DEFAULT_ALIAS)),
                jsonPath("$.role[0].id", is(1)),
                jsonPath("$.role[0].name", is(DEFAULT_ROLE)));
      }

      @Test
      @DisplayName("Should return 404 when user is not connected")
      void getCurrentUser_WhenUnauthorized_Returns404() {
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
        when(queryService.getCurrentUserDTO(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID))
            .thenThrow(new EntityNotFoundException("User not found or no access rights."));

        Exception exception =
            assertThrows(
                ServletException.class,
                () ->
                    mockMvc.perform(
                        get("/api/users/{targetId}", DEFAULT_TARGET_USER_ID)
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)));

        assertAll(
            () ->
                assertThat(exception.getCause())
                    .as("check if the exception is an instance of EntityNotFoundException")
                    .isInstanceOf(EntityNotFoundException.class),
            () ->
                assertThat(exception.getCause().getMessage())
                    .as("check if the exception message contains the expected message")
                    .contains("User not found or no access rights."));
      }

      @Test
      @DisplayName("Should return user info when connected")
      void getCurrentUser_WhenConnected_ReturnsUserDetails() throws Exception {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

        CurrentUserResponseDTO responseDTO = createTargetCurrentUserResponse();

        when(queryService.getCurrentUserDTO(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID))
            .thenReturn(responseDTO);

        // When/Then
        mockMvc
            .perform(
                get("/api/users/{targetId}", DEFAULT_TARGET_USER_ID)
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.id", is(DEFAULT_TARGET_USER_ID.intValue())),
                jsonPath("$.email", is(DEFAULT_TARGET_EMAIL)),
                jsonPath("$.firstName", is(DEFAULT_TARGET_FIRST_NAME)),
                jsonPath("$.lastName", is(DEFAULT_TARGET_LAST_NAME)),
                jsonPath("$.alias", is(DEFAULT_TARGET_ALIAS)),
                jsonPath("$.role[0].id", is(1)),
                jsonPath("$.role[0].name", is(DEFAULT_ROLE)));
      }
    }

    @Nested
    @DisplayName("getProfile Tests")
    class GetProfileTests {
      @Test
      @DisplayName("Should return user profile")
      void getProfile_WhenRequested_ReturnsUserProfile() throws Exception {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

        ProfileResponseDTO profileDTO = createProfileResponse();
        when(queryService.getUserProfileDTO(DEFAULT_USER_ID, DEFAULT_USER_ID))
            .thenReturn(profileDTO);

        // When/Then
        mockMvc
            .perform(
                get("/api/users/{targetId}/profile", DEFAULT_USER_ID)
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.first_name", is(DEFAULT_FIRST_NAME)),
                jsonPath("$.last_name", is(DEFAULT_LAST_NAME)),
                jsonPath("$.city", is(DEFAULT_CITY)));
      }

      @Test
      void getProfile_WhenUnauthorized_Returns404() {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
        when(queryService.getUserProfileDTO(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID))
            .thenThrow(new EntityNotFoundException("User not found or no access rights."));

        // When/Then
        Exception exception =
            assertThrows(
                ServletException.class,
                () ->
                    mockMvc.perform(
                        get("/api/users/{targetId}/profile", DEFAULT_TARGET_USER_ID)
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)));

        assertAll(
            () ->
                assertThat(exception.getCause())
                    .as("check if the exception is an instance of EntityNotFoundException")
                    .isInstanceOf(EntityNotFoundException.class),
            () ->
                assertThat(exception.getCause().getMessage())
                    .contains("User not found or no access rights."));
      }

      @Test
      @DisplayName("Should return user profile when connected")
      void getProfile_WhenConnected_ReturnsUserProfile() throws Exception {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

        ProfileResponseDTO profileDTO = createTargetProfileResponse();
        when(queryService.getUserProfileDTO(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID))
            .thenReturn(profileDTO);

        // When/Then
        mockMvc
            .perform(
                get("/api/users/{targetId}/profile", DEFAULT_TARGET_USER_ID)
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.first_name", is(DEFAULT_TARGET_FIRST_NAME)),
                jsonPath("$.last_name", is(DEFAULT_TARGET_LAST_NAME)),
                jsonPath("$.city", is(DEFAULT_TARGET_CITY)));
      }
    }

    @Nested
    @DisplayName("getBio Tests")
    class GetBioTests {
      @Test
      @DisplayName("Should return user bio")
      void getBio_WhenRequested_ReturnsUserBio() throws Exception {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

        BiographicalResponseDTO bioDTO = createBiographicalResponse();

        when(queryService.getBiographicalResponseDTO(DEFAULT_USER_ID, DEFAULT_USER_ID))
            .thenReturn(bioDTO);

        // When/Then
        mockMvc
            .perform(
                get("/api/users/{userId}/bio", DEFAULT_USER_ID)
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.gender_self.id", is(DEFAULT_GENDER_SELF_ID.intValue())),
                jsonPath("$.gender_self.name", is(DEFAULT_GENDER_SELF_NAME)),
                jsonPath("$.gender_other.id", is(DEFAULT_GENDER_OTHER_ID.intValue())),
                jsonPath("$.gender_other.name", is(DEFAULT_GENDER_OTHER_NAME)),
                jsonPath("$.age_self", is(DEFAULT_AGE_SELF)),
                jsonPath("$.age_max", is(DEFAULT_AGE_MAX)),
                jsonPath("$.age_min", is(DEFAULT_AGE_MIN)),
                jsonPath("$.distance", is(DEFAULT_DISTANCE)),
                jsonPath("$.probability_tolerance", is(DEFAULT_PROBABILITY_TOLERANCE)));
      }

      @Test
      @DisplayName("Should return 404 when user bio is not found")
      void getBio_WhenUnauthorized_Returns404() {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
        when(queryService.getBiographicalResponseDTO(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID))
            .thenThrow(new EntityNotFoundException("User not found or no access rights."));

        // When/Then
        Exception exception =
            assertThrows(
                ServletException.class,
                () ->
                    mockMvc.perform(
                        get("/api/users/{targetId}/bio", DEFAULT_TARGET_USER_ID)
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)));

        assertAll(
            () ->
                assertThat(exception.getCause())
                    .as("check if the exception is an instance of EntityNotFoundException")
                    .isInstanceOf(EntityNotFoundException.class),
            () ->
                assertThat(exception.getCause().getMessage())
                    .as("check if the exception message contains the expected message")
                    .contains("User not found or no access rights."));
      }

      @Test
      @DisplayName("Should return user bio when connected")
      void getBio_WhenConnected_ReturnsUserBio() throws Exception {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

        BiographicalResponseDTO bioDTO = createTargetBiographicalResponse();

        when(queryService.getBiographicalResponseDTO(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID))
            .thenReturn(bioDTO);

        // When/Then
        mockMvc
            .perform(
                get("/api/users/{targetId}/bio", DEFAULT_TARGET_USER_ID)
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                jsonPath("$.gender_self.id", is(DEFAULT_TARGET_GENDER_SELF_ID.intValue())),
                jsonPath("$.gender_self.name", is(DEFAULT_TARGET_GENDER_SELF_NAME)),
                jsonPath("$.gender_other.id", is(DEFAULT_TARGET_GENDER_OTHER_ID.intValue())),
                jsonPath("$.gender_other.name", is(DEFAULT_TARGET_GENDER_OTHER_NAME)),
                jsonPath("$.age_self", is(DEFAULT_TARGET_AGE_SELF)),
                jsonPath("$.age_max", is(DEFAULT_TARGET_AGE_MAX)),
                jsonPath("$.age_min", is(DEFAULT_TARGET_AGE_MIN)),
                jsonPath("$.distance", is(DEFAULT_TARGET_DISTANCE)),
                jsonPath("$.probability_tolerance", is(DEFAULT_TARGET_PROBABILITY_TOLERANCE)));
      }
    }

    @Nested
    @DisplayName("getConnections Tests")
    class GetConnectionsTests {
      @Test
      @DisplayName("Should return user connections")
      void getConnections_WhenRequested_ReturnsUserConnections() throws Exception {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

        List<ConnectionResponseDTO> connections = createConnectionsResponse(1);

        when(connectionService.getConnectionResponseDTO(DEFAULT_USER_ID, DEFAULT_USER_ID))
            .thenReturn(connections);

        mockMvc
            .perform(
                get("/api/users/{targetId}/connections", DEFAULT_USER_ID)
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpectAll(
                status().isOk(),
                jsonPath("$", hasSize(1)),
                jsonPath("$[0].id", is(1)),
                jsonPath("$[0].users[*].id", hasItem(DEFAULT_USER_ID.intValue())),
                jsonPath("$[0].users[*].email", hasItem(DEFAULT_EMAIL)),
                jsonPath("$[0].users[*].number", hasItem(DEFAULT_NUMBER)),
                jsonPath("$[0].users[*].id", hasItem(DEFAULT_TARGET_USER_ID.intValue())),
                jsonPath("$[0].users[*].email", hasItem(DEFAULT_TARGET_EMAIL)),
                jsonPath("$[0].users[*].number", hasItem(DEFAULT_TARGET_NUMBER)));
      }

      @Test
      @DisplayName("Should return 404 when user connections are not found")
      void getConnections_WhenUnauthorized_Returns404() {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

        when(connectionService.getConnectionResponseDTO(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID))
            .thenThrow(new EntityNotFoundException("User not found or no access rights."));

        // When/Then
        Exception exception =
            assertThrows(
                ServletException.class,
                () ->
                    mockMvc.perform(
                        get("/api/users/{targetId}/connections", DEFAULT_TARGET_USER_ID)
                            .principal(authentication)
                            .contentType(MediaType.APPLICATION_JSON)));

        assertAll(
            () ->
                assertThat(exception.getCause())
                    .as("check if the exception is an instance of EntityNotFoundException")
                    .isInstanceOf(EntityNotFoundException.class),
            () ->
                assertThat(exception.getCause().getMessage())
                    .as("check if the exception message contains the expected message")
                    .contains("User not found or no access rights."));
      }
    }

    @Nested
    @DisplayName("uploadProfilePicture Tests")
    class UploadProfilePictureTests {
      @Test
      @DisplayName("Should upload user profile picture")
      void uploadProfilePicture_WhenSuccess_Returns200() throws Exception {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

        // When/Then
        mockMvc
            .perform(
                post("/api/users/profile-picture")
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"base64Image\": \"" + DEFAULT_PROFILE_PICTURE + "\" }"))
            .andExpect(status().isOk());

        verify(profileService, times(1))
            .saveProfilePicture(eq(DEFAULT_USER_ID), any(ProfilePictureSettingsRequestDTO.class));
      }

      @Test
      @DisplayName("Should upload user profile picture when null request")
      void uploadProfilePicture_WhenNullRequest_Returns200() throws Exception {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

        // When/Then
        mockMvc
            .perform(
                post("/api/users/profile-picture")
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        verify(profileService, times(1)).saveProfilePicture(DEFAULT_USER_ID, null);
      }

      @Test
      @DisplayName("Should upload user profile picture when empty base64")
      void uploadProfilePicture_WhenEmptyBase64_Returns200() throws Exception {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
        String emptyBase64 = "";

        ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO();
        request.setBase64Image(emptyBase64);

        // When/Then
        mockMvc
            .perform(
                post("/api/users/profile-picture")
                    .principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"base64Image\":\"\"}"))
            .andExpect(status().isOk());

        verify(profileService, times(1))
            .saveProfilePicture(eq(DEFAULT_USER_ID), any(ProfilePictureSettingsRequestDTO.class));
      }

      @Test
      @DisplayName("Should upload user profile picture when invalid base64")
      void uploadProfilePicture_WhenInvalidBase64_Returns400() {
        // Given
        setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
        String invalidBase64 = "thisIsNotValid==";

        doThrow(new IllegalArgumentException("Invalid Base64 image data."))
            .when(profileService)
            .saveProfilePicture(
                eq(DEFAULT_USER_ID),
                argThat(dto -> dto != null && invalidBase64.equals(dto.getBase64Image())));

        // When/Then
        Exception exception =
            assertThrows(
                ServletException.class,
                () ->
                    mockMvc
                        .perform(
                            post("/api/users/profile-picture")
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"base64Image\": \"" + invalidBase64 + "\" }"))
                        .andExpect(status().isBadRequest()));

        assertAll(
            () ->
                assertThat(exception.getCause())
                    .as("check if the exception is an instance of IllegalArgumentException")
                    .isInstanceOf(IllegalArgumentException.class),
            () ->
                assertThat(exception.getCause().getMessage())
                    .as("check if the exception message contains the expected message")
                    .contains("Invalid Base64 image data."));
      }

      @Test
      @DisplayName("Should upload user profile picture when user not found")
      void uploadProfilePicture_WhenUserNotFound_Returns404() {
        // Given
        setupAuthenticatedUser(INVALID_USER_ID, "user1@example.com");

        ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO();
        request.setBase64Image(DEFAULT_PROFILE_PICTURE);

        doThrow(new EntityNotFoundException("User not found for ID: " + INVALID_USER_ID))
            .when(profileService)
            .saveProfilePicture(
                eq(INVALID_USER_ID),
                argThat(
                    dto -> dto != null && DEFAULT_PROFILE_PICTURE.equals(dto.getBase64Image())));

        // When/Then
        Exception exception =
            assertThrows(
                ServletException.class,
                () ->
                    mockMvc
                        .perform(
                            post("/api/users/profile-picture")
                                .principal(authentication)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(
                                    "{ \"base64Image\": \"" + DEFAULT_PROFILE_PICTURE + "\" }"))
                        .andExpect(status().isNotFound()));

        assertAll(
            () ->
                assertThat(exception.getCause())
                    .as("check if the exception is an instance of EntityNotFoundException")
                    .isInstanceOf(EntityNotFoundException.class),
            () ->
                assertThat(exception.getCause().getMessage())
                    .as("check if the exception message contains the expected message")
                    .contains("User not found for ID: " + INVALID_USER_ID));
      }
    }
  }

  @Nested
  @DisplayName("updateAccount Tests")
  class UpdateAccountTests {
    @Test
    @DisplayName("Successfully update account settings")
    void updateAccount_ValidRequest_Returns204() throws Exception {
      when(securityUtils.getCurrentUserId(any(Authentication.class)))
          .thenAnswer(
              invocation -> {
                UserDetailsImpl userDetails =
                    (UserDetailsImpl) ((Authentication) invocation.getArgument(0)).getPrincipal();
                return userDetails.getId();
              });
      setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
      AccountSettingsRequestDTO request = createValidAccountSettings();

      mockMvc
          .perform(
              put("/api/users/settings/account")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJsonString(request)))
          .andExpect(status().isNoContent());

      verify(settingsService, times(1))
          .updateAccountSettings(eq(DEFAULT_USER_ID), any(AccountSettingsRequestDTO.class));
    }

    @Test
    @DisplayName("Return 400 for invalid account settings")
    void updateAccount_InvalidRequest_Returns400() throws Exception {
      String invalidRequest = "{ \"email\": \"invalid\" }";

      mockMvc
          .perform(
              put("/api/users/settings/account")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(invalidRequest))
          .andExpect(status().isBadRequest());

      verify(settingsService, never()).updateAccountSettings(any(), any());
    }

    @Test
    @DisplayName("Return 404 when user doesn't exist")
    void updateAccount_UserNotFound_Returns404() {
      setupAuthenticatedUser(INVALID_USER_ID, DEFAULT_EMAIL);
      AccountSettingsRequestDTO request = createValidAccountSettings();

      doThrow(new EntityNotFoundException("User not found"))
          .when(settingsService)
          .updateAccountSettings(eq(INVALID_USER_ID), any());

      assertThrows(
          ServletException.class,
          () ->
              mockMvc.perform(
                  put("/api/users/settings/account")
                      .principal(authentication)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(asJsonString(request))));
    }
  }

  @Nested
  @DisplayName("updateProfile Tests")
  class UpdateProfileTests {

    @Test
    @DisplayName("Successfully update profile settings")
    void updateProfile_ValidRequest_Returns204() throws Exception {
      when(securityUtils.getCurrentUserId(any(Authentication.class)))
          .thenAnswer(
              invocation -> {
                UserDetailsImpl userDetails =
                    (UserDetailsImpl) ((Authentication) invocation.getArgument(0)).getPrincipal();
                return userDetails.getId();
              });
      setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
      ProfileSettingsRequestDTO request = createValidProfileSettings();

      mockMvc
          .perform(
              put("/api/users/settings/profile")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJsonString(request)))
          .andExpect(status().isNoContent());

      verify(settingsService)
          .updateProfileSettings(eq(DEFAULT_USER_ID), any(ProfileSettingsRequestDTO.class));
    }

    @Test
    @DisplayName("Return 400 for invalid profile settings")
    void updateProfile_InvalidRequest_Returns400() throws Exception {
      String invalidRequest = "{ \"first_name\": \"\" }";

      mockMvc
          .perform(
              put("/api/users/settings/profile")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(invalidRequest))
          .andExpect(status().isBadRequest());

      verify(settingsService, never()).updateProfileSettings(any(), any());
    }

    @Test
    @DisplayName("Return 404 when user doesn't exist")
    void updateProfile_UserNotFound_Returns404() {
      setupAuthenticatedUser(INVALID_USER_ID, DEFAULT_EMAIL);
      ProfileSettingsRequestDTO request = createValidProfileSettings();

      doThrow(new EntityNotFoundException("User not found"))
          .when(settingsService)
          .updateProfileSettings(eq(INVALID_USER_ID), any());

      assertThrows(
          ServletException.class,
          () ->
              mockMvc.perform(
                  put("/api/users/settings/profile")
                      .principal(authentication)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(asJsonString(request))));
    }
  }

  @Nested
  @DisplayName("updateAttributes Tests")
  class UpdateAttributesTests {

    @Test
    @DisplayName("Successfully update attributes settings")
    void updateAttributes_ValidRequest_Returns204() throws Exception {
      when(securityUtils.getCurrentUserId(any(Authentication.class)))
          .thenAnswer(
              invocation -> {
                UserDetailsImpl userDetails =
                    (UserDetailsImpl) ((Authentication) invocation.getArgument(0)).getPrincipal();
                return userDetails.getId();
              });
      setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
      AttributesSettingsRequestDTO request = createValidAttributesSettings();

      mockMvc
          .perform(
              put("/api/users/settings/attributes")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJsonString(request)))
          .andExpect(status().isNoContent());

      verify(settingsService)
          .updateAttributesSettings(eq(DEFAULT_USER_ID), any(AttributesSettingsRequestDTO.class));
    }

    @Test
    @DisplayName("Return 400 for invalid attributes settings")
    void updateAttributes_InvalidRequest_Returns400() throws Exception {
      String invalidRequest = "{ \"birth_date\": \"invalid-date\" }";

      mockMvc
          .perform(
              put("/api/users/settings/attributes")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(invalidRequest))
          .andExpect(status().isBadRequest());

      verify(settingsService, never()).updateAttributesSettings(any(), any());
    }

    @Test
    @DisplayName("Return 404 when user doesn't exist")
    void updateAttributes_UserNotFound_Returns404() {
      setupAuthenticatedUser(INVALID_USER_ID, DEFAULT_EMAIL);
      AttributesSettingsRequestDTO request = createValidAttributesSettings();

      doThrow(new EntityNotFoundException("User not found"))
          .when(settingsService)
          .updateAttributesSettings(eq(INVALID_USER_ID), any());

      assertThrows(
          ServletException.class,
          () ->
              mockMvc.perform(
                  put("/api/users/settings/attributes")
                      .principal(authentication)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(asJsonString(request))));
    }
  }

  @Nested
  @DisplayName("updatePreferences Tests")
  class UpdatePreferencesTests {

    @Test
    @DisplayName("Successfully update preferences settings")
    void updatePreferences_ValidRequest_Returns204() throws Exception {
      when(securityUtils.getCurrentUserId(any(Authentication.class)))
          .thenAnswer(
              invocation -> {
                UserDetailsImpl userDetails =
                    (UserDetailsImpl) ((Authentication) invocation.getArgument(0)).getPrincipal();
                return userDetails.getId();
              });
      setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
      PreferencesSettingsRequestDTO request = createValidPreferencesSettings();

      mockMvc
          .perform(
              put("/api/users/settings/preferences")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJsonString(request)))
          .andExpect(status().isNoContent());

      verify(settingsService)
          .updatePreferencesSettings(eq(DEFAULT_USER_ID), any(PreferencesSettingsRequestDTO.class));
    }

    @Test
    @DisplayName("Return 400 for invalid preferences settings")
    void updatePreferences_InvalidRequest_Returns400() throws Exception {
      String invalidRequest = "{ \"age_min\": -1 }";

      mockMvc
          .perform(
              put("/api/users/settings/preferences")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(invalidRequest))
          .andExpect(status().isBadRequest());

      verify(settingsService, never()).updatePreferencesSettings(any(), any());
    }

    @Test
    @DisplayName("Return 404 when user doesn't exist")
    void updatePreferences_UserNotFound_Returns404() {
      setupAuthenticatedUser(INVALID_USER_ID, DEFAULT_EMAIL);
      PreferencesSettingsRequestDTO request = createValidPreferencesSettings();

      doThrow(new EntityNotFoundException("User not found"))
          .when(settingsService)
          .updatePreferencesSettings(eq(INVALID_USER_ID), any());

      assertThrows(
          ServletException.class,
          () ->
              mockMvc.perform(
                  put("/api/users/settings/preferences")
                      .principal(authentication)
                      .contentType(MediaType.APPLICATION_JSON)
                      .content(asJsonString(request))));
    }
  }

  @Nested
  @DisplayName("setUserParameters Tests")
  class SetUserParametersTests {
    @Test
    @DisplayName("Should complete registration with valid parameters")
    void testSetParameters_Success() throws Exception {
      when(securityUtils.getCurrentUserId(any(Authentication.class)))
          .thenAnswer(
              invocation -> {
                UserDetailsImpl userDetails =
                    (UserDetailsImpl) ((Authentication) invocation.getArgument(0)).getPrincipal();
                return userDetails.getId();
              });

      setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
      UserParametersRequestDTO request = createValidParametersRequest();

      mockMvc
          .perform(
              patch("/api/users/complete-registration")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(asJsonString(request)))
          .andExpect(status().isOk());

      verify(creationService, times(1)).setUserParameters(eq(DEFAULT_USER_ID), any());
    }

    @Test
    @DisplayName("Should return 400 for invalid parameters")
    void testSetParameters_InvalidInput() throws Exception {
      // Include all required fields except the invalid one being tested
      String invalidRequest =
          "{ "
              + "\"firstName\": \"\", "
              + "\"lastName\": \"Doe\", "
              + "\"birth_date\": \"2000-01-01\", "
              + "\"gender_self\": 1"
              + " }";

      mockMvc
          .perform(
              patch("/api/users/complete-registration")
                  .principal(authentication)
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(invalidRequest))
          .andExpect(status().isBadRequest());

      verify(creationService, never()).setUserParameters(eq(DEFAULT_USER_ID), any());
    }
  }

  @Nested
  @DisplayName("verifyAccount Tests")
  class VerifyAccountTests {
    @Test
    @DisplayName("Should verify account with valid code")
    void verifyAccount_WhenValidCode_Returns200() throws Exception {
      mockMvc
          .perform(
              patch("/api/users/verify/{userId}", DEFAULT_USER_ID)
                  .param("verificationCode", "123456"))
          .andExpect(status().isOk());

      verify(creationService).verifyAccount(DEFAULT_USER_ID, 123456);
    }

    @Test
    @DisplayName("Should return 400 for invalid verification code")
    void testVerifyAccount_InvalidCode() {
      doThrow(new IllegalArgumentException("Invalid verification code"))
          .when(creationService)
          .verifyAccount(DEFAULT_USER_ID, 000000);

      Exception exception =
          assertThrows(
              ServletException.class,
              () ->
                  mockMvc.perform(
                      patch("/api/users/verify/{userId}", DEFAULT_USER_ID)
                          .param("verificationCode", "000000")));

      assertThat(exception.getCause()).isInstanceOf(IllegalArgumentException.class);
    }
  }

  /**
   * Helper method to setup authenticated status
   *
   * <p>When auth.getPrincipal -> returns userDetails
   *
   * @param userId
   * @param email
   */
  private void setupAuthenticatedUser(Long userId, String email) {
    UserDetailsImpl userDetails =
        new UserDetailsImpl(userId, email, "password", Collections.emptySet());
    when(authentication.getPrincipal()).thenReturn(userDetails);
  }

  private String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().registerModule(new JavaTimeModule()).writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}
