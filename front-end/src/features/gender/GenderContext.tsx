import { createContext } from 'react';
import { GenderTypeDTO } from '@/api/types';

export const GenderContext = createContext<GenderTypeDTO[] | null>(null);