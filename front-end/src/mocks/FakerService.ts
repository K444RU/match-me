import { Chat, ChatPreview, User } from '@/types/api';
import { faker, fakerFI } from '@faker-js/faker';
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

export const getLastMessageForConnection = (
    connectionId: number,
    userId: User
) => {
    return getMockChats(userId)
        .filter((chat) => chat.connectionId === connectionId)
        .sort((a, b) => b.sentAt - a.sentAt)[0];
};

export const getUnreadCountForConnection = (
    connectionId: number,
    userId: User
) => {
    return getMockChats(userId).filter(
        (chat) => chat.connectionId === connectionId && !chat.isRead
    ).length;
};

export const getCurrentUser = (user: User) => ({
    id: user.id,
    firstName: user.firstName,
    lastName: user.lastName,
    alias: user.alias,
    email: 'karl.romet@example.com',
    avatar:
        user.avatar ||
        'https://images.rawpixel.com/image_png_800/cHJpdmF0ZS9sci9pbWFnZXMvd2Vic2l0ZS8yMDI0LTAxL3Jhd3BpeGVsb2ZmaWNlN19waG90b19vZl9hX2NhdF9wZWVraW5nX3JvYW5fY2F0X3N0dWRpb19saWdodF9pc180ZDM5MDZhNy03MWY1LTQ2N2MtYTQyZC1hZmY0ZTIyYTY0ZmIucG5n.png',
});

export const generateSignupRequestDTO = (count = 1) => {
    let users = [];
    let emails = faker.helpers.uniqueArray(() => faker.internet.email(), count);
    let numbers = faker.helpers.uniqueArray(
        () => faker.phone.number({ style: 'international' }),
        count
    );
    for (let i = 0; i < count; i++) {
        users.push({
            email: emails[i],
            number: numbers[i],
            password: '123456',
        });
    }
    return users;
};

interface GenerateUserParametersRequestDTO {
    first_name: string;
    last_name: string;
    alias: string;
    gender_self: number;
    birth_date: Date;
    city: string;
    longitude: number;
    latitude: number;
    gender_other: number;
    age_min: number;
    age_max: number;
    distance: number;
    probability_tolerance: number;
}

/**
 * Generates an array of user parameter DTOs
 * @param count - The number of user parameter objects to generate
 * @returns Array of {@link GenerateUserParametersRequestDTO}
 */
export const generateUserParametersRequestDTO = (count = 1): GenerateUserParametersRequestDTO[] => {
    let users = []
    const firstNames = faker.helpers.uniqueArray(() => faker.person.firstName(), count);
    const lastNames = faker.helpers.uniqueArray(() => faker.person.lastName(), count);
    const aliases = faker.helpers.uniqueArray(() => faker.internet.username(), count);
    const longitudes = faker.helpers.uniqueArray(() => faker.location.longitude(), count);
    const latitudes = faker.helpers.uniqueArray(() => faker.location.latitude(), count);

    for (let i = 0; i < count; i++) {
        const age_min = faker.number.int({min: 18, max: 119});
        const age_max = faker.number.int({min: age_min + 1, max: 120});

        users.push({
            first_name: firstNames[i],
            last_name: lastNames[i],
            alias: aliases[i],
            gender_self: faker.number.int({min: 1, max: 3}),
            birth_date: faker.date.past(),
            city: fakerFI.location.city(),
            longitude: longitudes[i],
            latitude: latitudes[i],
            gender_other: faker.number.int({min: 1, max: 3}),
            age_min,
            age_max,
            distance: faker.number.int({min: 50, max: 300, multipleOf: 10}),
            probability_tolerance: faker.number.float({min: 0.1, max: 1, multipleOf: 0.1})
        });
    }
    return users;
}

interface UserPair {
    email1: string;
    email2: string;
}

/**
 * Generates an user pairs for inserting connections
 * @param min - Minimum connections per user
 * @param max - Maximum connections per user
 * @param emails - Array of user emails
 * @returns Array of {@link UserPair}
 */
export const generateUserPairs = (min = 0, max = 15, emails: string[]): UserPair[] => {
    const pairs: UserPair[] = [];
    emails.forEach((email1) => {
        const numberOfMatches = faker.number.int({ min, max });
        const potentialMatches = emails.filter((email2) => email2 !== email1);
        
        faker.helpers.shuffle(potentialMatches)
            .slice(0, numberOfMatches)
            .forEach((email2) => {
                pairs.push({ email1, email2 });
            });
    });
    return pairs;
}

interface FakerConnections {
    id: number;
    users: FakerConnectionUser[];
}

interface FakerConnectionUser {
    id: number;
    email: string;
    number: string;
}

interface GenerateMessageSendRequestDTO {
    connectionId: number;
    content: string;
    senderEmail: string;
    timestamp: number
}

/**
 * Generates an messages for generated connections
 * @param min - Minimum messages per connection
 * @param max - Maximum messages per connection
 * @param connections - Array of user emails
 * @returns Array of {@link GenerateMessageSendRequestDTO}
 */
export const generateMessagesSendRequestDTO = (min = 0, max = 100, connections: FakerConnections[]): GenerateMessageSendRequestDTO[] => {
    let messages = [];
    for (let i = 0; i < connections.length; i++) {
        let messageCount = faker.number.int({min, max});
        for (let j = 0; j < messageCount; j++) {
            messages.push({
                connectionId: connections[i].id,
                content: faker.lorem.sentences({min: 1, max: 5}),
                senderEmail: faker.datatype.boolean() ? connections[i].users[0].email : connections[i].users[1].email,
                 timestamp: faker.date.past().getTime(),
            })
        }
    }
    return messages;
}