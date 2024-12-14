import { Chat, ChatPreview } from '../types/api';
import { createRandomChat, createRandomUser, getLastMessageForConnection, getUnreadCountForConnection } from './FakerService';

export const user1 = {
    id: 1,
    firstName: 'Karl',
    lastName: 'Romet',
    alias: 'Shotgunner404',
    email: 'karl.romet@example.com',
    avatar: 'https://images.rawpixel.com/image_png_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDI0LTAxL3Jhd3BpeGVsb2ZmaWNlN19waG90b19vZl9hX2NhdF9wZWVraW5nX3JvYW5fY2F0X3N0dWRpb19saWdodF9pc180ZDM5MDZhNy03MWY1LTQ2N2MtYTQyZC1hZmY0ZTIyYTY0ZmIucG5n.png',
};

// const user1 = createRandomUser(1) We are user1
export const user2 = createRandomUser(2);
export const user3 = createRandomUser(3);
export const user4 = createRandomUser(4);
export const user5 = createRandomUser(5);

export const mockChats: Chat[] = [
    createRandomChat(1, user1, 3600000),
    createRandomChat(1, user2, 3500000),
    createRandomChat(1, user1, 3400000),
    createRandomChat(1, user2, 3300000),
    createRandomChat(1, user1, 3200000),

    createRandomChat(2, user1, 3600000),
    createRandomChat(2, user3, 3500000),
    createRandomChat(2, user1, 3400000),
    createRandomChat(2, user3, 3300000),
    createRandomChat(2, user1, 3200000),

    createRandomChat(3, user4, 7200000),
    createRandomChat(3, user1, 7100000),
    createRandomChat(3, user4, 7000000),
    createRandomChat(3, user4, 6900000),

    createRandomChat(4, user5, 86400000),
    createRandomChat(4, user1, 82800000),
    createRandomChat(4, user5, 79200000),
    createRandomChat(4, user1, 75600000),
    createRandomChat(4, user5, 75600000),
];

export const mockChatPreviews: ChatPreview[] = [
    {
        connectionId: 1,
        participant: user2,
        lastMessage: getLastMessageForConnection(1),
        unreadCount: getUnreadCountForConnection(1),
    },
    {
        connectionId: 2,
        participant: user3,
        lastMessage: getLastMessageForConnection(2),
        unreadCount: getUnreadCountForConnection(2),
    },
    {
        connectionId: 3,
        participant: user4,
        lastMessage: getLastMessageForConnection(3),
        unreadCount: getUnreadCountForConnection(3),
    },
    {
        connectionId: 4,
        participant: user5,
        lastMessage: getLastMessageForConnection(4),
        unreadCount: getUnreadCountForConnection(4),
    },
];