import { DownloadIcon } from "@chakra-ui/icons";
import {
	Button,
	IconButton,
	Modal,
	ModalBody,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
	Progress,
	Text,
	Tooltip,
	useDisclosure,
} from "@chakra-ui/react";
import prettyBytes from "pretty-bytes";
import { Fragment, useEffect, useState } from "react";
import { apiInstance } from "../../../../lib/axios";
import { useMutation } from "@tanstack/react-query";

const getSuggestedNameFromFile = (file) => {
	const { name, extension } = file;
	if (!extension) return name;
	return `${name}.${extension}`;
};

const DownloadFileButton = ({ file }) => {
	const { isOpen, onOpen, onClose } = useDisclosure();
	const [bytesDownloaded, setBytesDownloaded] = useState(0);
	const [downloadId, setDownloadId] = useState(null);

	const modalCloseHandler = () => {
		onClose();
		setDownloadId(null);
		setBytesDownloaded(0);
	};

	const downloadBtnClickHandler = async () => {
		try {
			const {
				data: { downloadId },
			} = await apiInstance.post(`/api/v1/downloads/initiate/${file.id}`);
			setDownloadId(downloadId);
		} catch (err) {
			console.error(err);
		}
	};

	const abortMutation = useMutation({
		mutationFn: async () => {
			try {
				await apiInstance.patch(
					`/api/v1/downloads/abort/${downloadId}`,
				);
			} catch (err) {
				console.error(err);
			}
		},
	});

	useEffect(() => {
		if (!downloadId) return;
		(async () => {
			try {
				const fileHandle = await window.showSaveFilePicker({
					suggestedName: getSuggestedNameFromFile(file),
				});
				const writableStream = await fileHandle.createWritable();
				const writer = writableStream.getWriter();
				onOpen();

				const res = await fetch(
					`${import.meta.env.VITE_API_SERVICE_URI}/api/v1/downloads/stream/${downloadId}`,
					{ credentials: "include" },
				);
				const reader = res.body.getReader();

				while (true) {
					const { value, done } = await reader.read();
					if (done) break;
					setBytesDownloaded((prev) => prev + value.length);
					await writer.ready;
					await writer.write(value);
				}
				writer.close();
				modalCloseHandler();
			} catch (err) {
				console.error(err);
			}
		})();
	}, [downloadId]);

	return (
		<Fragment>
			<Tooltip label="Download File" hasArrow>
				<IconButton
					icon={<DownloadIcon boxSize={5} />}
					colorScheme="blue"
					size="sm"
					onClick={downloadBtnClickHandler}
				/>
			</Tooltip>
			<Modal
				isOpen={isOpen}
				onClose={modalCloseHandler}
				closeOnOverlayClick={false}
			>
				<ModalOverlay />
				<ModalContent>
					<ModalHeader>Downloading File</ModalHeader>
					<ModalBody>
						<Progress
							value={(bytesDownloaded / file.sizeInBytes) * 100}
						/>
						<Text mt={2}>
							Downloaded {prettyBytes(bytesDownloaded)} out of{" "}
							{prettyBytes(file.sizeInBytes)}
						</Text>
					</ModalBody>
					<ModalFooter>
						<Button
							colorScheme="red"
							onClick={abortMutation.mutate}
							isLoading={
								abortMutation.isPending ||
								abortMutation.isSuccess
							}
							loadingText="Aborting"
						>
							Abort
						</Button>
					</ModalFooter>
				</ModalContent>
			</Modal>
		</Fragment>
	);
};

export default DownloadFileButton;
