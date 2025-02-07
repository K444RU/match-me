package com.matchme.srv.controller;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.service.type.UserGenderTypeService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class UserGenderTypeControllerTest {

  private MockMvc mockMvc;

  @Mock private UserGenderTypeService userGenderTypeService;

  @InjectMocks private UserGenderTypeController userGenderTypeController;

  private static final class GenderConstants {
    static final String BASE_URL = "/api/genders";
    static final String MALE = "MALE";
    static final Long MALE_ID = 1L;
    static final String FEMALE = "FEMALE";
    static final Long FEMALE_ID = 2L;
    static final String OTHER = "OTHER";
    static final Long OTHER_ID = 3L;
  }

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(userGenderTypeController).build();
  }

  @Test
  @DisplayName("User fetches all user gender types")
  void getAllGenders_WhenRequested_ReturnsAllGenders() throws Exception {
    // Given
    List<GenderTypeDTO> genderTypes =
        Arrays.asList(
            new GenderTypeDTO(GenderConstants.MALE_ID, GenderConstants.MALE),
            new GenderTypeDTO(GenderConstants.FEMALE_ID, GenderConstants.FEMALE),
            new GenderTypeDTO(GenderConstants.OTHER_ID, GenderConstants.OTHER));
    when(userGenderTypeService.getAll()).thenReturn(genderTypes);

    // When/Then
    mockMvc
        .perform(get(GenderConstants.BASE_URL))
        .andExpectAll(
            status().isOk(),
            jsonPath("$", hasSize(3)),
            jsonPath(
                "$[*].id",
                containsInAnyOrder(
                    GenderConstants.MALE_ID.intValue(),
                    GenderConstants.FEMALE_ID.intValue(),
                    GenderConstants.OTHER_ID.intValue())),
            jsonPath("$[*].name", containsInAnyOrder(GenderConstants.MALE, GenderConstants.FEMALE, GenderConstants.OTHER)));
  }
}
