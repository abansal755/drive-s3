import { Navigate, Outlet } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";

const UnprotectedRoute = () => {
	const { isLoading, user } = useAuthContext();
	if (isLoading) return null;
	if (!user) return <Outlet />;
	return <Navigate to="/" />;
};

export default UnprotectedRoute;
