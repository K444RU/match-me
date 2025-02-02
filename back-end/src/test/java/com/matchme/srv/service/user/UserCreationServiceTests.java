package com.matchme.srv.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.exception.DuplicateFieldException;
import com.matchme.srv.exception.InvalidVerificationException;
import com.matchme.srv.exception.ResourceNotFoundException;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.UserStateTypes;
import com.matchme.srv.model.user.activity.ActivityLogType;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.ProfileChangeType;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.AttributeChangeType;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.PreferenceChangeType;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.HobbyService;
import com.matchme.srv.service.type.ActivityLogTypeService;
import com.matchme.srv.service.type.AttributeChangeTypeService;
import com.matchme.srv.service.type.PreferenceChangeTypeService;
import com.matchme.srv.service.type.ProfileChangeTypeService;
import com.matchme.srv.service.type.UserGenderTypeService;
import com.matchme.srv.service.type.UserRoleTypeService;
import com.matchme.srv.service.type.UserStateTypesService;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UserCreationServiceTests {

  @Mock private UserRepository userRepository;

  @Mock private UserRoleTypeService userRoleTypeService;

  @Mock private UserStateTypesService userStateTypesService;

  @Mock private ActivityLogTypeService activityLogTypeService;

  @Mock private ProfileChangeTypeService profileChangeTypeService;

  @Mock private AttributeChangeTypeService attributeChangeTypeService;

  @Mock private PreferenceChangeTypeService preferenceChangeTypeService;

  @Mock private PasswordEncoder encoder;

  @Mock private AttributesMapper attributesMapper;

  @Mock private PreferencesMapper preferencesMapper;

  @Mock private UserGenderTypeService userGenderTypeService;

  @Mock private HobbyService hobbyService;

  @InjectMocks private UserCreationService userCreationService;

  @Nested
  @DisplayName("createUser Tests")
  class CreateUserTests {

    @Test
    @DisplayName("Should create user with valid request")
    void createUser_ValidRequest_CreatesUser() {
      // Assign
      String email = "test@example.com";
      String number = "123";
      String password = "password";
      SignupRequestDTO request =
          SignupRequestDTO.builder().email(email).number(number).password(password).build();

      UserStateTypes userStateTypes = new UserStateTypes();
      UserRoleType role = new UserRoleType();
      ActivityLogType activityLogType = new ActivityLogType();

      when(userStateTypesService.getByName("UNVERIFIED")).thenReturn(userStateTypes);
      when(userRoleTypeService.getByName("ROLE_USER")).thenReturn(role);
      when(activityLogTypeService.getByName("CREATED")).thenReturn(activityLogType);
      when(encoder.encode(password)).thenReturn("encodedPassword");

      // Act
      userCreationService.createUser(request);

      // Assert
      ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
      verify(userRepository).save(userCaptor.capture());

      User savedUser = userCaptor.getValue();
      assertAll(
          () ->
              assertThat(savedUser.getEmail())
                  .as("checking if the email is correct")
                  .isEqualTo(email),
          () ->
              assertThat(savedUser.getNumber())
                  .as("checking if the number is correct")
                  .isEqualTo(number),
          () ->
              assertThat(savedUser.getState())
                  .as("checking if the state is correct")
                  .isEqualTo(userStateTypes),
          () ->
              assertThat(savedUser.getRoles())
                  .as("checking if the roles are correct")
                  .contains(role),
          () ->
              assertThat(savedUser.getUserAuth().getPassword())
                  .as("checking if the password is correct")
                  .isEqualTo("encodedPassword"),
          () -> verify(userRepository, times(1)).save(savedUser));
    }

    @Test
    @DisplayName("Should throw exception when email already exists")
    void createUser_DuplicateEmail_ThrowsDuplicateFieldException() {
      // Assign
      String email = "test@example.com";
      SignupRequestDTO request = SignupRequestDTO.builder().email(email).build();

      User existingUser = new User();
      existingUser.setId(1L);
      when(userRepository.findByEmailIgnoreCase(email)).thenReturn(Optional.of(existingUser));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.createUser(request))
                  .as("checking if the exception is an instance of DuplicateFieldException")
                  .isInstanceOf(DuplicateFieldException.class)
                  .hasMessageContaining("Email already exists"),
          () -> verify(userRepository, times(1)).findByEmailIgnoreCase(email));
    }

    @Test
    @DisplayName("Should throw exception when number already exists")
    void createUser_DuplicateNumber_ThrowsDuplicateFieldException() {
      // Assign
      String number = "123";
      SignupRequestDTO request = SignupRequestDTO.builder().number(number).build();

      User existingUser = new User();
      existingUser.setId(1L); // Set non-null ID
      when(userRepository.findByEmailIgnoreCase(null)).thenReturn(Optional.empty());
      when(userRepository.findByNumber(number)).thenReturn(Optional.of(existingUser));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.createUser(request))
                  .as("checking if the exception is an instance of DuplicateFieldException")
                  .isInstanceOf(DuplicateFieldException.class)
                  .hasMessageContaining("Phone number already exists"),
          () -> verify(userRepository, times(1)).findByNumber(number));
    }
  }

  @Nested
  @DisplayName("verifyAccount Tests")
  class VerifyAccountTests {
    @Test
    @DisplayName("Should verify account with valid verification code")
    void verifyAccount_ValidVerificationCode_VerifiesAccount() {
      // Assign
      User user = new User();
      UserAuth userAuth = new UserAuth();
      userAuth.setRecovery(123);
      user.setUserAuth(userAuth);

      UserStateTypes userStateType = new UserStateTypes();
      ActivityLogType activityLogType = new ActivityLogType();
      ProfileChangeType profileChangeType = new ProfileChangeType();
      AttributeChangeType attributeChangeType = new AttributeChangeType();
      PreferenceChangeType preferenceChangeType = new PreferenceChangeType();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userStateTypesService.getByName("VERIFIED")).thenReturn(userStateType);
      when(activityLogTypeService.getByName("VERIFIED")).thenReturn(activityLogType);
      when(profileChangeTypeService.getByName("CREATED")).thenReturn(profileChangeType);
      when(attributeChangeTypeService.getByName("CREATED")).thenReturn(attributeChangeType);
      when(preferenceChangeTypeService.getByName("CREATED")).thenReturn(preferenceChangeType);

      // Act
      userCreationService.verifyAccount(1L, 123);

      // Assert
      assertAll(
          () ->
              assertThat(user.getState())
                  .as("checking if the state is correct")
                  .isEqualTo(userStateType),
          () -> assertThat(userAuth.getRecovery()).as("checking if the recovery is null").isNull(),
          () -> assertThat(user.getProfile()).as("checking if the profile is not null").isNotNull(),
          () ->
              assertThat(user.getProfile().getAttributes())
                  .as("checking if the attributes are not null")
                  .isNotNull(),
          () ->
              assertThat(user.getProfile().getPreferences())
                  .as("checking if the preferences are not null")
                  .isNotNull(),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void verifyAccount_NonExistentUser_ThrowsEntityNotFoundException() {
      // Assign
      when(userRepository.findById(1L)).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.verifyAccount(1L, 0))
                  .as("checking if the exception is an instance of EntityNotFoundException")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found"),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should throw exception when invalid verification code")
    void verifyAccount_InvalidVerificationCode_ThrowsInvalidVerificationException() {
      // Assign
      User user = new User();
      UserAuth userAuth = new UserAuth();
      userAuth.setRecovery(123);
      user.setUserAuth(userAuth);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.verifyAccount(1L, 321))
                  .as("checking if the exception is an instance of InvalidVerificationException")
                  .isInstanceOf(InvalidVerificationException.class)
                  .hasMessageContaining(
                      "Verification code was wrong! Would you like us to generate the code again?"),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should throw exception when recovery code is null")
    void verifyAccount_WithNullRecovery_ThrowsInvalidVerificationException() {
      // Arrange
      User user = new User();
      UserAuth auth = new UserAuth();
      auth.setRecovery(null);
      user.setUserAuth(auth);

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.verifyAccount(1L, 123))
                  .as("checking if the exception is an instance of InvalidVerificationException")
                  .isInstanceOf(InvalidVerificationException.class)
                  .hasMessageContaining("Verification code was wrong!"),
          () -> verify(userRepository, times(1)).findById(1L));
    }
  }

  @Nested
  @DisplayName("setUserParameters Tests")
  class SetUserParametersTests {
    @Test
    @DisplayName("Should set all parameters with valid request")
    void setUserParameters_ValidRequest_SetsAllParameters() {
      // Assign
      User user = new User();
      UserParametersRequestDTO request =
          UserParametersRequestDTO.builder()
              .longitude(12.34)
              .latitude(56.78)
              .gender_self(1L)
              .gender_other(2L)
              .first_name("John")
              .last_name("Doe")
              .alias("JD")
              .city("New York")
              .hobbies(Set.of(3L, 4L))
              .build();

      UserGenderType genderType = new UserGenderType();
      Hobby hobby1 = Hobby.builder().id(3L).name("3D printing").category("General").build();
      Hobby hobby2 = Hobby.builder().id(4L).name("Acrobatics").category("General").build();

      UserStateTypes userStateType = new UserStateTypes();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userGenderTypeService.getById(1L)).thenReturn(genderType);
      when(userGenderTypeService.getById(2L)).thenReturn(genderType);
      when(hobbyService.getById(3L)).thenReturn(hobby1);
      when(hobbyService.getById(4L)).thenReturn(hobby2);
      when(userStateTypesService.getByName("NEW")).thenReturn(userStateType);

      // Act
      userCreationService.setUserParameters(1L, request);

      // Assert
      UserProfile profile = user.getProfile();
      assertAll(
          () ->
              assertThat(profile.getFirst_name())
                  .as("checking if the first name is correct")
                  .isEqualTo("John"),
          () ->
              assertThat(profile.getLast_name())
                  .as("checking if the last name is correct")
                  .isEqualTo("Doe"),
          () ->
              assertThat(profile.getAlias()).as("checking if the alias is correct").isEqualTo("JD"),
          () ->
              assertThat(profile.getCity())
                  .as("checking if the city is correct")
                  .isEqualTo("New York"),
          () ->
              assertThat(profile.getHobbies())
                  .as("checking if the hobbies are correct")
                  .containsExactlyInAnyOrder(hobby1, hobby2),
          () ->
              assertThat(user.getState())
                  .as("checking if the state is correct")
                  .isEqualTo(userStateType),
          () -> assertThat(user.getScore()).as("checking if the score is not null").isNotNull(),
          () -> verify(userRepository, times(1)).save(user));

      UserAttributes attributes = profile.getAttributes();
      assertAll(
          () ->
              assertThat(attributes.getLocation())
                  .as("checking if the location is correct")
                  .containsExactly(12.34, 56.78),
          () ->
              assertThat(attributes.getGender())
                  .as("checking if the gender is correct")
                  .isEqualTo(genderType));

      UserPreferences preferences = profile.getPreferences();
      assertAll(
          () ->
              assertThat(preferences.getGender())
                  .as("checking if the gender is correct")
                  .isEqualTo(genderType));

      assertAll(
          () ->
              assertThat(user.getState())
                  .as("checking if the state is correct")
                  .isEqualTo(userStateType),
          () -> assertThat(user.getScore()).as("checking if the score is not null").isNotNull());
    }

    @Test
    @DisplayName("Should throw exception when missing location")
    void setUserParameters_WithMissingLocation_ThrowsIllegalArgumentException() {
      // Assign
      UserParametersRequestDTO invalidRequest = UserParametersRequestDTO.builder().build();

      when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.setUserParameters(1L, invalidRequest))
                  .as("checking if the exception is an instance of IllegalArgumentException")
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessageContaining("Longitude and latitude must be provided"),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should throw exception when invalid gender")
    void setUserParameters_WithInvalidGender_ThrowsResourceNotFoundException() {
      // Assign
      UserParametersRequestDTO request =
          UserParametersRequestDTO.builder().gender_self(99L).gender_other(2L).build();

      when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
      when(userGenderTypeService.getById(99L)).thenThrow(new ResourceNotFoundException("Gender"));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.setUserParameters(1L, request))
                  .as("checking if the exception is an instance of ResourceNotFoundException")
                  .isInstanceOf(ResourceNotFoundException.class)
                  .hasMessageContaining("Gender not found"),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should create empty set of hobbies when empty hobbies are provided")
    void setUserParameters_WithEmptyHobbies_CreatesEmptySet() {
      // Assign
      User user = new User();
      UserParametersRequestDTO request =
          UserParametersRequestDTO.builder()
              .longitude(12.34)
              .latitude(56.78)
              .gender_self(1L)
              .gender_other(2L)
              .hobbies(Collections.emptySet())
              .build();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userGenderTypeService.getById(anyLong())).thenReturn(new UserGenderType());

      // Act
      userCreationService.setUserParameters(1L, request);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getHobbies())
                  .as("checking if the hobbies are empty")
                  .isEmpty(),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should set available fields with partial data")
    void setUserParameters_WithPartialData_SetsAvailableFields() {
      // Assign
      User user = new User();
      UserParametersRequestDTO request =
          UserParametersRequestDTO.builder()
              .longitude(12.34)
              .latitude(56.78)
              .gender_self(1L)
              .gender_other(2L)
              .alias("JD")
              .build();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userGenderTypeService.getById(anyLong())).thenReturn(new UserGenderType());

      // Act
      userCreationService.setUserParameters(1L, request);

      // Assert
      UserProfile profile = user.getProfile();
      assertAll(
          () ->
              assertThat(profile.getAlias()).as("checking if the alias is correct").isEqualTo("JD"),
          () ->
              assertThat(profile.getFirst_name()).as("checking if the first name is null").isNull(),
          () -> assertThat(profile.getLast_name()).as("checking if the last name is null").isNull(),
          () -> assertThat(profile.getCity()).as("checking if the city is null").isNull(),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should create profile when null")
    void setUserParameters_WithNullProfile_CreatesProfile() {
      // Arrange
      User user = new User();
      user.setProfile(null);
      UserParametersRequestDTO request = validRequest();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userGenderTypeService.getById(anyLong())).thenReturn(new UserGenderType());

      // Act
      userCreationService.setUserParameters(1L, request);

      // Assert
      assertAll(
          () -> assertThat(user.getProfile()).as("checking if the profile is not null").isNotNull(),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should create attributes when null")
    void setUserParameters_WithNullAttributes_CreatesAttributes() {
      // Arrange
      User user = new User();
      user.setProfile(new UserProfile());
      user.getProfile().setAttributes(null);
      UserParametersRequestDTO request = validRequest();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userGenderTypeService.getById(anyLong())).thenReturn(new UserGenderType());

      // Act
      userCreationService.setUserParameters(1L, request);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getAttributes())
                  .as("checking if the attributes are not null")
                  .isNotNull(),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should create preferences when null")
    void setUserParameters_WithNullPreferences_CreatesPreferences() {
      // Arrange
      User user = new User();
      user.setProfile(new UserProfile());
      user.getProfile().setPreferences(null);
      UserParametersRequestDTO request = validRequest();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userGenderTypeService.getById(anyLong())).thenReturn(new UserGenderType());

      // Act
      userCreationService.setUserParameters(1L, request);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getPreferences())
                  .as("checking if the preferences are not null")
                  .isNotNull(),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should throw exception when longitude is null")
    void setUserParameters_WithNullLongitude_ThrowsException() {
      // Arrange
      UserParametersRequestDTO invalidRequest =
          UserParametersRequestDTO.builder().longitude(null).latitude(56.78).build();

      when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.setUserParameters(1L, invalidRequest))
                  .as("checking if the exception is an instance of IllegalArgumentException")
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessageContaining("Longitude and latitude must be provided"),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should throw exception when latitude is null")
    void setUserParameters_WithNullLatitude_ThrowsException() {
      // Arrange
      UserParametersRequestDTO invalidRequest =
          UserParametersRequestDTO.builder().longitude(12.34).latitude(null).build();

      when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.setUserParameters(1L, invalidRequest))
                  .as("checking if the exception is an instance of IllegalArgumentException")
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessageContaining("Longitude and latitude must be provided"),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void setUserParameters_UserNotFound_ThrowsException() {
      // Arrange
      when(userRepository.findById(1L)).thenReturn(Optional.empty());
      UserParametersRequestDTO request = validRequest();

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userCreationService.setUserParameters(1L, request))
                  .as("checking if the exception is an instance of EntityNotFoundException")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found"),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should create both attributes and preferences when both are null")
    void setUserParameters_WithNullAttributesAndPreferences_CreatesBoth() {
      // Arrange
      User user = new User();
      user.setProfile(new UserProfile());
      user.getProfile().setAttributes(null);
      user.getProfile().setPreferences(null);
      UserParametersRequestDTO request = validRequest();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userGenderTypeService.getById(anyLong())).thenReturn(new UserGenderType());

      // Act
      userCreationService.setUserParameters(1L, request);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getAttributes())
                  .as("checking if attributes are created")
                  .isNotNull(),
          () ->
              assertThat(user.getProfile().getPreferences())
                  .as("checking if preferences are created")
                  .isNotNull(),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should retain existing attributes when present")
    void setUserParameters_WithExistingAttributes_KeepsOriginalAttributes() {
      // Arrange
      User user = new User();
      UserProfile profile = new UserProfile();
      UserAttributes existingAttributes = new UserAttributes();
      profile.setAttributes(existingAttributes);
      user.setProfile(profile);

      UserParametersRequestDTO request = validRequest();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userGenderTypeService.getById(anyLong())).thenReturn(new UserGenderType());

      // Act
      userCreationService.setUserParameters(1L, request);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getAttributes())
                  .as("checking if attributes remain the same instance")
                  .isSameAs(existingAttributes),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should retain existing preferences when present")
    void setUserParameters_WithExistingPreferences_KeepsOriginalPreferences() {
      // Arrange
      User user = new User();
      UserProfile profile = new UserProfile();
      UserPreferences existingPreferences = new UserPreferences();
      profile.setPreferences(existingPreferences);
      user.setProfile(profile);

      UserParametersRequestDTO request = validRequest();

      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      when(userGenderTypeService.getById(anyLong())).thenReturn(new UserGenderType());

      // Act
      userCreationService.setUserParameters(1L, request);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getPreferences())
                  .as("checking if preferences remain the same instance")
                  .isSameAs(existingPreferences),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, times(1)).save(user));
    }

    private UserParametersRequestDTO validRequest() {
      return UserParametersRequestDTO.builder()
          .longitude(12.34)
          .latitude(56.78)
          .gender_self(1L)
          .gender_other(2L)
          .build();
    }
  }

  @Nested
  @DisplayName("assignDefaultRole Tests")
  class AssignDefaultRoleTests {
    @Test
    @DisplayName("Should assign default role to user")
    void assignDefaultRole_WithExistentRole_AssignsRole() {
      // Assign
      User user = new User();
      UserRoleType role = new UserRoleType();
      when(userRoleTypeService.getByName("ROLE_USER")).thenReturn(role);

      // Act
      userCreationService.assignDefaultRole(user);

      // Assert
      assertAll(
          () -> assertThat(user.getRoles()).as("checking if the role is correct").contains(role),
          () -> verify(userRoleTypeService, times(1)).getByName("ROLE_USER"));
    }
  }

  @Nested
  @DisplayName("removeUserByEmail Tests")
  class RemoveUserByEmailTests {
    @Test
    @DisplayName("Should remove user with existent email")
    void removeUserByEmail_WithExistentEmail_RemovesUser() {
      // Arrange
      User user = new User();
      when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

      // Act
      userCreationService.removeUserByEmail("test@example.com");

      // Assert
      assertAll(
          () -> verify(userRepository, times(1)).delete(user),
          () -> verify(userRepository, times(1)).findByEmail("test@example.com"));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void removeUserByEmail_WithNonExistentEmail_ThrowsEntityNotFoundException() {
      // Arrange
      when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(
                      () -> userCreationService.removeUserByEmail("nonexistent@example.com"))
                  .as("checking if the exception is an instance of EntityNotFoundException")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found!"),
          () -> verify(userRepository, times(1)).findByEmail("nonexistent@example.com"));
    }
  }
}
