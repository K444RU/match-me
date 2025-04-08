import {ConnectionProvider} from "@/api/types";

export interface ConnectionUpdateMessage {
    action: 'NEW_REQUEST' | 'REQUEST_SENT' | 'REQUEST_ACCEPTED' | 'REQUEST_REJECTED' | 'DISCONNECTED';
    connection: ConnectionProvider;
}