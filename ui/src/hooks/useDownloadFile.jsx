import { useMutation } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { apiInstance } from "../lib/axios";

const useDownloadFile = (file, onComplete) => {
	const [downloadId, setDownloadId] = useState(null);
	const [bytesDownloaded, setBytesDownloaded] = useState(0);
	const [writableStream, setWritableStream] = useState(null);
	const [status, setStatus] = useState("NOT_STARTED");

	const initiateDownloadMutation = useMutation({
		mutationFn: async (writableStream) => {
			setStatus("STARTING");
			const {
				data: { downloadId },
			} = await apiInstance.post(`/api/v1/downloads/initiate/${file.id}`);
			return downloadId;
		},
		onSuccess: (downloadId, writableStream) => {
			setDownloadId(downloadId);
			setWritableStream(writableStream);
		},
	});

	const abortMutation = useMutation({
		mutationFn: async () => {
			await apiInstance.patch(`/api/v1/downloads/abort/${downloadId}`);
		},
		onSuccess: () => setStatus("ABORTING"),
	});

	useEffect(() => {
		if (!downloadId) return;
		(async () => {
			try {
				const res = await fetch(
					`${import.meta.env.VITE_API_SERVICE_URI}/api/v1/downloads/stream/${downloadId}`,
					{ credentials: "include" },
				);
				const reader = res.body.getReader();
				const writer = writableStream.getWriter();
				setStatus("DOWNLOADING");

				while (true) {
					const { value, done } = await reader.read();
					if (done) break;
					setBytesDownloaded((prev) => prev + value.length);
					await writer.ready;
					await writer.write(value);
				}
				writer.close();
				if (typeof onComplete === "function") await onComplete();
				setStatus((prev) => {
					if (prev === "DOWNLOADING") return "DOWNLOADED";
					else if (prev === "ABORTING") return "ABORTED";
				});

				// clearing states after download
				initiateDownloadMutation.reset();
				abortMutation.reset();
				setDownloadId(null);
				setBytesDownloaded(0);
				setWritableStream(null);
			} catch (err) {
				console.error(err);
				setStatus("ERROR");
			}
		})();
	}, [downloadId]);

	return {
		initiateDownloadMutation,
		abortMutation,
		bytesDownloaded,
		status,
	};
};

export default useDownloadFile;
