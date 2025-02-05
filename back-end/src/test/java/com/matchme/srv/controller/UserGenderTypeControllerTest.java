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

  public static final String BASE_URL = "/api/genders";
  public static final String GENDER_MALE = "MALE";
  public static final Long GENDER_MALE_ID = 1L;
  public static final String GENDER_FEMALE = "FEMALE";
  public static final Long GENDER_FEMALE_ID = 2L;
  public static final String GENDER_OTHER = "OTHER";
  public static final Long GENDER_OTHER_ID = 3L;


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
            new GenderTypeDTO(GENDER_MALE_ID, GENDER_MALE),
            new GenderTypeDTO(GENDER_FEMALE_ID, GENDER_FEMALE),
            new GenderTypeDTO(GENDER_OTHER_ID, GENDER_OTHER));
    when(userGenderTypeService.getAll()).thenReturn(genderTypes);

    // When/Then
    mockMvc
        .perform(get(BASE_URL))
        .andExpectAll(
            status().isOk(),
            jsonPath("$", hasSize(3)),
            jsonPath("$[*].id", containsInAnyOrder(GENDER_MALE_ID.intValue(), GENDER_FEMALE_ID.intValue(), GENDER_OTHER_ID.intValue())),
            jsonPath("$[*].name", containsInAnyOrder(GENDER_MALE, GENDER_FEMALE, GENDER_OTHER)));
  }
}
