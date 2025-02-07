import { motion } from 'motion/react';
import { forwardRef } from 'react';
import { ImSpinner8 } from 'react-icons/im';

const MotionSpinner = forwardRef((_props, _ref) => {
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
      <ImSpinner8 />
    </motion.div>
  );
});

MotionSpinner.displayName = 'MotionSpinner';

export default MotionSpinner;
