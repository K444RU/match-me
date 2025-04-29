package com.matchme.srv.controller;

import com.matchme.srv.dto.response.MatchingRecommendationsDTO;
import com.matchme.srv.exception.PotentialMatchesNotFoundException;
import com.matchme.srv.security.jwt.SecurityUtils;
import com.matchme.srv.service.ConnectionService;
import com.matchme.srv.service.MatchingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConnectionController.class)
class ConnectionControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @MockitoBean
  private MatchingService matchingService;

  @MockitoBean
  private SecurityUtils securityUtils;

  @MockitoBean
  private ConnectionService connectionService;

  private static final Long TEST_USER_ID = 1L;
  private static final Long RECOMMENDED_USER_ID_1 = 2L;
  private static final Long RECOMMENDED_USER_ID_2 = 3L;

  @BeforeEach
  void setUp() {
    // Set up security mocking
    when(securityUtils.getCurrentUserId(any(Authentication.class))).thenReturn(TEST_USER_ID);
  }

  @Test
  @WithMockUser
  void getRecommendations_ShouldReturnMatchingRecommendationListOfIds_WhenDataExists() throws Exception {
    // Arrange
    MatchingRecommendationsDTO mockResponse = new MatchingRecommendationsDTO();
    mockResponse.setRecommendations(Arrays.asList(RECOMMENDED_USER_ID_1, RECOMMENDED_USER_ID_2));

    when(matchingService.getRecommendations(TEST_USER_ID)).thenReturn(mockResponse);

    // Act & Assert
    mockMvc.perform(get("/connections/recommendations")
                    .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.recommendations", hasSize(2)))
            .andExpect(jsonPath("$.recommendations[0]", is(RECOMMENDED_USER_ID_1.intValue())))
            .andExpect(jsonPath("$.recommendations[1]", is(RECOMMENDED_USER_ID_2.intValue())));
  }

  @Test
  @WithMockUser
  void getRecommendations_ShouldReturnNotFound_WhenNoMatchesExist() throws Exception {
    // Arrange
    when(matchingService.getRecommendations(TEST_USER_ID))
        .thenThrow(new PotentialMatchesNotFoundException("No matches found"));

    // Act & Assert
    mockMvc.perform(get("/connections/recommendations")
        .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }
}