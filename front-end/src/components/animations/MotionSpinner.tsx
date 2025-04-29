import { motion } from 'motion/react';
import { forwardRef } from 'react';
import { ImSpinner8 } from 'react-icons/im';

interface MotionSpinnerProps {
  size?: number;
}

const MotionSpinner = forwardRef<HTMLDivElement, MotionSpinnerProps>(({ size = 16 }, _ref) => {
  return (
    <motion.div
      animate={{
        rotate: 360,
      }}
      transition={{
        duration: 2,
        repeat: Infinity,
        ease: 'linear',
      }}
    >
      <ImSpinner8 size={size} />
    </motion.div>
  );
});

MotionSpinner.displayName = 'MotionSpinner';

export default MotionSpinner;
