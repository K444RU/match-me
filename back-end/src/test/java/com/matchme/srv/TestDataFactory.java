package com.matchme.srv;

import static org.mockito.Mockito.when;

import com.matchme.srv.dto.request.UserParametersRequestDTO;
import com.matchme.srv.dto.request.settings.AccountSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.AttributesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.PreferencesSettingsRequestDTO;
import com.matchme.srv.dto.request.settings.ProfileSettingsRequestDTO;
import com.matchme.srv.dto.response.*;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.security.services.UserDetailsImpl;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.IntStream;
import org.springframework.security.core.Authentication;

public class TestDataFactory {

    // Initial user data
    public static final Long DEFAULT_USER_ID = 1L;
    public static final String DEFAULT_EMAIL = "user@example.com";
    public static final String DEFAULT_NUMBER = "+372 55555555";
    public static final String DEFAULT_FIRST_NAME = "John";
    public static final String DEFAULT_LAST_NAME = "Doe";
    public static final String DEFAULT_ALIAS = "johndoe";
    public static final String DEFAULT_CITY = "Tallinn";
    public static final String DEFAULT_PROFILE_PICTURE = "data:image/png;base64,dummy";
    public static final String DEFAULT_ROLE = "ROLE_USER";
    public static final int DEFAULT_AGE_SELF = 28;
    public static final int DEFAULT_AGE_MIN = 25;
    public static final int DEFAULT_AGE_MAX = 35;
    public static final int DEFAULT_DISTANCE = 50;
    public static final double DEFAULT_PROBABILITY_TOLERANCE = 1.0;
    public static final Long DEFAULT_GENDER_SELF_ID = 1L;
    public static final String DEFAULT_GENDER_SELF_NAME = "MALE";
    public static final Long DEFAULT_GENDER_OTHER_ID = 2L;
    public static final String DEFAULT_GENDER_OTHER_NAME = "FEMALE";
    public static final Set<Long> DEFAULT_HOBBY_IDS = Set.of(1L, 2L);
    public static final String DEFAULT_BIRTH_DATE = "1995-01-01";
    public static final Double DEFAULT_LONGITUDE = 25.5412;
    public static final Double DEFAULT_LATITUDE = 58.8879;

    // Update user data
    public static final String DEFAULT_UPDATE_EMAIL = "update@example.com";
    public static final String DEFAULT_UPDATE_NUMBER = "+372 5341 4494";
    public static final String DEFAULT_UPDATE_FIRST_NAME = "Johnny";
    public static final String DEFAULT_UPDATE_LAST_NAME = "Doey";
    public static final String DEFAULT_UPDATE_ALIAS = "johnnydoey";
    public static final String DEFAULT_UPDATE_CITY = "Tallinn 2";
    public static final String DEFAULT_UPDATE_PROFILE_PICTURE = "data:image/png;base64,dummy3";
    public static final int DEFAULT_UPDATE_AGE_SELF = 29;
    public static final int DEFAULT_UPDATE_AGE_MIN = 26;
    public static final int DEFAULT_UPDATE_AGE_MAX = 34;
    public static final int DEFAULT_UPDATE_DISTANCE = 52;
    public static final double DEFAULT_UPDATE_PROBABILITY_TOLERANCE = 0.9;
    public static final Long DEFAULT_UPDATE_GENDER_SELF_ID = 2L;
    public static final String DEFAULT_UPDATE_GENDER_SELF_NAME = "FEMALE";
    public static final Long DEFAULT_UPDATE_GENDER_OTHER_ID = 1L;
    public static final String DEFAULT_UPDATE_GENDER_OTHER_NAME = "MALE";
    public static final Set<Long> DEFAULT_UPDATE_HOBBY_IDS = Set.of(5L, 6L);
    public static final String DEFAULT_UPDATE_BIRTH_DATE = "1996-03-02";
    public static final Double DEFAULT_UPDATE_LONGITUDE = 26.5412;
    public static final Double DEFAULT_UPDATE_LATITUDE = 54.8879;

