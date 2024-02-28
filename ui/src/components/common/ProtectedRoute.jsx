import { Navigate, Outlet } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";
import AuthContextLoading from "./AuthContextLoading";

const ProtectedRoute = () => {
	const { isLoading, user } = useAuthContext();
	if (isLoading) return <AuthContextLoading/>;
	if (!user) return <Navigate to="/login" />;
	return <Outlet />;
};

export default ProtectedRoute;
