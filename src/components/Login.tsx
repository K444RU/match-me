import { useState } from "react";
const Login = ({ isLogin = false}) => {
  const buttonClass =
    isLogin ? "bg-primary-50 text-text hover:bg-primary-200 hover:text-text rounded-md px-5 py-2" : "bg-primary text-text hover:bg-primary-700 hover:text-text rounded-md px-5 py-2";

  const [showOverlay, setShowOverlay] = useState(false);

  return (
    <div>
      <button onClick={() => setShowOverlay(true)} 
      className={buttonClass}>{isLogin ? "Log in" : "Create account"}</button>
      {showOverlay && (
        <div className="fixed inset-0 z-50 flex items-center justify-center">
        <div className="absolute inset-0 bg-black bg-opacity-50" 
        onClick={() => setShowOverlay(false)}></div>
        <div className="relative w-1/3 bg-primary-50 rounded-lg shadow-lg p-6 z-10">
          <h2 className="text-xl font-semibold mb-4">{isLogin ? "Log in" : "Create account"}</h2>
          {/* <p className="text-text-500">Enter your login details</p> */}
          <div className="mb-6">
            <div className="place-items-start mb-3">
              <label className="ml-2" htmlFor="email_address">Email Address</label>
              <div className="p-1 rounded-md border border-primary bg-text-100">
                <input id="email_address" className="bg-text-100"></input>
              </div>
            </div>
            <div className="flex space-x-2 place-items-start mb-3">
              <div className="place-items-start">
                <label className="ml-2" htmlFor="country_code">Country</label>
                <div className="p-1 rounded-md border border-primary bg-text-100">
                  <input id="country_code" className="bg-text-100 w-20" placeholder="+372"></input>
                </div>
              </div>
              <div className="place-items-start">
                <label className="ml-2" htmlFor="phone_number">Phone number</label>
                <div className="p-1 rounded-md border border-primary bg-text-100">
                  <input id="phone_number" className="bg-text-100"></input>
                </div>
              </div>
            </div>
            <div className="place-items-start mb-3">
              <label className="ml-2" htmlFor="user_name">Username</label>
              <div className="p-1 rounded-md border border-primary bg-text-100">
                <input id="user_name" className="bg-text-100"></input>
              </div>
            </div>
          </div>
          <button 
          onClick={() => setShowOverlay(false)} className="bg-primary text-text hover:bg-primary-200 hover:text-text rounded-md px-5 py-2">Register</button>
        </div>
        </div>
      )}
    </div>
  );
}

export default Login;