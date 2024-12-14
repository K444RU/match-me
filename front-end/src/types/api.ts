export interface UserProfile {
    firstName: string;
    lastName: string;
    alias: string;
    email: string;
    city: string;
    latitude: number;
    longitude: number;
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