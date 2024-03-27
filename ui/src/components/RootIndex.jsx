import { useQuery } from "@tanstack/react-query";
import { Navigate } from "react-router-dom";
import { apiInstance } from "../lib/axios";
import { useAuthContext } from "../context/AuthContext.jsx";
import Loading from "./common/Loading.jsx";

const RootIndex = () => {
	const { user } = useAuthContext();
	const { data: rootFolder, isSuccess } = useQuery({
		queryKey: ["user", user.id, "rootFolder"],
		queryFn: async () => {
			const { data } = await apiInstance.get("/api/v1/folders/root");
			return data;
		},
		staleTime: Infinity,
		gcTime: Infinity,
	});
	if (isSuccess) return <Navigate to={`../folder/${rootFolder.id}`} />;
	return <Loading />;
};

export default RootIndex;
