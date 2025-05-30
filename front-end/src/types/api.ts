export interface UserProfile extends UserPreferences, UserAttributes {
  firstName: string;
  lastName: string;
  alias: string;
  hobbies?: number[];
  email: string;
  number: string;
  city: string;
}

export interface UserPreferences {
  ageMin?: number;
  ageMax?: number;
  genderOther?: number;
  distance?: number;
  probabilityTolerance?: number;
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
  profilePicture?: string;
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

export type ChatPreview = {
  connectionId: number;
  participant: {
    alias: string;
    avatar: string;
    firstName: string;
    lastName: string;
  };
  lastMessage: {
    content: string;
    sentAt: number;
  };
  unreadCount: number;
};

export interface Gender {
  id: number;
  name: string;
}
