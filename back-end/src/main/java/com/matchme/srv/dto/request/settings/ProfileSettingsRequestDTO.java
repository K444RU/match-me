package com.matchme.srv.dto.request.settings;

import java.util.Set;
import com.matchme.srv.validation.annotations.NotBlankIfPresent;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSettingsRequestDTO {
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "First name can only contain letters, spaces, hyphens and apostrophes")
    private String first_name;

    @NotBlank(message = "Last name is required")
    @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z\\s-']+$", message = "Last name can only contain letters, spaces, hyphens and apostrophes")
    private String last_name;

    @NotBlankIfPresent(message = "Alias cannot be empty if provided")
    @Size(min = 2, max = 30, message = "Alias must be between 2 and 30 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\s-_]+$", message = "Alias can only contain letters, numbers, spaces, hyphens and underscores")
    private String alias;

    @Size(max = 5, message = "Maximum 5 hobbies allowed")
    private Set<Long> hobbies;
}