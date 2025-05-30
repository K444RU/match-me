import {getConnectionController} from '@/api/connection-controller';
import type {MatchingRecommendationsDTO} from '@/api/types';

const connectionController = getConnectionController();

export const connectionService = {
  getRecommendations: async (): Promise<MatchingRecommendationsDTO> => {
    try {
      console.debug('ConnectionService: Making request');
      return await connectionController.getMatchingRecommendations();
    } catch (error) {
      console.error('Error fetching connections', error);
      throw error;
    }
  },

  dismissRecommendations: async (userId: number): Promise<void> => {
    try {
      await connectionController.dismissRecommendations(userId);
    } catch (error) {
      console.error('Error dismissing recommendation', error);
      throw error;
    }
  },
};

/**
 * Fetch all connections for the authenticated user.
 * @param token - The user's authentication token.
 * @returns Promise resolving to the connections' data.
 */
export const getConnections = async (token: string) => {
  const response = await fetch('http://localhost:8000/connections', {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to fetch connections');
  }

  return response.json();
};

/**
 * Accept a pending connection request.
 * @param requestId - The ID of the connection request to accept.
 * @param token - The user's authentication token.
 * @returns Promise resolving to the updated connection data.
 */
export const acceptConnection = async (requestId: string, token: string) => {
  const response = await fetch(`http://localhost:8000/connections/requests/${requestId}/accept`, {
    method: 'PATCH',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to accept connection');
  }
};

/**
 * Reject a pending connection request.
 * @param requestId - The ID of the connection request to reject.
 * @param token - The user's authentication token.
 * @returns Promise resolving to the updated connection data.
 */
export const rejectConnection = async (requestId: string, token: string) => {
  const response = await fetch(`http://localhost:8000/connections/requests/${requestId}/reject`, {
    method: 'PATCH',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to reject connection');
  }
};

/**
 * Disconnect an active connection.
 * @param connectionId - The ID of the connection to disconnect.
 * @param token - The user's authentication token.
 * @returns Promise resolving to the disconnection confirmation.
 */
export const disconnectConnection = async (connectionId: string, token: string) => {
  const response = await fetch(`http://localhost:8000/connections/${connectionId}`, {
    method: 'DELETE',
    headers: {
      Authorization: `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  });

  if (!response.ok) {
    throw new Error('Failed to disconnect');
  }
};
