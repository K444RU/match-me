package com.matchme.srv.controller;

import com.matchme.srv.service.ConnectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    //TODO: POST /connections/requests/{userId} - Send request (creates PENDING state)
    //TODO: PATCH /connections/requests/{requestId}/accept - Accept request
    //TODO: PATCH /connections/requests/{requestId}/reject - Reject request
    //TODO: DELETE /connections/{connectionId} - Disconnect

}
