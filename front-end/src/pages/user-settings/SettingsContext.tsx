import { createContext } from 'react';
import { UserProfile } from '@/types/api';

export const SettingsContext = createContext<UserProfile | null>(null);