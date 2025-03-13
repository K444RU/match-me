package com.matchme.srv.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConnectionsDTO {
    private List<Long> active;
    private List<Long> pendingIncoming;
    private List<Long> pendingOutgoing;
}
