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

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(userGenderTypeController).build();
  }

  @Test
  @DisplayName("User fetches all user gender types")
  void getAllGenders_returnsAllGenders() throws Exception {
    // Given
    List<GenderTypeDTO> genderTypes =
        Arrays.asList(
            new GenderTypeDTO(1L, "MALE"),
            new GenderTypeDTO(2L, "FEMALE"),
            new GenderTypeDTO(3L, "OTHER"));
    when(userGenderTypeService.getAll()).thenReturn(genderTypes);

    // When/Then
    mockMvc
        .perform(get("/api/genders"))
        .andExpectAll(
            status().isOk(),
            jsonPath("$", hasSize(3)),
            jsonPath("$[*].id", containsInAnyOrder(1, 2, 3)),
            jsonPath("$[*].name", containsInAnyOrder("MALE", "FEMALE", "OTHER")));
  }
}
