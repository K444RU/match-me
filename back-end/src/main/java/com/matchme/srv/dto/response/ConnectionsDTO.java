package com.matchme.srv.dto.response;

import com.matchme.srv.model.connection.ConnectionProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionsDTO {
    private List<ConnectionProvider> active;
    private List<ConnectionProvider> pendingIncoming;
    private List<ConnectionProvider> pendingOutgoing;
}
