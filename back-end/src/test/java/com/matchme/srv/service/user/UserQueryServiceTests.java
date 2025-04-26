package com.matchme.srv.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.matchme.srv.dto.response.BiographicalResponseDTO;
import com.matchme.srv.dto.response.CurrentUserResponseDTO;
import com.matchme.srv.dto.response.ProfileResponseDTO;
import com.matchme.srv.dto.response.SettingsResponseDTO;
import com.matchme.srv.dto.response.UserParametersResponseDTO;
import com.matchme.srv.mapper.UserParametersMapper;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.AccessValidationService;
import com.matchme.srv.mapper.user.UserDTOMapper;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceTests {

  @Mock private UserRepository userRepository;

  @Mock private AccessValidationService accessValidationService;

  @Mock private UserDTOMapper userDtoMapper;

  @Mock private UserParametersMapper parametersMapper;

  @InjectMocks private UserQueryService userQueryService;

  private static final Long VALID_USER_ID = 1L;
  private static final Long TARGET_USER_ID = 2L;
  private static final Long INVALID_USER_ID = 999L;

  private User user;
  private User user2;
  private UserProfile profile;
  private UserProfile profile2;
  private UserAuth userAuth;
  private UserAuth userAuth2;
  private UserAttributes attributes;
  private UserPreferences preferences;
  private UserParametersResponseDTO userParametersDTO;
  private CurrentUserResponseDTO currentUserDTO;
  private ProfileResponseDTO profileDTO;
  private BiographicalResponseDTO biographicalDTO;
  private SettingsResponseDTO settingsDTO;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(VALID_USER_ID);
    user.setEmail("owner@example.com");

    profile = new UserProfile();
    attributes = new UserAttributes();
    preferences = new UserPreferences();
    profile.setAttributes(attributes);
    profile.setPreferences(preferences);
    user.setProfile(profile);

    userAuth = new UserAuth();
    user.setUserAuth(userAuth);

    user2 = new User();
    user2.setId(TARGET_USER_ID);
    user2.setEmail("target@example.com");

    profile2 = new UserProfile();
    UserAttributes attributes2 = new UserAttributes();
    UserPreferences preferences2 = new UserPreferences();
    profile2.setAttributes(attributes2);
    profile2.setPreferences(preferences2);
    user2.setProfile(profile2);

    userAuth2 = new UserAuth();
    user2.setUserAuth(userAuth2);

    userParametersDTO =
            new UserParametersResponseDTO(
                    "test@example.com", // email
                    "password", // password
                    "123456789", // number
                    "John", // first_name
                    "Doe", // last_name
                    "JohnDoe", // alias
                    "This is the about me section.", // aboutMe
                    Set.of(1L), // hobbies
                    UserGenderEnum.MALE, // gender_self
                    "1990-01-01", // birth_date
                    // LocalDate.of(1990, 1, 1)
                    "New York", // city
                    1.0, // longitude
                    1.0, // latitude
                    UserGenderEnum.FEMALE, // gender_other
                    18, // age_min
                    99, // age_max
                    100, // distance
                    0.5, // probability_tolerance
                    null // profilePicture
            );

    currentUserDTO = CurrentUserResponseDTO.builder().id(TARGET_USER_ID).firstName("Target").build(); // Add some data
    profileDTO = ProfileResponseDTO.builder().firstName("Target").city("Target City").aboutMe("Target About").build(); // Add data
    biographicalDTO = BiographicalResponseDTO.builder().age_self(30).distance(50).build(); // Add data
    settingsDTO = SettingsResponseDTO.builder().email("test@example.com").city("New York").build(); // Add data
  }

  @Nested
  @DisplayName("getCurrentUserDTO Tests")
  class GetCurrentUserDTOTests {


    @Test
    @DisplayName("Should return email since requested currentUserId == targetUserId")
    void shouldReturnEmailSinceRequestedCurrentUserId() {
      // Arrange
      Long currentUserId = VALID_USER_ID;
      Long targetUserId = VALID_USER_ID;
      user.setEmail("owner@example.com");
      doNothing().when(accessValidationService).validateUserAccess(currentUserId, targetUserId);
      when(userRepository.findById(targetUserId)).thenReturn(Optional.of(user));

      CurrentUserResponseDTO expected = CurrentUserResponseDTO.builder()
              .id(user.getId())
              .email(user.getEmail())
              .build();

      when(userDtoMapper.toCurrentUserResponseDTO(user, true)).thenReturn(expected);

      // Act
      CurrentUserResponseDTO result = userQueryService.getCurrentUserDTO(currentUserId, targetUserId);

      // Assert
      assertThat(result.email())
              .as("Email should be included when currentUserId equals targetUserId")
              .isEqualTo(user.getEmail());

      // Verify interactions
      verify(accessValidationService, times(1)).validateUserAccess(currentUserId, targetUserId);
      verify(userRepository, times(1)).findById(targetUserId);
      verify(userDtoMapper, times(1)).toCurrentUserResponseDTO(user, true);
    }

    @Test
    @DisplayName("Should omit email for non-owners requests since currentUserId != targetUserId")
    void shouldOmitEmailForNonOwnersSinceCurrentUserIdNotEqualTargetUserId() {
      // Arrange
      Long currentUserId = VALID_USER_ID;
      Long targetUserId = TARGET_USER_ID; // Different from currentUserId
      doNothing().when(accessValidationService).validateUserAccess(currentUserId, targetUserId);
      when(userRepository.findById(targetUserId)).thenReturn(Optional.of(user2));

      CurrentUserResponseDTO expectedDTO = CurrentUserResponseDTO.builder()
              .id(user2.getId())
              .email(null) // Email should be null since isOwner is false
              .build();

      when(userDtoMapper.toCurrentUserResponseDTO(user2, false)).thenReturn(expectedDTO);

      // Act
      CurrentUserResponseDTO result = userQueryService.getCurrentUserDTO(currentUserId, targetUserId);

      // Assert
      assertThat(result.email())
              .as("Email should be null when currentUserId does not equal targetUserId")
              .isNull();

      // Verify interactions
      verify(accessValidationService, times(1)).validateUserAccess(currentUserId, targetUserId);
      verify(userRepository, times(1)).findById(targetUserId);
      verify(userDtoMapper, times(1)).toCurrentUserResponseDTO(user2, false);
    }

    @Test
    @DisplayName("Should return CurrentUserResponseDTO when user exists and access is valid")
    void getCurrentUserDTO_ValidUser_ReturnsDTO() {
      // Arrange
      doNothing().when(accessValidationService).validateUserAccess(VALID_USER_ID, TARGET_USER_ID);
      when(userRepository.findById(TARGET_USER_ID)).thenReturn(Optional.of(user));
      when(userDtoMapper.toCurrentUserResponseDTO(user, false)).thenReturn(currentUserDTO);

      // Act
      CurrentUserResponseDTO result =
          userQueryService.getCurrentUserDTO(VALID_USER_ID, TARGET_USER_ID);

      // Assert
      assertAll(
          () ->
              assertThat(result)
                  .as("checking if the result is equal to currentUserDTO")
                  .isEqualTo(currentUserDTO),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, TARGET_USER_ID),
          () -> verify(userRepository, times(1)).findById(TARGET_USER_ID),
          () -> verify(userDtoMapper, times(1)).toCurrentUserResponseDTO(user, false));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getCurrentUserDTO_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () -> userQueryService.getCurrentUserDTO(VALID_USER_ID, INVALID_USER_ID))
                  .as("checking if the user was not found")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found!"),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, INVALID_USER_ID),
          () -> verify(userRepository, times(1)).findById(INVALID_USER_ID),
          () -> verify(userDtoMapper, never()).toCurrentUserResponseDTO(any(), eq(false)));
    }

    @Test
    @DisplayName("Should call AccessValidationService with correct parameters")
    void getCurrentUserDTO_ValidUser_CallsAccessValidation() {
      // Arrange
      doNothing().when(accessValidationService).validateUserAccess(VALID_USER_ID, TARGET_USER_ID);
      when(userRepository.findById(TARGET_USER_ID)).thenReturn(Optional.of(user));
      when(userDtoMapper.toCurrentUserResponseDTO(user, false)).thenReturn(currentUserDTO);

      // Act
      userQueryService.getCurrentUserDTO(VALID_USER_ID, TARGET_USER_ID);

      // Assert
      verify(accessValidationService, times(1)).validateUserAccess(VALID_USER_ID, TARGET_USER_ID);
    }
  }

  @Nested
  @DisplayName("getParameters Tests")
  class GetParametersTests {

    @Test
    @DisplayName("Should return UserParametersResponseDTO when user exists")
    void getParameters_ValidUser_ReturnsDTO() {
      // Arrange
      when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
      when(parametersMapper.toUserParametersDTO(
              user, profile.getAttributes(), profile.getPreferences(), userAuth))
          .thenReturn(userParametersDTO);

      // Act
      UserParametersResponseDTO result = userQueryService.getParameters(VALID_USER_ID);

      // Assert
      assertAll(
          () ->
              assertThat(result)
                  .as("checking if the result is equal to userParametersDTO")
                  .isEqualTo(userParametersDTO),
          () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
          () ->
              verify(parametersMapper, times(1))
                  .toUserParametersDTO(
                      user, profile.getAttributes(), profile.getPreferences(), userAuth));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getParameters_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userQueryService.getParameters(INVALID_USER_ID))
                  .as("checking if the user was not found")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found!"),
          () -> verify(userRepository, times(1)).findById(INVALID_USER_ID),
          () -> verify(parametersMapper, never()).toUserParametersDTO(any(), any(), any(), any()));
    }
  }

  @Nested
  @DisplayName("getUser Tests")
  class GetUserTests {

    @Test
    @DisplayName("Should return User when user exists")
    void getUser_ValidUser_ReturnsUser() {
      // Arrange
      when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));

      // Act
      User result = userQueryService.getUser(VALID_USER_ID);

      // Assert
      assertAll(
          () -> assertThat(result).as("checking if the result is equal to user").isEqualTo(user),
          () -> verify(userRepository, times(1)).findById(VALID_USER_ID));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getUser_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userQueryService.getUser(INVALID_USER_ID))
                  .as("checking if the user was not found")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found!"),
          () -> verify(userRepository, times(1)).findById(INVALID_USER_ID));
    }
  }

  @Nested
  @DisplayName("getUserByEmail Tests")
  class GetUserByEmailTests {

    private static final String VALID_EMAIL = "test@example.com";
    private static final String INVALID_EMAIL = "invalid@example.com";

    @Test
    @DisplayName("Should return User when email exists")
    void getUserByEmail_ValidEmail_ReturnsUser() {
      // Arrange
      when(userRepository.findByEmail(VALID_EMAIL)).thenReturn(Optional.of(user));

      // Act
      User result = userQueryService.getUserByEmail(VALID_EMAIL);

      // Assert
      assertAll(
          () -> assertThat(result).as("checking if the result is equal to user").isEqualTo(user),
          () -> verify(userRepository, times(1)).findByEmail(VALID_EMAIL));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when email does not exist")
    void getUserByEmail_InvalidEmail_ThrowsException() {
      // Arrange
      when(userRepository.findByEmail(INVALID_EMAIL)).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userQueryService.getUserByEmail(INVALID_EMAIL))
                  .as("checking if the user was not found")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found!"),
          () -> verify(userRepository, times(1)).findByEmail(INVALID_EMAIL));
    }
  }

  @Nested
  @DisplayName("getUserProfileDTO Tests")
  class GetUserProfileDTOTests {

    @Test
    @DisplayName("Should return ProfileResponseDTO when user and profile exist and access is valid")
    void getUserProfileDTO_ValidUser_ReturnsProfileDTO() {
      // Arrange
      doNothing().when(accessValidationService).validateUserAccess(VALID_USER_ID, TARGET_USER_ID);
      when(userRepository.findById(TARGET_USER_ID)).thenReturn(Optional.of(user));
      when(userDtoMapper.toProfileResponseDTO(profile)).thenReturn(profileDTO);

      // Act
      ProfileResponseDTO result = userQueryService.getUserProfileDTO(VALID_USER_ID, TARGET_USER_ID);

      // Assert
      assertAll(
          () ->
              assertThat(result)
                  .as("checking if the result is equal to profileDTO")
                  .isEqualTo(profileDTO),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, TARGET_USER_ID),
          () -> verify(userRepository, times(1)).findById(TARGET_USER_ID),
          () -> verify(userDtoMapper, times(1)).toProfileResponseDTO(profile));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getUserProfileDTO_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () -> userQueryService.getUserProfileDTO(VALID_USER_ID, INVALID_USER_ID))
                  .as("checking if the user was not found")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found!"),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, INVALID_USER_ID),
          () -> verify(userRepository, times(1)).findById(INVALID_USER_ID),
          () -> verify(userDtoMapper, never()).toProfileResponseDTO(any()));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when profile is null")
    void getUserProfileDTO_NullProfile_ThrowsException() {
      // Arrange
      user.setProfile(null);
      doNothing().when(accessValidationService).validateUserAccess(VALID_USER_ID, TARGET_USER_ID);
      when(userRepository.findById(TARGET_USER_ID)).thenReturn(Optional.of(user));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () -> userQueryService.getUserProfileDTO(VALID_USER_ID, TARGET_USER_ID))
                  .as("checking if the profile was not found")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("Profile not found!"),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, TARGET_USER_ID),
          () -> verify(userRepository, times(1)).findById(TARGET_USER_ID),
          () -> verify(userDtoMapper, never()).toProfileResponseDTO(any()));
    }
  }

  @Nested
  @DisplayName("getBiographicalResponseDTO Tests")
  class GetBiographicalResponseDTOTests {

    @Test
    @DisplayName(
        "Should return BiographicalResponseDTO when user and profile exist and access is valid")
    void getBiographicalResponseDTO_ValidUser_ReturnsBiographicalDTO() {
      // Arrange
      doNothing().when(accessValidationService).validateUserAccess(VALID_USER_ID, TARGET_USER_ID);
      when(userRepository.findById(TARGET_USER_ID)).thenReturn(Optional.of(user));
      when(userDtoMapper.tobBiographicalResponseDTO(profile)).thenReturn(biographicalDTO);

      // Act
      BiographicalResponseDTO result =
          userQueryService.getBiographicalResponseDTO(VALID_USER_ID, TARGET_USER_ID);

      // Assert
      assertAll(
          () ->
              assertThat(result)
                  .as("checking if the result is equal to biographicalDTO")
                  .isEqualTo(biographicalDTO),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, TARGET_USER_ID),
          () -> verify(userRepository, times(1)).findById(TARGET_USER_ID),
          () -> verify(userDtoMapper, times(1)).tobBiographicalResponseDTO(profile));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getBiographicalResponseDTO_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () ->
                          userQueryService.getBiographicalResponseDTO(
                              VALID_USER_ID, INVALID_USER_ID))
                  .as("checking if the user was not found")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found!"),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, INVALID_USER_ID),
          () -> verify(userRepository, times(1)).findById(INVALID_USER_ID),
          () -> verify(userDtoMapper, never()).tobBiographicalResponseDTO(any()));
    }

    @Test
    @DisplayName("Should throw EntityNotFoundException when profile is null")
    void getBiographicalResponseDTO_NullProfile_ThrowsException() {
      // Arrange
      user.setProfile(null);
      doNothing().when(accessValidationService).validateUserAccess(VALID_USER_ID, TARGET_USER_ID);
      when(userRepository.findById(TARGET_USER_ID)).thenReturn(Optional.of(user));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () ->
                          userQueryService.getBiographicalResponseDTO(
                              VALID_USER_ID, TARGET_USER_ID))
                  .as("checking if the profile was not found")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("Profile not found!"),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, TARGET_USER_ID),
          () -> verify(userRepository, times(1)).findById(TARGET_USER_ID),
          () -> verify(userDtoMapper, never()).tobBiographicalResponseDTO(any()));
    }
  }

  @Nested
  @DisplayName("getSettingsResponseDTO Tests")
  class GetSettingsResponseDTOTests {

    @Test
    @DisplayName("Should return SettingsResponseDTO when user exists and access is valid")
    void getSettingsResponseDTO_ValidUser_ReturnsSettingsDTO() {
      // Arrange
      doNothing().when(accessValidationService).validateUserAccess(VALID_USER_ID, TARGET_USER_ID);
      when(userRepository.findById(TARGET_USER_ID)).thenReturn(Optional.of(user));
      when(parametersMapper.toUserParametersDTO(
              user, profile.getAttributes(), profile.getPreferences(), userAuth))
          .thenReturn(userParametersDTO);
      when(userDtoMapper.toSettingsResponseDTO(userParametersDTO)).thenReturn(settingsDTO);

      // Act
      SettingsResponseDTO result =
          userQueryService.getSettingsResponseDTO(VALID_USER_ID, TARGET_USER_ID);

      // Assert
      assertAll(
          () ->
              assertThat(result)
                  .as("checking if the result is equal to settingsDTO")
                  .isEqualTo(settingsDTO),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, TARGET_USER_ID),
          () -> verify(userRepository, times(1)).findById(TARGET_USER_ID),
          () ->
              verify(parametersMapper, times(1))
                  .toUserParametersDTO(
                      user, profile.getAttributes(), profile.getPreferences(), userAuth),
          () -> verify(userDtoMapper, times(1)).toSettingsResponseDTO(userParametersDTO));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void getSettingsResponseDTO_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () -> userQueryService.getSettingsResponseDTO(VALID_USER_ID, INVALID_USER_ID))
                  .as("checking if the user was not found")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found!"),
          () ->
              verify(accessValidationService, times(1))
                  .validateUserAccess(VALID_USER_ID, INVALID_USER_ID),
          () -> verify(userRepository, times(1)).findById(INVALID_USER_ID),
          () -> verify(parametersMapper, never()).toUserParametersDTO(any(), any(), any(), any()),
          () -> verify(userDtoMapper, never()).toSettingsResponseDTO(any()));
    }
  }
}
