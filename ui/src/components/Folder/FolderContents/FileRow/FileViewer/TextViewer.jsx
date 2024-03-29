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
import { useEffect, useState } from "react";
import useDownloadFile from "../../../../../hooks/useDownloadFile";
import escapeHTML from "escape-html";
import { useTheme } from "@emotion/react";
import Loading from "../../../../common/Loading";

const TextViewer = ({ file, isViewerOpen, onViewerOpen, onViewerClose }) => {
	const { initiateDownloadMutation, status, abortMutation } =
		useDownloadFile(file);
	const [contents, setContents] = useState("");
	const theme = useTheme();

	useEffect(() => {
		if (!isViewerOpen) return;
		(async () => {
			try {
				const { readable, writable } = new TransformStream();
				const reader = readable.getReader();
				initiateDownloadMutation.mutate(writable);
				let contents = new Uint8Array(0);
				while (true) {
					const { value, done } = await reader.read();
					if (done) break;
					contents = new Uint8Array([...contents, ...value]);
				}
				const textDecoder = new TextDecoder();
				setContents(textDecoder.decode(contents));
			} catch (err) {
				console.error(err);
			}
		})();
	}, [isViewerOpen]);

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
							<pre>{escapeHTML(contents)}</pre>
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
