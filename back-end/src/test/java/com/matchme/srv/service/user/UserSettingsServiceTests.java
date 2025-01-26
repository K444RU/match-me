package com.matchme.srv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.type.UserGenderTypeService;
import com.matchme.srv.service.user.UserSettingsServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDate;
import java.util.List;
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
class UserSettingsServiceTests {

  @Mock private UserRepository userRepository;

  @Mock private AttributesMapper attributesMapper;

  @Mock private PreferencesMapper preferencesMapper;

  @Mock private UserGenderTypeService userGenderTypeService;

  @Mock private HobbyService hobbyService;

  @InjectMocks private UserSettingsServiceImpl userSettingsService;

  private static final Long VALID_USER_ID = 1L;
  private static final Long INVALID_USER_ID = 999L;

  private User user;
  private UserGenderType genderType;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(VALID_USER_ID);
    user.setProfile(new UserProfile());
    user.getProfile().setAttributes(new UserAttributes());
    user.getProfile().setPreferences(new UserPreferences());
    genderType = new UserGenderType(1L, "Gender");
  }

  @Nested
  @DisplayName("updateAccountSettings Tests")
  class UpdateAccountSettingsTests {
    private AccountSettingsRequestDTO validRequest;

    @BeforeEach
    void setUp() {
      validRequest = new AccountSettingsRequestDTO();
      validRequest.setEmail("test@example.com");
      validRequest.setNumber("123");
    }

    @Test
    @DisplayName("Should update email and number when user exists")
    void updateAccountSettings_ValidRequest_UpdatesFields() {
      // Arrange
      when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));

      // Act
      userSettingsService.updateAccountSettings(VALID_USER_ID, validRequest);

      // Assert
      assertAll(
          () ->
              assertThat(user)
                  .as("User should have updated email and number")
                  .extracting(User::getEmail, User::getNumber)
                  .containsExactly(validRequest.getEmail(), validRequest.getNumber()),
          () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void updateAccountSettings_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(
              () -> userSettingsService.updateAccountSettings(INVALID_USER_ID, validRequest))
          .as("checking if the user was not found")
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("User not found!");
      verify(userRepository, times(1)).findById(INVALID_USER_ID);
    }
  }

  @Nested
  @DisplayName("updateProfileSettings Tests")
  class UpdateProfileSettingsTests {

    private ProfileSettingsRequestDTO validRequest;
    private Hobby hobby = Hobby.builder().id(1L).name("Hobby1").build();

    @BeforeEach
    void setUp() {

      validRequest = new ProfileSettingsRequestDTO();
      validRequest.setFirst_name("John");
      validRequest.setLast_name("Doe");
      validRequest.setAlias("JD");
      validRequest.setHobbies(Set.of(hobby.getId()));
    }

    @Test
    @DisplayName("Should update first name, last name, alias, hobbies, and city when user exists")
    void updateProfileSettings_ValidRequest_UpdatesFields() {
      // Arrange
      when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
      when(hobbyService.getById(hobby.getId())).thenReturn(hobby);

      // Act
      userSettingsService.updateProfileSettings(VALID_USER_ID, validRequest);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getFirst_name())
                  .as("checking if the user's first name was updated correctly")
                  .isEqualTo(validRequest.getFirst_name()),
          () ->
              assertThat(user.getProfile().getLast_name())
                  .as("checking if the user's last name was updated correctly")
                  .isEqualTo(validRequest.getLast_name()),
          () ->
              assertThat(user.getProfile().getAlias())
                  .as("checking if the user's alias was updated correctly")
                  .isEqualTo(validRequest.getAlias()),
          () ->
              assertThat(user.getProfile().getHobbies())
                  .as("checking if the user's hobbies were updated correctly")
                  .isEqualTo(Set.of(hobby)),
          () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void updateProfileSettings_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(
              () -> userSettingsService.updateProfileSettings(INVALID_USER_ID, validRequest))
          .as("checking if the user was not found")
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("User not found!");
      verify(userRepository, times(1)).findById(INVALID_USER_ID);
    }

    @Test
    @DisplayName("Should clear hobbies when null hobbies provided")
    void updateProfileSettings_NullHobbies_ClearsHobbies() {
      // Arrange
      validRequest.setHobbies(null);
      when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));

      // Act
      userSettingsService.updateProfileSettings(VALID_USER_ID, validRequest);

      // Assert
      assertThat(user.getProfile().getHobbies())
          .as("checking if hobbies were cleared for null input")
          .isEmpty();
      verify(userRepository).save(user);
    }

    @Test
    @DisplayName("Should clear hobbies when empty hobbies provided")
    void updateProfileSettings_EmptyHobbies_ClearsHobbies() {
      // Arrange
      validRequest.setHobbies(Set.of());
      when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));

      // Act
      userSettingsService.updateProfileSettings(VALID_USER_ID, validRequest);

      // Assert
      assertThat(user.getProfile().getHobbies())
          .as("checking if hobbies were cleared for empty input")
          .isEmpty();
      verify(userRepository).save(user);
    }
  }

  @Nested
  @DisplayName("updateAttributesSettings Tests")
  class UpdateAttributesSettingsTests {
    private AttributesSettingsRequestDTO validRequest;

    @BeforeEach
    void setUp() {
      validRequest = new AttributesSettingsRequestDTO();
      validRequest.setGender_self(1L);
      validRequest.setBirth_date(LocalDate.now().minusYears(18));
      validRequest.setCity("City");
      validRequest.setLongitude(1.0);
      validRequest.setLatitude(1.0);
    }

    @Test
    @DisplayName("Should update gender, longitude, latitude, birth date, and city when user exists")
    void updateAttributesSettings_ValidRequest_UpdatesFields() {
      // Arrange
      when(userGenderTypeService.getById(1L)).thenReturn(genderType);
      when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));

      // Act
      userSettingsService.updateAttributesSettings(VALID_USER_ID, validRequest);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getAttributes().getGender())
                  .as("checking if the user's gender was updated correctly")
                  .isEqualTo(genderType),
          () ->
              assertThat(user.getProfile().getAttributes().getLocation())
                  .as("checking if the user's location was updated correctly")
                  .isEqualTo(List.of(1.0, 1.0)),
          () ->
              assertThat(user.getProfile().getAttributes().getBirth_date())
                  .as("checking if the user's birth date was updated correctly")
                  .isEqualTo(validRequest.getBirth_date()),
          () ->
              assertThat(user.getProfile().getCity())
                  .as("checking if the user's city was updated correctly")
                  .isEqualTo(validRequest.getCity()),
          () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void updateAttributesSettings_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(
              () -> userSettingsService.updateAttributesSettings(INVALID_USER_ID, validRequest))
          .as("checking if the user was not found")
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("User not found!");
      verify(userRepository, times(1)).findById(INVALID_USER_ID);
    }
  }

  @Nested
  @DisplayName("updatePreferencesSettings Tests")
  class UpdatePreferencesSettingsTests {
    private PreferencesSettingsRequestDTO validRequest;

    @BeforeEach
    void setUp() {
      validRequest = new PreferencesSettingsRequestDTO();
      validRequest.setGender_other(1L);
      validRequest.setAge_min(18);
      validRequest.setAge_max(120);
      validRequest.setDistance(50);
      validRequest.setProbability_tolerance(0.5);
    }

    @Test
    @DisplayName(
        "Should update gender_other, age_min, age_max, distance, and probability_tolerance when"
            + " user exists")
    void updatePreferencesSettings_ValidRequest_UpdatesFields() {
      // Arrange
      when(userGenderTypeService.getById(1L)).thenReturn(genderType);
      when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));

      // Act
      userSettingsService.updatePreferencesSettings(VALID_USER_ID, validRequest);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getPreferences().getGender())
                  .as("checking if the user's gender was updated correctly")
                  .isEqualTo(genderType),
          () ->
              assertThat(user.getProfile().getPreferences().getAge_min())
                  .as("checking if the user's age_min was updated correctly")
                  .isEqualTo(validRequest.getAge_min()),
          () ->
              assertThat(user.getProfile().getPreferences().getAge_max())
                  .as("checking if the user's age_max was updated correctly")
                  .isEqualTo(validRequest.getAge_max()),
          () ->
              assertThat(user.getProfile().getPreferences().getDistance())
                  .as("checking if the user's distance was updated correctly")
                  .isEqualTo(validRequest.getDistance()),
          () ->
              assertThat(user.getProfile().getPreferences().getProbability_tolerance())
                  .as("checking if the user's probability_tolerance was updated correctly")
                  .isEqualTo(validRequest.getProbability_tolerance()),
          () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void updatePreferencesSettings_InvalidUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(INVALID_USER_ID)).thenReturn(Optional.empty());

      // Act & Assert
      assertThatThrownBy(
              () -> userSettingsService.updatePreferencesSettings(INVALID_USER_ID, validRequest))
          .as("checking if the user was not found")
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("User not found!");
      verify(userRepository, times(1)).findById(INVALID_USER_ID);
    }
  }
}
