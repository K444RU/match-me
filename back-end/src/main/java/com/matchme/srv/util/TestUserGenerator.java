package com.matchme.srv.util;

import com.github.javafaker.Faker;
import com.matchme.srv.model.connection.Connection;
import com.matchme.srv.model.connection.ConnectionState;
import com.matchme.srv.model.enums.ConnectionStatus;
import com.matchme.srv.model.enums.UserState;
import com.matchme.srv.model.message.MessageEvent;
import com.matchme.srv.model.message.MessageEventTypeEnum;
import com.matchme.srv.model.message.UserMessage;
import com.matchme.srv.model.user.User;
import com.matchme.srv.model.user.UserAuth;
import com.matchme.srv.model.user.UserRoleType;
import com.matchme.srv.model.user.profile.Hobby;
import com.matchme.srv.model.user.profile.UserGenderEnum;
import com.matchme.srv.model.user.profile.UserProfile;
import com.matchme.srv.model.user.profile.user_attributes.UserAttributes;
import com.matchme.srv.model.user.profile.user_preferences.UserPreferences;
import com.matchme.srv.model.user.profile.user_score.UserScore;
import com.matchme.srv.repository.*;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Utility class for generating test users in development environment. This class is only active in
 * the "dev" profile and runs after the application context is refreshed.
 */
@Slf4j
@Component
@Profile("dev")
@RequiredArgsConstructor
public class TestUserGenerator implements ApplicationListener<ContextRefreshedEvent> {

  // Constant for number of test users to generate
  private static final int NUM_TEST_USERS = 100;
  private static final int PERCENT_USERS_WITH_CONNECTIONS = 20;
  private static final int MAX_CONNECTIONS_PER_USER = 3;
  private static final int MIN_MESSAGES_PER_CONNECTION = 2;
  private static final int MAX_MESSAGES_PER_CONNECTION = 10;
  private static final int MAX_HOBBIES_PER_USER = 5;
  private static final int PROFILE_PIC_CHANCE_PERCENT = 50;

  private static final List<String> PLACEHOLDER_IMAGE_PATHS =
      List.of(
          "classpath:placeholder_images/placeholder1.jpg",
          "classpath:placeholder_images/placeholder2.jpg",
          "classpath:placeholder_images/placeholder3.jpg");

  // Standard password for all test users for easy testing
  private static final String TEST_USER_PASSWORD = "123456";

  // Map coordinates to city names
  private static final Map<Integer, String> CITY_MAP =
      Map.of(
          0, "Tallinn",
          1, "Tartu",
          2, "Narva",
          3, "Pärnu");

  // Estonia coordinates
  private static final List<List<Double>> COORDINATES =
      Arrays.asList(
          // Tallinn
          Arrays.asList(24.742301, 59.422406),
          // Tartu
          Arrays.asList(26.699296, 58.364447),
          // Narva
          Arrays.asList(28.169394, 59.371291),
          // Pärnu
          Arrays.asList(24.500813, 58.379383));

  // Maximum distance range for random location generation (in degrees)
  private static final double LOCATION_VARIANCE = 0.5;

  // --- Repositories ---
  private final UserRepository userRepository;
  private final UserRoleTypeRepository roleRepository;
  private final HobbyRepository hobbyRepository;
  private final ConnectionRepository connectionRepository;
  private final ConnectionTypeRepository connectionTypeRepository;
  private final UserMessageRepository userMessageRepository;
  private final MessageEventRepository messageEventRepository;
  // --- Services ---
  private final PasswordEncoder passwordEncoder;
  private final ResourceLoader resourceLoader;
  // --- Utilities ---
  private final Faker faker = new Faker(Locale.forLanguageTag("et-EE"));
  private final Random random = new Random();

  // To store loaded image data
  private final List<byte[]> placeholderImageData = new ArrayList<>();

