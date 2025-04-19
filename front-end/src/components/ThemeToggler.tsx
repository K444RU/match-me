import { Moon, Sun } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Button } from './ui/button';
import { useTheme } from './ui/theme-context';
import { motion } from 'motion/react';

const MotionButton = motion.create(Button);

export default function ThemeToggler() {
  const { theme, setTheme } = useTheme();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  if (!mounted) {
    return (
      <Button variant="ghost" size="icon">
        <span className="size-4" />
      </Button>
    );
  }

  return (
    <MotionButton
      whileHover={{ scale: 1.1 }}
      whileTap={{ scale: 0.9 }}
      variant="secondary"
      size="icon"
      onClick={() => setTheme(theme === 'dark' ? 'light' : 'dark')}
      title={`Switch to ${theme === 'dark' ? 'light' : 'dark'} theme`}
      className='focus-visible:ring-ring bg-card text-primary-foreground shadow-xs ring-offset-background focus-visible:outline-hidden inline-flex h-10 items-center justify-center gap-2 whitespace-nowrap rounded-md px-4 py-2 text-sm font-semibold tracking-wide transition-colors duration-300 focus-visible:ring-2 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0 hover:bg-card'
    >
      {theme === 'dark' ? <Sun /> : <Moon />}
      <span className="sr-only">Toggle theme</span>
    </MotionButton>
  );
};
