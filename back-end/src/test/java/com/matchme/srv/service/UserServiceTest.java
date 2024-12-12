package com.matchme.srv.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.matchme.srv.dto.request.SignupRequestDTO;
import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.exception.DuplicateFieldException;
import com.matchme.srv.mapper.AttributesMapper;
import com.matchme.srv.mapper.PreferencesMapper;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserStateTypes;
import com.matchme.srv.model.user.activity.ActivityLog;
import com.matchme.srv.model.user.activity.ActivityLogType;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.repository.ActivityLogTypeRepository;
import com.matchme.srv.repository.UserGenderTypeRepository;
import com.matchme.srv.repository.UserRepository;
import com.matchme.srv.repository.UserRoleTypeRepository;
import com.matchme.srv.repository.UserStateTypesRepository;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(MockitoExtension.class) // equals to openMocks for BeforeEach
public class UserServiceTest {
  
  @InjectMocks
  private UserService userService;

  @Mock
  private UserRepository userRepository;

  @Mock
  private UserGenderTypeRepository genderRepository;

  @Mock
  private UserRoleTypeRepository roleRepository;

  @Mock
  private UserStateTypesRepository userStateTypesRepository;

  @Mock
  private ActivityLogTypeRepository activityLogTypeRepository;

  @Mock
  private PasswordEncoder encoder;

  @Mock
  private AttributesMapper attributesMapper;
    
  @Mock
  private PreferencesMapper preferencesMapper;

  private User user;
  private UserProfile profile;
  private UserAttributes attributes;
  private UserPreferences preferences;
  // private UserParametersRequestDTO parameters;

  @BeforeEach
  void setUp() {

    user = new User();
    profile = new UserProfile();
    attributes = new UserAttributes();
    preferences = new UserPreferences();
    
    profile.setAttributes(attributes);
    profile.setPreferences(preferences);
    user.setProfile(profile);
  }

  @Test
  void setUserParameters_Success() {

    var parameters = new UserParametersRequestDTO("test@test.com", "password", "12345678", 1L, "1995-10-10", 2.22, 3.33, 2L, 20, 22, 50, 0.5);
    var newState = new UserStateTypes();
    var verifiedLogType = new ActivityLogType();
    
    when(userRepository.findById(1L)).thenReturn(Optional.of(user));
    when(userStateTypesRepository.findByName("NEW")).thenReturn(Optional.of(newState));
    when(activityLogTypeRepository.findByName("VERIFIED")).thenReturn(Optional.of(verifiedLogType));
    
  
    ActivityLog result = userService.setUserParameters(1L, parameters);
    
    // Assert
    assertNotNull(result);
    assertEquals(user, result.getUser());
    assertEquals(verifiedLogType, result.getType());
    assertNotNull(user.getScore());
    
    verify(attributesMapper).toEntity(attributes, parameters);
    verify(preferencesMapper).toEntity(preferences, parameters);
    verify(userRepository).save(user);
  }
  
  // @Test
  // void setUserParameters_UserNotFound() {
  //     // Arrange
  //     var parameters = new UserParametersRequestDTO("MALE", "FEMALE");
  //     when(userRepository.findById(1L)).thenReturn(Optional.empty());
      
  //     // Act & Assert
  //     assertThrows(EntityNotFoundException.class, 
  //         () -> userService.setUserParameters(1L, parameters));
      
  //     verifyNoInteractions(attributesMapper, preferencesMapper);
  // }
  
  // @Test
  // void setUserParameters_StateNotFound() {
  //     // Arrange
  //     var parameters = new UserParametersRequestDTO("MALE", "FEMALE");
  //     when(userRepository.findById(1L)).thenReturn(Optional.of(user));
  //     when(userStateTypesRepository.findByName("NEW")).thenReturn(Optional.empty());
      
  //     // Act & Assert
  //     assertThrows(RuntimeException.class, 
  //         () -> userService.setUserParameters(1L, parameters));
      
  //     verify(userRepository, never()).save(any());
  // }
  
  // @Test
  // void setUserParameters_LogTypeNotFound() {
  //     // Arrange
  //     var parameters = new UserParametersRequestDTO("MALE", "FEMALE");
  //     var newState = new UserStateType();
      
  //     when(userRepository.findById(1L)).thenReturn(Optional.of(user));
  //     when(userStateTypesRepository.findByName("NEW")).thenReturn(Optional.of(newState));
  //     when(activityLogTypeRepository.findByName("VERIFIED")).thenReturn(Optional.empty());
      
  //     // Act & Assert
  //     assertThrows(RuntimeException.class, 
  //         () -> userService.setUserParameters(1L, parameters));
      
  //     verify(userRepository, never()).save(any());
  // }
  
