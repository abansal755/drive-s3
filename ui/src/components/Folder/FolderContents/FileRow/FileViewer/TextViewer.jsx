import {
	Box,
	Button,
	Modal,
	ModalBody,
	ModalCloseButton,
	ModalContent,
	ModalFooter,
	ModalHeader,
	ModalOverlay,
} from "@chakra-ui/react";
import { useEffect, useMemo, useRef } from "react";
import useDownloadFile from "../../../../../hooks/useDownloadFile";
import { useTheme } from "@emotion/react";
import Loading from "../../../../common/Loading";

const TextViewer = ({ file, isViewerOpen, onViewerOpen, onViewerClose }) => {
	const { initiateDownloadMutation, status, abortMutation } =
		useDownloadFile(file);
	const blobParts = useRef([]);
	const theme = useTheme();

	useEffect(() => {
		if (!isViewerOpen) {
			blobParts.current = [];
			return;
		}
		(async () => {
			try {
				const { readable, writable } = new TransformStream();
				const reader = readable.getReader();
				initiateDownloadMutation.mutate(writable);
				while (true) {
					const { value, done } = await reader.read();
					if (done) break;
					blobParts.current.push(value);
				}
			} catch (err) {
				console.error(err);
			}
		})();
	}, [isViewerOpen]);

	const getTextFromBlobParts = (blobParts) => {
		const uint8arr = [];
		blobParts.forEach((blobPart) => {
			blobPart.forEach((uint8) => uint8arr.push(uint8));
		});
		const textDecoder = new TextDecoder();
		return textDecoder.decode(new Uint8Array(uint8arr));
	};

	const decodedText = useMemo(() => {
		if (status === "DOWNLOADED")
			return getTextFromBlobParts(blobParts.current);
	}, [status]);

	const closeBtnClickHandler = () => {
		if (status === "DOWNLOADING") abortMutation.mutate();
		else if (status === "DOWNLOADED") onViewerClose();
	};

	useEffect(() => {
		if (status === "ABORTED") onViewerClose();
	}, [status]);

	return (
		<Modal
			isOpen={isViewerOpen}
			onClose={onViewerClose}
			size="xl"
			scrollBehavior="inside"
			closeOnOverlayClick={false}
		>
			<ModalOverlay />
			<ModalContent>
				<ModalHeader>
					{file.name}
					{file.extension && `.${file.extension}`}
				</ModalHeader>
				<ModalBody overflowX="auto">
					{status !== "DOWNLOADED" && <Loading />}
					{status === "DOWNLOADED" && (
						<Box
							bgColor={theme.colors.gray[800]}
							p={3}
							borderRadius={5}
							w="fit-content"
							minW="100%"
						>
							<pre>{decodedText}</pre>
						</Box>
					)}
				</ModalBody>
				<ModalFooter>
					<Button
						colorScheme="blue"
						onClick={closeBtnClickHandler}
						isDisabled={status === "STARTING"}
						loadingText="Closing"
						isLoading={status === "ABORTING"}
					>
						Close
					</Button>
				</ModalFooter>
				<ModalCloseButton
					isDisabled={status === "STARTING" || status === "ABORTING"}
				/>
			</ModalContent>
		</Modal>
	);
};

export default TextViewer;
