package com.matchme.srv.model.connection;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConnectionUpdateMessage {
    private String action;
    private ConnectionProvider connection;
}
