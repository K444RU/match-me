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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.HobbyService;
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

  @Mock private ChatService chatService;

  @Mock private UserCreationService creationService;

  @Mock private UserQueryService queryService;

  @Mock private UserProfileService profileService;

  @Mock private UserSettingsService settingsService;

  @Mock private ConnectionService connectionService;

  @Mock private HobbyService hobbyService;

  @Mock private Authentication authentication;

  @Mock private SecurityUtils securityUtils;

  @InjectMocks private UserController userController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(userController)
            .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
            .build();

    when(securityUtils.getCurrentUserId(any(Authentication.class)))
        .thenAnswer(
            invocation -> {
              UserDetailsImpl userDetails =
                  (UserDetailsImpl) ((Authentication) invocation.getArgument(0)).getPrincipal();
              return userDetails.getId();
            });
  }

  @Test
  @DisplayName("Should return current user details when authenticated")
  void testGetUser() throws Exception {
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
  void testGetUser_Unauthorized() {
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
  void testGetUser_Connected() throws Exception {
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

  @Test
  @DisplayName("Should return user profile")
  void testGetProfile() throws Exception {
    // Given
    setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

    ProfileResponseDTO profileDTO = createProfileResponse();
    when(queryService.getUserProfileDTO(DEFAULT_USER_ID, DEFAULT_USER_ID)).thenReturn(profileDTO);

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
  void testGetProfile_Unauthorized() {
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
  void testGetProfile_Connected() throws Exception {
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

  @Test
  @DisplayName("Should return user bio")
  void testGetBio() throws Exception {
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
  void testGetBio_Unauthorized() {
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
  void testGetBio_Connected() throws Exception {
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

  @Test
  @DisplayName("Should return user connections")
  void testGetConnections() throws Exception {
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
  void testGetConnections_Unauthorized() {
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

  @Test
  @DisplayName("Should upload user profile picture")
  void testUploadProfilePicture_Success() throws Exception {
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
  void testUploadProfilePicture_NullRequest() throws Exception {
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
  void testUploadProfilePicture_EmptyBase64() throws Exception {
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
  void testUploadProfilePicture_InvalidBase64() {
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
  void testUploadProfilePicture_UserNotFound() {
    // Given
    setupAuthenticatedUser(INVALID_USER_ID, "user1@example.com");

    ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO();
    request.setBase64Image(DEFAULT_PROFILE_PICTURE);

    doThrow(new EntityNotFoundException("User not found for ID: " + INVALID_USER_ID))
        .when(profileService)
        .saveProfilePicture(
            eq(INVALID_USER_ID),
            argThat(dto -> dto != null && DEFAULT_PROFILE_PICTURE.equals(dto.getBase64Image())));

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
                            .content("{ \"base64Image\": \"" + DEFAULT_PROFILE_PICTURE + "\" }"))
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
}
