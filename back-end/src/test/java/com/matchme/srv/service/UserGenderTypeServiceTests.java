package com.matchme.srv.service;

import static org.mockito.Mockito.when;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.matchme.srv.dto.response.GenderTypeDTO;
import com.matchme.srv.model.user.profile.UserGenderType;
import com.matchme.srv.repository.UserGenderTypeRepository;

@ExtendWith(MockitoExtension.class)
class UserGenderTypeServiceTests {

    @Mock
    private UserGenderTypeRepository userGenderTypeRepository;

    @InjectMocks
    private UserGenderTypeService userGenderTypeService;

    @Test
    void UserGenderTypeService_GetAllGenders_ReturnListGenderTypeDTO() {

        // Arrange
        List<UserGenderType> mockGenders = List.of(
            UserGenderType.builder().id(1L).name("MALE").build(),
            UserGenderType.builder().id(2L).name("FEMALE").build(),
            UserGenderType.builder().id(3L).name("OTHER").build()
        );

        when(userGenderTypeRepository.findAll()).thenReturn(mockGenders);

        // Act
        List<GenderTypeDTO> result = userGenderTypeService.getAllGenders();

        // Assert
        Assertions.assertThat(result).hasSize(3);
        Assertions.assertThat(result.get(0).getName()).isEqualTo("MALE");
        Assertions.assertThat(result.get(1).getName()).isEqualTo("FEMALE");
        Assertions.assertThat(result.get(2).getName()).isEqualTo("OTHER");
    }

}
