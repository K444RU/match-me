package com.matchme.srv.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.user.UserProfileServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTests {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserProfileServiceImpl userProfileService;

  @Nested
  @DisplayName("saveProfilePicture Tests")
  class SaveProfilePictureTests {
    @Test
    @DisplayName("Should save decoded image")
    void saveProfilePicture_ValidRequest_SavesDecodedImage() {
      // Arrange
      User user = new User();
      user.setProfile(new UserProfile());
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      String testImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";
      ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO(testImage);

      // Act
      userProfileService.saveProfilePicture(1L, request);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getProfilePicture())
                  .as("checking if the profile picture is not null")
                  .isNotNull(),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should remove picture when null request")
    void saveProfilePicture_NullRequest_RemovesPicture() {
      // Arrange
      User user = new User();
      UserProfile profile = new UserProfile();
      profile.setProfilePicture(new byte[] {1, 2, 3});
      user.setProfile(profile);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      // Act
      userProfileService.saveProfilePicture(1L, null);

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getProfilePicture())
                  .as("checking if the profile picture is null")
                  .isNull(),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should throw exception when invalid base64")
    void saveProfilePicture_InvalidBase64_ThrowsException() {
      // Arrange
      User user = new User();
      user.setProfile(new UserProfile());
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      // Act & Assert
      ProfilePictureSettingsRequestDTO request =
          new ProfilePictureSettingsRequestDTO("invalid_base64");
      assertAll(
          () ->
              assertThatThrownBy(() -> userProfileService.saveProfilePicture(1L, request))
                  .as("checking if the exception is an instance of IllegalArgumentException")
                  .isInstanceOf(IllegalArgumentException.class)
                  .hasMessageContaining("Invalid Base64 image data."),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should throw exception when user not found")
    void saveProfilePicture_NonExistentUser_ThrowsException() {
      // Arrange
      when(userRepository.findById(1L)).thenReturn(Optional.empty());

      // Act & Assert
      ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO("test");
      assertAll(
          () ->
              assertThatThrownBy(() -> userProfileService.saveProfilePicture(1L, request))
                  .as("checking if the exception is an instance of EntityNotFoundException")
                  .isInstanceOf(EntityNotFoundException.class)
                  .hasMessageContaining("User not found for ID: 1"),
          () -> verify(userRepository, times(1)).findById(1L));
    }

    @Test
    @DisplayName("Should create profile when null")
    void saveProfilePicture_NullProfile_CreatesProfile() {
      // Arrange
      User user = new User();
      user.setProfile(null);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      String testImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";
      ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO(testImage);

      // Act
      userProfileService.saveProfilePicture(1L, request);

      // Assert
      assertAll(
          () -> assertThat(user.getProfile()).as("checking if the profile is not null").isNotNull(),
          () ->
              assertThat(user.getProfile().getProfilePicture())
                  .as("checking if the profile picture is not null")
                  .isNotNull(),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should remove picture when empty base64 string")
    void saveProfilePicture_EmptyBase64_RemovesPicture() {
      // Arrange
      User user = new User();
      UserProfile profile = new UserProfile();
      profile.setProfilePicture(new byte[] {1, 2, 3});
      user.setProfile(profile);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      // Act
      userProfileService.saveProfilePicture(1L, new ProfilePictureSettingsRequestDTO(""));

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getProfilePicture())
                  .as("checking if the profile picture is null")
                  .isNull(),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, times(1)).save(user));
    }

    @Test
    @DisplayName("Should remove picture when base64 image is null")
    void saveProfilePicture_NullBase64Image_RemovesPicture() {
      // Arrange
      User user = new User();
      UserProfile profile = new UserProfile();
      profile.setProfilePicture(new byte[] {1, 2, 3});
      user.setProfile(profile);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      // Act
      userProfileService.saveProfilePicture(1L, new ProfilePictureSettingsRequestDTO(null));

      // Assert
      assertAll(
          () ->
              assertThat(user.getProfile().getProfilePicture())
                  .as("checking if the profile picture is null")
                  .isNull(),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, times(1)).save(user));
    }
  }
}