    // Target
    public static final Long DEFAULT_TARGET_USER_ID = 2L;
    public static final String DEFAULT_TARGET_EMAIL = "user2@example.com";
    public static final String DEFAULT_TARGET_NUMBER = "+372 44554455";
    public static final String DEFAULT_TARGET_FIRST_NAME = "Jane";
    public static final String DEFAULT_TARGET_LAST_NAME = "Doom";
    public static final String DEFAULT_TARGET_ALIAS = "janedoom";
    public static final String DEFAULT_TARGET_CITY = "Tartu";
    public static final String DEFAULT_TARGET_PROFILE_PICTURE = "data:image/png;base64,dummy2";
    public static final Long DEFAULT_TARGET_GENDER_SELF_ID = 2L;
    public static final String DEFAULT_TARGET_GENDER_SELF_NAME = "FEMALE";
    public static final Long DEFAULT_TARGET_GENDER_OTHER_ID = 1L;
    public static final String DEFAULT_TARGET_GENDER_OTHER_NAME = "MALE";
    public static final Set<Long> DEFAULT_TARGET_HOBBY_IDS = Set.of(3L, 4L);
    public static final int DEFAULT_TARGET_AGE_SELF = 25;
    public static final int DEFAULT_TARGET_AGE_MIN = 20;
    public static final int DEFAULT_TARGET_AGE_MAX = 30;
    public static final int DEFAULT_TARGET_DISTANCE = 10;
    public static final double DEFAULT_TARGET_PROBABILITY_TOLERANCE = 0.5;

    // Invalid
    public static final Long INVALID_USER_ID = 999L;

    public static User createBasicUser() {
        return createUser(DEFAULT_USER_ID, DEFAULT_EMAIL);
    }

    public static User createUser(Long id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setProfile(createBasicProfile());
        user.setRole(createUserRole());
        return user;
    }

    public static UserProfile createBasicProfile() {
        return UserProfile.builder()
                .first_name(DEFAULT_FIRST_NAME)
                .last_name(DEFAULT_LAST_NAME)
                .alias(DEFAULT_ALIAS)
                .city(DEFAULT_CITY)
                .build();
    }

    public static CurrentUserResponseDTO createCurrentUserResponse() {
        return CurrentUserResponseDTO.builder()
                .id(DEFAULT_USER_ID)
                .email(DEFAULT_EMAIL)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .alias(DEFAULT_ALIAS)
                .profilePicture(DEFAULT_PROFILE_PICTURE)
                .role(Set.of(createUserRole()))
                .build();
    }

    public static CurrentUserResponseDTO createTargetCurrentUserResponse() {
        return CurrentUserResponseDTO.builder()
                .id(DEFAULT_TARGET_USER_ID)
                .email(DEFAULT_TARGET_EMAIL)
                .firstName(DEFAULT_TARGET_FIRST_NAME)
                .lastName(DEFAULT_TARGET_LAST_NAME)
                .alias(DEFAULT_TARGET_ALIAS)
                .profilePicture(DEFAULT_TARGET_PROFILE_PICTURE)
                .role(Set.of(createUserRole()))
                .build();
    }

    public static ProfileResponseDTO createProfileResponse() {
        return ProfileResponseDTO.builder()
                .first_name(DEFAULT_FIRST_NAME)
                .last_name(DEFAULT_LAST_NAME)
                .city(DEFAULT_CITY)
                .build();
    }

    public static ProfileResponseDTO createTargetProfileResponse() {
        return ProfileResponseDTO.builder()
                .first_name(DEFAULT_TARGET_FIRST_NAME)
                .last_name(DEFAULT_TARGET_LAST_NAME)
                .city(DEFAULT_TARGET_CITY)
                .build();
    }

    public static BiographicalResponseDTO createBiographicalResponse() {
        return BiographicalResponseDTO.builder()
                .gender_self(createGenderTypeDTO(DEFAULT_GENDER_SELF_ID))
                .gender_other(createGenderTypeDTO(DEFAULT_GENDER_OTHER_ID))
                .hobbies(DEFAULT_HOBBY_IDS)
                .age_self(DEFAULT_AGE_SELF)
                .age_min(DEFAULT_AGE_MIN)
                .age_max(DEFAULT_AGE_MAX)
                .distance(DEFAULT_DISTANCE)
                .probability_tolerance(DEFAULT_PROBABILITY_TOLERANCE)
                .build();
    }

    public static BiographicalResponseDTO createTargetBiographicalResponse() {
        return BiographicalResponseDTO.builder()
                .gender_self(createGenderTypeDTO(DEFAULT_TARGET_GENDER_SELF_ID))
                .gender_other(createGenderTypeDTO(DEFAULT_TARGET_GENDER_OTHER_ID))
                .hobbies(DEFAULT_TARGET_HOBBY_IDS)
                .age_self(DEFAULT_TARGET_AGE_SELF)
                .age_min(DEFAULT_TARGET_AGE_MIN)
                .age_max(DEFAULT_TARGET_AGE_MAX)
                .distance(DEFAULT_TARGET_DISTANCE)
                .probability_tolerance(DEFAULT_TARGET_PROBABILITY_TOLERANCE)
                .build();
    }

    public static UserPreferences createUserPreferences() {
        UserPreferences prefs = new UserPreferences();
        prefs.setAgeMin(DEFAULT_AGE_MIN);
        prefs.setAgeMax(DEFAULT_AGE_MAX);
        prefs.setDistance(DEFAULT_DISTANCE);
        prefs.setProbability_tolerance(DEFAULT_PROBABILITY_TOLERANCE);
        prefs.setGender(createUserGender(DEFAULT_GENDER_OTHER_ID));
        return prefs;
    }

