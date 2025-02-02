package com.matchme.srv.controller;

import static com.matchme.srv.TestDataFactory.*;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.HobbyService;
import com.matchme.srv.service.user.UserQueryService;
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
class MeControllerTest {

  private MockMvc mockMvc;

  @Mock private ChatService chatService;

  @Mock private UserQueryService queryService;

  @Mock private ConnectionService connectionService;

  @Mock private HobbyService hobbyService;

  @Mock private Authentication authentication;

  @Mock private SecurityUtils securityUtils;

  @InjectMocks private MeController meController;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(meController)
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
  @DisplayName("User fetches himself")
  void testGetUser() throws Exception {
    setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
    CurrentUserResponseDTO response = createCurrentUserResponse();

    when(queryService.getCurrentUserDTO(DEFAULT_USER_ID, DEFAULT_USER_ID)).thenReturn(response);

    mockMvc
        .perform(get("/api/me").principal(authentication).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.id", is(DEFAULT_USER_ID.intValue())),
            jsonPath("$.email", is(DEFAULT_EMAIL)),
            jsonPath("$.firstName", is(DEFAULT_FIRST_NAME)),
            jsonPath("$.lastName", is(DEFAULT_LAST_NAME)),
            jsonPath("$.alias", is(DEFAULT_ALIAS)),
            jsonPath("$.profilePicture", is(DEFAULT_PROFILE_PICTURE)),
            jsonPath("$.role[0].id", is(1)),
            jsonPath("$.role[0].name", is(DEFAULT_ROLE)));
  }

  @Test
  @DisplayName("User fetches his profile")
  void testGetProfile() throws Exception {
    setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
    ProfileResponseDTO profile = createProfileResponse();

    when(queryService.getUserProfileDTO(DEFAULT_USER_ID, DEFAULT_USER_ID)).thenReturn(profile);

    mockMvc
        .perform(
            get("/api/me/profile")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.first_name", is(DEFAULT_FIRST_NAME)),
            jsonPath("$.last_name", is(DEFAULT_LAST_NAME)),
            jsonPath("$.city", is(DEFAULT_CITY)));
  }

  @Test
  @DisplayName("User fetches his bio")
  void testGetBio() throws Exception {
    // Given
    setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
    BiographicalResponseDTO bioDTO = createBiographicalResponse();

    when(queryService.getBiographicalResponseDTO(DEFAULT_USER_ID, DEFAULT_USER_ID))
        .thenReturn(bioDTO);

    // When/Then
    mockMvc
        .perform(
            get("/api/me/bio").principal(authentication).contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.gender_self.id", is(DEFAULT_GENDER_SELF_ID.intValue())),
            jsonPath("$.gender_self.name", is(DEFAULT_GENDER_SELF_NAME)),
            jsonPath("$.gender_other.id", is(DEFAULT_GENDER_OTHER_ID.intValue())),
            jsonPath("$.gender_other.name", is(DEFAULT_GENDER_OTHER_NAME)),
            jsonPath("$.hobbies", hasSize(DEFAULT_HOBBY_IDS.size())),
            jsonPath("$.age_self", is(DEFAULT_AGE_SELF)),
            jsonPath("$.age_min", is(DEFAULT_AGE_MIN)),
            jsonPath("$.age_max", is(DEFAULT_AGE_MAX)),
            jsonPath("$.distance", is(DEFAULT_DISTANCE)),
            jsonPath("$.probability_tolerance", is(DEFAULT_PROBABILITY_TOLERANCE)));
  }

  @Test
  @DisplayName("User fetches his connections")
  void testGetConnections() throws Exception {
    // Given
    setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);

    List<ConnectionResponseDTO> connections =
        List.of(createConnectionResponse(DEFAULT_USER_ID, DEFAULT_TARGET_USER_ID));

    when(connectionService.getConnectionResponseDTO(DEFAULT_USER_ID, DEFAULT_USER_ID))
        .thenReturn(connections);

    // When/Then
    mockMvc
        .perform(
            get("/api/connections")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$", hasSize(1)),
            jsonPath(
                "$[0].users[*].id",
                containsInAnyOrder(DEFAULT_USER_ID.intValue(), DEFAULT_TARGET_USER_ID.intValue())),
            jsonPath(
                "$[0].users[*].email", containsInAnyOrder(DEFAULT_EMAIL, DEFAULT_TARGET_EMAIL)),
            jsonPath(
                "$[0].users[*].number", containsInAnyOrder(DEFAULT_NUMBER, DEFAULT_TARGET_NUMBER)));
  }

  @Test
  @DisplayName("User fetches his connections when empty")
  void testGetConnections_isEmpty() throws Exception {
    // Given
    setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
    when(connectionService.getConnectionResponseDTO(DEFAULT_USER_ID, DEFAULT_USER_ID))
        .thenReturn(Collections.emptyList());

    // When/Then
    mockMvc
        .perform(
            get("/api/connections")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(status().isOk(), jsonPath("$", empty()));
  }

  @Test
  @DisplayName("User fetches settings parameters")
  void testGetSettings() throws Exception {
    // Given
    setupAuthenticatedUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
    SettingsResponseDTO response = createSettingsResponse();

    when(queryService.getSettingsResponseDTO(DEFAULT_USER_ID, DEFAULT_USER_ID))
        .thenReturn(response);

    // When/Then
    mockMvc
        .perform(
            get("/api/me/settings")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
        .andExpectAll(
            status().isOk(),
            jsonPath("$.email", is(DEFAULT_EMAIL)),
            jsonPath("$.number", is(DEFAULT_NUMBER)),
            jsonPath("$.firstName", is(DEFAULT_FIRST_NAME)),
            jsonPath("$.lastName", is(DEFAULT_LAST_NAME)),
            jsonPath("$.alias", is(DEFAULT_ALIAS)),
            jsonPath("$.hobbies", hasSize(DEFAULT_HOBBY_IDS.size())),
            jsonPath("$.hobbies", containsInAnyOrder(DEFAULT_HOBBY_IDS.stream().map(Long::intValue).toArray(Integer[]::new))),
            jsonPath("$.genderSelf", is(DEFAULT_GENDER_SELF_ID.intValue())),
            jsonPath("$.birthDate", is(DEFAULT_BIRTH_DATE)),
            jsonPath("$.city", is(DEFAULT_CITY)),
            jsonPath("$.longitude", is(DEFAULT_LONGITUDE)),
            jsonPath("$.latitude", is(DEFAULT_LATITUDE)),
            jsonPath("$.genderOther", is(DEFAULT_GENDER_OTHER_ID.intValue())),
            jsonPath("$.ageMin", is(DEFAULT_AGE_MIN)),
            jsonPath("$.ageMax", is(DEFAULT_AGE_MAX)),
            jsonPath("$.distance", is(DEFAULT_DISTANCE)),
            jsonPath("$.probabilityTolerance", is(DEFAULT_PROBABILITY_TOLERANCE)));
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
