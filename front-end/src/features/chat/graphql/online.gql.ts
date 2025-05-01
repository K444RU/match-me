import { gql } from '@apollo/client';

const OnlineStatusFragment = gql`
  fragment OnlineStatusFields on OnlineStatus {
    connectionId
    userId
    isOnline
  }
`;

export const PING = gql`
  query Ping {
    ping {
      timestamp
      status
      userId
      peerStatuses {
        ...OnlineStatusFields
      }
    }
  }
  ${OnlineStatusFragment}
`;

export const ONLINE_STATUS_SUBSCRIPTION = gql`
  subscription OnOnlineStatusUpdate {
    onlineStatusUpdates {
      ...OnlineStatusFields
    }
  }
  ${OnlineStatusFragment}
`;
