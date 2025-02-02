package com.matchme.srv.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import com.matchme.srv.exception.ImageValidationException;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.lang.reflect.Field;
import java.util.Base64;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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

  @InjectMocks private UserProfileService userProfileService;

  @BeforeEach
  void setUp() {
    try {
      Field maxSizeField = UserProfileService.class.getDeclaredField("maxAvatarSizeMb");
      maxSizeField.setAccessible(true);
      maxSizeField.set(userProfileService, 5L);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException("Failed to set max avatar size", e);
    }
  }

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
                  .as("checking if the exception is an instance of ImageValidationException")
                  .isInstanceOf(ImageValidationException.class)
                  .hasMessageContaining("Invalid format: missing 'data:' prefix."),
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

    @Test
    @DisplayName("Should throw exception when missing base64 delimiter")
    void saveProfilePicture_MissingBase64Delimiter_ThrowsException() {
      // Arrange
      String base64Image = "data:image/png,testImageBase64String";

      User mockUser = new User();
      UserProfile mockProfile = new UserProfile();
      mockUser.setProfile(mockProfile);
      when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

      ProfilePictureSettingsRequestDTO requestDTO =
          new ProfilePictureSettingsRequestDTO(base64Image);

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userProfileService.saveProfilePicture(1L, requestDTO))
                  .as("checking if the exception is an instance of ImageValidationException")
                  .isInstanceOf(ImageValidationException.class)
                  .hasMessageContaining("Invalid Base64 format: missing ';base64,' segment."),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, never()).save(any(User.class)));
    }

    @Test
    @DisplayName("Should throw exception when image size exceeds max size")
    void saveProfilePicture_ExceedsMaxSize_ThrowsException() {
      // Arrange
      // Create a large image byte array (e.g., 6 MB)
      byte[] largeImageBytes = new byte[6 * 1024 * 1024];
      String base64LargeImage =
          "data:image/png;base64," + Base64.getEncoder().encodeToString(largeImageBytes);

      User mockUser = new User();
      UserProfile mockProfile = new UserProfile();
      mockUser.setProfile(mockProfile);
      when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

      ProfilePictureSettingsRequestDTO requestDTO =
          new ProfilePictureSettingsRequestDTO(base64LargeImage);

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userProfileService.saveProfilePicture(1L, requestDTO))
                  .as("checking if the exception is an instance of ImageValidationException")
                  .isInstanceOf(ImageValidationException.class)
                  .hasMessageContaining("Image size exceeds the maximum allowed of 5 MB"),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, never()).save(any(User.class)));
    }

    @Test
    @DisplayName("Should save profile picture when size is at max limit")
    void saveProfilePicture_SizeAtMaxLimit_Success() throws Exception {
      // Create an image byte array of exactly 5 MB
      byte[] exactSizeImageBytes = new byte[5 * 1024 * 1024];
      String base64ExactSizeImage =
          "data:image/png;base64," + Base64.getEncoder().encodeToString(exactSizeImageBytes);

      User mockUser = new User();
      UserProfile mockProfile = new UserProfile();
      mockUser.setProfile(mockProfile);
      when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

      ProfilePictureSettingsRequestDTO requestDTO =
          new ProfilePictureSettingsRequestDTO(base64ExactSizeImage);

      // Use reflection to set maxAvatarSizeMb to 5
      Field maxSizeField = UserProfileService.class.getDeclaredField("maxAvatarSizeMb");
      maxSizeField.setAccessible(true);
      maxSizeField.set(userProfileService, 5L);

      userProfileService.saveProfilePicture(1L, requestDTO);

      assertAll(
          () -> verify(userRepository, times(1)).save(mockUser),
          () -> verify(userRepository, times(1)).findById(1L),
          () ->
              assertThat(mockUser.getProfile().getProfilePicture())
                  .as("checking if the profile picture is not null")
                  .isNotNull(),
          () ->
              assertThat(mockUser.getProfile().getProfilePicture())
                  .as("checking if the profile picture length is correct")
                  .hasSameSizeAs(exactSizeImageBytes));
    }

    @Test
    @DisplayName("Should throw exception when unsupported mime type")
    void saveProfilePicture_UnsupportedMimeType_ThrowsException() {
      // Arrange
      String base64Image =
          "data:image/gif;base64," + Base64.getEncoder().encodeToString("testImage".getBytes());

      User mockUser = new User();
      UserProfile mockProfile = new UserProfile();
      mockUser.setProfile(mockProfile);
      when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

      ProfilePictureSettingsRequestDTO requestDTO =
          new ProfilePictureSettingsRequestDTO(base64Image);

      // Act & Assert
      assertAll(
          () ->
              assertThatThrownBy(() -> userProfileService.saveProfilePicture(1L, requestDTO))
                  .as("checking if the exception is an instance of ImageValidationException")
                  .isInstanceOf(ImageValidationException.class)
                  .hasMessageContaining("Only PNG or JPEG images are allowed."),
          () -> verify(userRepository, times(1)).findById(1L),
          () -> verify(userRepository, never()).save(any(User.class)));
    }
  }
}
