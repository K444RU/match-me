import { Outlet } from "react-router-dom";
import { motion } from "motion/react";
//import Navbar from "../components/Navbar";

const MainLayout = () => {
  return (
    <div className="dark h-screen w-screen bg-background text-text">
      <motion.button
        whileHover={{ scale: 1.1 }}
        whileTap={{ scale: 0.95 }}
        className="m-4 rounded-lg bg-primary-500 p-4 text-text transition-colors hover:bg-primary-400"
      >
        Button
      </motion.button>
      <Outlet />
    </div>
  );
};

export default MainLayout;
