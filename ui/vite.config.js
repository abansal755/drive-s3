import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import fs from "fs";

// https://vitejs.dev/config/
export default defineConfig({
	plugins: [react()],
	server: {
		https: {
			key: fs.readFileSync(
				"C:\\Users\\akshi\\Documents\\Java Projects\\Drive-S3\\certs\\localhost.key",
			),
			cert: fs.readFileSync(
				"C:\\Users\\akshi\\Documents\\Java Projects\\Drive-S3\\certs\\localhost.crt",
			),
		},
	},
});
