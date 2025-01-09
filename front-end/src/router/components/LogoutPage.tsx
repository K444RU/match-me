import { useAuth } from "@/features/authentication";
import { useEffect } from "react";

const LogoutPage = () => {
    const { user, logout } = useAuth();

    useEffect(() => {
        if (user) logout();
    }, [user, logout]);
    return null;
};

export default LogoutPage;