  /**
   * Initialize test users after the application context has been refreshed. This method is only
   * executed in the dev environment.
   *
   * @param event The context refreshed event.
   */
  @Override
  @Transactional
  public void onApplicationEvent(ContextRefreshedEvent event) {

    long existingUserCount = userRepository.count();
    if (existingUserCount >= NUM_TEST_USERS) {
        log.info("Skipping test user generation: Found {} existing users (>= {} required).", existingUserCount, NUM_TEST_USERS);
        return; // Exit early
    }

    log.info(
        "Dev environment detected - Context refreshed - Generating {} test users", NUM_TEST_USERS);

    // --- Load placeholder images ---
    loadPlaceholderImages();

    // Get the user role
    UserRoleType userRole =
        roleRepository
            .findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Required ROLE_USER not found in database"));

    // --- Pre-fetch data needed for generation ---
    List<Hobby> allHobbies = hobbyRepository.findAll();

    // Delete existing test users (if re-running)
    List<User> existingUsers = userRepository.findAll();
    List<User> testUsers =
        existingUsers.stream()
            .filter(user -> user.getEmail() != null && user.getEmail().contains("testuser"))
            .toList();

    if (!testUsers.isEmpty()) {
      log.info("Removing {} existing test users before generating new ones", testUsers.size());
      // Need to delete associated connections/messages first if constraints exist
      // For simplicity, assuming cascading delete or manual cleanup handles this
      userRepository.deleteAll(testUsers);
      userRepository.flush(); // Ensure deletes are executed before inserts
    }

    // --- Generate Users ---
    List<User> generatedUsers = new ArrayList<>();
    for (int i = 0; i < NUM_TEST_USERS; i++) {
      User user = createRandomUser(i, userRole, allHobbies);
      generatedUsers.add(userRepository.save(user));
    }
    log.info("Successfully generated {} test users", NUM_TEST_USERS);

    // --- Generate Connections and Messages ---
    createConnectionsAndMessages(generatedUsers);

    log.info("Successfully generated connections and messages for test users");
  }

  /** Loads placeholder image data from classpath resources. */
  private void loadPlaceholderImages() {
    placeholderImageData.clear(); // Clear in case of context refresh
    for (String path : PLACEHOLDER_IMAGE_PATHS) {
      try {
        Resource resource = resourceLoader.getResource(path);
        if (resource.exists()) {
          try (InputStream inputStream = resource.getInputStream()) {
            placeholderImageData.add(inputStream.readAllBytes());
            log.debug("Loaded placeholder image: {}", path);
          }
        } else {
          log.warn("Placeholder image not found at path: {}", path);
        }
      } catch (IOException e) {
        log.error("Failed to load placeholder image from path: {}", path, e);
      }
    }
    if (placeholderImageData.isEmpty()) {
      log.warn("No placeholder images were loaded. Users will not have profile pictures.");
    }
  }

