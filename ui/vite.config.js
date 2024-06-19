import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import fs from "fs";
import { nodePolyfills } from "vite-plugin-node-polyfills";

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [
		react(),
		nodePolyfills({
			include: ["path"],
		}),
	],
	// https for local dev
	// server: {
	// 	https: {
	// 		key: fs.readFileSync(
	// 			"C:\\Users\\Akshit\\Documents\\Java\\drive-s3\\certs\\localhost.key",
	// 		),
	// 		cert: fs.readFileSync(
	// 			"C:\\Users\\Akshit\\Documents\\Java\\drive-s3\\certs\\localhost.crt",
	// 		),
	// 	},
	// },
});
