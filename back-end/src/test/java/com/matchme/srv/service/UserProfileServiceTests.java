package com.matchme.srv.service;

import static org.mockito.Mockito.when;

import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.service.user.UserProfileServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTests {

  @Mock private UserRepository userRepository;

  @InjectMocks private UserProfileServiceImpl userProfileService;

  @Nested
  class SaveProfilePictureTests {
    @Test
    void UserProfileService_SaveProfilePicture_SavesDecodedImage() {
      // Arrange
      User user = new User();
      user.setProfile(new UserProfile());
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      String testImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";
      ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO(testImage);

      // Act
      userProfileService.saveProfilePicture(1L, request);

      // Assert
      Assertions.assertThat(user.getProfile().getProfilePicture()).isNotNull();
      Mockito.verify(userRepository).save(user);
    }

    @Test
    void UserProfileService_SaveProfilePicture_RemovesPictureWhenNullRequest() {
      // Arrange
      User user = new User();
      UserProfile profile = new UserProfile();
      profile.setProfilePicture(new byte[] {1, 2, 3});
      user.setProfile(profile);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      // Act
      userProfileService.saveProfilePicture(1L, null);

      // Assert
      Assertions.assertThat(user.getProfile().getProfilePicture()).isNull();
      Mockito.verify(userRepository).save(user);
    }

    @Test
    void UserProfileService_SaveProfilePicture_ThrowsWhenInvalidBase64() {
      // Arrange
      User user = new User();
      user.setProfile(new UserProfile());
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));

      // Act & Assert
      ProfilePictureSettingsRequestDTO request =
          new ProfilePictureSettingsRequestDTO("invalid_base64");
      Assertions.assertThatThrownBy(() -> userProfileService.saveProfilePicture(1L, request))
          .isInstanceOf(IllegalArgumentException.class)
          .hasMessageContaining("Invalid Base64 image data.");
    }

    @Test
    void UserProfileService_SaveProfilePicture_ThrowsWhenUserNotFound() {
      // Arrange
      when(userRepository.findById(1L)).thenReturn(Optional.empty());

      // Act & Assert
      ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO("test");
      Assertions.assertThatThrownBy(() -> userProfileService.saveProfilePicture(1L, request))
          .isInstanceOf(EntityNotFoundException.class)
          .hasMessageContaining("User not found for ID: 1");
    }

    @Test
    void UserProfileService_SaveProfilePicture_CreatesProfileWhenNull() {
      // Arrange
      User user = new User();
      user.setProfile(null);
      when(userRepository.findById(1L)).thenReturn(Optional.of(user));
      String testImage = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUA";
      ProfilePictureSettingsRequestDTO request = new ProfilePictureSettingsRequestDTO(testImage);

      // Act
      userProfileService.saveProfilePicture(1L, request);

      // Assert
      Assertions.assertThat(user.getProfile()).isNotNull();
      Assertions.assertThat(user.getProfile().getProfilePicture()).isNotNull();
      Mockito.verify(userRepository).save(user);
    }
  }
}
