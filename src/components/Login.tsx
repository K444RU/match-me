import { useState } from "react";
import LoginForm from "./LoginForm";
import RegisterForm from "./RegisterForm";


const Login = ({ isLogin = false }) => {
  const buttonClass = isLogin
    ? 'bg-primary-50 text-text hover:bg-primary-200 hover:text-text rounded-md px-5 py-2 duration-300 transition-colors font-semibold tracking-wide'
    : 'bg-primary text-background font-semibold tracking-wide transition-colors border-2 border-primary hover:text-primary hover:bg-transparent rounded-md px-5 py-2 duration-300';

    const [showOverlay, setShowOverlay] = useState(false);
  return (
    <div>
      <button onClick={() => setShowOverlay(true)} className={buttonClass}>
        {isLogin ? 'Log in' : 'Create account'}
      </button>
      {showOverlay && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
          <div
            className="absolute inset-0 bg-black bg-opacity-50"
            onClick={() => setShowOverlay(false)}
          ></div>
          <div className="relative z-10 w-1/3 rounded-lg bg-primary-50 p-6 shadow-lg">
            <h2 className="mb-4 text-xl font-semibold">
              {isLogin ? 'Log in' : 'Create account'}
            </h2>
            {/* <p className="text-text-500">Enter your login details</p> */}
            <div className="mb-6">
              {isLogin ? <LoginForm closeOverlay={() => setShowOverlay(false)}/> : <RegisterForm closeOverlay={() => setShowOverlay(false)}/>}
            </div>
          </div>
        </div>
      )}
    </div>
  );
};

export default Login;