  /** Creates a random user with all required associations */
  private User createRandomUser(int index, UserRoleType userRole, List<Hobby> allHobbies) {
    UserGenderEnum gender = getRandomGender();
    String firstName = faker.name().firstName();
    String lastName = faker.name().lastName();
    String email = String.format("testuser%d@example.com", index);
    String city =
        CITY_MAP.getOrDefault(
            index % COORDINATES.size(), faker.address().city()); // Use mapped city name

    // --- Create User ---
    User user = new User();
    user.setEmail(email);
    user.setState(UserState.ACTIVE);
    user.setRoles(new HashSet<>(Collections.singletonList(userRole)));
    // Add phone number
    user.setNumber(String.format("+3725%07d", random.nextInt(10000000))); // +3725xxxxxxx

    // --- Create UserAuth ---
    UserAuth userAuth = new UserAuth(passwordEncoder.encode(TEST_USER_PASSWORD));
    user.setUserAuth(userAuth);

    // --- Create UserProfile ---
    UserProfile profile = new UserProfile();
    profile.setUser(user); // Set bidirectional relationship
    profile.setFirst_name(firstName);
    profile.setLast_name(lastName);
    profile.setAlias(faker.superhero().prefix() + faker.animal().name());
    profile.setCity(city); // Set city name
    profile.setAboutMe(faker.lorem().paragraph(3));
    // Add profile picture 50% of the time
    if (!placeholderImageData.isEmpty() && random.nextInt(100) < PROFILE_PIC_CHANCE_PERCENT) {
      byte[] pic = placeholderImageData.get(random.nextInt(placeholderImageData.size()));
      profile.setProfilePicture(pic);
    }
    // Add Hobbies
    int numHobbies = random.nextInt(MAX_HOBBIES_PER_USER + 1); // 0 to MAX_HOBBIES_PER_USER
    if (numHobbies > 0 && !allHobbies.isEmpty()) {
      Collections.shuffle(allHobbies);
      profile.setHobbies(
          new HashSet<>(allHobbies.subList(0, Math.min(numHobbies, allHobbies.size()))));
    } else {
      profile.setHobbies(new HashSet<>());
    }
    user.setProfile(profile); // Set bidirectional relationship

    // --- Create UserAttributes ---
    UserAttributes attributes = new UserAttributes();
    // attributes.setUserProfile(profile); // Handled by profile.setAttributes
    attributes.setGender(gender);
    Date birthdateDate = faker.date().past(365 * 47, 365 * 18, TimeUnit.DAYS);
    LocalDate birthdate = birthdateDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    attributes.setBirthdate(birthdate);
    List<Double> location = new ArrayList<>();
    int coordIndex = index % COORDINATES.size();
    location.add(
        COORDINATES.get(coordIndex).get(0)
            + (random.nextDouble() * LOCATION_VARIANCE * 2 - LOCATION_VARIANCE));
    location.add(
        COORDINATES.get(coordIndex).get(1)
            + (random.nextDouble() * LOCATION_VARIANCE * 2 - LOCATION_VARIANCE));
    attributes.setLocation(location);
    profile.setAttributes(attributes); // Set bidirectional relationship

    // --- Create UserPreferences ---
    UserPreferences preferences = new UserPreferences();
    // preferences.setUserProfile(profile); // Handled by profile.setPreferences
    preferences.setGender(getOppositeGenderPreference(gender));
    preferences.setAgeMin(18 + random.nextInt(10));
    preferences.setAgeMax(30 + random.nextInt(35));
    preferences.setDistance(10 + random.nextInt(90));
    // Round probability tolerance to 1 decimal place
    double rawTolerance = 0.7 + (random.nextDouble() * 0.3);
    preferences.setProbabilityTolerance(
        BigDecimal.valueOf(rawTolerance).setScale(1, RoundingMode.HALF_UP).doubleValue());
    profile.setPreferences(preferences); // Set bidirectional relationship

    // --- Create UserScore ---
    UserScore score = new UserScore();
    user.setScore(score); // Set bidirectional relationship

    return user;
  }

