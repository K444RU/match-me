import { createContext } from 'react';
import { UserGenderType } from '@/api/types';

export const GenderContext = createContext<UserGenderType[] | null>(null);