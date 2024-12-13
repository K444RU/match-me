import { ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { motion } from 'motion/react';

const MotionLink = motion.create(Link);

interface LinkButtonProps {
  to: string;
  content: ReactNode;
}

const LinkButton = ({ to, content }: LinkButtonProps) => {
  return (
    <MotionLink
      to={to}
      className="focus-visible:ring-ring text-primary-foreground inline-flex h-10 items-center justify-center gap-2 whitespace-nowrap rounded-md bg-primary-50 px-4 py-2 text-sm font-semibold tracking-wide ring-offset-background transition-colors duration-300 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:size-4 [&_svg]:shrink-0"
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
