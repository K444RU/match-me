import { motion } from 'motion/react';
import { ReactNode } from 'react';
import { Link } from 'react-router-dom';

const MotionLink = motion.create(Link);

interface LinkButtonProps {
  to: string;
  content: ReactNode;
}

const LinkButton = ({ to, content }: LinkButtonProps) => {
  return (
    <MotionLink
      to={to}
      className="focus-visible:ring-ring bg-card text-primary-foreground shadow-xs ring-offset-background focus-visible:outline-hidden inline-flex h-10 items-center justify-center gap-2 whitespace-nowrap rounded-md px-4 py-2 text-sm font-semibold tracking-wide transition-colors duration-300 focus-visible:ring-2 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0 "
      whileHover={{
        scale: 1.1,
      }}
      whileTap={{ scale: 0.9 }}
    >
      {content}
    </MotionLink>
  );
};

export default LinkButton;
