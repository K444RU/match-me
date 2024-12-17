import { Chat, ChatPreview, User } from '../types/api';
import { createRandomChat, createRandomUser, getCurrentUser, getLastMessageForConnection, getUnreadCountForConnection } from './FakerService';


// const user1 = createRandomUser(1) We are user1
export const user2 = createRandomUser(2);
export const user3 = createRandomUser(3);
export const user4 = createRandomUser(4);
export const user5 = createRandomUser(5);


export const getMockChats = (user: User): Chat[] => [
    createRandomChat(1, getCurrentUser(user), 3600000),
    createRandomChat(1, user2, 3500000),
    createRandomChat(1, getCurrentUser(user), 3400000),
    createRandomChat(1, user2, 3300000),
    createRandomChat(1, getCurrentUser(user), 3200000),

    createRandomChat(2, getCurrentUser(user), 3600000),
    createRandomChat(2, user3, 3500000),
    createRandomChat(2, getCurrentUser(user), 3400000),
    createRandomChat(2, user3, 3300000),
    createRandomChat(2, getCurrentUser(user), 3200000),

    createRandomChat(3, user4, 7200000),
    createRandomChat(3, getCurrentUser(user), 7100000),
    createRandomChat(3, user4, 7000000),
    createRandomChat(3, user4, 6900000),

    createRandomChat(4, user5, 86400000),
    createRandomChat(4, getCurrentUser(user), 82800000),
    createRandomChat(4, user5, 79200000),
    createRandomChat(4, getCurrentUser(user), 75600000),
    createRandomChat(4, user5, 75600000),
];

export const getMockChatPreviews = (user: User): ChatPreview[] => [
    {
        connectionId: 1,
        participant: user2,
        lastMessage: getLastMessageForConnection(1, getCurrentUser(user)),
        unreadCount: getUnreadCountForConnection(1, getCurrentUser(user)),
    },
    {
        connectionId: 2,
        participant: user3,
        lastMessage: getLastMessageForConnection(2, getCurrentUser(user)),
        unreadCount: getUnreadCountForConnection(2, getCurrentUser(user)),
    },
    {
        connectionId: 3,
        participant: user4,
        lastMessage: getLastMessageForConnection(3, getCurrentUser(user)),
        unreadCount: getUnreadCountForConnection(3, getCurrentUser(user)),
    },
    {
        connectionId: 4,
        participant: user5,
        lastMessage: getLastMessageForConnection(4, getCurrentUser(user)),
        unreadCount: getUnreadCountForConnection(4, getCurrentUser(user)),
    },
];