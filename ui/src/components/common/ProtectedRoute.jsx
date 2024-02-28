import { Navigate, Outlet } from "react-router-dom";
import { useAuthContext } from "../../context/AuthContext";

const ProtectedRoute = () => {
	const { isLoading, user } = useAuthContext();
	if (isLoading) return null;
	if (!user) return <Navigate to="/login" />;
	return <Outlet />;
};

export default ProtectedRoute;
