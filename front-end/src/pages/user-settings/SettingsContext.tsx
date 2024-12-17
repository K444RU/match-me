import { createContext } from 'react';
import { UserProfile } from '@/types/api';

interface SettingsContextType {
    settings: UserProfile | null;
    refreshSettings: () => Promise<void>;
}

export const SettingsContext = createContext<SettingsContextType | null>(null);