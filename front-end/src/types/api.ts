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
    firstName: string;
    lastName: string;
    alias: string;
    role: Role[]
}

export interface Role {
    id: number,
    name: string
}