import { ChakraProvider, ColorModeScript, extendTheme } from "@chakra-ui/react";
import React, { Fragment } from "react";
import ReactDOM from "react-dom/client";
import {
	createBrowserRouter,
	createRoutesFromChildren,
	Route,
	RouterProvider,
} from "react-router-dom";
import Root from "./components/Root";
import Login from "./components/Login";
import Register from "./components/Register";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";
import { AuthContextProvider } from "./context/AuthContext";
import ProtectedRoute from "./components/common/ProtectedRoute";
import UnprotectedRoute from "./components/common/UnprotectedRoute";
import Folder from "./components/Folder";
import RootIndex from "./components/RootIndex";
import File from "./components/File";

const router = createBrowserRouter(
	createRoutesFromChildren(
		<Fragment>
			<Route element={<ProtectedRoute />}>
				<Route path="/" element={<Root />}>
					<Route element={<RootIndex />} index />
					<Route path="folder/:folderId" element={<Folder />} />
					<Route path="file/:fileId" element={<File />} />
				</Route>
			</Route>
			<Route element={<UnprotectedRoute />}>
				<Route path="login" element={<Login />} />
				<Route path="register" element={<Register />} />
			</Route>
		</Fragment>,
	),
);

const config = {
	initialColorMode: "dark",
	useSystemColorMode: false,
};

const theme = extendTheme({ config });

const queryClient = new QueryClient();

ReactDOM.createRoot(document.getElementById("root")).render(
	<React.StrictMode>
		<ColorModeScript
			initialColorMode={theme.config.initialColorMode}
			storageKey="localStorage"
		/>
		<QueryClientProvider client={queryClient}>
			<AuthContextProvider>
				<ChakraProvider theme={theme}>
					<RouterProvider router={router} />
					<ReactQueryDevtools />
				</ChakraProvider>
			</AuthContextProvider>
		</QueryClientProvider>
	</React.StrictMode>,
);
