import axios from "axios";

export const authInstance = axios.create({
	baseURL: import.meta.env.VITE_AUTH_SERVICE_URI,
	withCredentials: true,
});

export const apiInstance = axios.create({
	baseURL: import.meta.env.VITE_API_SERVICE_URI,
	withCredentials: true,
});
