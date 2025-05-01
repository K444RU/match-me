import { gql } from '@apollo/client';

const ChatPreviewFragment = gql`
  fragment ChatPreviewFields on ChatPreview {
    connectionId
    connectedUserId
    connectedUserAlias
    connectedUserFirstName
    connectedUserLastName
    connectedUserProfilePicture
    lastMessageContent
    lastMessageTimestamp
    unreadMessageCount
  }
`;

export const GET_CHAT_PREVIEWS = gql`
  query GetChatPreviews {
    chatPreviews {
      ...ChatPreviewFields
    }
  }
  ${ChatPreviewFragment}
`;

export const CHAT_PREVIEWS_SUBSCRIPTION = gql`
  subscription OnChatPreviewsUpdate {
    chatPreviews {
      ...ChatPreviewFields
    }
  }
  ${ChatPreviewFragment}
`;
