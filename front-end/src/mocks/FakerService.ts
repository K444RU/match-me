import { Chat, ChatPreview } from '@/types/api';
import { faker } from '@faker-js/faker';
import { mockChats, user1 } from './chatData';

export function createRandomUser(id: number) {
    const shouldHaveAvatar = faker.datatype.boolean();
    const shouldHaveName = faker.datatype.boolean();

    return {
        id: id,
        firstName: shouldHaveName ? faker.person.firstName() : '',
        lastName: shouldHaveName ? faker.person.lastName() : '',
        alias: faker.internet.username(),
        email: faker.internet.email(),
        avatar: shouldHaveAvatar ? faker.image.avatar() : '',
        //   sex: faker.person.sex(),
        //   gender: faker.person.gender(),
        //   password: faker.internet.password(),
        //   birthdate: faker.date.birthdate(),
        //   registeredAt: faker.date.past(),
    };
}

export const createRandomChatPreview = (
    connectionId: number,
    participant: any
): ChatPreview => ({
    connectionId,
    participant,
    lastMessage: {
        connectionId,
        sender: participant,
        content: faker.lorem.sentence(),
        sentAt: Date.now() - faker.number.int({ min: 3600000, max: 172800000 }),
        isRead: faker.datatype.boolean(),
    },
    unreadCount: faker.number.int({ min: 0, max: 10 }),
});

export const createRandomChat = (
    connectionId: number,
    sender: any,
    timeOffset: number
): Chat => {
    const shouldSwapSender = faker.datatype.boolean();

    return {
        connectionId,
        sender: shouldSwapSender ? sender : user1,
        content: faker.lorem.sentences({ min: 1, max: 5 }),
        sentAt: Date.now() - timeOffset,
        isRead: faker.datatype.boolean(),
    };
};

export const getLastMessageForConnection = (connectionId: number) => {
    return mockChats
        .filter(chat => chat.connectionId === connectionId)
        .sort((a, b) => b.sentAt - a.sentAt)[0];
};

export const getUnreadCountForConnection = (connectionId: number) => {
    return mockChats
        .filter(chat => chat.connectionId === connectionId && !chat.isRead)
        .length;
};