package com.matchme.srv.service;

import static org.mockito.ArgumentMatchers.anyLong;
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
import com.matchme.srv.service.type.ActivityLogTypeService;
import com.matchme.srv.service.type.AttributeChangeTypeService;
import com.matchme.srv.service.type.PreferenceChangeTypeService;
import com.matchme.srv.service.type.ProfileChangeTypeService;
import com.matchme.srv.service.type.UserGenderTypeService;
import com.matchme.srv.service.type.UserRoleTypeService;
import com.matchme.srv.service.type.UserStateTypesService;
import com.matchme.srv.service.user.UserCreationServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
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

  @InjectMocks private UserCreationServiceImpl userCreationService;

  @Test
  void UserCreationService_CreateUser_WithValidRequest_CreatesUser() {
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
    Mockito.verify(userRepository).save(userCaptor.capture());

    User savedUser = userCaptor.getValue();
    Assertions.assertThat(savedUser.getEmail()).isEqualTo(email);
    Assertions.assertThat(savedUser.getNumber()).isEqualTo(number);
    Assertions.assertThat(savedUser.getState()).isEqualTo(userStateTypes);
    Assertions.assertThat(savedUser.getRoles()).contains(role);
    Assertions.assertThat(savedUser.getUserAuth().getPassword()).isEqualTo("encodedPassword");
  }

  @Test
  void UserCreationService_CreateUser_WithExistentEmail_ThrowsDuplicateFieldException() {
    // Assign
    String email = "test@example.com";
    SignupRequestDTO request = SignupRequestDTO.builder().email(email).build();
    when(userRepository.existsByEmail(email)).thenReturn(true);

    // Act & Assert
    Assertions.assertThatThrownBy(() -> userCreationService.createUser(request))
        .isInstanceOf(DuplicateFieldException.class)
        .hasMessageContaining("Email already exists");
  }

  @Test
  void UserCreationService_CreateUser_WithExistentNumber_ThrowsDuplicateFieldException() {
    // Assign
    String number = "123";
    SignupRequestDTO request = SignupRequestDTO.builder().number(number).build();
    when(userRepository.existsByNumber(number)).thenReturn(true);

    // Act & Assert
    Assertions.assertThatThrownBy(() -> userCreationService.createUser(request))
        .isInstanceOf(DuplicateFieldException.class)
        .hasMessageContaining("Phone number already exists");
  }

  @Test
  void UserCreationService_VerifyAccount_WithValidVerificationCode_VerifiesAccount() {
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
    Assertions.assertThat(user.getState()).isEqualTo(userStateType);
    Assertions.assertThat(userAuth.getRecovery()).isNull();
    UserProfile profile = user.getProfile();
    Assertions.assertThat(profile).isNotNull();
    Assertions.assertThat(profile.getAttributes()).isNotNull();
    Assertions.assertThat(profile.getPreferences()).isNotNull();
    Mockito.verify(userRepository).save(user);
  }

  @Test
  void UserCreationService_VerifyAccount_WithNonExistentUser_ThrowsEntityNotFoundException() {
    // Assign
    when(userRepository.findById(1L)).thenReturn(Optional.empty());

    // Act & Assert
    Assertions.assertThatThrownBy(() -> userCreationService.verifyAccount(1L, 0))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("User not found");
  }

  @Test
  void UserCreationService_VerifyAccount_WithInvalidVerificationCode_ThrowsInvalidVerificationException() {
    // Assign
    User user = new User();
    UserAuth userAuth = new UserAuth();
    userAuth.setRecovery(123);
    user.setUserAuth(userAuth);
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));

    // Act & Assert
    Assertions.assertThatThrownBy(() -> userCreationService.verifyAccount(1L, 321))
        .isInstanceOf(InvalidVerificationException.class)
        .hasMessageContaining(
            "Verification code was wrong! Would you like us to generate the code again?");
  }

  @Test
  void UserCreationService_SetUserParameters_WithValidRequest_SetsAllParameters() {
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
    Assertions.assertThat(profile.getFirst_name()).isEqualTo("John");
    Assertions.assertThat(profile.getLast_name()).isEqualTo("Doe");
    Assertions.assertThat(profile.getAlias()).isEqualTo("JD");
    Assertions.assertThat(profile.getCity()).isEqualTo("New York");
    Assertions.assertThat(profile.getHobbies()).containsExactlyInAnyOrder(hobby1, hobby2);

    UserAttributes attributes = profile.getAttributes();
    Assertions.assertThat(attributes.getLocation()).containsExactly(12.34, 56.78);
    Assertions.assertThat(attributes.getGender()).isEqualTo(genderType);

    UserPreferences preferences = profile.getPreferences();
    Assertions.assertThat(preferences.getGender()).isEqualTo(genderType);

    Assertions.assertThat(user.getState()).isEqualTo(userStateType);
    Assertions.assertThat(user.getScore()).isNotNull();
  }

  @Test
  void UserCreationService_SetUserParameters_WithMissingLocation_ThrowsIllegalArgumentException() {
    // Assign
    UserParametersRequestDTO invalidRequest = UserParametersRequestDTO.builder().build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));

    // Act & Assert
    Assertions.assertThatThrownBy(() -> userCreationService.setUserParameters(1L, invalidRequest))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Longitude and latitude must be provided");
  }

  @Test
  void UserCreationService_SetUserParameters_WithInvalidGender_ThrowsResourceNotFoundException() {
    // Assign
    UserParametersRequestDTO request =
        UserParametersRequestDTO.builder().gender_self(99L).gender_other(2L).build();

    when(userRepository.findById(1L)).thenReturn(Optional.of(new User()));
    when(userGenderTypeService.getById(99L)).thenThrow(new ResourceNotFoundException("Gender"));

    // Act & Assert
    Assertions.assertThatThrownBy(() -> userCreationService.setUserParameters(1L, request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Gender not found");
  }

  @Test
  void UserCreationService_SetUserParameters_WithEmptyHobbies_CreatesEmptySet() {
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
    Assertions.assertThat(user.getProfile().getHobbies()).isEmpty();
  }

  @Test
  void UserCreationService_SetUserParameters_WithPartialData_SetsAvailableFields() {
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
    Assertions.assertThat(profile.getAlias()).isEqualTo("JD");
    Assertions.assertThat(profile.getFirst_name()).isNull();
    Assertions.assertThat(profile.getLast_name()).isNull();
    Assertions.assertThat(profile.getCity()).isNull();
  }

  @Test
  void UserCreationService_AssignDefaultRole_WithExistentRole_AssignsRole() {
    // Assign
    User user = new User();
    UserRoleType role = new UserRoleType();
    when(userRoleTypeService.getByName("ROLE_USER")).thenReturn(role);

    // Act
    userCreationService.assignDefaultRole(user);

    // Assert
    Assertions.assertThat(user.getRoles()).contains(role);
    Mockito.verify(userRoleTypeService).getByName("ROLE_USER");
  }

  @Test
  void UserCreationService_RemoveUserByEmail_WithExistentEmail_RemovesUser() {
    // Arrange
    User user = new User();
    when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));

    // Act
    userCreationService.removeUserByEmail("test@example.com");

    // Assert
    Mockito.verify(userRepository).delete(user);
  }

  @Test
  void UserCreationService_RemoveUserByEmail_WithNonExistentEmail_ThrowsEntityNotFoundException() {
    // Arrange
    when(userRepository.findByEmail("nonexistent@example.com")).thenReturn(Optional.empty());

    // Act & Assert
    Assertions.assertThatThrownBy(
            () -> userCreationService.removeUserByEmail("nonexistent@example.com"))
        .isInstanceOf(EntityNotFoundException.class)
        .hasMessageContaining("User not found!");
  }
}
