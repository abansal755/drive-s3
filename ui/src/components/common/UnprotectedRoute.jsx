import { Navigate, Outlet } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";
import AuthContextLoading from "./AuthContextLoading";

const UnprotectedRoute = () => {
	const { isLoading, user } = useAuthContext();
	if (isLoading) return <AuthContextLoading />;
	if (!user) return <Outlet />;
	return <Navigate to="/" />;
};

export default UnprotectedRoute;