  // @Test
  // void setUserParameters_ValidatesGenderMappings() {
  //     // Arrange
  //     var parameters = new UserParametersRequestDTO("MALE", "FEMALE");
  //     var newState = new UserStateType();
  //     var verifiedLogType = new ActivityLogType();
      
  //     when(userRepository.findById(1L)).thenReturn(Optional.of(user));
  //     when(userStateTypesRepository.findByName("NEW")).thenReturn(Optional.of(newState));
  //     when(activityLogTypeRepository.findByName("VERIFIED")).thenReturn(Optional.of(verifiedLogType));
      
  //     // Act
  //     userService.setUserParameters(1L, parameters);
      
  //     // Assert
  //     assertEquals("MALE", attributes.getGender());
  //     assertEquals("FEMALE", preferences.getGender());
  // }

  @Test
  void getGender_validId_returnsGender() {
    Long genderId = 1L;
    UserGenderType mockGender = new UserGenderType();
    mockGender.setId(genderId);
    mockGender.setName("MALE");

    when(genderRepository.findById(genderId)).thenReturn(Optional.of(mockGender));

    UserGenderType result = userService.getGender(genderId);

    assertNotNull(result);
    assertEquals(mockGender.getId(), result.getId());
    assertEquals(mockGender.getName(), result.getName());

    verify(genderRepository).findById(genderId);
  }

  @Test
  void getGender_invalidId_throwsException() {
    Long invalidGenderId = 99L;

    when(genderRepository.findById(invalidGenderId)).thenReturn(Optional.empty());


    Exception exception = assertThrows(RuntimeException.class, () -> {
        userService.getGender(invalidGenderId);
    });

    assertEquals("Gender not found!", exception.getMessage());
    verify(genderRepository).findById(invalidGenderId); // Verify repository interaction
  }

  @Test
  void testCreateUser_Success() {
    SignupRequestDTO request = new SignupRequestDTO();
    request.setEmail("test@example.com");
    request.setNumber("+372 55512999");
    request.setPassword("password");
    
    UserStateTypes mockState = new UserStateTypes();
    mockState.setId(1L);
    mockState.setName("UNVERIFIED");

    ActivityLogType mockLogType = new ActivityLogType();
    mockLogType.setId(1L);
    mockLogType.setName("CREATED");


    when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
    when(userRepository.existsByNumber("+372 55512999")).thenReturn(false);
    when(activityLogTypeRepository.findByName("CREATED")).thenReturn(Optional.of(mockLogType));
    when(userStateTypesRepository.findByName("UNVERIFIED")).thenReturn(Optional.of(mockState));
    when(encoder.encode("password")).thenReturn("encodedPassword");

    ActivityLog activityLog = userService.createUser(request);

    assertNotNull(activityLog);
    assertEquals("CREATED", activityLog.getType().getName());
    assertEquals("test@example.com", activityLog.getUser().getEmail());
    verify(userRepository).save(any(User.class));
  }

  @Test
  void testCreateUser_DuplicateEmail() {
    SignupRequestDTO request = new SignupRequestDTO();
    request.setEmail("test@example.com");
    request.setNumber("+372 55512999");
    request.setPassword("password");

    when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

    DuplicateFieldException ex = assertThrows(
      DuplicateFieldException.class, () -> userService.createUser(request));
    
    assertEquals("email", ex.getFieldName());
    assertEquals("Email already exists", ex.getMessage());

    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testCreateUser_MissingUserState() {
    SignupRequestDTO request = new SignupRequestDTO();
    request.setEmail("test@example.com");
    request.setNumber("+372 55512999");
    request.setPassword("password");

    when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
    when(userRepository.existsByNumber("+372 55512999")).thenReturn(false);
    when(userStateTypesRepository.findByName("UNVERIFIED")).thenReturn(Optional.empty());

    RuntimeException exception = assertThrows(
        RuntimeException.class, () -> userService.createUser(request)
    );

    assertEquals("UserState not found", exception.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }

  @Test
  void testCreateUser_MissingLogType() {

    SignupRequestDTO request = new SignupRequestDTO();
    request.setEmail("test@example.com");
    request.setNumber("+372 55512999");
    request.setPassword("password");

    UserStateTypes state = new UserStateTypes();
    state.setId(1L);
    state.setName("UNVERIFIED");

    when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
    when(userRepository.existsByNumber("+372 55512999")).thenReturn(false);
    when(userStateTypesRepository.findByName("UNVERIFIED")).thenReturn(Optional.of(state));
    when(activityLogTypeRepository.findByName("CREATED")).thenReturn(Optional.empty());


    RuntimeException exception = assertThrows(
        RuntimeException.class, () -> userService.createUser(request)
    );

    assertEquals("LogType not found", exception.getMessage());
    verify(userRepository, never()).save(any(User.class));
  }

}
