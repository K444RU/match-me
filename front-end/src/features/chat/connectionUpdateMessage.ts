import {ConnectionProvider} from "@/api/types";

export interface ConnectionUpdateMessage {
    action: string;
    connection: ConnectionProvider;
}