import { GenderTypeDTO } from '@/api/types';
import { createContext } from 'react';

export const GenderContext = createContext<GenderTypeDTO[] | null>(null);
