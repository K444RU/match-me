package com.matchme.srv.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import java.time.LocalDate;
import java.time.Period;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MeControllerTest {

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
    private MeController meController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(meController)
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
        Long userId = 1L;
        String email = "user1@example.com";
        String firstName = "firstName";
        String lastName = "lastName";
        String alias = "alias";
        String profilePicture = "data:image/png;base64,dummyImageData";

        UserRoleType roleUser = new UserRoleType();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");
        Set<UserRoleType> roles = Set.of(roleUser);

        setupAuthenticatedUser(userId, email);

        CurrentUserResponseDTO responseDTO = CurrentUserResponseDTO.builder()
                .id(userId)
                .email(email)
                .firstName(firstName)
                .lastName(lastName)
                .alias(alias)
                .profilePicture(profilePicture)
                .role(roles)
                .build();

        when(userService.getCurrentUserDTO(userId)).thenReturn(responseDTO);

        mockMvc.perform(get("/api/me")
                        .principal(authentication)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.email").value(email))
                .andExpect(jsonPath("$.firstName").value(firstName))
                .andExpect(jsonPath("$.lastName").value(lastName))
                .andExpect(jsonPath("$.alias").value(alias))
                .andExpect(jsonPath("$.profilePicture").value(profilePicture))
                .andExpect(jsonPath("$.role[0].id").value(1))
                .andExpect(jsonPath("$.role[0].name").value("ROLE_USER"));
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
        mockMvc.perform(get("/api/me/profile")
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.first_name", is(firstName)))
                .andExpect(jsonPath("$.last_name", is(lastName)))
                .andExpect(jsonPath("$.city", is(city)));
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
        mockMvc.perform(get("/api/me/bio")
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

        mockMvc.perform(get("/api/connections")
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
    void testGetConnections_isEmpty() throws Exception {
        // Given
        Long userId = 1L;
        String email = "user1@example.com";
        setupAuthenticatedUser(userId, email);

        // When/Then
        mockMvc.perform(get("/api/connections", userId)
                .principal(authentication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
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
}
