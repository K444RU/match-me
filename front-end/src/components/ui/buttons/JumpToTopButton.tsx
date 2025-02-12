import { AnimatePresence, motion } from 'motion/react';
import { useEffect, useState } from 'react';
import { FaArrowUp } from 'react-icons/fa';

const JumpToTopButton = () => {
  const [scrollPosition, setScrollPosition] = useState(0);

  useEffect(() => {
    const updatePosition = () => {
      setScrollPosition(window.scrollY);
    };
    window.addEventListener('scroll', updatePosition);
    updatePosition();
    return () => window.removeEventListener('scroll', updatePosition);
  }, []);

  return (
    <AnimatePresence>
      {scrollPosition > 0 && (
        <motion.div
          animate={{ x: 0 }}
          initial={{ x: 100 }}
          exit={{ x: 100 }}
          onClick={() => {
            window.scrollTo({ top: 0, behavior: 'smooth' });
          }}
          className="fixed bottom-5 right-5 overflow-hidden"
          role="presentation"
        >
          <button
            aria-label="Scroll to top"
            className="rounded-full bg-primary p-4 text-xl font-semibold text-background opacity-50 transition-opacity duration-300 hover:opacity-100"
          >
            <FaArrowUp aria-hidden="true" />
          </button>
        </motion.div>
      )}
    </AnimatePresence>
  );
};

export default JumpToTopButton;
