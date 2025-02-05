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

  private static final List<UserGenderType> mockGenders = List.of(
    UserGenderType.builder().id(1L).name("MALE").build(),
    UserGenderType.builder().id(2L).name("FEMALE").build(),
    UserGenderType.builder().id(3L).name("OTHER").build()
  );

  private static final class GenderConstants {
    static final String MALE = "MALE";
    static final Long MALE_ID = 1L;
    static final String FEMALE = "FEMALE";
    static final Long FEMALE_ID = 2L;
    static final String OTHER = "OTHER";
    static final Long OTHER_ID = 3L;
  }

  @Test
  @DisplayName("getAllGenders Tests")
  void getAllGenders_ReturnListGenderTypeDTO() {
    when(userGenderTypeRepository.findAll()).thenReturn(mockGenders);

    // Act
    List<GenderTypeDTO> result = userGenderTypeService.getAll();

    // Assert
    assertAll(
        () -> assertThat(result).as("checking if the result has size 3").hasSize(3),
        () ->
            assertThat(result.get(0).getName())
                .as("checking if the first gender is MALE")
                .isEqualTo(GenderConstants.MALE),
        () ->
            assertThat(result.get(0).getId())
                .as("checking if the first gender id is 1")
                .isEqualTo(GenderConstants.MALE_ID),
        () ->
            assertThat(result.get(1).getName())
                .as("checking if the second gender is FEMALE")
                .isEqualTo(GenderConstants.FEMALE),
        () ->
            assertThat(result.get(1).getId())
                .as("checking if the second gender id is 2")
                .isEqualTo(GenderConstants.FEMALE_ID),
        () ->
            assertThat(result.get(2).getName())
                .as("checking if the third gender is OTHER")
                .isEqualTo(GenderConstants.OTHER),
        () ->
            assertThat(result.get(2).getId())
                .as("checking if the third gender id is 3")
                .isEqualTo(GenderConstants.OTHER_ID),
        () -> verify(userGenderTypeRepository, times(1)).findAll());
  }
}
