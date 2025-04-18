/**
 * Generated by orval v7.8.0 🍺
 * Do not edit manually.
 * Blind API
 * kood/Jõhvi match-me task API
 * OpenAPI spec version: v0.0.1
 */
import { customInstance } from '../lib/custom-axios-instance';
import type {
  ChatMessageResponseDTO,
  ChatPreviewResponseDTO,
  GetChatMessagesParams,
  MessagesSendRequestDTO,
  PageChatMessageResponseDTO,
} from './types';

export const getChatController = () => {
  const getChatMessages = (connectionId: number, params: GetChatMessagesParams) => {
    return customInstance<PageChatMessageResponseDTO>({
      url: `http://localhost:8000/api/chats/${connectionId}/messages`,
      method: 'GET',
      params,
    });
  };
  const sendChatMessage = (connectionId: number, messagesSendRequestDTO: MessagesSendRequestDTO) => {
    return customInstance<ChatMessageResponseDTO>({
      url: `http://localhost:8000/api/chats/${connectionId}/messages`,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      data: messagesSendRequestDTO,
    });
  };
  const readChatMessages = (connectionId: number) => {
    return customInstance<ChatPreviewResponseDTO>({
      url: `http://localhost:8000/api/chats/${connectionId}/messages/read`,
      method: 'POST',
    });
  };
  const getChatPreviews = () => {
    return customInstance<ChatPreviewResponseDTO[]>({ url: `http://localhost:8000/api/chats/previews`, method: 'GET' });
  };
  return { getChatMessages, sendChatMessage, readChatMessages, getChatPreviews };
};
export type GetChatMessagesResult = NonNullable<
  Awaited<ReturnType<ReturnType<typeof getChatController>['getChatMessages']>>
>;
export type SendChatMessageResult = NonNullable<
  Awaited<ReturnType<ReturnType<typeof getChatController>['sendChatMessage']>>
>;
export type ReadChatMessagesResult = NonNullable<
  Awaited<ReturnType<ReturnType<typeof getChatController>['readChatMessages']>>
>;
export type GetChatPreviewsResult = NonNullable<
  Awaited<ReturnType<ReturnType<typeof getChatController>['getChatPreviews']>>
>;
