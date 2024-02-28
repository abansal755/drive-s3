import { useQuery } from "@tanstack/react-query";
import { Navigate } from "react-router-dom";
import { apiInstance } from "../lib/axios";
import AuthContextLoading from "./common/AuthContextLoading";

const RootIndex = () => {
    const { data: rootFolder, isSuccess } = useQuery({
        queryKey: ['rootFolder'],
        queryFn: async () => {
            const { data } = await apiInstance.get('/api/v1/folders/root');
            return data;
        },
        gcTime: 0
    });
    if(isSuccess) return <Navigate to={`../folder/${rootFolder.id}`}/>;
    return <AuthContextLoading/>;
}

export default RootIndex;