import { Chat, ChatPreview, User } from '@/types/api';
import { faker } from '@faker-js/faker';
import { getMockChats } from './chatData';

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

    return {
        connectionId,
        sender: sender,
        content: faker.lorem.sentences({ min: 1, max: 5 }),
        sentAt: Date.now() - timeOffset,
        isRead: faker.datatype.boolean(),
    };
};

export const getLastMessageForConnection = (connectionId: number, userId: User) => {
    return getMockChats(userId)
        .filter(chat => chat.connectionId === connectionId)
        .sort((a, b) => b.sentAt - a.sentAt)[0];
};

export const getUnreadCountForConnection = (connectionId: number, userId: User) => {
    return getMockChats(userId)
        .filter(chat => chat.connectionId === connectionId && !chat.isRead)
        .length;
};

export const getCurrentUser = (user: User) => ({
    id: user.id,
    firstName: user.firstName,
    lastName: user.lastName,
    alias: user.alias,
    email: 'karl.romet@example.com',
    avatar: user.avatar || 'https://images.rawpixel.com/image_png_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDI0LTAxL3Jhd3BpeGVsb2ZmaWNlN19waG90b19vZl9hX2NhdF9wZWVraW5nX3JvYW5fY2F0X3N0dWRpb19saWdodF9pc180ZDM5MDZhNy03MWY1LTQ2N2MtYTQyZC1hZmY0ZTIyYTY0ZmIucG5n.png',
});