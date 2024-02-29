import { useQuery } from "@tanstack/react-query";
import { Navigate } from "react-router-dom";
import { apiInstance } from "../lib/axios";
import AuthContextLoading from "./common/AuthContextLoading";
import { useAuthContext } from "../context/AuthContext.jsx";

const RootIndex = () => {
  const { user } = useAuthContext();
  const { data: rootFolder, isSuccess } = useQuery({
    queryKey: ["user", user.id, "rootFolder"],
    queryFn: async () => {
      const { data } = await apiInstance.get("/api/v1/folders/root");
      return data;
    },
    staleTime: Infinity,
  });
  if (isSuccess) return <Navigate to={`../folder/${rootFolder.id}`} />;
  return <AuthContextLoading />;
};

export default RootIndex;
