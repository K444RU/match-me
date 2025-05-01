import { gql } from '@apollo/client';

const TypingStatusFragment = gql`
  fragment TypingStatusFields on TypingStatus {
    connectionId
    senderId
    isTyping
  }
`;

export const SET_TYPING_STATUS = gql`
  mutation SetTypingStatus($input: TypingStatusInput!) {
    typingStatus(input: $input)
  }
`;

export const TYPING_STATUS_SUBSCRIPTION = gql`
  subscription OnTypingStatusUpdate {
    typingStatusUpdates {
      ...TypingStatusFields
    }
  }
  ${TypingStatusFragment}
`;
