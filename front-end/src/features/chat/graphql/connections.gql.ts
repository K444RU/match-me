import { gql } from '@apollo/client';

// Fragment for the connection update payload
const ConnectionUpdateEventFragment = gql`
  fragment ConnectionUpdateEventFields on ConnectionUpdateEvent {
    action
    connection {
      connectionId
      userId
    }
  }
`;

// === Mutations ===

export const SEND_CONNECTION_REQUEST = gql`
  mutation SendConnectionRequest($targetUserId: ID!) {
    sendConnectionRequest(targetUserId: $targetUserId) {
      ...ConnectionUpdateEventFields
    }
  }
  ${ConnectionUpdateEventFragment}
`;

export const ACCEPT_CONNECTION_REQUEST = gql`
  mutation AcceptConnectionRequest($connectionId: ID!) {
    acceptConnectionRequest(connectionId: $connectionId) {
      ...ConnectionUpdateEventFields
    }
  }
  ${ConnectionUpdateEventFragment}
`;

export const REJECT_CONNECTION_REQUEST = gql`
  mutation RejectConnectionRequest($connectionId: ID!) {
    rejectConnectionRequest(connectionId: $connectionId) {
      ...ConnectionUpdateEventFields
    }
  }
  ${ConnectionUpdateEventFragment}
`;

export const DISCONNECT_CONNECTION = gql`
  mutation DisconnectConnection($connectionId: ID!) {
    disconnectConnection(connectionId: $connectionId) {
      ...ConnectionUpdateEventFields
    }
  }
  ${ConnectionUpdateEventFragment}
`;

// === Subscription ===

export const CONNECTION_UPDATES_SUBSCRIPTION = gql`
  subscription OnConnectionUpdate {
    connectionUpdates {
      ...ConnectionUpdateEventFields
    }
  }
  ${ConnectionUpdateEventFragment}
`;