    public static UserAttributes createUserAttributes() {
        UserAttributes attrs = new UserAttributes();
        attrs.setBirthdate(LocalDate.now().minusYears(DEFAULT_AGE_SELF));
        attrs.setLocation(Arrays.asList(58.8879, 25.5412));
        attrs.setGender(createUserGender(DEFAULT_GENDER_SELF_ID));
        return attrs;
    }

    public static UserGenderType createUserGender(Long id) {
        return UserGenderType.builder().id(id).name(id == 1L ? "MALE" : "FEMALE").build();
    }

    public static GenderTypeDTO createGenderTypeDTO(Long id) {
        return new GenderTypeDTO(id, id == 1L ? "MALE" : "FEMALE");
    }

    public static UserRoleType createUserRole() {
        UserRoleType role = new UserRoleType();
        role.setId(1L);
        role.setName(DEFAULT_ROLE);
        return role;
    }

    public static Set<Hobby> createHobbies() {
        return Set.of(
                Hobby.builder().id(1L).name("3D printing").category("General").build(),
                Hobby.builder().id(2L).name("Acrobatics").category("General").build());
    }

    public static User userBuilder() {
        return User.builder()
                .id(DEFAULT_USER_ID)
                .email(DEFAULT_EMAIL)
                .profile(createBasicProfile())
                .roles(Set.of(createUserRole()))
                .build();
    }

    public static CurrentUserResponseDTO.CurrentUserResponseDTOBuilder currentUserResponseBuilder() {
        return CurrentUserResponseDTO.builder()
                .id(DEFAULT_USER_ID)
                .email(DEFAULT_EMAIL)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .alias(DEFAULT_ALIAS)
                .profilePicture(DEFAULT_PROFILE_PICTURE)
                .role(Set.of(createUserRole()));
    }

    // Utility method for modifying objects
    public static <T> T with(T object, Consumer<T> modifier) {
        modifier.accept(object);
        return object;
    }

    public static ConnectionResponseDTO createConnectionResponse(Long user1Id, Long user2Id) {
        return new ConnectionResponseDTO(
                1L,
                Set.of(
                        createUserResponse(user1Id, DEFAULT_EMAIL, DEFAULT_NUMBER),
                        createUserResponse(user2Id, DEFAULT_TARGET_EMAIL, DEFAULT_TARGET_NUMBER)));
    }

    public static UserResponseDTO createUserResponse(Long id, String email, String number) {
        return new UserResponseDTO(id, email, number);
    }

    public static List<ConnectionResponseDTO> createConnectionsResponse(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(i -> createConnectionResponse(DEFAULT_USER_ID, DEFAULT_USER_ID + i))
                .toList();
    }

    public static BiographicalResponseDTO.BiographicalResponseDTOBuilder biographicalResponseBuilder() {
        return BiographicalResponseDTO.builder()
                .gender_self(createGenderTypeDTO(DEFAULT_GENDER_SELF_ID))
                .gender_other(createGenderTypeDTO(DEFAULT_GENDER_OTHER_ID))
                .hobbies(DEFAULT_HOBBY_IDS)
                .age_self(DEFAULT_AGE_SELF)
                .age_min(DEFAULT_AGE_MIN)
                .age_max(DEFAULT_AGE_MAX)
                .distance(DEFAULT_DISTANCE)
                .probability_tolerance(DEFAULT_PROBABILITY_TOLERANCE);
    }

    public static UserGenderType createDefaultUserGender() {
        return createUserGender(DEFAULT_GENDER_SELF_ID);
    }

    public static GenderTypeDTO createDefaultGenderTypeDTO() {
        return createGenderTypeDTO(DEFAULT_GENDER_SELF_ID);
    }

    public static SettingsResponseDTO createSettingsResponse() {
        return SettingsResponseDTO.builder()
                .email(DEFAULT_EMAIL)
                .number(DEFAULT_NUMBER)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .alias(DEFAULT_ALIAS)
                .hobbies(DEFAULT_HOBBY_IDS)
                .genderSelf(DEFAULT_GENDER_SELF_ID)
                .birthDate(DEFAULT_BIRTH_DATE)
                .city(DEFAULT_CITY)
                .longitude(DEFAULT_LONGITUDE)
                .latitude(DEFAULT_LATITUDE)
                .genderOther(DEFAULT_GENDER_OTHER_ID)
                .ageMin(DEFAULT_AGE_MIN)
                .ageMax(DEFAULT_AGE_MAX)
                .distance(DEFAULT_DISTANCE)
                .probabilityTolerance(DEFAULT_PROBABILITY_TOLERANCE)
                .build();
    }

