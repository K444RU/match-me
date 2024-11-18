import { Outlet } from "react-router-dom";
import { motion } from "motion/react";
import Navbar from "../components/Navbar"
//import Navbar from "../components/Navbar";

const MainLayout = () => {
  return (
    <>
      <Navbar />
      <Outlet />
    </>
  );
};

export default MainLayout;
