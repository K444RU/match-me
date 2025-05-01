import { ConnectionUpdateEvent } from '@features/chat';
import { useCallback, useState } from 'react';
import { toast } from 'sonner';
import {
  ACCEPT_CONNECTION_REQUEST,
  CONNECTION_UPDATES_SUBSCRIPTION,
  DISCONNECT_CONNECTION,
  REJECT_CONNECTION_REQUEST,
  SEND_CONNECTION_REQUEST,
} from '../graphql/connections.gql';
import { useAppMutation } from './useAppMutation';
import { useAppSubscription } from './useAppSubscription';

export default function useConnectionRequestManager() {
  const [connectionUpdates, setConnectionUpdates] = useState<ConnectionUpdateEvent[]>([]);

  // Subscribe to connection updates
  useAppSubscription<{ connectionUpdates: ConnectionUpdateEvent }>(CONNECTION_UPDATES_SUBSCRIPTION, {
    onData: ({ data }) => {
      try {
        if (data.data?.connectionUpdates) {
          const update = data.data.connectionUpdates;
          update.connection.connectionId = Number(update.connection.connectionId);
          update.connection.userId = Number(update.connection.userId);
          setConnectionUpdates((prev) => [...prev, update]);

          // Use the correct properties from the ConnectionUpdateEvent structure
          const userId = update.connection?.userId || 'Unknown';
          toast.info(`Connection update: ${update.action} with user ${userId}`);
        } else if (data.error) {
          // This might catch specific errors within the data payload
          console.error('[useConnReqMgr] Error received in data:', data.error);
          toast.error(`Subscription data error: ${data.error.message}`);
        } else {
          console.warn(
            '[useConnReqMgr] Received subscription data, but connectionUpdates field is missing or null:',
            data
          );
        }
      } catch (e) {
        console.error('[useConnReqMgr] Error in onData processing:', e);
        toast.error('Error processing subscription update.');
      }
    },
    onError: (error) => {
      // ADD or ENHANCE onError specifically
      console.error('[useConnReqMgr] onError callback triggered:', error);
      toast.error(`Subscription Error: ${error.message}`);
    },
    shouldResubscribe: true,
  });

  // --- Mutations ---
  const [sendRequestMutate, { loading: sending }] = useAppMutation(SEND_CONNECTION_REQUEST);
  const [acceptRequestMutate, { loading: accepting }] = useAppMutation(ACCEPT_CONNECTION_REQUEST);
  const [rejectRequestMutate, { loading: rejecting }] = useAppMutation(REJECT_CONNECTION_REQUEST);
  const [disconnectMutate, { loading: disconnecting }] = useAppMutation(DISCONNECT_CONNECTION);

  const sendConnectionRequest = useCallback(
    async (targetUserId: string) => {
      console.debug(`Sending connection request to ${targetUserId}`);
      try {
        const result = await sendRequestMutate({ variables: { targetUserId } });
        console.debug('Send request result:', result);
        toast.success(`Connection request sent to ${targetUserId}`);
      } catch (err) {
        console.error('Error sending connection request:', err);
      }
    },
    [sendRequestMutate]
  );

  const acceptConnectionRequest = useCallback(
    async (connectionId: string) => {
      console.log(`Accepting connection request: ${connectionId}`);
      try {
        const result = await acceptRequestMutate({ variables: { connectionId } });
        console.log('Accept request result:', result);
        // Both users involved should receive an update via the subscription
        toast.success(`Connection request ${connectionId} accepted.`);
      } catch (e) {
        console.error('Failed to accept connection request:', e);
      }
    },
    [acceptRequestMutate]
  );

  const rejectConnectionRequest = useCallback(
    async (connectionId: string) => {
      console.log(`Rejecting connection request: ${connectionId}`);
      try {
        const result = await rejectRequestMutate({ variables: { connectionId } });
        console.log('Reject request result:', result);
        // Both users involved should receive an update via the subscription
        toast.info(`Connection request ${connectionId} rejected.`);
      } catch (e) {
        console.error('Failed to reject connection request:', e);
      }
    },
    [rejectRequestMutate]
  );

  const disconnectConnection = useCallback(
    async (connectionId: string) => {
      console.log(`Disconnecting connection: ${connectionId}`);
      try {
        const result = await disconnectMutate({ variables: { connectionId } });
        console.log('Disconnect result:', result);
        // Both users involved should receive an update via the subscription
        toast.info(`Connection ${connectionId} disconnected.`);
      } catch (e) {
        console.error('Failed to disconnect connection:', e);
      }
    },
    [disconnectMutate]
  );

  const loading = sending || accepting || rejecting || disconnecting;

  return {
    connectionUpdates, // The state containing received updates
    sendConnectionRequest,
    acceptConnectionRequest,
    rejectConnectionRequest,
    disconnectConnection,
    loading, // Combined loading state
  };
}
