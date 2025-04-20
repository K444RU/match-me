package com.matchme.srv.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;
import com.matchme.srv.exception.DuplicateFieldException;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.HobbyService;
import com.matchme.srv.service.user.validation.UserValidationService;
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

    @Mock
    private UserRepository userRepository;

    @Mock
    private AttributesMapper attributesMapper;

    @Mock
    private PreferencesMapper preferencesMapper;

    @Mock
    private UserValidationService userValidationService;

    @Mock
    private HobbyService hobbyService;

    @InjectMocks
    private UserSettingsService userSettingsService;

    private static final Long VALID_USER_ID = 1L;
    private static final Long INVALID_USER_ID = 999L;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(VALID_USER_ID);
        user.setProfile(new UserProfile());
        user.getProfile().setAttributes(new UserAttributes());
        user.getProfile().setPreferences(new UserPreferences());
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
                    () -> assertThat(user)
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

        @Test
        @DisplayName("Should update email only when user exists")
        void updateUserAccountSettings_EmailOnly_Success() {
            // Arrange
            String newEmail = "new-test@example.com";
            String oldNumber = user.getNumber();
            AccountSettingsRequestDTO settings = new AccountSettingsRequestDTO(newEmail, oldNumber);

            when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
            doNothing()
                    .when(userValidationService)
                    .validateUniqueEmailAndNumber(newEmail, oldNumber, VALID_USER_ID);

            // Act
            userSettingsService.updateAccountSettings(VALID_USER_ID, settings);

            // Assert
            assertAll(
                    () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
                    () -> verify(userRepository, times(1)).save(user),
                    () -> assertThat(user.getEmail())
                            .as("checking if the email was updated correctly")
                            .isEqualTo(newEmail),
                    () -> assertThat(user.getNumber())
                            .as("checking if the number was updated correctly")
                            .isEqualTo(oldNumber));
        }

        @Test
        @DisplayName("Should update number only when user exists")
        void updateAccount_PhoneOnly_Success() {
            // Arrange
            String oldEmail = user.getEmail();
            String newNumber = "123";
            AccountSettingsRequestDTO settings = new AccountSettingsRequestDTO(oldEmail, newNumber);

            when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
            doNothing()
                    .when(userValidationService)
                    .validateUniqueEmailAndNumber(oldEmail, newNumber, VALID_USER_ID);
            // Act
            userSettingsService.updateAccountSettings(VALID_USER_ID, settings);

            // Assert
            assertAll(
                    () -> verify(userRepository).save(user),
                    () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
                    () -> assertThat(user.getEmail())
                            .as("checking if the email was updated correctly")
                            .isEqualTo(oldEmail),
                    () -> assertThat(user.getNumber())
                            .as("checking if the number was updated correctly")
                            .isEqualTo(newNumber));
            verify(userValidationService, times(1))
                    .validateUniqueEmailAndNumber(oldEmail, newNumber, VALID_USER_ID);
        }

        @Test
        @DisplayName("Should update both email and number when both are unique")
        void updateAccount_BothFieldsUnique_Success() {
            // Arrange
            String newEmail = "new-test@example.com";
            String newNumber = "+3725552222";
            AccountSettingsRequestDTO settings = new AccountSettingsRequestDTO(newEmail, newNumber);

            when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
            doNothing()
                    .when(userValidationService)
                    .validateUniqueEmailAndNumber(newEmail, newNumber, VALID_USER_ID);

            // Act
            userSettingsService.updateAccountSettings(VALID_USER_ID, settings);

            // Assert
            assertAll(
                    () -> verify(userRepository).save(user),
                    () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
                    () -> assertThat(user.getEmail())
                            .as("checking if the email was updated correctly")
                            .isEqualTo(newEmail),
                    () -> assertThat(user.getNumber())
                            .as("checking if the number was updated correctly")
                            .isEqualTo(newNumber));
            verify(userValidationService, times(1))
                    .validateUniqueEmailAndNumber(newEmail, newNumber, VALID_USER_ID);
        }

        @Test
        @DisplayName("Should throw when email belongs to another user")
        void updateAccountSettings_EmailExists_ThrowsDuplicateFieldException() {
            // Arrange
            User differentUser = new User();
            differentUser.setId(2L);
            differentUser.setEmail("taken@example.com");

            AccountSettingsRequestDTO settings = new AccountSettingsRequestDTO(differentUser.getEmail(),
                    user.getNumber());

            when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
            doThrow(new DuplicateFieldException("email", "Email already exists"))
                    .when(userValidationService)
                    .validateUniqueEmailAndNumber(differentUser.getEmail(), user.getNumber(), VALID_USER_ID);

            // Act & Assert
            assertAll(
                    () -> assertThatThrownBy(
                            () -> userSettingsService.updateAccountSettings(VALID_USER_ID, settings))
                            .as(
                                    "checking if updateAccountSettings throws DuplicateFieldException when email"
                                            + " belongs to another user")
                            .isInstanceOf(DuplicateFieldException.class),
                    () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
                    () -> verify(userRepository, never()).save(any(User.class)),
                    () -> verify(userValidationService, times(1))
                            .validateUniqueEmailAndNumber(
                                    differentUser.getEmail(), user.getNumber(), VALID_USER_ID));
        }

        @Test
        @DisplayName("Should throw when phone belongs to another user")
        void updateAccountSettings_PhoneExists_ThrowsDuplicateFieldException() {
            User differentUser = new User();
            differentUser.setId(2L);
            differentUser.setNumber("+372999000");

            AccountSettingsRequestDTO settings = new AccountSettingsRequestDTO(user.getEmail(),
                    differentUser.getNumber());

            when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));
            doThrow(new DuplicateFieldException("number", "Phone number already exists"))
                    .when(userValidationService)
                    .validateUniqueEmailAndNumber(user.getEmail(), differentUser.getNumber(), VALID_USER_ID);

            assertAll(
                    () -> assertThatThrownBy(
                            () -> userSettingsService.updateAccountSettings(VALID_USER_ID, settings))
                            .as(
                                    "checking if updateAccountSettings throws DuplicateFieldException when phone"
                                            + " belongs to another user")
                            .isInstanceOf(DuplicateFieldException.class),
                    () -> verify(userRepository, times(1)).findById(VALID_USER_ID),
                    () -> verify(userRepository, never()).save(any(User.class)),
                    () -> verify(userValidationService, times(1))
                            .validateUniqueEmailAndNumber(
                                    user.getEmail(), differentUser.getNumber(), VALID_USER_ID));
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
                    () -> assertThat(user.getProfile().getFirst_name())
                            .as("checking if the user's first name was updated correctly")
                            .isEqualTo(validRequest.getFirst_name()),
                    () -> assertThat(user.getProfile().getLast_name())
                            .as("checking if the user's last name was updated correctly")
                            .isEqualTo(validRequest.getLast_name()),
                    () -> assertThat(user.getProfile().getAlias())
                            .as("checking if the user's alias was updated correctly")
                            .isEqualTo(validRequest.getAlias()),
                    () -> assertThat(user.getProfile().getHobbies())
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
            validRequest.setGender_self(UserGenderEnum.MALE);
            validRequest.setBirth_date(LocalDate.now().minusYears(18));
            validRequest.setCity("City");
            validRequest.setLongitude(1.0);
            validRequest.setLatitude(1.0);
        }

        @Test
        @DisplayName("Should update gender, longitude, latitude, birth date, and city when user exists")
        void updateAttributesSettings_ValidRequest_UpdatesFields() {
            // Arrange
            when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));

            // Act
            userSettingsService.updateAttributesSettings(VALID_USER_ID, validRequest);

            // Assert
            assertAll(
                    () -> assertThat(user.getProfile().getAttributes().getGender())
                            .as("checking if the user's gender was updated correctly")
                            .isEqualTo(UserGenderEnum.MALE),
                    () -> assertThat(user.getProfile().getAttributes().getLocation())
                            .as("checking if the user's location was updated correctly")
                            .isEqualTo(List.of(1.0, 1.0)),
                    () -> assertThat(user.getProfile().getAttributes().getBirthdate())
                            .as("checking if the user's birth date was updated correctly")
                            .isEqualTo(validRequest.getBirth_date()),
                    () -> assertThat(user.getProfile().getCity())
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
            validRequest.setGender_other(UserGenderEnum.FEMALE);
            validRequest.setAge_min(18);
            validRequest.setAge_max(120);
            validRequest.setDistance(50);
            validRequest.setProbability_tolerance(0.5);
        }

        @Test
        @DisplayName("Should update gender_other, age_min, age_max, distance, and probability_tolerance when"
                + " user exists")
        void updatePreferencesSettings_ValidRequest_UpdatesFields() {
            // Arrange
            when(userRepository.findById(VALID_USER_ID)).thenReturn(Optional.of(user));

            // Act
            userSettingsService.updatePreferencesSettings(VALID_USER_ID, validRequest);

            // Assert
            assertAll(
                    () -> assertThat(user.getProfile().getPreferences().getGender())
                            .as("checking if the user's gender was updated correctly")
                            .isEqualTo(UserGenderEnum.FEMALE),
                    () -> assertThat(user.getProfile().getPreferences().getAgeMin())
                            .as("checking if the user's age_min was updated correctly")
                            .isEqualTo(validRequest.getAge_min()),
                    () -> assertThat(user.getProfile().getPreferences().getAgeMax())
                            .as("checking if the user's age_max was updated correctly")
                            .isEqualTo(validRequest.getAge_max()),
                    () -> assertThat(user.getProfile().getPreferences().getDistance())
                            .as("checking if the user's distance was updated correctly")
                            .isEqualTo(validRequest.getDistance()),
                    () -> assertThat(user.getProfile().getPreferences().getProbabilityTolerance())
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
