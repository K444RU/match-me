import { Outlet } from 'react-router-dom';
import Navbar from '../Navbar';
import { ThemeProvider } from '../ThemeProvider';

const MainLayout = () => {
    return (
        <ThemeProvider defaultTheme='light' storageKey="vite-ui-theme">
            <Navbar />
            <Outlet />
        </ThemeProvider>
    );
};

export default MainLayout;
