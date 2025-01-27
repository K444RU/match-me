package com.matchme.srv.service.type;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.repository.UserGenderTypeRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserGenderTypeServiceTests {

  @Mock private UserGenderTypeRepository userGenderTypeRepository;

  @InjectMocks private UserGenderTypeService userGenderTypeService;

  @Test
  @DisplayName("getAllGenders Tests")
  void getAllGenders_ReturnListGenderTypeDTO() {

    // Arrange
    List<UserGenderType> mockGenders =
        List.of(
            UserGenderType.builder().id(1L).name("MALE").build(),
            UserGenderType.builder().id(2L).name("FEMALE").build(),
            UserGenderType.builder().id(3L).name("OTHER").build());

    when(userGenderTypeRepository.findAll()).thenReturn(mockGenders);

    // Act
    List<GenderTypeDTO> result = userGenderTypeService.getAll();

    // Assert
    assertAll(
        () -> assertThat(result).as("checking if the result has size 3").hasSize(3),
        () ->
            assertThat(result.get(0).getName())
                .as("checking if the first gender is MALE")
                .isEqualTo("MALE"),
        () ->
            assertThat(result.get(1).getName())
                .as("checking if the second gender is FEMALE")
                .isEqualTo("FEMALE"),
        () ->
            assertThat(result.get(2).getName())
                .as("checking if the third gender is OTHER")
                .isEqualTo("OTHER"),
        () -> verify(userGenderTypeRepository, times(1)).findAll());
  }
}