    public static SettingsResponseDTO.SettingsResponseDTOBuilder settingsResponseBuilder() {
        return SettingsResponseDTO.builder()
                .email(DEFAULT_EMAIL)
                .number(DEFAULT_NUMBER)
                .firstName(DEFAULT_FIRST_NAME)
                .lastName(DEFAULT_LAST_NAME)
                .alias(DEFAULT_ALIAS)
                .hobbies(DEFAULT_HOBBY_IDS)
                .genderSelf(DEFAULT_GENDER_SELF_ID)
                .birthDate(DEFAULT_BIRTH_DATE)
                .city(DEFAULT_CITY)
                .longitude(DEFAULT_LONGITUDE)
                .latitude(DEFAULT_LATITUDE)
                .genderOther(DEFAULT_GENDER_OTHER_ID)
                .ageMin(DEFAULT_AGE_MIN)
                .ageMax(DEFAULT_AGE_MAX)
                .distance(DEFAULT_DISTANCE)
                .probabilityTolerance(DEFAULT_PROBABILITY_TOLERANCE);
    }

    public static UserParametersRequestDTO createValidParametersRequest() {
        return UserParametersRequestDTO.builder()
                .first_name(DEFAULT_FIRST_NAME)
                .last_name(DEFAULT_LAST_NAME)
                .alias(DEFAULT_ALIAS)
                .hobbies(DEFAULT_HOBBY_IDS)
                .gender_other(DEFAULT_GENDER_OTHER_ID)
                .age_min(DEFAULT_AGE_MIN)
                .age_max(DEFAULT_AGE_MAX)
                .distance(DEFAULT_DISTANCE)
                .probability_tolerance(DEFAULT_PROBABILITY_TOLERANCE)
                .gender_self(DEFAULT_GENDER_SELF_ID)
                .birth_date(LocalDate.parse(DEFAULT_BIRTH_DATE))
                .city(DEFAULT_CITY)
                .longitude(DEFAULT_LONGITUDE)
                .latitude(DEFAULT_LATITUDE)
                .build();
    }

    public static AccountSettingsRequestDTO createValidAccountSettings() {
        return AccountSettingsRequestDTO.builder()
                .email(DEFAULT_UPDATE_EMAIL)
                .number(DEFAULT_UPDATE_NUMBER)
                .build();
    }

    public static ProfileSettingsRequestDTO createValidProfileSettings() {
        return ProfileSettingsRequestDTO.builder()
                .first_name(DEFAULT_UPDATE_FIRST_NAME)
                .last_name(DEFAULT_UPDATE_LAST_NAME)
                .alias(DEFAULT_UPDATE_ALIAS)
                .hobbies(DEFAULT_UPDATE_HOBBY_IDS)
                .build();
    }

    public static AttributesSettingsRequestDTO createValidAttributesSettings() {
        return AttributesSettingsRequestDTO.builder()
                .gender_self(DEFAULT_UPDATE_GENDER_SELF_ID)
                .birth_date(LocalDate.parse(DEFAULT_UPDATE_BIRTH_DATE))
                .city(DEFAULT_UPDATE_CITY)
                .longitude(DEFAULT_UPDATE_LONGITUDE)
                .latitude(DEFAULT_UPDATE_LATITUDE)
                .build();
    }

    public static PreferencesSettingsRequestDTO createValidPreferencesSettings() {
        return PreferencesSettingsRequestDTO.builder()
                .gender_other(DEFAULT_UPDATE_GENDER_OTHER_ID)
                .age_min(DEFAULT_UPDATE_AGE_MIN)
                .age_max(DEFAULT_UPDATE_AGE_MAX)
                .distance(DEFAULT_UPDATE_DISTANCE)
                .probability_tolerance(DEFAULT_UPDATE_PROBABILITY_TOLERANCE)
                .build();
    }

    /**
     * Helper method to setup authenticated status
     *
     * <p>
     * When auth.getPrincipal -> returns userDetails
     *
     * @param authentication
     * @param userId
     * @param email
     */
    public static void setupAuthenticatedUser(
            Authentication authentication, Long userId, String email) {
        UserDetailsImpl userDetails = new UserDetailsImpl(userId, email, "password", Collections.emptySet());
        when(authentication.getPrincipal()).thenReturn(userDetails);
    }

    /**
     * Helper method to setup authenticated status
     *
     * <p>
     * When auth.getPrincipal -> returns userDetails
     *
     * @param authentication
     */
    public static void setupAuthenticatedUser(Authentication authentication) {
        setupAuthenticatedUser(authentication, DEFAULT_USER_ID, DEFAULT_EMAIL);
    }
}
