package com.matchme.srv.controller;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.dto.response.MatchingRecommendationsDTO.RecommendedUserDTO;
import com.matchme.srv.exception.PotentialMatchesNotFoundException;

import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.MatchingService;

@WebMvcTest(ConnectionController.class)
public class ConnectionControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private MatchingService matchingService;

  @MockBean
  private SecurityUtils securityUtils;

  private static final Long TEST_USER_ID = 1L;

  @BeforeEach
  void setUp() {
    // Set up security mocking
    when(securityUtils.getCurrentUserId(any(Authentication.class))).thenReturn(TEST_USER_ID);
  }

  @Test
  @WithMockUser
  void getRecommendations_ShouldReturnMatchingRecommendations_WhenDataExists() throws Exception {
    // Arrange
    MatchingRecommendationsDTO mockResponse = createMockRecommendationsDTO();
    when(matchingService.getRecommendations(TEST_USER_ID)).thenReturn(mockResponse);

    // Act & Assert
    mockMvc.perform(get("/api/recommendations")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.recommendations", hasSize(2)))
        .andExpect(jsonPath("$.recommendations[0].userId", is(2)))
        .andExpect(jsonPath("$.recommendations[0].firstName", is("Jane")))
        .andExpect(jsonPath("$.recommendations[0].lastName", is("Doe")))
        .andExpect(jsonPath("$.recommendations[0].age", is(28)))
        .andExpect(jsonPath("$.recommendations[0].gender", is("Female")))
        .andExpect(jsonPath("$.recommendations[0].distance", is(10)))
        .andExpect(jsonPath("$.recommendations[0].probability", closeTo(0.75, 0.001)))
        .andExpect(jsonPath("$.recommendations[0].hobbies", hasSize(2)))
        .andExpect(jsonPath("$.recommendations[0].hobbies", hasItems("Reading", "Swimming")))
        .andExpect(jsonPath("$.recommendations[0].profilePicture", startsWith("data:image/png;base64,")));
  }

  @Test
  @WithMockUser
  void getRecommendations_ShouldReturnNotFound_WhenNoMatchesExist() throws Exception {
    // Arrange
    when(matchingService.getRecommendations(TEST_USER_ID))
        .thenThrow(new PotentialMatchesNotFoundException("No matches found"));

    // Act & Assert
    mockMvc.perform(get("/api/recommendations")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

  private MatchingRecommendationsDTO createMockRecommendationsDTO() {
    MatchingRecommendationsDTO dto = new MatchingRecommendationsDTO();
    List<RecommendedUserDTO> recommendations = new ArrayList<>();

    // First recommendation
    RecommendedUserDTO rec1 = new RecommendedUserDTO();
    rec1.setUserId(2L);
    rec1.setFirstName("Jane");
    rec1.setLastName("Doe");
    rec1.setAge(28);
    rec1.setGender("Female");
    rec1.setDistance(10);
    rec1.setProbability(0.75);
    rec1.setHobbies(new HashSet<>(Arrays.asList("Reading", "Swimming")));
    rec1.setProfilePicture("data:image/png;base64,testImageData1");

    // Second recommendation
    RecommendedUserDTO rec2 = new RecommendedUserDTO();
    rec2.setUserId(3L);
    rec2.setFirstName("Alice");
    rec2.setLastName("Smith");
    rec2.setAge(25);
    rec2.setGender("Female");
    rec2.setDistance(15);
    rec2.setProbability(0.65);
    rec2.setHobbies(new HashSet<>(Arrays.asList("Hiking", "Cooking")));
    rec2.setProfilePicture("data:image/png;base64,testImageData2");

    recommendations.add(rec1);
    recommendations.add(rec2);
    dto.setRecommendations(recommendations);

    return dto;
  }
}