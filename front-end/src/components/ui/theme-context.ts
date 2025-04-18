import { createContext, useContext } from 'react';

export type Theme = 'dark' | 'light' | 'system';

export type ThemeProviderState = {
  theme: Theme;
  setTheme: (theme: Theme) => void;
};

const initialState: ThemeProviderState = {
  theme: 'system',
  setTheme: () => null,
};

export const ThemeContext = createContext<ThemeProviderState>(initialState);

export function useTheme() {
  const context = useContext(ThemeContext);

  if (context === undefined) throw new Error('useTheme must be used within a ThemeProvider');

  return context;
}
