import { useState } from 'react';
import LoginForm from './LoginForm';
import RegisterForm from './RegisterForm';
import { IoClose } from 'react-icons/io5';

const Login = ({ isLogin = false }) => {
  const buttonClass = isLogin
    ? 'bg-primary-50 text-text hover:bg-primary-200 hover:text-text  px-5 py-2  '
    : 'bg-primary text-background  border-2 border-primary hover:text-primary hover:bg-transparent  px-5 py-2 ';

  const [showOverlay, setShowOverlay] = useState(false);
  return (
    <div>
      <button
        onClick={() => setShowOverlay(true)}
        className={`${buttonClass} rounded-md font-semibold tracking-wide transition-colors duration-300`}
      >
        {isLogin ? 'Log in' : 'Create account'}
      </button>
      {showOverlay && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div
            className="absolute inset-0 bg-black bg-opacity-50 backdrop-blur-sm"
            onClick={() => setShowOverlay(false)}
          ></div>
          <div className="relative z-10 max-w-xs w-full rounded-lg bg-primary-50 p-6 shadow-lg">
            <h2 className="w-full text-center text-2xl font-semibold mb-3">
              {isLogin ? 'Log in' : 'Create account'}
            </h2>
            <IoClose
              className="absolute -right-3 -top-3 h-8 w-8 rounded-3xl bg-primary-300 p-1.5 text-primary-50 hover:cursor-pointer hover:bg-primary-400 transition-colors"
              onClick={() => setShowOverlay(false)}
            />
            {/* <p className="text-text-500">Enter your login details</p> */}
            <div>
              {isLogin ? (
                <LoginForm setShowOverlay={setShowOverlay} />
              ) : (
                <RegisterForm setShowOverlay={setShowOverlay} />
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Login;
