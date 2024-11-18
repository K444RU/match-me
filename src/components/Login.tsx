import { useState } from 'react';
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
              <div className="mb-3 place-items-start">
                <label className="ml-2" htmlFor="email_address">
                  Email Address
                </label>
                <div className="rounded-md border border-primary bg-text-100 p-1">
                  <input id="email_address" className="bg-text-100"></input>
                </div>
              </div>
              <div className="mb-3 flex place-items-start space-x-2">
                <div className="place-items-start">
                  <label className="ml-2" htmlFor="country_code">
                    Country
                  </label>
                  <div className="rounded-md border border-primary bg-text-100 p-1">
                    <input
                      id="country_code"
                      className="w-20 bg-text-100"
                      placeholder="+372"
                    ></input>
                  </div>
                </div>
                <div className="place-items-start">
                  <label className="ml-2" htmlFor="phone_number">
                    Phone number
                  </label>
                  <div className="rounded-md border border-primary bg-text-100 p-1">
                    <input id="phone_number" className="bg-text-100"></input>
                  </div>
                </div>
              </div>
              <div className="mb-3 place-items-start">
                <label className="ml-2" htmlFor="user_name">
                  Username
                </label>
                <div className="rounded-md border border-primary bg-text-100 p-1">
                  <input id="user_name" className="bg-text-100"></input>
                </div>
              </div>
            </div>
            <button
              onClick={() => setShowOverlay(false)}
              className="rounded-md bg-primary px-5 py-2 text-text hover:bg-primary-200 hover:text-text"
            >
              Register
            </button>
          </div>
        </div>
      )}
    </div>
  );
};

export default Login;
