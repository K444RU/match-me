import React, {createContext, useContext, useEffect, useState} from 'react';
import { useSubscription } from 'react-stomp-hooks';
import { ConnectionProvider as ConnectionProviderType } from '@/api/types';
import { ConnectionUpdateMessage } from '@/features/chat/connectionUpdateMessage';
import {getConnections} from "@features/chat/connection-service.ts";

interface ConnectionState {
    pendingIncoming: ConnectionProviderType[];
    active: ConnectionProviderType[];
}

interface ConnectionContextType {
    connections: ConnectionState;
    setConnections: React.Dispatch<React.SetStateAction<ConnectionState>>;
}

const ConnectionContext = createContext<ConnectionContextType | undefined>(undefined);

export function ConnectionProvider({ children }: { children: React.ReactNode }) {
    const [connections, setConnections] = useState<ConnectionState>({
        pendingIncoming: [],
        active: [],
    });

    useEffect(() => {
        const fetchInitialConnections = async () => {
            const token = localStorage.getItem('authToken');
            if (!token) return;
            try {
                const data = await getConnections(token);
                setConnections({
                    pendingIncoming: data.pendingIncoming || [],
                    active: data.active || [],
                });
            } catch (error) {
                console.error('Failed to fetch connections:', error);
            }
        };
        fetchInitialConnections();
    }, []);

    useSubscription('/user/queue/connectionUpdates', (message) => {
        const update: ConnectionUpdateMessage = JSON.parse(message.body);
        console.log('Received connection update in ConnectionContext:', update);

        setConnections((prev) => {
            const { action, connection } = update;
            if (!connection || connection.connectionId == null) return prev;

            switch (action) {
                case 'NEW_REQUEST':
                    return {
                        ...prev,
                        pendingIncoming: [...prev.pendingIncoming, connection],
                    };
                case 'REQUEST_ACCEPTED':
                    return {
                        ...prev,
                        pendingIncoming: prev.pendingIncoming.filter(
                            (c) => c.connectionId !== connection.connectionId
                        ),
                        active: [...prev.active, connection],
                    };
                case 'REQUEST_REJECTED':
                    return {
                        ...prev,
                        pendingIncoming: prev.pendingIncoming.filter(
                            (c) => c.connectionId !== connection.connectionId
                        ),
                    };
                case 'DISCONNECTED':
                    return {
                        ...prev,
                        active: prev.active.filter(
                            (c) => c.connectionId !== connection.connectionId
                        ),
                    };
                default:
                    return prev;
            }
        });
    });

    return (
        <ConnectionContext.Provider value={{ connections, setConnections }}>
            {children}
        </ConnectionContext.Provider>
    );
}

export function useConnections() {
    const ctx = useContext(ConnectionContext);
    if (!ctx) {
        throw new Error('useConnections must be used within ConnectionProvider');
    }
    return ctx;
}
