package com.matchme.srv.service.user;

import com.matchme.srv.dto.request.settings.ProfilePictureSettingsRequestDTO;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

  private final UserRepository userRepository;

  // TODO: Add validateImageSize method to check Checks if the image exceeds 5 MB
  // TODO: validateImageFormat method to check that the image is either PNG or JPEG method.

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
      String base64Part = extractBase64Part(request.getBase64Image());
      byte[] imageBytes = decodeBase64Image(base64Part);
      profile.setProfilePicture(imageBytes);
    }

    userRepository.save(user);
  }

  private String extractBase64Part(String base64Image) {
    return base64Image.contains(",")
        ? base64Image.substring(base64Image.indexOf(',') + 1)
        : base64Image;
  }

  private byte[] decodeBase64Image(String base64Part) {
    try {
      return Base64.getDecoder().decode(base64Part);
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid Base64 image data.", e);
    }
  }
}
