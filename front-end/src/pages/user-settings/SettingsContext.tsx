import { createContext } from 'react';
import { SettingsResponseDTO } from '@/api/types';

interface SettingsContextType {
    settings: SettingsResponseDTO | null;
    refreshSettings: () => Promise<void>;
}

export const SettingsContext = createContext<SettingsContextType | null>(null);