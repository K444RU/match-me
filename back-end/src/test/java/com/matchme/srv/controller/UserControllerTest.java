package com.matchme.srv.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
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
import java.util.stream.Collectors;
import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
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
import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.UserResponseDTO;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.security.jwt.SecurityUtils;
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

    @Mock
    private SecurityUtils securityUtils;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver()).build();

        when(securityUtils.getCurrentUserId(any(Authentication.class))).thenAnswer(invocation -> {
            UserDetailsImpl userDetails =
                    (UserDetailsImpl) ((Authentication) invocation.getArgument(0)).getPrincipal();
            return userDetails.getId();
        });
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

        setupAuthenticatedUser(userId, email);

        UserRoleType roleUser = new UserRoleType();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");
        Set<UserRoleType> roles = Set.of(roleUser);

        CurrentUserResponseDTO responseDTO =
                CurrentUserResponseDTO.builder().id(userId).email(email).firstName(firstName)
                        .lastName(lastName).alias(alias).profilePicture(null).role(roles).build();

        when(userService.getUserDTO(userId, userId)).thenReturn(responseDTO);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}", userId).principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
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
        when(userService.getUserDTO(req_userId, target_userId))
                .thenThrow(new EntityNotFoundException("User not found or no access rights."));

        // When/Then
        try {
            mockMvc.perform(get("/api/users/{targetId}", target_userId).principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNotFound());
        } catch (ServletException e) {
            // Expected exception, test passes
            assertThat(e.getCause()).isInstanceOf(EntityNotFoundException.class);
            assertThat(e.getCause().getMessage()).contains("User not found or no access rights.");
        }
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

        setupAuthenticatedUser(req_userId, req_email);

        UserRoleType roleUser = new UserRoleType();
        roleUser.setId(1L);
        roleUser.setName("ROLE_USER");
        Set<UserRoleType> roles = Set.of(roleUser);

        CurrentUserResponseDTO responseDTO = CurrentUserResponseDTO.builder().id(target_userId)
                .email(target_email).firstName(target_firstName).lastName(target_lastName)
                .alias(target_alias).profilePicture(null).role(roles).build();

        when(userService.getUserDTO(req_userId, target_userId)).thenReturn(responseDTO);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}", target_userId).principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
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
        String city = "city";

        setupAuthenticatedUser(userId, email);

        ProfileResponseDTO profileDTO = ProfileResponseDTO.builder().first_name(firstName)
                .last_name(lastName).city(city).build();
        when(userService.getUserProfileDTO(userId, userId)).thenReturn(profileDTO);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/profile", userId).principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
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
        when(userService.getUserProfileDTO(req_userId, target_userId))
                .thenThrow(new EntityNotFoundException("User not found or no access rights."));

        // When/Then
        try {
            mockMvc.perform(get("/api/users/{targetId}/profile", target_userId)
                    .principal(authentication).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        } catch (ServletException e) {
            // Expected exception, test passes
            assertThat(e.getCause()).isInstanceOf(EntityNotFoundException.class);
            assertThat(e.getCause().getMessage()).contains("User not found or no access rights.");
        }
    }

    @Test
    void testGetProfile_Connected() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";

        Long target_userId = 2L;
        String target_firstName = "firstName";
        String target_lastName = "lastName";
        String target_city = "city";

        setupAuthenticatedUser(req_userId, req_email);

        ProfileResponseDTO profileDTO = ProfileResponseDTO.builder().first_name(target_firstName)
                .last_name(target_lastName).city(target_city).build();
        when(userService.getUserProfileDTO(req_userId, target_userId)).thenReturn(profileDTO);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/profile", target_userId)
                .principal(authentication).contentType(MediaType.APPLICATION_JSON))
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
        UserGenderType genderSelf = createMockGenderType(1L);
        UserGenderType genderOther = createMockGenderType(2L);
        Set<Hobby> hobbies = createMockHobbies();
        Integer ageMin = 18;
        Integer ageMax = 100;
        Integer distance = 50;
        Double probabilityTolerance = 1.0;
        Integer ageSelf = 28;

        setupAuthenticatedUser(userId, email);

        BiographicalResponseDTO bioDTO = BiographicalResponseDTO.builder()
                .gender_self(new GenderTypeDTO(genderSelf.getId(), genderSelf.getName()))
                .gender_other(new GenderTypeDTO(genderOther.getId(), genderOther.getName()))
                .hobbies(hobbies.stream().map(hobby -> hobby.getId()).collect(Collectors.toSet()))
                .age_self(ageSelf).age_min(ageMin).age_max(ageMax).distance(distance)
                .probability_tolerance(probabilityTolerance).build();

        when(userService.getBiographicalResponseDTO(userId, userId)).thenReturn(bioDTO);

        // When/Then
        mockMvc.perform(get("/api/users/{userId}/bio", userId).principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.gender_self.id", is(genderSelf.getId().intValue())))
                .andExpect(jsonPath("$.gender_self.name", is(genderSelf.getName())))
                .andExpect(jsonPath("$.gender_other.id", is(genderOther.getId().intValue())))
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

        when(userService.getBiographicalResponseDTO(req_userId, target_userId))
                .thenThrow(new EntityNotFoundException("User not found or no access rights."));

        // When/Then
        try {
            mockMvc.perform(get("/api/users/{targetId}/bio", target_userId)
                    .principal(authentication).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        } catch (ServletException e) {
            assertThat(e.getCause()).isInstanceOf(EntityNotFoundException.class);
            assertThat(e.getCause().getMessage()).contains("User not found or no access rights.");
        }
    }

    @Test
    void testGetBio_Connected() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        Long target_userId = 2L;
        UserGenderType target_genderSelf = createMockGenderType(1L); // MALE
        UserGenderType target_genderOther = createMockGenderType(2L); // FEMALE
        Set<Hobby> hobbies = createMockHobbies();
        Integer target_ageMin = 18;
        Integer target_ageMax = 100;
        Integer target_distance = 50;
        Double target_probabilityTolerance = 1.0;
        LocalDate target_birthDate = LocalDate.of(1995, 07, 20);

        Integer target_ageSelf = Period.between(target_birthDate, LocalDate.now()).getYears();

        setupAuthenticatedUser(req_userId, req_email);
        setupConnectionStatus(req_userId, target_userId, true);

        BiographicalResponseDTO bioDTO = BiographicalResponseDTO.builder()
                .gender_self(
                        new GenderTypeDTO(target_genderSelf.getId(), target_genderSelf.getName()))
                .gender_other(
                        new GenderTypeDTO(target_genderOther.getId(), target_genderOther.getName()))
                .hobbies(hobbies.stream().map(hobby -> hobby.getId()).collect(Collectors.toSet()))
                .age_self(target_ageSelf).age_min(target_ageMin).age_max(target_ageMax)
                .distance(target_distance).probability_tolerance(target_probabilityTolerance)
                .build();

        when(userService.getBiographicalResponseDTO(req_userId, target_userId)).thenReturn(bioDTO);

        // When/Then
        mockMvc.perform(get("/api/users/{targetId}/bio", target_userId).principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.gender_self.id", is(target_genderSelf.getId().intValue())))
                .andExpect(jsonPath("$.gender_self.name", is(target_genderSelf.getName())))
                .andExpect(jsonPath("$.gender_other.id", is(target_genderOther.getId().intValue())))
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

        Long target_userId = 2L;
        String target_email = "user2@example.com";
        String target_number = "+372 44554455";

        Long connectionId = 1L;

        setupAuthenticatedUser(req_userId, req_email);

        List<ConnectionResponseDTO> connections =
                Arrays.asList(new ConnectionResponseDTO(connectionId,
                        Set.of(new UserResponseDTO(target_userId, target_email, target_number),
                                new UserResponseDTO(req_userId, req_email, req_number))));

        when(connectionService.getConnectionResponseDTO(req_userId, req_userId))
                .thenReturn(connections);

        mockMvc.perform(get("/api/users/{targetId}/connections", req_userId)
                .principal(authentication).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(connectionId.intValue())))
                .andExpect(jsonPath("$[0].users[*].id", hasItem(req_userId.intValue())))
                .andExpect(jsonPath("$[0].users[*].email", hasItem(req_email)))
                .andExpect(jsonPath("$[0].users[*].number", hasItem(req_number)))
                .andExpect(jsonPath("$[0].users[*].id", hasItem(target_userId.intValue())))
                .andExpect(jsonPath("$[0].users[*].email", hasItem(target_email)))
                .andExpect(jsonPath("$[0].users[*].number", hasItem(target_number)));
    }

    @Test
    void testGetConnections_Unauthorized() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        Long target_userId = 2L;
        setupAuthenticatedUser(req_userId, req_email);

        doThrow(new EntityNotFoundException("User not found or no access rights."))
                .when(connectionService).getConnectionResponseDTO(req_userId, target_userId);

        // When/Then
        try {
            mockMvc.perform(get("/api/users/{targetId}/connections", target_userId)
                    .principal(authentication).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        } catch (ServletException e) {
            assertThat(e.getCause()).isInstanceOf(EntityNotFoundException.class);
            assertThat(e.getCause().getMessage()).contains("User not found or no access rights.");
        }
    }

    @Test
    void testGetConnections_Connected() throws Exception {
        // Given
        Long req_userId = 1L;
        String req_email = "user1@example.com";
        Long target_userId = 2L;
        setupAuthenticatedUser(req_userId, req_email);

        when(connectionService.getConnectionResponseDTO(req_userId, target_userId))
                .thenThrow(new EntityNotFoundException("User not found or no access rights."));


        // When/Then
        try {
            mockMvc.perform(get("/api/users/{targetId}/connections", target_userId)
                    .principal(authentication).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        } catch (ServletException e) {
            assertThat(e.getCause()).isInstanceOf(EntityNotFoundException.class);
            assertThat(e.getCause().getMessage()).contains("User not found or no access rights.");
        }
    }

    @Test
    void testUploadProfilePicture_Success() throws Exception {
        // Given
        Long userId = 1L;
        setupAuthenticatedUser(userId, "user1@example.com");
        String validBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...";

        // When/Then
        mockMvc.perform(post("/api/users/profile-picture").principal(authentication)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ \"base64Image\": \"" + validBase64 + "\" }")).andExpect(status().isOk())
                .andExpect(status().isOk());
    }

    @Test
    void testUploadProfilePicture_NullRequest() throws Exception {
        // Given
        Long userId = 1L;
        setupAuthenticatedUser(userId, "user1@example.com");

        doThrow(new IllegalArgumentException("No valid base64 image found in the request."))
                .when(userService).saveProfilePicture(eq(userId), eq(null));

        // When/Then
        try {
            mockMvc.perform(post("/api/users/profile-picture").principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isBadRequest());
        } catch (ServletException e) {
            // Expected exception, test passes
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause().getMessage())
                    .contains("No valid base64 image found in the request.");
        }
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
                .when(userService)
                .saveProfilePicture(eq(userId), argThat(requestDto -> requestDto != null
                        && "".equals(requestDto.getBase64Image())));

        // When/Then
        try {
            mockMvc.perform(post("/api/users/profile-picture").principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"base64Image\":\"\"}"))
                    .andExpect(status().isBadRequest());
        } catch (ServletException e) {
            // Expected exception, test passes
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause().getMessage())
                    .contains("Base64 image data cannot be null or empty.");
        }
    }

    @Test
    void testUploadProfilePicture_InvalidBase64() throws Exception {
        // Given
        Long userId = 1L;
        setupAuthenticatedUser(userId, "user1@example.com");
        String invalidBase64 = "thisIsNotValid==";

        doThrow(new IllegalArgumentException("Invalid Base64 image data.")).when(userService)
                .saveProfilePicture(eq(userId),
                        argThat(dto -> dto != null && invalidBase64.equals(dto.getBase64Image())));

        // When/Then
        try {
            mockMvc.perform(post("/api/users/profile-picture").principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"base64Image\": \"" + invalidBase64 + "\" }"))
                    .andExpect(status().isBadRequest());
        } catch (ServletException e) {
            // Expected exception, test passes
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause().getMessage()).contains("Invalid Base64 image data.");
        }
    }

    @Test
    void testUploadProfilePicture_UserNotFound() throws Exception {
        // Given
        Long userId = 999L;
        setupAuthenticatedUser(userId, "user1@example.com");

        String validBase64 = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...";
        ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO();
        request.setBase64Image(validBase64);

        doThrow(new EntityNotFoundException("User not found for ID: " + userId)).when(userService)
                .saveProfilePicture(eq(userId), argThat(
                        dto -> dto != null && "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA..."
                                .equals(dto.getBase64Image())));

        // When/Then
        try {
            mockMvc.perform(post("/api/users/profile-picture").principal(authentication)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{ \"base64Image\": \"" + validBase64 + "\" }"))
                    .andExpect(status().isNotFound());
        } catch (ServletException e) {
            // Expected exception, test passes
            assertThat(e.getCause()).isInstanceOf(EntityNotFoundException.class);
            assertThat(e.getCause().getMessage()).contains("User not found for ID: " + userId);
        }
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
     * Helper method to create UserGenderType
     * 
     * @param id - 1 MALE | 2 FEMALE | 3 OTHER
     * @return {@link UserGenderType}
     */
    private UserGenderType createMockGenderType(Long id) {
        UserGenderType genderType = new UserGenderType();
        genderType.setId(id);
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
        UserDetailsImpl userDetails =
                new UserDetailsImpl(userId, email, "password", Collections.emptySet());
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
        when(connectionService.isConnected(req_userId, target_userId)).thenReturn(isConnected);
    }
}
