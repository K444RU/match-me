package com.matchme.srv.dto.graphql;

import com.matchme.srv.model.connection.ConnectionUpdateType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionUpdateEvent {
    private ConnectionUpdateType type;
    private String connectionId;
    private UserGraphqlDTO otherUser;
}