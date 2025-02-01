package com.matchme.srv.service.user;

import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import com.matchme.srv.exception.ImageValidationException;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Base64;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

  private final UserRepository userRepository;

  @Value("${max.avatar.size.mb}")
  private long maxAvatarSizeMb;

  private static final String DATA_PREFIX = "data:";
  private static final String BASE64_DELIMITER = ";base64,";
  private static final Set<String> ALLOWED_MIME_TYPES = Set.of("image/png", "image/jpeg");

  /**
   * Saves or updates the profile picture for a user.
   *
   * @param userId ID of the user.
   * @param request DTO containing the base64 image string.
   */
  @Transactional
  public void saveProfilePicture(Long userId, ProfilePictureSettingsRequestDTO request) {
    User user =
        userRepository
            .findById(userId)
            .orElseThrow(() -> new EntityNotFoundException("User not found for ID: " + userId));

    UserProfile profile = user.getProfile();
    if (profile == null) {
      profile = new UserProfile();
      user.setProfile(profile);
    }

    // If request is null or base64Image is null/empty, remove the profile picture
    if (request == null || request.getBase64Image() == null || request.getBase64Image().isEmpty()) {
      profile.setProfilePicture(null);
    } else {
      validateImageFormat(request.getBase64Image());

      String base64Part = extractBase64Part(request.getBase64Image());
      byte[] imageBytes = decodeBase64Image(base64Part);

      validateImageSize(imageBytes);

      profile.setProfilePicture(imageBytes);
    }

    userRepository.save(user);
  }

  /**
   * Extracts the Base64 part of the image string.
   *
   * @param base64Image The full Base64 image string.
   * @return The Base64-encoded image data.
   */
  private String extractBase64Part(String base64Image) {
    return base64Image.contains(BASE64_DELIMITER)
        ? base64Image.substring(base64Image.indexOf(BASE64_DELIMITER) + BASE64_DELIMITER.length())
        : base64Image;
  }

  /**
   * Decodes the Base64-encoded image data.
   *
   * @param base64Part The Base64-encoded image data.
   * @return The decoded byte array of the image.
   */
  private byte[] decodeBase64Image(String base64Part) {
    try {
      return Base64.getDecoder().decode(base64Part);
    } catch (IllegalArgumentException e) {
      throw new ImageValidationException("Invalid Base64 image data.");
    }
  }

  /**
   * Validates the format of the Base64-encoded image.
   *
   * @param base64Image The full Base64 image string.
   */
  private void validateImageFormat(String base64Image) {
    if (!base64Image.startsWith(DATA_PREFIX)) {
      throw new ImageValidationException("Invalid format: missing 'data:' prefix.");
    }

    int delimiterIndex = base64Image.indexOf(BASE64_DELIMITER);
    if (delimiterIndex == -1) {
      throw new ImageValidationException("Invalid Base64 format: missing ';base64,' segment.");
    }

    String mimeType = base64Image.substring(DATA_PREFIX.length(), delimiterIndex).toLowerCase();

    if (!ALLOWED_MIME_TYPES.contains(mimeType)) {
      throw new ImageValidationException("Only PNG or JPEG images are allowed.");
    }
  }

  /**
   * Validates the size of the decoded image.
   *
   * @param imageBytes The byte array of the decoded image.
   */
  private void validateImageSize(byte[] imageBytes) {
    long maxSize = getMaxImageSize();
    if (imageBytes.length > maxSize) {
      throw new ImageValidationException(
          "Image size exceeds the maximum allowed of " + (maxSize / (1024 * 1024)) + " MB");
    }
  }

  /**
   * Calculates the maximum allowed image size in bytes.
   *
   * @return The maximum image size in bytes.
   */
  private long getMaxImageSize() {
    return maxAvatarSizeMb * 1024 * 1024;
  }
}
