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
				try { authInstance.post("/api/v1/token") }
				catch(err) {
					console.error(err);
					logoutMutation.mutate();
					clearTimeout(id);
				}
			},
			expireAt - 10 * 1000 - Date.now()
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
				}
			);
			return user;
		},
		onSuccess: (user) => queryClient.setQueryData(["user"], user),
	});

	const registerMutation = useMutation({
		mutationFn: async ({ email, password, firstName, lastName }) => {
			const { data: user } = await authInstance.post("/api/v1/users", {
				email,
				password,
				firstName,
				lastName,
			});
			return user;
		},
		onSuccess: (user) => queryClient.setQueryData(["user"], user),
	});

	const logoutMutation = useMutation({
		mutationFn: () => authInstance.post("/api/v1/users/logout"),
		onSuccess: () => queryClient.setQueryData(["user"], null),
	});

	return (
		<AuthContext.Provider
			value={{
				user,
				isLoading:
					isLoading ||
					loginMutation.isPending ||
					registerMutation.isPending ||
					logoutMutation.isPending,
				isLoggedIn: !!user,
				login: loginMutation.mutate,
				register: registerMutation.mutate,
				logout: logoutMutation.mutate,
			}}
		>
			{children}
		</AuthContext.Provider>
	);
};
