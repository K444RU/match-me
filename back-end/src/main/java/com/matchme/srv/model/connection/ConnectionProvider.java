package com.matchme.srv.model.connection;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionProvider {
    @NotNull private Long connectionId;
    @NotNull private Long userId;
}