  /** Generates connections and messages for a subset of users */
  private void createConnectionsAndMessages(List<User> users) {
    if (users.isEmpty()) return;

    int numUsersToConnect = (users.size() * PERCENT_USERS_WITH_CONNECTIONS) / 100;
    List<User> usersToConnect = new ArrayList<>(users);
    Collections.shuffle(usersToConnect);
    usersToConnect = usersToConnect.subList(0, Math.min(numUsersToConnect, usersToConnect.size()));

    log.info("Creating connections for {} users.", usersToConnect.size());

    for (User user1 : usersToConnect) {
      int numConnections =
          1 + random.nextInt(MAX_CONNECTIONS_PER_USER); // 1 to MAX_CONNECTIONS_PER_USER
      Set<Long> connectedUserIds =
          new HashSet<>(); // Track who user1 is already connected to in this run
      connectedUserIds.add(user1.getId());

      for (int i = 0; i < numConnections; i++) {
        User user2 = null;
        // Find a distinct user to connect with
        for (int attempt = 0; attempt < users.size(); attempt++) { // Avoid infinite loop
          User potentialUser2 = users.get(random.nextInt(users.size()));
          if (!connectedUserIds.contains(potentialUser2.getId())) {
            user2 = potentialUser2;
            break;
          }
        }

        if (user2 == null) continue; // Couldn't find a distinct user to connect with

        connectedUserIds.add(user2.getId()); // Mark user2 as connected to user1 for this run

        // --- Create Connection ---
        Connection connection =
            Connection.builder()
                .users(new HashSet<>(Arrays.asList(user1, user2)))
                .connectionStates(new HashSet<>())
                .userMessages(new HashSet<>())
                .build();

        // --- Create Connection State (Accepted) ---
        ConnectionState acceptedState =
            ConnectionState.builder()
                .connection(connection)
                .user(user1) // Arbitrarily assign one user to the state
                .status(ConnectionStatus.ACCEPTED)
                .timestamp(LocalDateTime.now())
                .requesterId(user1.getId()) // Assume user1 initiated
                .targetId(user2.getId())
                .build();
        connection.getConnectionStates().add(acceptedState);

        Connection savedConnection = connectionRepository.save(connection);

        // --- Create Messages ---
        int numMessages =
            MIN_MESSAGES_PER_CONNECTION
                + random.nextInt(MAX_MESSAGES_PER_CONNECTION - MIN_MESSAGES_PER_CONNECTION + 1);
        Instant messageTime =
            Instant.now().minusSeconds(random.nextInt(3600 * 24)); // Start messages up to a day ago
        User lastSender = null;

        for (int j = 0; j < numMessages; j++) {
          User sender = (random.nextBoolean() ? user1 : user2); // Random sender
          User receiver = (sender == user1 ? user2 : user1);
          messageTime = messageTime.plusSeconds(random.nextInt(60) + 1); // Increment time

          UserMessage message =
              UserMessage.builder()
                  .connection(savedConnection)
                  .sender(sender)
                  .content(faker.lorem().sentence())
                  .createdAt(messageTime)
                  .messageEvents(new HashSet<>())
                  .build();

          UserMessage savedMessage = userMessageRepository.save(message);

          // --- Create Message Events ---
          // SENT event
          MessageEvent sentEvent =
              MessageEvent.builder()
                  .message(savedMessage)
                  .messageEventType(MessageEventTypeEnum.SENT)
                  .timestamp(messageTime.plusMillis(random.nextInt(100))) // Slightly after creation
                  .build();
          messageEventRepository.save(sentEvent);

          // RECEIVED event (assume instant for simplicity)
          MessageEvent receivedEvent =
              MessageEvent.builder()
                  .message(savedMessage)
                  .messageEventType(MessageEventTypeEnum.RECEIVED)
                  .timestamp(
                      messageTime.plusMillis(random.nextInt(200) + 100)) // Slightly after sent
                  .build();
          messageEventRepository.save(receivedEvent);

          // Optionally add READ event sometimes
          if (random.nextInt(10) < 3) { // 30% chance message is read
            MessageEvent readEvent =
                MessageEvent.builder()
                    .message(savedMessage)
                    .messageEventType(MessageEventTypeEnum.READ)
                    .timestamp(
                        messageTime.plusMillis(random.nextInt(1000) + 300)) // Later than received
                    .build();
            messageEventRepository.save(readEvent);
          }
          lastSender = sender;
        }
      }
    }
  }

  /** Returns a random gender with a somewhat realistic distribution */
  private UserGenderEnum getRandomGender() {
    int randomNumber = random.nextInt(100);
    if (randomNumber < 48) {
      return UserGenderEnum.MALE;
    } else if (randomNumber < 96) {
      return UserGenderEnum.FEMALE;
    } else {
      return UserGenderEnum.OTHER;
    }
  }

  /**
   * Returns appropriate gender preference based on user's gender This creates a more realistic
   * dating pool where the majority of users are looking for the opposite gender
   */
  private UserGenderEnum getOppositeGenderPreference(UserGenderEnum gender) {
    // 90% chance of preferring the opposite gender
    if (random.nextInt(100) < 90) {
      if (gender == UserGenderEnum.MALE) {
        return UserGenderEnum.FEMALE;
      } else if (gender == UserGenderEnum.FEMALE) {
        return UserGenderEnum.MALE;
      }
    }

    // Otherwise random preference
    int randValue = random.nextInt(3);
    return switch (randValue) {
      case 0 -> UserGenderEnum.MALE;
      case 1 -> UserGenderEnum.FEMALE;
      default -> UserGenderEnum.OTHER;
    };
  }
}
