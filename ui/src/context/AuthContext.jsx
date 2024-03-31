import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { createContext, useContext, useEffect } from "react";
import { authInstance } from "../lib/axios";

const AuthContext = createContext();

export const useAuthContext = () => {
	return useContext(AuthContext);
};

export const AuthContextProvider = ({ children }) => {
	const queryClient = useQueryClient();

	const { data: user, isLoading } = useQuery({
		queryKey: ["user"],
		queryFn: async () => {
			try {
				await authInstance.post("/api/v1/token");
				const { data: user } = await authInstance.get("/api/v1/users");
				return user;
			} catch (err) {
				console.log(err);
				return null;
			}
		},
		staleTime: Infinity,
	});

	useEffect(() => {
		if (!user) return;
		const expireAt = user.accessTokenExpireAtMillis;
		const id = setInterval(
			async () => {
				try {
					authInstance.post("/api/v1/token");
				} catch (err) {
					console.error(err);
					clearInterval(id);
					logoutMutation.mutate();
				}
			},
			expireAt - 10 * 1000 - Date.now(),
		);

		return () => clearInterval(id);
	}, [user]);

	const loginMutation = useMutation({
		mutationFn: async ({ email, password }) => {
			const { data: user } = await authInstance.post(
				"/api/v1/users/login",
				{
					email,
					password,
				},
			);
			return user;
		},
		onSuccess: (user) => queryClient.setQueryData(["user"], user),
	});

	const registerMutation = useMutation({
		mutationFn: async ({
			email,
			password,
			confirmPassword,
			firstName,
			lastName,
		}) => {
			const { data: user } = await authInstance.post("/api/v1/users", {
				email,
				password,
				confirmPassword,
				firstName,
				lastName,
			});
			return user;
		},
		onSuccess: (user) => queryClient.setQueryData(["user"], user),
	});

	const logoutMutation = useMutation({
		mutationFn: () => authInstance.post("/api/v1/users/logout"),
		onSuccess: () => {
			queryClient.setQueryData(["user"], null);
			queryClient.removeQueries({
				queryKey: ["folder"],
			});
			queryClient.removeQueries({
				queryKey: ["file"],
			});
		},
	});

	return (
		<AuthContext.Provider
			value={{
				user,
				isLoading: isLoading,
				isLoggedIn: !!user,
				login: loginMutation,
				register: registerMutation,
				logout: logoutMutation.mutate,
			}}
		>
			{children}
		</AuthContext.Provider>
	);
};
