import { forwardRef } from 'react';
import { ImSpinner8 } from "react-icons/im";
import { motion } from 'motion/react';

// @ts-ignore
const MotionSpinner = forwardRef((props, ref) => {
  return (
    <motion.div
      animate={{
        rotate: 360,
      }}
      transition={{
        duration: 2,
        repeat: Infinity,
        ease: 'linear'
      }}
    >
      <ImSpinner8 />
    </motion.div>
  );
});

export default MotionSpinner;
