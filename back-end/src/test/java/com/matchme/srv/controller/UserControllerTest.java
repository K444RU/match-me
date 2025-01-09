package com.matchme.srv.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.ConnectionResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.security.services.UserDetailsImpl;
import com.matchme.srv.service.ChatService;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.HobbyService;
import com.matchme.srv.service.UserService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ChatService chatService;

    @Mock
    private UserService userService;

    @Mock
    private ConnectionService connectionService;

    @Mock
    private HobbyService hobbyService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .build();
    }

    /**
     * User fetches himself
     * 
     * @return {@link CurrentUserResponseDTO}
     * @throws Exception
     */
    @Test
    void testGetUser() throws Exception {
        // Given
        Long userId = 1L;
        String email = "user1@example.com";
        String firstName = "firstName";
        String lastName = "lastName";
        String alias = "alias";
        String city = "city";

        setupAuthenticatedUser(userId, email);

        User mockUser = createMockUser(userId, email, firstName, lastName, alias, city);

        when(userService.getUser(1L)).thenReturn(mockUser);
        when(userService.getUserProfile(1L)).thenReturn(mockUser.getProfile());

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}", userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userId.intValue())))
                .andExpect(jsonPath("$.email", is(email)))
                .andExpect(jsonPath("$.firstName", is(firstName)))
                .andExpect(jsonPath("$.lastName", is(lastName)))
                .andExpect(jsonPath("$.alias", is(alias)))
                .andExpect(jsonPath("$.role[0].id", is(1)))
                .andExpect(jsonPath("$.role[0].name", is("ROLE_USER")));
    }

    /**
     * Requesting user fetches a user whom he is not connected with
     * 
     * @return HTTP Status code 404 (instead of 401)
     * @throws Exception
     */
    @Test
    void testGetUser_Unauthorized() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        Long target_userId = 2L;

        setupAuthenticatedUser(req_userId, req_email);
        setupConnectionStatus(req_userId, target_userId, false);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}", target_userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Requesting user fetches a users info whom he is connected with
     * 
     * @return {@link CurrentUserResponseDTO}
     * @throws Exception
     */
    @Test
    void testGetUser_Connected() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";

        Long target_userId = 2L;
        String target_email = "user2@example.com";
        String target_firstName = "firstName";
        String target_lastName = "lastName";
        String target_alias = "alias";
        String target_city = "city";

        User mockTargetUser = createMockUser(target_userId, target_email, target_firstName, target_lastName,
                target_alias, target_city);

        when(userService.isConnected(req_userId, target_userId)).thenReturn(true);

        UserDetailsImpl userDetails = new UserDetailsImpl(req_userId, req_email, "password",
                Collections.emptySet());
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userService.getUser(target_userId)).thenReturn(mockTargetUser);
        when(userService.getUserProfile(target_userId)).thenReturn(mockTargetUser.getProfile());

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}", target_userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(target_userId.intValue())))
                .andExpect(jsonPath("$.email", is(target_email)))
                .andExpect(jsonPath("$.firstName", is(target_firstName)))
                .andExpect(jsonPath("$.lastName", is(target_lastName)))
                .andExpect(jsonPath("$.alias", is(target_alias)))
                .andExpect(jsonPath("$.role[0].id", is(1)))
                .andExpect(jsonPath("$.role[0].name", is("ROLE_USER")));
    }

    /**
     * User fetches his profile
     * 
     * @return {@link ProfileResponseDTO}
     * @throws Exception
     */
    @Test
    void testGetProfile() throws Exception {
        // Given
        Long userId = 1L;
        String email = "user1@example.com";
        String firstName = "firstName";
        String lastName = "lastName";
        String alias = "alias";
        String city = "city";

        setupAuthenticatedUser(userId, email);

        User mockUser = createMockUser(userId, email, firstName, lastName, alias, city);

        when(userService.getUser(userId)).thenReturn(mockUser);
        when(userService.getUserProfile(userId)).thenReturn(mockUser.getProfile());

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/profile", userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", is(firstName)))
                .andExpect(jsonPath("$.last_name", is(lastName)))
                .andExpect(jsonPath("$.city", is(city)));
    }

    @Test
    void testGetProfile_Unauthorized() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        Long target_userId = 2L;
        setupAuthenticatedUser(req_userId, req_email);
        setupConnectionStatus(req_userId, target_userId, false);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/profile", target_userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetProfile_Connected() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";

        Long target_userId = 2L;
        String target_email = "user2@example.com";
        String target_firstName = "firstName";
        String target_lastName = "lastName";

        setupAuthenticatedUser(req_userId, req_email);
        setupConnectionStatus(req_userId, target_userId, true);

        User mockTargetUser = createMockUser(target_userId, target_email, target_firstName, target_lastName, "alias",
                "city");
        when(userService.getUser(target_userId)).thenReturn(mockTargetUser);
        when(userService.getUserProfile(target_userId)).thenReturn(mockTargetUser.getProfile());

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/profile", target_userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", is(target_firstName)))
                .andExpect(jsonPath("$.last_name", is(target_lastName)))
                .andExpect(jsonPath("$.city", is("city")));
    }

    /**
     * User fetches his bio
     * 
     * @return {@link BiographicalResponseDTO}
     * @throws Exception
     */
    @Test
    void testGetBio() throws Exception {
        // Given
        Long userId = 1L;
        String email = "user1@example.com";
        String firstName = "firstName";
        String lastName = "lastName";
        String alias = "alias";
        String city = "city";
        UserGenderType genderSelf = createMockGenderType(1L); // MALE
        UserGenderType genderOther = createMockGenderType(2L); // FEMALE
        Set<Hobby> hobbies = createMockHobbies();
        Integer ageMin = 18;
        Integer ageMax = 100;
        Integer distance = 50;
        Double probabilityTolerance = 1.0;
        LocalDate birthDate = LocalDate.of(1995, 07, 20);
        List<Double> location = Arrays.asList(58.8879, 25.5412);

        Integer ageSelf = Period.between(birthDate, LocalDate.now()).getYears();

        setupAuthenticatedUser(userId, email);

        User mockUser = createMockUser(userId, email, firstName, lastName, alias, city);
        UserPreferences mockUserPreferences = createMockUserPreferences(ageMin, ageMax, distance, probabilityTolerance,
                genderOther);
        UserAttributes mockUserAttributes = createMockUserAttributes(birthDate, location, genderSelf);
        UserProfile mockUserProfile = createMockUserProfile(mockUser.getProfile(), mockUserPreferences,
                mockUserAttributes);
        mockUser.setProfile(mockUserProfile);
        mockUserProfile.setHobbies(hobbies);

        when(userService.getUser(1L)).thenReturn(mockUser);
        when(userService.getUserProfile(1L)).thenReturn(mockUser.getProfile());
        for (Hobby hobby : hobbies) {
            when(hobbyService.findById(hobby.getId())).thenReturn(hobby);
        }

    // When/Then
        mockMvc.perform(get("/api/users/{targetId}/bio", userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gender_self.id", is(genderSelf.getId())))
                .andExpect(jsonPath("$.gender_self.name", is(genderSelf.getName())))
                .andExpect(jsonPath("$.gender_other.id", is(genderOther.getId())))
                .andExpect(jsonPath("$.gender_other.name", is(genderOther.getName())))
                .andExpect(jsonPath("$.age_self", is(ageSelf)))
                .andExpect(jsonPath("$.age_max", is(ageMax)))
                .andExpect(jsonPath("$.age_min", is(ageMin)))
                .andExpect(jsonPath("$.distance", is(distance)))
                .andExpect(jsonPath("$.probability_tolerance", is(probabilityTolerance)));
    }

    @Test
    void testGetBio_Unauthorized() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        Long target_userId = 2L;
        setupAuthenticatedUser(req_userId, req_email);
        setupConnectionStatus(req_userId, target_userId, false);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/bio", target_userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetBio_Connected() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        Long target_userId = 2L;
        String target_email = "user2@example.com";
        String target_firstName = "firstName";
        String target_lastName = "lastName";
        String target_alias = "alias";
        String target_city = "city";
        UserGenderType target_genderSelf = createMockGenderType(1L); // MALE
        UserGenderType target_genderOther = createMockGenderType(2L); // FEMALE
        Set<Hobby> hobbies = createMockHobbies();
        Integer target_ageMin = 18;
        Integer target_ageMax = 100;
        Integer target_distance = 50;
        Double target_probabilityTolerance = 1.0;
        LocalDate target_birthDate = LocalDate.of(1995, 07, 20);
        List<Double> target_location = Arrays.asList(58.8879, 25.5412);

        Integer target_ageSelf = Period.between(target_birthDate, LocalDate.now()).getYears();

        setupAuthenticatedUser(req_userId, req_email);
        setupConnectionStatus(req_userId, target_userId, true);

        User mockUser = createMockUser(target_userId, target_email, target_firstName, target_lastName, target_alias,
                target_city);
        UserPreferences mockUserPreferences = createMockUserPreferences(target_ageMin, target_ageMax, target_distance,
                target_probabilityTolerance,
                target_genderOther);
        UserAttributes mockUserAttributes = createMockUserAttributes(target_birthDate, target_location,
                target_genderSelf);
        UserProfile mockUserProfile = createMockUserProfile(mockUser.getProfile(), mockUserPreferences,
                mockUserAttributes);
        mockUser.setProfile(mockUserProfile);
        mockUserProfile.setHobbies(hobbies);

        when(userService.getUser(target_userId)).thenReturn(mockUser);
        when(userService.getUserProfile(target_userId)).thenReturn(mockUser.getProfile());
        for (Hobby hobby : hobbies) {
            when(hobbyService.findById(hobby.getId())).thenReturn(hobby);
        }

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/bio", target_userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gender_self.id", is(target_genderSelf.getId())))
                .andExpect(jsonPath("$.gender_self.name", is(target_genderSelf.getName())))
                .andExpect(jsonPath("$.gender_other.id", is(target_genderOther.getId())))
                .andExpect(jsonPath("$.gender_other.name", is(target_genderOther.getName())))
                .andExpect(jsonPath("$.age_self", is(target_ageSelf)))
                .andExpect(jsonPath("$.age_max", is(target_ageMax)))
                .andExpect(jsonPath("$.age_min", is(target_ageMin)))
                .andExpect(jsonPath("$.distance", is(target_distance)))
                .andExpect(jsonPath("$.probability_tolerance", is(target_probabilityTolerance)));
    }

    /**
     * User fetches his connections
     * 
     * @return List of {@link ConnectionResponseDTO}
     * @throws Exception
     */
    @Test
    void testGetConnections() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        String req_number = "+372 55445544";
        String req_firstName = "firstName";
        String req_lastName = "lastName";
        String req_alias = "alias";
        String req_city = "city";

        Long target_userId = 2L;
        String target_email = "user2@example.com";
        String target_number = "+372 44554455";
        String target_firstName = "firstName";
        String target_lastName = "lastName";
        String target_alias = "alias";
        String target_city = "city";

        Long connectionId = 1L;

        setupAuthenticatedUser(req_userId, req_email);

        User mockRequestingUser = createMockUser(req_userId, req_email, req_firstName, req_lastName, req_alias,
                req_city, req_number);
        User mockTargetUser = createMockUser(target_userId, target_email, target_firstName, target_lastName,
                target_alias, target_city, target_number);

        Connection mockConnection = new Connection();
        mockConnection.setId(connectionId);
        mockConnection.setUsers(Set.of(mockRequestingUser, mockTargetUser));

        when(userService.getUser(req_userId)).thenReturn(mockRequestingUser);
        when(connectionService.getUserConnections(mockRequestingUser)).thenReturn(List.of(mockConnection));

        mockMvc.perform(get("/api/users/{targetId}/connections", req_userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(connectionId.intValue())))
                .andExpect(jsonPath("$[0].users[0].id", is(target_userId.intValue())))
                .andExpect(jsonPath("$[0].users[0].email", is(target_email)))
                .andExpect(jsonPath("$[0].users[0].number", is(target_number)))
                .andExpect(jsonPath("$[0].users[1].id", is(req_userId.intValue())))
                .andExpect(jsonPath("$[0].users[1].email", is(req_email)))
                .andExpect(jsonPath("$[0].users[1].number", is(req_number)));
    }

    @Test
    void testGetConnections_Unauthorized() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        Long target_userId = 2L;
        setupAuthenticatedUser(req_userId, req_email);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/connections", target_userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetConnections_Connected() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        Long target_userId = 2L;
        setupAuthenticatedUser(req_userId, req_email);
        setupConnectionStatus(req_userId, target_userId, true);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/connections", target_userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUploadProfilePicture_Success() throws Exception {
        // Given
        Long userId = 1L;
        setupAuthenticatedUser(userId, "user1@example.com");
        String validBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...";

        // When/Then
        mockMvc.perform(post("/api/users/profile-picture")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"base64Image\": \"" + validBase64 + "\" }"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Profile picture uploaded successfully.")));
    }

    @Test
    void testUploadProfilePicture_NullRequest() throws Exception {
        // Given
        Long userId = 1L;
        setupAuthenticatedUser(userId, "user1@example.com");

        doThrow(new IllegalArgumentException("No valid base64 image found in the request."))
                .when(userService).saveProfilePicture(eq(userId), eq(null));

        // When/Then
        mockMvc.perform(post("/api/users/profile-picture")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("No valid base64 image found in the request.")));
    }

    @Test
    void testUploadProfilePicture_EmptyBase64() throws Exception {
        // Given
        Long userId = 1L;
        setupAuthenticatedUser(userId, "user1@example.com");
        String emptyBase64 = "";

        ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO();
        request.setBase64Image(emptyBase64);

        doThrow(new IllegalArgumentException("Base64 image data cannot be null or empty."))
                .when(userService).saveProfilePicture(eq(userId), argThat(requestDto ->
                        requestDto != null && "".equals(requestDto.getBase64Image())));

        // When/Then
        mockMvc.perform(post("/api/users/profile-picture")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"base64Image\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Base64 image data cannot be null or empty.")));
    }

    @Test
    void testUploadProfilePicture_InvalidBase64() throws Exception {
        // Given
        Long userId = 1L;
        setupAuthenticatedUser(userId, "user1@example.com");
        String invalidBase64 = "thisIsNotValid==";

        doThrow(new IllegalArgumentException("Invalid Base64 image data."))
                .when(userService).saveProfilePicture(eq(userId), argThat(dto ->
                        dto != null && invalidBase64.equals(dto.getBase64Image())));

        // When/Then
        mockMvc.perform(post("/api/users/profile-picture")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"base64Image\": \"" + invalidBase64 + "\" }"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Invalid Base64 image data.")));
    }

    @Test
    void testUploadProfilePicture_UserNotFound() throws Exception {
        // Given
        Long userId = 999L;
        setupAuthenticatedUser(userId, "user1@example.com");

        String validBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...";
        ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO();
        request.setBase64Image(validBase64);

        doThrow(new EntityNotFoundException("User not found for ID: " + userId))
                .when(userService).saveProfilePicture(eq(userId), argThat(dto ->
                        dto != null && "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...".equals(dto.getBase64Image())));

        // When/Then
        mockMvc.perform(post("/api/users/profile-picture")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{ \"base64Image\": \"" + validBase64 + "\" }"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("User not found for ID: 999")));
    }

    /**
     * Helper method to create a set of hobbies
     * 
     * @return Set<Hobby>
     */
    private Set<Hobby> createMockHobbies() {
        Hobby hobby1 = new Hobby();
        hobby1.setId(1L);
        hobby1.setName("3D printing");
        hobby1.setCategory("General");
    
        Hobby hobby2 = new Hobby();
        hobby2.setId(2L);
        hobby2.setName("Acrobatics");
        hobby2.setCategory("General");

        return Set.of(hobby1, hobby2);
    }

    /**
     * Helper method to create user
     * 
     * @param id
     * @param email
     * @param firstName
     * @param lastName
     * @param alias
     * @return {@link User}
     */
    private User createMockUser(Long id, String email, String firstName, String lastName, String alias, String city) {
        User mockUser = new User();
        mockUser.setId(id);
        mockUser.setEmail(email);

        UserProfile profile = UserProfile.builder()
                .first_name(firstName)
                .last_name(lastName)
                .alias(alias)
                .city(city)
                .build();

        UserRoleType defaultRole = new UserRoleType();
        defaultRole.setId(1L);
        defaultRole.setName("ROLE_USER");

        mockUser.setProfile(profile);
        mockUser.setRole(defaultRole);

        return mockUser;
    }

    /**
     * Helper method to create user
     * 
     * @param id
     * @param email
     * @param firstName
     * @param lastName
     * @param alias
     * @param number
     * @return {@link User}
     */
    private User createMockUser(Long id, String email, String firstName, String lastName, String alias, String city,
            String number) {
        User mockUser = createMockUser(id, email, firstName, lastName, alias, city);
        mockUser.setNumber(number);
        return mockUser;
    }

    /**
     * Helper method to create user with complete profile
     * 
     * @param profile
     * @param preferences
     * @param attributes
     * @return {@link UserProfile} with {@link UserPreferences} and
     *         {@link UserAttributes}
     */
    private UserProfile createMockUserProfile(UserProfile profile, UserPreferences preferences,
            UserAttributes attributes) {
        if (preferences != null) {
            preferences.setUserProfile(profile);
            profile.setPreferences(preferences);
        }

        if (attributes != null) {
            attributes.setUserProfile(profile);
            profile.setAttributes(attributes);
        }

        return profile;
    }

    /**
     * Helper method to create UserPreferences
     * 
     * @param ageMin
     * @param ageMax
     * @param distance
     * @param probabilityTolerance
     * @param gender
     * @return {@link UserPreferences}
     */
    private UserPreferences createMockUserPreferences(Integer ageMin, Integer ageMax, Integer distance,
            Double probabilityTolerance, UserGenderType gender) {
        UserPreferences preferences = new UserPreferences();
        preferences.setAge_min(ageMin);
        preferences.setAge_max(ageMax);
        preferences.setDistance(distance);
        preferences.setProbability_tolerance(probabilityTolerance);
        preferences.setGender(gender);
        return preferences;
    }

    /**
     * Helper method to create UserAttributes
     * 
     * @param birthDate
     * @param location
     * @param gender
     * @return {@link UserAttributes}
     */
    private UserAttributes createMockUserAttributes(LocalDate birthDate, List<Double> location, UserGenderType gender) {
        UserAttributes attributes = new UserAttributes();
        attributes.setBirth_date(birthDate);
        attributes.setLocation(location);
        attributes.setGender(gender);
        return attributes;
    }

    /**
     * Helper method to create UserGenderType
     * 
     * @param id - 1 MALE | 2 FEMALE | 3 OTHER
     * @return {@link UserGenderType}
     */
    private UserGenderType createMockGenderType(Long id) {
        UserGenderType genderType = new UserGenderType();
        switch (id.intValue()) {
            case 1:
                genderType.setName("MALE");
                break;
            case 2:
                genderType.setName("FEMALE");
            default:
                genderType.setName("OTHER");
                break;
        }
        return genderType;
    }

    /**
     * Helper method to setup authenticated status
     * <p>
     * When auth.getPrincipal -> returns userDetails
     * 
     * @param userId
     * @param email
     */
    private void setupAuthenticatedUser(Long userId, String email) {
        UserDetailsImpl userDetails = new UserDetailsImpl(userId, email, "password",
                Collections.emptySet());
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    /**
     * Helper method to setup connection status
     * <p>
     * When userService.isConnected -> returns isConnected
     * 
     * @param req_userId
     * @param target_userId
     * @param isConnected
     */
    private void setupConnectionStatus(Long req_userId, Long target_userId, boolean isConnected) {
        when(userService.isConnected(req_userId, target_userId)).thenReturn(isConnected);
    }
}
