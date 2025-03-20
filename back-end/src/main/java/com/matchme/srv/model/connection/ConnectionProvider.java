package com.matchme.srv.model.connection;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionProvider {
    private Long connectionId;
    private Long userId;
}
