import { AnimatePresence, motion } from 'motion/react';
import { useState } from 'react';
import { IoClose } from 'react-icons/io5';
import LoginForm from './LoginForm';
import RegisterForm from './RegisterForm';
import { Button } from '@/components/ui/button';

const Login = ({ isLogin = false }) => {
  const [showOverlay, setShowOverlay] = useState(false);
  return (
    <div>
      <Button
        onClick={() => setShowOverlay(true)}
        className={`font-semibold tracking-wide`}
      >
        {isLogin ? 'Log in' : 'Create account'}
      </Button>
      <AnimatePresence>
        {showOverlay && (
          <motion.div exit={{ opacity: 0 }} className="fixed inset-0 z-50 flex items-center justify-center">
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              className="backdrop-blur-xs absolute inset-0 bg-black/50"
              onClick={() => setShowOverlay(false)}
            ></motion.div>
            <motion.div
              initial={{ scale: 0.3 }}
              animate={{ scale: 1 }}
              exit={{ scale: 0 }}
              className="bg-primary-50 relative z-10 w-full max-w-xs rounded-lg p-6 shadow-lg"
            >
              <h2 className="mb-3 w-full text-center text-2xl font-semibold">
                {isLogin ? 'Log in' : 'Create account'}
              </h2>
              <IoClose
                className="bg-primary text-primary-foreground hover:bg-primary/50 absolute -right-3 -top-3 size-8 rounded-3xl p-1.5"
                onClick={() => setShowOverlay(false)}
              />
              {/* <p className="text-text-500">Enter your login details</p> */}
              <div>{isLogin ? <LoginForm /> : <RegisterForm />}</div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
};

export default Login;
