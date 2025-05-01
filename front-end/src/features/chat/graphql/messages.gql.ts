import { gql } from '@apollo/client';

const ChatMessageFragment = gql`
  fragment ChatMessageFields on ChatMessage {
    messageId
    connectionId
    senderId
    senderAlias
    content
    createdAt
    event {
      type
      timestamp
    }
  }
`;

const MessageStatusUpdateFragment = gql`
  fragment MessageStatusUpdateFields on MessageStatusUpdate {
    messageId
    connectionId
    type
    timestamp
  }
`;

export const GET_CHAT_MESSAGES = gql`
  query GetChatMessages($connectionId: ID!, $before: String, $limit: Int) {
    chatMessages(connectionId: $connectionId, before: $before, limit: $limit) {
      ...ChatMessageFields
    }
  }
  ${ChatMessageFragment}
`;

export const SEND_MESSAGE = gql`
  mutation SendMessage($input: MessagesSendInput!) {
    sendMessage(input: $input) {
      ...ChatMessageFields
    }
  }
  ${ChatMessageFragment}
`;

export const MARK_MESSAGES_READ = gql`
  mutation MarkMessagesRead($input: MarkReadInput!) {
    markMessagesAsRead(input: $input) {
      connectionId
      connectedUserId
      connectedUserAlias
      lastMessageContent
      lastMessageTimestamp
      unreadMessageCount
    }
  }
`;

export const CHAT_MESSAGES_SUBSCRIPTION = gql`
  subscription OnNewMessage {
    messages {
      ...ChatMessageFields
    }
  }
  ${ChatMessageFragment}
`;

export const MESSAGE_STATUS_SUBSCRIPTION = gql`
  subscription OnMessageStatusUpdate {
    messageStatus {
      ...MessageStatusUpdateFields
    }
  }
  ${MessageStatusUpdateFragment}
`;