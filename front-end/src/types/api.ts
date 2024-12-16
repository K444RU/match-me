export interface UserProfile extends UserPreferences, UserAttributes {
    firstName: string;
    lastName: string;
    alias: string;
    email: string;
    number: string;
    city: string;
}

export interface UserPreferences {
    ageMin?: number;
    ageMax?: number;
    genderOther?: number;
    distance?: number;
}

export interface UserAttributes {
    latitude?: number | null;
    longitude?: number | null;
    birthDate?: string;
    genderSelf?: number;
}

export interface CurrentUser {
    id: number;
    email: string;
    firstName: string;
    lastName: string;
    alias: string;
    role: Role[];
}

export interface Role {
    id: number;
    name: string;
}

export interface User {
    id: number;
    firstName?: string;
    lastName?: string;
    alias: string;
    avatar?: string;
}

export interface Chat {
    connectionId: number;
    sender: User;
    content: string;
    sentAt: number;
    isRead: boolean;
}

export interface ChatPreview {
    connectionId: number;
    participant: User;
    lastMessage: Chat;
    unreadCount: number;
}

export interface Gender {
    id: number;
    name: string;
}
