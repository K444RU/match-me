import React, { createContext, useContext, useEffect, useState } from 'react';
import { useSubscription } from 'react-stomp-hooks';
import { ConnectionProvider as ConnectionProviderType } from '@/api/types';
import { ConnectionUpdateMessage } from '@/features/chat/connectionUpdateMessage';
import { getConnections } from '@/features/chat/connection-service';

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

    // Optionally fetch the initial connections on mount
    useEffect(() => {
        const fetchInitialConnections = async () => {
            const token = localStorage.getItem('authToken');
            if (!token) {
                console.warn('[ConnectionContext] No auth token found; skipping initial fetch.');
                return;
            }
            try {
                const data = await getConnections(token);
                console.log('[ConnectionContext] Fetched initial connections:', data);
                setConnections({
                    pendingIncoming: data.pendingIncoming || [],
                    active: data.active || [],
                });
            } catch (error) {
                console.error('[ConnectionContext] Failed to fetch initial connections:', error);
            }
        };
        fetchInitialConnections();
    }, []);

    // Subscribe to live updates
    useSubscription('/user/queue/connectionUpdates', (message) => {
        console.log('[ConnectionContext] Raw message body:', message.body);
        try {
            const update: ConnectionUpdateMessage = JSON.parse(message.body);
            console.log('[ConnectionContext] Received update:', update);
            setConnections((prev) => {
                const { action, connection } = update;
                if (!connection || connection.connectionId == null) return prev;
                switch (action) {
                    case 'NEW_REQUEST':
                        console.log('[ConnectionContext] NEW_REQUEST action: Adding connection', connection);
                        return {
                            ...prev,
                            pendingIncoming: [...prev.pendingIncoming, connection],
                        };
                    case 'REQUEST_ACCEPTED':
                        console.log('[ConnectionContext] REQUEST_ACCEPTED action: Moving connection to active', connection);
                        return {
                            ...prev,
                            pendingIncoming: prev.pendingIncoming.filter((c) => c.connectionId !== connection.connectionId),
                            active: [...prev.active, connection],
                        };
                    case 'REQUEST_REJECTED':
                        console.log('[ConnectionContext] REQUEST_REJECTED action: Removing connection from pending', connection);
                        return {
                            ...prev,
                            pendingIncoming: prev.pendingIncoming.filter((c) => c.connectionId !== connection.connectionId),
                        };
                    case 'DISCONNECTED':
                        console.log('[ConnectionContext] DISCONNECTED action: Removing connection from active', connection);
                        return {
                            ...prev,
                            active: prev.active.filter((c) => c.connectionId !== connection.connectionId),
                        };
                    default:
                        console.warn('[ConnectionContext] Unknown action:', action);
                        return prev;
                }
            });
        } catch (error) {
            console.error('[ConnectionContext] Failed to parse update:', error, message.body);
        }
    });

    useSubscription('/topic/test', (message) => {
        console.log('[ConnectionContext] Received test message from /topic/test:', message.body);
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
