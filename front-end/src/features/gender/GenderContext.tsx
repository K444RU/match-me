import { createContext } from 'react';
import { Gender } from '@/types/api';

export const GenderContext = createContext<Gender[] | null>(null);